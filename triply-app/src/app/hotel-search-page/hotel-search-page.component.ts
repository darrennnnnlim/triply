import { Component } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';
import { HotelSearchPageService } from './hotel-search-page.service';
import { Router } from '@angular/router';
import { HotelOffer, HotelSearchDTO } from './hotel-search.model';

@Component({
  selector: 'app-hotel-search-page',
  standalone: false,
  templateUrl: './hotel-search-page.component.html',
  styleUrl: './hotel-search-page.component.css'
})
export class HotelSearchPageComponent {
//   searchQuery: string = '';
  searchForm: FormGroup;

  /* BEGIN: Mock data */
  // hotelOffers: HotelOffer[] = [
  //   { 
  //     hotel: {
  //       location: 'Singapore',
  //       name: 'Hilton'
  //     },
  //     checkInDate: '2025-08-01', 
  //     checkOutDate: '2025-08-03', 
  //     pricing: {
  //       totalPrice: 500
  //     }
  //   },
  //   { 
  //     hotel: {
  //       location: 'Singapore',
  //       name: 'Mariott'
  //     },
  //     checkInDate: '2025-08-01', 
  //     checkOutDate: '2025-08-03',  
  //     pricing: {
  //       totalPrice: 100
  //     }
  //   },
  //   { 
  //     hotel: {
  //       location: 'Singapore',
  //       name: 'MBS'
  //     },
  //     checkInDate: '2025-08-01', 
  //     checkOutDate: '2025-08-03',  
  //     pricing: {
  //       totalPrice: 1000
  //     }
  //   }
  // ];
  /* END: Mock data */
  filteredHotelOffers: HotelOffer[] = [];

  constructor(
    private fb: FormBuilder, 
    private hotelSearchPageService: HotelSearchPageService,
    private router: Router
  ) {
    this.searchForm = this.fb.group({
      location: [''],
      checkInDate: [''],
      checkOutDate: [''],
      maxPrice: ['']
    });
  }

  onSearch() {
    const searchRequest: HotelSearchDTO = {
      location: this.searchForm.value.location,
      checkInDate: this.searchForm.value.checkInDate,
      checkOutDate: this.searchForm.value.checkOutDate,
      maxPrice: this.searchForm.value.maxPrice
    };
    console.log(searchRequest)
    this.hotelSearchPageService.searchHotels(searchRequest).subscribe(response => {
      console.log('Search Results:', response);
      this.filteredHotelOffers = response;
    })
  }

  ngOnInit(): void {
    // Dynamically setting the offerUrl after initialization
    // this.hotelOffers.forEach(f => {
    //   f.offerUrl = 'https://www.google.com';  // Set a placeholder URL for each hotel offer
    // });
  }

  onHotelOfferClick(hotelOffer: any): void {
    // Save the selected hotel offer in the service
    // this.hotelOfferDataService.setHotelOfferData(hotelOffer);

    // Navigate to the hotel offer details page
    // this.router.navigate(['/hotel-offer']);
    console.log(hotelOffer)
    this.router.navigate(['/hotel-offer'], {
      state: { selectedHotelOffer : hotelOffer }
    });
  }
}



