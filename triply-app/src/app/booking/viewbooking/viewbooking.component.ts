import { Component, OnInit } from '@angular/core';
import { BookingService } from '../booking.service';
import { Booking } from '../booking.model';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-view-booking',
  templateUrl: './viewbooking.component.html',
  styleUrls: ['./viewbooking.component.css'],
  standalone: false,
})
export class ViewBookingComponent implements OnInit {
  bookings: any[] = []; // Explicitly define the type
  filteredBookings: any[] = [];
  activeTabIndex = 0; // Stores which tab is currently active
  selectedBookingId!: number;

  constructor(private bookingService: BookingService, private modalService: NgbModal) {}

  ngOnInit(): void {
    this.fetchBookings();
  }

  fetchBookings(): void {
    this.bookingService.getBookings().subscribe({
      next: (data: Booking[]) => {
        this.bookings = data || []; // Ensure data is always an array
        this.filterBookings(0); // Load upcoming bookings by default
      },
      error: (err) => console.error('Error fetching bookings:', err),
    });
  }

  //   filterBookings(tabIndex: number): void {
  //     this.activeTabIndex = tabIndex; // Track selected tab
  //     const now = new Date();

  //     this.filteredBookings = this.bookings.filter((booking) => {
  //       const departureDate = booking.flightBooking?.flight?.departureTime
  //         ? new Date(booking.flightBooking.flight.departureTime)
  //         : null;
  //       const arrivalDate = booking.flightBooking?.flight?.arrivalTime
  //         ? new Date(booking.flightBooking.flight.arrivalTime)
  //         : null;
  //       const checkInDate = booking.hotelBooking?.checkIn
  //         ? new Date(booking.hotelBooking.checkIn)
  //         : null;
  //       const checkOutDate = booking.hotelBooking?.checkOut
  //         ? new Date(booking.hotelBooking.checkOut)
  //         : null;

  //       if (tabIndex === 0) {
  //         // Upcoming Bookings: Any date in the future
  //         return (
  //           (departureDate && departureDate >= now) ||
  //           (arrivalDate && arrivalDate >= now) ||
  //           (checkInDate && checkInDate >= now) ||
  //           (checkOutDate && checkOutDate >= now)
  //         );
  //       } else {
  //         // Past Bookings: All dates must be in the past
  //         return (
  //           (!departureDate || departureDate < now) &&
  //           (!arrivalDate || arrivalDate < now) &&
  //           (!checkInDate || checkInDate < now) &&
  //           (!checkOutDate || checkOutDate < now)
  //         );
  //       }
  //     });
  //   }

  filterBookings(tabIndex: number) {
    const now = new Date();

    if (tabIndex === 0) {
      // ✅ Upcoming Bookings (Not cancelled & at least one future date)
      this.filteredBookings = this.bookings.filter((booking) => {
        if (booking.status === 'CANCELLED') return false; // Skip cancelled bookings

        const departureDate = booking.flightBooking?.flight?.departureTime
          ? new Date(booking.flightBooking.flight.departureTime)
          : null;
        const arrivalDate = booking.flightBooking?.flight?.arrivalTime
          ? new Date(booking.flightBooking.flight.arrivalTime)
          : null;
        const checkInDate = booking.hotelBooking?.checkIn
          ? new Date(booking.hotelBooking.checkIn)
          : null;
        const checkOutDate = booking.hotelBooking?.checkOut
          ? new Date(booking.hotelBooking.checkOut)
          : null;

        return (
          (departureDate && departureDate >= now) ||
          (arrivalDate && arrivalDate >= now) ||
          (checkInDate && checkInDate >= now) ||
          (checkOutDate && checkOutDate >= now)
        );
      });
    } else if (tabIndex === 1) {
      // ✅ Past Bookings (Not cancelled & all dates are in the past)
      this.filteredBookings = this.bookings.filter((booking) => {
        if (booking.status === 'CANCELLED') return false; // Skip cancelled bookings

        const departureDate = booking.flightBooking?.flight?.departureTime
          ? new Date(booking.flightBooking.flight.departureTime)
          : null;
        const arrivalDate = booking.flightBooking?.flight?.arrivalTime
          ? new Date(booking.flightBooking.flight.arrivalTime)
          : null;
        const checkInDate = booking.hotelBooking?.checkIn
          ? new Date(booking.hotelBooking.checkIn)
          : null;
        const checkOutDate = booking.hotelBooking?.checkOut
          ? new Date(booking.hotelBooking.checkOut)
          : null;

        return (
          (!departureDate || departureDate < now) &&
          (!arrivalDate || arrivalDate < now) &&
          (!checkInDate || checkInDate < now) &&
          (!checkOutDate || checkOutDate < now)
        );
      });
    } else {
      // ✅ Cancelled Bookings (Status = "CANCELLED")
      this.filteredBookings = this.bookings.filter(
        (booking) => booking.status === 'CANCELLED'
      );
    }
  }

  isUpcomingBooking(bookingTime: string): boolean {
    return new Date(bookingTime) >= new Date();
  }

  openCancelModal(content: any, bookingId: number): void {
    this.selectedBookingId = bookingId;
    this.modalService.open(content, { centered: true });
  }

  confirmCancel(): void {
    if (!this.selectedBookingId) return;

    this.bookingService.cancelBooking(this.selectedBookingId).subscribe({
      next: () => {
        alert('Booking cancelled successfully!');
        this.fetchBookings(); // Refresh bookings
        this.modalService.dismissAll(); // Close the modal
      },
      error: (err) => console.error('Cancellation failed:', err),
    });
  }
}
