import { Component, OnInit } from '@angular/core'; // Add OnInit
import { ActivatedRoute, Router } from '@angular/router';
import { HotelOfferDataService } from './hotel-offer-data.service';
import { HotelOffer } from '../hotel-search-page/hotel-search.model';
import { RatingService } from './rating.service';
import { BanedUserRating } from '../banned-ratings/banned-ratings.service';
import { MatDialog } from '@angular/material/dialog';
// Import PriceThresholdDialogData along with the component
import { PriceThresholdDialogComponent, PriceThresholdDialogData } from '../flight-offer/price-threshold-dialog/price-threshold-dialog.component';
// Import necessary services and types
import { AuthService } from '../auth/auth.service';
import { AdminService } from '../admin/admin.service';
import { PriceThresholdService } from '../flight-offer/price-threshold-service';
import { Observable } from 'rxjs';


@Component({
  selector: 'app-hotel-offer',
  standalone: false,
  templateUrl: './hotel-offer.component.html',
  styleUrl: './hotel-offer.component.css'
})
// Implement OnInit
export class HotelOfferComponent implements OnInit {
  selectedHotelOffer: HotelOffer | null = null;
  hotelId: any = 1; // Keep existing or use selectedHotelOffer.hotelId if available
  ratings: any[] = [];
  averageRating: number = 0;
  // Add authState$ and currentUserId properties
  authState$!: Observable<{
    isLoggedIn: boolean;
    username?: string;
    role?: string;
  }>;
  currentUserId: number = 0;

  constructor(
    private route: ActivatedRoute,
    private hotelOfferDataService: HotelOfferDataService,
    private router: Router,
    private ratingService: RatingService,
    private bannedUserRatingService: BanedUserRating,
    private dialog: MatDialog,
    // Inject the required services
    private authService: AuthService,
    private adminService: AdminService,
    private priceThresholdService: PriceThresholdService
  ) {
     // Initialize authState$
     this.authState$ = this.authService.authState$ as Observable<{
      isLoggedIn: boolean;
      username?: string;
      role?: string;
    }>;
  }

  ngOnInit(): void {
    // Existing ngOnInit logic...
    this.loadRatings(); // Keep existing loadRatings call
    const navigation = this.router.lastSuccessfulNavigation;
    if (navigation?.extras?.state) {
      this.selectedHotelOffer = navigation.extras.state['selectedHotelOffer'];
      console.log(navigation.extras.state['selectedHotelOffer'])
      // Potentially update hotelId based on the offer if needed for loadRatings
      // if (this.selectedHotelOffer) {
      //   this.hotelId = this.selectedHotelOffer.hotelId;
      //   this.loadRatings(); // Reload ratings if hotelId changes
      // }
    }
  }

  makeBooking(): void {
    // Existing makeBooking logic...
    console.log(this.selectedHotelOffer)
    this.router.navigate(['/makebooking'], {
      state: { selectedHotelOffer: this.selectedHotelOffer }
    });
  }

  loadRatings(): void {
    // Existing loadRatings logic... (NO CHANGES HERE)
    this.ratingService.getRatings(this.hotelId).subscribe((data: any[]) => {
      this.ratings = data.filter(rating => rating.delete === 'F');

      const userIds = [...new Set(this.ratings.map(rating => rating.userId))];

      console.log(userIds);


      const userDetailsPromises = userIds.map((userId: number) =>
        this.bannedUserRatingService.getUser(userId).toPromise()
      );

      Promise.all(userDetailsPromises)
        .then(userDetailsResponses => {
          const userDetailsMap = new Map<number, string>();
          userDetailsResponses.forEach((user: any) => {
            console.log('Data:', user);
            const newdata = JSON.parse(user);
            console.log(newdata);
            userDetailsMap.set(newdata.id, newdata.username);
          });

          console.log(userDetailsMap);

          this.ratings = this.ratings.map(rating => {
            const user = userDetailsMap.get(rating.userId);
            console.log('User:', user);
            return {
              ...rating,
              user,
            };
          });

          const total = this.ratings.reduce((sum, rating) => sum + rating.rating, 0);
          this.averageRating = this.ratings.length > 0 ? Math.round(total / this.ratings.length) : 0;

          console.log('Average Rating:', this.averageRating);
          console.log('Ratings:', this.ratings);
        })
        .catch(err => {
          console.error("Error fetching user details:", err);
        });
    });
  }

