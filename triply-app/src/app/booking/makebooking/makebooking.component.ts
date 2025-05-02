import { Component, OnInit } from '@angular/core';
import { BookingService } from '../booking.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { FlightOffer } from '../../flight-search-page/flight-search.model';
import { Router } from '@angular/router';
import {
  Booking,
  ConfirmedBooking,
  FlightAddon,
  FlightBooking,
  HotelAddon,
} from '../booking.model';
import { FlightAddonService } from '../flightaddon.service';
import { HotelOffer } from '../../hotel-search-page/hotel-search.model';
import { HotelAddonService } from '../hoteladdon.service';
import { AuthService } from '../../auth/auth.service';

@Component({
  selector: 'app-view-booking',
  templateUrl: './makebooking.component.html',
  styleUrls: ['./makebooking.component.css'],
  standalone: false,
})
export class MakeBookingComponent implements OnInit {
  selectedFlightOffer: FlightOffer | null = null;
  selectedHotelOffer: HotelOffer | null = null;
  availableFlightAddons: {
    flightAddonDTO: FlightAddon;
    price: number;
    quantity: number;
  }[] = [];
  availableHotelAddons: {
    hotelAddonDTO: HotelAddon;
    quantity: number;
  }[] = [];
  isConfirming: boolean = false;
  userId: number | null = null;

  constructor(
    private router: Router,
    private bookingService: BookingService,
    private flightAddonService: FlightAddonService,
    private hotelAddonService: HotelAddonService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const navigation = this.router.lastSuccessfulNavigation;
    console.log(navigation);
    if (navigation?.extras?.state?.['selectedFlightOffer']) {
      this.selectedFlightOffer = navigation.extras.state['selectedFlightOffer'];
      this.fetchFlightAddons(this.selectedFlightOffer!.flightId);
    }

    if (navigation?.extras?.state?.['selectedHotelOffer']) {
      this.selectedHotelOffer = navigation.extras.state['selectedHotelOffer'];
      this.fetchHotelAddons(this.selectedHotelOffer!.hotelId);
    }

    this.authService.getUserId().subscribe((userId: number) => {
      this.userId = userId;
    });
  }

  fetchFlightAddons(flightId: number) {
    this.flightAddonService
      .searchAddonsByFlightId(flightId)
      .subscribe((addons) => {
        this.availableFlightAddons = addons.map((addon: any) => ({
          ...addon,
          quantity: 0,
        }));

        console.log(this.availableFlightAddons);
      });
  }

  fetchHotelAddons(flightId: number) {
    this.hotelAddonService
      .searchAddonsByHotelId(flightId)
      .subscribe((addons) => {
        this.availableHotelAddons = addons.map((addon: any) => ({
          hotelAddonDTO: addon,
          quantity: 0,
        }));

        console.log(this.availableHotelAddons);
      });
  }

  confirmBooking() {
    if (!this.selectedFlightOffer && !this.selectedHotelOffer) return;

    const selectedFlightAddons = this.availableFlightAddons
      .filter((addon) => addon.quantity > 0)
      .map((addon) => ({
        flightAddonId: addon.flightAddonDTO.id!,
        quantity: addon.quantity,
      }));

    const selectedHotelAddons = this.availableHotelAddons
      .filter((addon) => addon.quantity > 0)
      .map((addon) => ({
        hotelAddonId: addon.hotelAddonDTO.id!,
        quantity: addon.quantity,
      }));

    let bookingPayload: Booking = {
      userId: this.userId!, // Replace with actual userId if available
    };

    if (this.selectedFlightOffer) {
      bookingPayload.flightBooking = {
        flightId: this.selectedFlightOffer.flightId,
        flightClassId: this.selectedFlightOffer.flightClassId,
        userId: this.userId!,
        departureDate: this.selectedFlightOffer.departureDate,
      };

      bookingPayload.flightBookingAddon = selectedFlightAddons;
    }

    if (this.selectedHotelOffer) {
      bookingPayload.hotelBooking = {
        hotelId: this.selectedHotelOffer.hotelId,
        hotelRoomTypeId: this.selectedHotelOffer.hotelRoomTypeId,
        userId: this.userId!,
        checkIn: this.selectedHotelOffer.checkInDate + 'T00:00:00',
        checkOut: this.selectedHotelOffer.checkOutDate + 'T00:00:00',
      };

      bookingPayload.hotelBookingAddon = selectedHotelAddons;
    }

    console.log('Booking Payload:', bookingPayload);

    this.isConfirming = true;
    this.bookingService.createBooking(bookingPayload).subscribe({
      next: (response) => {
        console.log('Booking successful: ', response);
        this.isConfirming = false;
        console.log(this.selectedFlightOffer);
        const confirmedBooking: ConfirmedBooking = {
          bookingFlightInfo: this.selectedFlightOffer!,
          bookingHotelInfo: this.selectedHotelOffer!,
          bookingPayload: bookingPayload,
          bookingResponse: response,
        };
        this.router.navigate(['/confirmedbooking'], {
          state: { booking: confirmedBooking },
        });
      },
      error: (error) => {
        console.error('Booking failed: ', error);
        this.isConfirming = false;
      },
    });
  }
}

// import { Component, OnInit } from '@angular/core';
// import { BookingService } from '../booking.service';
// import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
// import { FlightOffer } from '../../flight-search-page/flight-search.model';
// import { Router } from '@angular/router';

// @Component({
//   selector: 'app-view-booking',
//   templateUrl: './makebooking.component.html',
//   styleUrls: ['./makebooking.component.css'],
//   standalone: false,
// })
// export class MakeBookingComponent implements OnInit {
//   selectedFlightOffer: FlightOffer | null = null;

//   constructor(private router: Router) {}

//   ngOnInit(): void {
//     const navigation = this.router.lastSuccessfulNavigation;
//     console.log(navigation)
//     if (navigation?.extras?.state) {
//       this.selectedFlightOffer = navigation.extras.state['selectedFlightOffer'];
//       console.log(this.selectedFlightOffer)
//     }
//   }
// }
