import { Component, OnInit } from '@angular/core';
import { BookingService } from '../booking.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { FlightOffer } from '../../flight-search-page/flight-search.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-view-booking',
  templateUrl: './makebooking.component.html',
  styleUrls: ['./makebooking.component.css'],
  standalone: false,
})
export class MakeBookingComponent implements OnInit {
  selectedFlightOffer: FlightOffer | null = null;

  constructor(private router: Router) {}

  ngOnInit(): void {
    const navigation = this.router.lastSuccessfulNavigation;
    console.log(navigation)
    if (navigation?.extras?.state) {
      this.selectedFlightOffer = navigation.extras.state['selectedFlightOffer'];
      console.log(this.selectedFlightOffer)
    }
  }
}
