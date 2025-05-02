import { Component, OnInit } from '@angular/core'; // Added OnInit
import { ActivatedRoute, Router } from '@angular/router';
import { FlightOfferDataService } from './flight-offer-data.service';
import { FlightOffer } from '../flight-search-page/flight-search.model';
import { RatingService } from './rating.service';
import { BanedUserRating } from '../banned-ratings/banned-ratings.service';
import { MatDialog } from '@angular/material/dialog';
import { PriceThresholdDialogComponent, PriceThresholdDialogData } from './price-threshold-dialog/price-threshold-dialog.component'; // Import PriceThresholdDialogData
import { AuthService } from '../auth/auth.service'; // Import AuthService
import { HistoryService } from '../history/history.service';
import { Observable } from 'rxjs';
import { AdminService } from '../admin/admin.service';
import { CommonModule } from '@angular/common';
import { PriceThresholdService } from './price-threshold-service';


@Component({
  selector: 'app-flight-offer',
  standalone: false,
  templateUrl: './flight-offer.component.html',
  styleUrls: ['./flight-offer.component.css'] // Corrected styleUrl to styleUrls
})
export class FlightOfferComponent implements OnInit { // Implement OnInit
  authState$!: Observable<{
    isLoggedIn: boolean;
    username?: string;
    role?: string;
  }>;
  selectedFlightOffer: FlightOffer | null = null;
  ratings: any[] = [];
  averageRating: number = 0;
  currentUserId: number = 0;

  constructor(
    private route: ActivatedRoute,
    private flightOfferDataService: FlightOfferDataService,
    private router: Router,
    private ratingService: RatingService,
    private bannedUserRatingService: BanedUserRating,
    private dialog: MatDialog,
    private authService: AuthService, // Inject AuthService
    private historyService: HistoryService, // Inject HistoryService
    private adminService: AdminService, // Inject AdminService
    private priceThresholdService: PriceThresholdService // Inject PriceThresholdDialogComponent
  ) {
    this.authState$ = this.authService.authState$ as Observable<{
      isLoggedIn: boolean;
      username?: string;
      role?: string;
    }>;
  }

  ngOnInit(): void {
    // TODO: Implement logic to get the actual current user ID, perhaps from authService
    // Example: this.currentUserId = this.authService.getCurrentUserId();
    // For now, we use the placeholder initialized above.

    const navigation = this.router.lastSuccessfulNavigation;
    if (navigation?.extras?.state) {
      this.selectedFlightOffer = navigation.extras.state['selectedFlightOffer'];
      console.log(navigation.extras.state['selectedFlightOffer'])
      if (this.selectedFlightOffer) { // Check if selectedFlightOffer is not null
         this.loadRatings();
      }
    }
  }

  makeBooking(): void {
    console.log(this.selectedFlightOffer)
    this.router.navigate(['/makebooking'], {
      state: { selectedFlightOffer: this.selectedFlightOffer }
    });
  }

  setPriceThreshold(): void {
    if (!this.selectedFlightOffer) {
        console.error("Cannot set price threshold: No flight offer selected.");
        alert("Please select a flight offer first.");
        return;
    }

    const dialogRef = this.dialog.open(PriceThresholdDialogComponent, {
      width: '300px',
      data: { currentPrice: this.selectedFlightOffer.basePrice } // Pass current price
    });

    dialogRef.afterClosed().subscribe(result => {
      // Check if result is a valid number (dialog wasn't cancelled)
      if (typeof result === 'number') {
        const thresholdPrice = result;
        console.log('Price threshold set to:', thresholdPrice);

        this.fetchUser().subscribe({
          next: () => {
            this.createPriceThreshold(thresholdPrice);
          },
          error: (error) => {
            console.error('Failed to fetch user:', error);
          }
        });

      } else {
        console.log('Set price threshold cancelled or closed without value.');
      }
    });
  }

  fetchUser (): Observable<any> {
      return new Observable(observer => {
        this.authService.initAuthStateFromBackend();

        this.authState$.subscribe({
          next: (authState) => {
            console.log('Auth state:', authState);
          },
          error: (err) => {
            observer.error('Error in auth state');
          }
        });

        this.adminService.getCurrentUser().subscribe({
          next: (data) => {
            console.log('Data:', data);
            const newdata = JSON.parse(data);
            this.currentUserId=Number(newdata.userId);
            observer.next();
            observer.complete();
          },
          error: (err) => {
            observer.error('Error fetching current user');
            this.router.navigate(['/login']);
          }
        });
      });
    }

  createPriceThreshold(thresholdPrice: number): void {
    if (this.selectedFlightOffer) {
      const thresholdData: PriceThresholdDialogData = {
          thresholdPrice: thresholdPrice,
          conceptType: 'FLIGHT',
          conceptId: this.selectedFlightOffer.flightId,
          userId: this.currentUserId,
          startDate: this.selectedFlightOffer.departureDate,
          endDate: this.selectedFlightOffer.arrivalDate
      };
      console.log('Constructed PriceThresholdDialogData:', thresholdData);
      // TODO: Send thresholdData to backend service here
      this.priceThresholdService.setPriceThreshold(thresholdData).subscribe(
        response => {
          console.log('Price threshold set successfully:', response);
          alert(`Price threshold set to: $${thresholdPrice}. Data prepared (see console).`);
        },
        error => {
          console.error('Error setting price threshold:', error);
          alert("An error occurred. Could not set price threshold.");
        }
      );
  } else {
       console.error("Flight offer became null after dialog closed.");
       alert("An error occurred. Could not set price threshold.");
  }
  }

  loadRatings(): void {
    // Ensure selectedFlightOffer is not null before accessing airlineId
    if (this.selectedFlightOffer?.airlineId) {
        const airlineId: number = this.selectedFlightOffer.airlineId;

      this.ratingService.getRatings(airlineId).subscribe((data: any[]) => {
        this.ratings = data.filter(rating => rating.delete === 'F');
        const userIds = [...new Set(this.ratings.map(rating => rating.userId))];
        const userDetailsPromises = userIds.map((userId: number) =>
          this.bannedUserRatingService.getUser(userId).toPromise()
        );

        Promise.all(userDetailsPromises)
          .then(userDetailsResponses => {
            const userDetailsMap = new Map<number, any>();
            userDetailsResponses.forEach((user: any) => {
              // Assuming user response needs parsing if it's a stringified JSON
              try {
                  const parsedUser = typeof user === 'string' ? JSON.parse(user) : user;
                  if(parsedUser && parsedUser.id) {
                     userDetailsMap.set(parsedUser.id, parsedUser.username);
                  }
              } catch (e) {
                  console.error("Error parsing user details:", e, "User data:", user);
              }
            });

            this.ratings = this.ratings.map(rating => {
              const username = userDetailsMap.get(rating.userId);
              return {
                ...rating,
                username: username || 'Unknown User', // Provide fallback username
              };
            });

            const total = this.ratings.reduce((sum, rating) => sum + rating.rating, 0);
            this.averageRating = this.ratings.length > 0 ? Math.round(total / this.ratings.length) : 0;
          })
          .catch(err => {
            console.error("Error fetching user details:", err);
          });
      });
    } else {
        console.log("Cannot load ratings: No flight offer selected or airlineId missing.");
    }
  }
}
