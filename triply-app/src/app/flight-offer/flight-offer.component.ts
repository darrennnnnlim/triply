import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FlightOfferDataService } from './flight-offer-data.service';

@Component({
  selector: 'app-flight-offer',
  standalone: false,
  templateUrl: './flight-offer.component.html',
  styleUrl: './flight-offer.component.css'
})
export class FlightOfferComponent {
  flightOffer: any;

  constructor(
    private route: ActivatedRoute,
    private flightOfferDataService: FlightOfferDataService // Assumed service to fetch flight data
  ) {}

  ngOnInit(): void {
    // Get the flight offer data from the shared service
    this.flightOffer = this.flightOfferDataService.getFlightOfferData();
  }
}
