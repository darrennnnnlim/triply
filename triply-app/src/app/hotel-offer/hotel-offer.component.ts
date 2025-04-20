import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HotelOfferDataService } from './hotel-offer-data.service';
import { HotelOffer } from '../hotel-search-page/hotel-search.model';

@Component({
  selector: 'app-hotel-offer',
  standalone: false,
  templateUrl: './hotel-offer.component.html',
  styleUrl: './hotel-offer.component.css'
})
export class HotelOfferComponent {
  selectedHotelOffer: HotelOffer | null = null;

  constructor(
    private route: ActivatedRoute,
    private hotelOfferDataService: HotelOfferDataService, // assumed service to fetch hotel data
    private router: Router
  ) {}

  ngOnInit(): void {
    // Get the hotel offer data from the shared service
    // this.hotelOffer = this.hotelOfferDataService.getHotelOfferData();
    const navigation = this.router.lastSuccessfulNavigation;
    if (navigation?.extras?.state) {
      this.selectedHotelOffer = navigation.extras.state['selectedHotelOffer'];
      console.log(navigation.extras.state['selectedHotelOffer'])
    }
  }

  makeBooking(): void {
    console.log(this.selectedHotelOffer)
    this.router.navigate(['/makebooking'], {
      state: { selectedHotelOffer: this.selectedHotelOffer }
    });
  }
}