  // Updated setPriceThreshold method
  setPriceThreshold(): void {
    if (!this.selectedHotelOffer) {
        console.error("Cannot set price threshold: No hotel offer selected.");
        alert("Please select a hotel offer first.");
        return;
    }

    const dialogRef = this.dialog.open(PriceThresholdDialogComponent, {
      width: '300px',
      // Pass current price if available and needed by the dialog
      data: { currentPrice: this.selectedHotelOffer.totalPrice }
    });

    dialogRef.afterClosed().subscribe(result => {
      // Check if result is a valid number (dialog wasn't cancelled)
      // Use typeof check for robustness as in flight component
      if (typeof result === 'number') {
        const thresholdPrice = result;
        console.log('Hotel price threshold set to:', thresholdPrice);

        // Fetch user first, then create threshold
        this.fetchUser().subscribe({
          next: () => {
            this.createPriceThreshold(thresholdPrice);
          },
          error: (error) => {
            console.error('Failed to fetch user:', error);
            // Handle error, maybe redirect to login or show message
            alert("Could not verify user. Please log in again.");
          }
        });

      } else {
        console.log('Set hotel price threshold cancelled or closed without value.');
      }
    });
  }

  // Add fetchUser method (copied from FlightOfferComponent)
  fetchUser(): Observable<any> {
      return new Observable(observer => {
        this.authService.initAuthStateFromBackend(); // Ensure auth state is fresh

        this.adminService.getCurrentUser().subscribe({
          next: (data) => {
            console.log('Current User Data:', data);
            try {
              // Ensure data is parsed correctly
              const newdata = typeof data === 'string' ? JSON.parse(data) : data;
              this.currentUserId = Number(newdata.userId);
              if (isNaN(this.currentUserId) || this.currentUserId <= 0) {
                 throw new Error('Invalid userId received');
              }
              observer.next(); // Signal success
              observer.complete();
            } catch (e) {
               console.error('Error parsing user data or invalid userId:', e);
               observer.error('Error processing user data');
               this.router.navigate(['/login']); // Redirect on error
            }
          },
          error: (err) => {
            console.error('Error fetching current user:', err);
            observer.error('Error fetching current user');
            this.router.navigate(['/login']); // Redirect on error
          }
        });
      });
    }

  // Add createPriceThreshold method
  createPriceThreshold(thresholdPrice: number): void {
    // Ensure selectedHotelOffer and required properties exist
    if (this.selectedHotelOffer && this.selectedHotelOffer.hotelId && this.selectedHotelOffer.checkInDate && this.selectedHotelOffer.checkOutDate) {
      const thresholdData: PriceThresholdDialogData = {
          thresholdPrice: thresholdPrice,
          conceptType: 'HOTEL', // Set conceptType to HOTEL
          conceptId: this.selectedHotelOffer.hotelId, // Use hotelId from the offer
          userId: this.currentUserId,
          // Assuming checkInDate and checkOutDate exist on HotelOffer model
          startDate: this.selectedHotelOffer.checkInDate,
          endDate: this.selectedHotelOffer.checkOutDate
      };
      console.log('Constructed Hotel PriceThreshold Data:', thresholdData);

      // Call the service to set the threshold
      this.priceThresholdService.setPriceThreshold(thresholdData).subscribe(
        response => {
          console.log('Hotel price threshold set successfully:', response);
          alert(`Hotel price threshold set to: $${thresholdPrice}.`);
        },
        error => {
          console.error('Error setting hotel price threshold:', error);
          alert("An error occurred. Could not set hotel price threshold.");
        }
      );
  } else {
       // Log error and inform user if essential data is missing
       console.error("Hotel offer details (ID, dates) missing or became null after dialog closed.");
       alert("An error occurred. Could not set hotel price threshold due to missing offer details.");
  }
  }
}
