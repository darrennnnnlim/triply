import { Component } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';
import { FlightSearchPageService } from './flight-search-page.service';
import { Router } from '@angular/router';
import { FlightOfferDataService } from '../flight-offer/flight-offer-data.service';
import { FlightOffer, SearchDTO } from './flight-search.model';

@Component({
  selector: 'app-flight-search-page',
  standalone: false,
  templateUrl: './flight-search-page.component.html',
  styleUrl: './flight-search-page.component.css'
})
export class FlightSearchPageComponent {
//   searchQuery: string = '';
  searchForm: FormGroup;

  /* BEGIN: Mock data */
  // flightOffers: FlightOffer[] = [
  //   { origin: 'New York', destination: 'London', departureDate: '2025-03-10', arrivalDate: '2025-03-11', price: 500, offerUrl: '', carrierCode: '6X' },
  //   { origin: 'Los Angeles', destination: 'Tokyo', departureDate: '2025-04-01', arrivalDate: '2025-04-02', price: 800, offerUrl: '', carrierCode: '6X' },
  //   { origin: 'San Francisco', destination: 'Paris', departureDate: '2025-05-15', arrivalDate: '2025-05-16', price: 600, offerUrl: '', carrierCode: '6X' },
  //   { origin: 'Chicago', destination: 'Dubai', departureDate: '2025-06-20', arrivalDate: '2025-06-21', price: 900, offerUrl: '', carrierCode: '6X' },
  // ];
  /* END: Mock data */
  filteredFlightOffers: FlightOffer[] = [];

  constructor(
    private fb: FormBuilder, 
    private flightSearchPageService: FlightSearchPageService,
    private router: Router,
    private flightOfferDataService: FlightOfferDataService
  ) {
    this.searchForm = this.fb.group({
      origin: [''],
      destination: [''],
      departureDate: [''],
      arrivalDate: [''],
      maxPrice: ['']
    });
  }

  onSearch() {
    const searchRequest: SearchDTO = {
      origin: this.searchForm.value.origin,
      destination: this.searchForm.value.destination,
      departureDate: this.searchForm.value.departureDate,
      arrivalDate: this.searchForm.value.arrivalDate,
      maxPrice: this.searchForm.value.maxPrice ? this.searchForm.value.maxPrice : undefined
    };
    console.log(searchRequest)
    this.flightSearchPageService.searchFlights(searchRequest).subscribe(response => {
      console.log('Search Results:', response);
      this.filteredFlightOffers = response;
    })
  }

  ngOnInit(): void {
    // Dynamically setting the offerUrl after initialization
    // this.flightOffers.forEach(f => {
    //   f.offerUrl = 'https://www.google.com';  // Set a placeholder URL for each flight offer
    // });
  }

  onFlightOfferClick(flightOffer: any): void {
    // Save the selected flight offer in the service
    // this.flightOfferDataService.setFlightOfferData(flightOffer);

    // Navigate to the flight offer details page
    // this.router.navigate(['/flight-offer']);
    console.log(flightOffer)
    this.router.navigate(['/flight-offer'], {
      state: { selectedFlightOffer : flightOffer }
    });
  }
}



