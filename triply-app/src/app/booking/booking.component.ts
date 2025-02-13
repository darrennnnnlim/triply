import { Component } from '@angular/core';
import { BookingService } from './booking.service';

@Component({
  selector: 'app-booking',
  templateUrl: './booking.component.html',
  styleUrls: ['./booking.component.css'],
  standalone: false,
})
export class BookingComponent {
  response: string | null = null;
  errorMessage: string | null = null;

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
}
