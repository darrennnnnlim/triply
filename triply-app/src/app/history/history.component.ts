import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RatingComponent } from '../rating/rating.component';
import axios from 'axios';
import moment from 'moment-timezone';
import { HistoryService } from './history.service';
import { Rating } from './history.model';
import { AdminService } from '../admin/admin.service';
import { AuthService } from '../auth/auth.service';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-history',
  imports: [RatingComponent, CommonModule],
  templateUrl: './history.component.html',
  styleUrl: './history.component.css',
})
export class HistoryComponent implements OnInit {
  authState$!: Observable<{
    isLoggedIn: boolean;
    username?: string;
    role?: string;
  }>;
  items: any = [];
  currentUsername: string | null = null;
  expandedId: number | null = null;
  counter: number = 1;
  userId: number = 1;

  constructor(
    private historyService: HistoryService,
    private router: Router,
    private adminService: AdminService,
    public authService: AuthService
  ) {
    this.authState$ = this.authService.authState$ as Observable<{
      isLoggedIn: boolean;
      username?: string;
      role?: string;
    }>;
  }

  ngOnInit(): void {
    this.fetchUser().subscribe({
      next: () => {
        this.loadBookingData(this.userId);
      },
      error: (error) => {
        console.error('Failed to fetch user:', error);
      },
    });
  }

  fetchUser(): Observable<any> {
    return new Observable((observer) => {
      this.authService.initAuthStateFromBackend();

      this.authState$.subscribe({
        next: (authState) => {
          console.log('Auth state:', authState);
        },
        error: (err) => {
          observer.error('Error in auth state');
        },
      });

      this.adminService.getCurrentUser().subscribe({
        next: (data) => {
          console.log('Data:', data);
          const newdata = JSON.parse(data);
          this.userId = Number(newdata.userId);
          observer.next();
          observer.complete();
        },
        error: (err) => {
          observer.error('Error fetching current user');
          this.router.navigate(['/login']);
        },
      });
    });
  }

  loadBookingData(userId: number): void {
    console.log('loadBookingData: ' + userId);
    this.historyService
      .getFlightBookings(userId)
      .subscribe((flightBookings) => {
        this.historyService.getRatings(userId).subscribe((flightRatings) => {
          const flightRatingsMap = flightRatings.reduce(
            (acc: any, rating: any) => {
              acc[rating.flightId] = rating.rating;
              return acc;
            },
            {}
          );

          const flightDetailsPromises = flightBookings.map((booking: any) =>
            this.historyService.getFlightDetails(booking.flightId).toPromise()
          );

          Promise.all(flightDetailsPromises).then((flightDetailsResponses) => {
            const flightResults = flightBookings.map(
              (booking: any, index: number) => {
                const flightDetails = flightDetailsResponses[index];
                const rating = flightRatingsMap[booking.flightId] || 0;
                const rateType = rating ? 'exist' : 'new';

                return {
                  ...booking,
                  flightDetails,
                  rating,
                  rateType,
                  type: 'Flight',
                };
              }
            );
            console.log(flightResults);
            this.addItemsToList(flightResults);
          });
        });
      });

    this.historyService.getHotelBookings(userId).subscribe((hotelBookings) => {
      this.historyService.getRatings(userId).subscribe((hotelRatings) => {
        const hotelRatingsMap = hotelRatings.reduce((acc: any, rating: any) => {
          acc[rating.hotelId] = rating.rating;
          return acc;
        }, {});

        const hotelDetailsPromises = hotelBookings.map((booking: any) =>
          this.historyService.getHotelDetails(booking.hotelId).toPromise()
        );

        Promise.all(hotelDetailsPromises).then((hotelDetailsResponses) => {
          const hotelResults = hotelBookings.map(
            (booking: any, index: number) => {
              const hotelDetails = hotelDetailsResponses[index];
              const rating = hotelRatingsMap[booking.hotelId] || 0;
              const rateType = rating ? 'exist' : 'new';

              return {
                ...booking,
                hotelDetails,
                rating,
                rateType,
                type: 'Hotel',
              };
            }
          );
          console.log(hotelResults);
          this.addItemsToList(hotelResults);
        });
      });
    });
  }

  // Method to add results to the list
  addItemsToList(results: any[]): void {
    results.forEach((result: any) => {
      if (result.type === 'Flight') {
        this.items.push({
          name: result.flightDetails.airline,
          arrivalTime: convertDateTime(result.flightDetails.arrivalTime),
          departureTime: convertDateTime(result.flightDetails.departureTime),
          flightNumber: result.flightDetails.flightNumber,
          seatClass: result.seatClass,
          seatNumber: result.seatNumber,
          flightId: result.flightDetails.id,
          id: this.counter++,
          rating: result.rating || 0,
          rateType: result.rateType,
          type: result.type,
        });
      } else if (result.type === 'Hotel') {
        this.items.push({
          name: result.hotelDetails.name,
          checkInDate: convertDateTime(result.hotelDetails.checkInDate),
          checkOutDate: convertDateTime(result.hotelDetails.checkOutDate),
          hotelId: result.hotelDetails.id,
          id: this.counter++,
          rating: result.rating || 0,
          rateType: result.rateType,
          type: result.type,
          description: result.hotelDetails.description,
        });
      }
    });

    console.log(this.items);
  }
  isAfterDateTime(time: string): boolean {
    const currentTime = new Date();

    const [day, month, year, hour, minute] = time.split(/[ /:]/);
    const date = new Date(
      Number(year),
      Number(month) - 1,
      Number(day),
      Number(hour),
      Number(minute)
    );

    return currentTime > date;
  }

  toggleExpand(id: number) {
    this.expandedId = this.expandedId === id ? null : id;
  }

  onRatingChange(newRating: number, id: number, type: String): void {
    console.log(id);
    const item = this.items.find((item: { id: number }) => item.id === id);
    if (item) {
      item.rating = newRating;
      console.log('Rating: ' + item.rating);
      console.log('ID: ' + id);

      let ratingData: Rating = {
        userId: 0,
        flightId: null,
        hotelId: null,
        rating: 0,
        type: type,
      };
      if (type == 'Flight') {
        ratingData = {
          userId: this.userId,
          flightId: id,
          rating: newRating,
          hotelId: null,
          type: type,
        };
      } else {
        ratingData = {
          userId: this.userId,
          flightId: null,
          rating: newRating,
          hotelId: id,
          type: type,
        };
      }

      console.log(ratingData);
      this.saveRating(ratingData);
    }
  }
  saveRating(ratingData: Rating): void {
    this.historyService.postRating(ratingData).subscribe({
      next: (res: any) => {
        console.log(res);
      },
      error: (err) => {
        console.error(`Error: ${err.status} - ${err.statusText}`);
      },
    });
  }
}

function convertDateTime(timestamp: string): string {
  return (
    moment(timestamp).tz('Asia/Singapore').format('DD/MM/yyyy') +
    ' ' +
    moment(timestamp).tz('Asia/Singapore').format('HH:mm')
  );
}
