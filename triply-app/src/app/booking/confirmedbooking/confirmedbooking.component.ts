import { Component, OnInit } from '@angular/core';
import { BookingService } from '../booking.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { FlightOffer } from '../../flight-search-page/flight-search.model';
import { ActivatedRoute, Router } from '@angular/router';
import {
  Booking,
  ConfirmedBooking,
  FlightAddon,
  FlightBooking,
} from '../booking.model';
import { FlightAddonService } from '../flightaddon.service';

@Component({
  selector: 'app-view-confirmedbooking',
  templateUrl: './confirmedbooking.component.html',
  styleUrls: ['./confirmedbooking.component.css'],
  standalone: false,
})
export class ConfirmedBookingComponent implements OnInit {
  booking: ConfirmedBooking | null = null;

  constructor(private route: ActivatedRoute, private router: Router) {}

  ngOnInit(): void {
    const navigation = this.router.lastSuccessfulNavigation;
    console.log(navigation);
    if (navigation?.extras?.state?.['booking']) {
      this.booking = navigation.extras.state['booking'];
    } else {
      // Fallback: redirect or load from backend
      this.router.navigate(['/']);
    }
  }
}
