import { Component } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';
import { FlightSearchPageService } from './flight-search-page.service';

interface SearchDTO {
    origin: string;
    destination: string;
    departureDate: string;
    arrivalDate: string;
    maxPrice: number;
}

interface FlightOffer {
  origin: string;
  destination: string;
  departureDate: string;
  arrivalDate: string;
  price: number;
  offerUrl: string;
}

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
  flightOffers: FlightOffer[] = [
    { origin: 'New York', destination: 'London', departureDate: '2025-03-10', arrivalDate: '2025-03-11', price: 500, offerUrl: '' },
    { origin: 'Los Angeles', destination: 'Tokyo', departureDate: '2025-04-01', arrivalDate: '2025-04-02', price: 800, offerUrl: '' },
    { origin: 'San Francisco', destination: 'Paris', departureDate: '2025-05-15', arrivalDate: '2025-05-16', price: 600, offerUrl: '' },
    { origin: 'Chicago', destination: 'Dubai', departureDate: '2025-06-20', arrivalDate: '2025-06-21', price: 900, offerUrl: '' },
  ];
  /* END: Mock data */
  filteredFlightOffers: FlightOffer[] = this.flightOffers;

  constructor(private fb: FormBuilder, private flightSearchPageService: FlightSearchPageService   ) {
    this.searchForm = this.fb.group({
      origin: [''],
      destination: [''],
      departureDate: [''],
      arrivalDate: [''],
      maxPrice: ['']
    });
  }

//   onSearch() {
//     const query = this.searchQuery.toLowerCase();
//     this.filteredFlightOffers = this.flightOffers.filter(flightOffers =>
//       flightOffers.origin.toLowerCase().includes(query) ||
//       flightOffers.destination.toLowerCase().includes(query) ||
//       flightOffers.departureDate.includes(query) ||
//       flightOffers.arrivalDate.includes(query)
//     );
//   }
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
    })
  }

  ngOnInit(): void {
    // Dynamically setting the offerUrl after initialization
    this.flightOffers.forEach(f => {
      f.offerUrl = 'https://www.google.com';  // Set a placeholder URL for each flight offer
    });
  }
}



