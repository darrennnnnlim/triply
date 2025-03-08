import { Component } from '@angular/core';
import { BookingService } from './booking.service';
import { Booking } from './booking.model';

@Component({
  selector: 'app-booking',
  templateUrl: './booking.component.html',
  styleUrls: ['./booking.component.css'],
  standalone: false,
})
export class BookingComponent {
  response: string | null = null;
  errorMessage: string | null = null;
  isConfirming: boolean = false;

  booking: Booking = {
    userId: 1,
    flightBooking: {
      flightId: 1,
      flightClassId: 1,
      userId: 1,
      departureDate: '2025-04-05T08:30:00',
    },
    hotelBooking: {
      hotelId: 1,
      hotelRoomTypeId: 1,
      userId: 1,
      checkIn: '2025-06-01T14:00:00',
      checkOut: '2025-06-08T12:00:00',
    },
    flightBookingAddon: [
      { flightAddonId: 1, quantity: 1 },
      { flightAddonId: 2, quantity: 1 },
    ],
    hotelBookingAddon: [
      { hotelAddonId: 1, quantity: 1 },
      { hotelAddonId: 2, quantity: 1 },
    ],
  };

  constructor(private bookingService: BookingService) {}

  testApi(): void {
    this.bookingService.test().subscribe({
      next: (res: any) => {
        this.response = res;
        this.errorMessage = null;
      },
      error: (err) => {
        this.errorMessage = `Error: ${err.status} - ${err.statusText}`;
        console.error(err);
      },
    });
  }

  postTestApi(): void {
    this.bookingService.postTest().subscribe({
      next: (res: any) => {
        this.response = res;
        this.errorMessage = null;
      },
      error: (err) => {
        this.errorMessage = `Error: ${err.status} - ${err.statusText}`;
        console.error(err);
      },
    });
  }

  confirmBooking() {
    // this.isConfirming = true;
    // this.http.post(this.apiUrl, this.booking).subscribe({
    //   next: (response) => {
    //     alert('Booking confirmed successfully!');
    //     console.log(response);
    //     this.isConfirming = false;
    //   },
    //   error: (error) => {
    //     alert('Error confirming booking');
    //     console.error(error);
    //     this.isConfirming = false;
    //   }
    // });

    this.isConfirming = true;
    this.bookingService.createBooking(this.booking).subscribe({
      next: (response) => {
        console.log('Booking successful: ', response);
        this.isConfirming = false;
      },
      error: (error) => {
        console.error('Booking failed: ', error);
        this.isConfirming = false;
      },
    });
  }
}
