import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';



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
//   imports: [CommonModule, FormsModule],
  standalone: false,
  templateUrl: './flight-search-page.component.html',
  styleUrl: './flight-search-page.component.css'
})
export class FlightSearchPageComponent {
  searchQuery: string = '';
  /* BEGIN: Mock data */
  flightOffers: FlightOffer[] = [
    { origin: 'New York', destination: 'London', departureDate: '2025-03-10', arrivalDate: '2025-03-11', price: 500, offerUrl: '' },
    { origin: 'Los Angeles', destination: 'Tokyo', departureDate: '2025-04-01', arrivalDate: '2025-04-02', price: 800, offerUrl: '' },
    { origin: 'San Francisco', destination: 'Paris', departureDate: '2025-05-15', arrivalDate: '2025-05-16', price: 600, offerUrl: '' },
    { origin: 'Chicago', destination: 'Dubai', departureDate: '2025-06-20', arrivalDate: '2025-06-21', price: 900, offerUrl: '' },
  ];
  /* END: Mock data */
  filteredFlightOffers: FlightOffer[] = this.flightOffers;

  onSearch() {
    const query = this.searchQuery.toLowerCase();
    this.filteredFlightOffers = this.flightOffers.filter(flightOffers =>
      flightOffers.origin.toLowerCase().includes(query) ||
      flightOffers.destination.toLowerCase().includes(query) ||
      flightOffers.departureDate.includes(query) ||
      flightOffers.arrivalDate.includes(query)
    );
  }

  ngOnInit(): void {
    // Dynamically setting the offerUrl after initialization
    this.flightOffers.forEach(f => {
      f.offerUrl = 'https://www.google.com';  // Set a placeholder URL for each flight offer
    });
  }
}



