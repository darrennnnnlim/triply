import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FlightOfferDataService } from './flight-offer-data.service';
import { RatingService } from './rating.service';

@Component({
  selector: 'app-flight-offer',
  standalone: false,
  templateUrl: './flight-offer.component.html',
  styleUrl: './flight-offer.component.css'
})
export class FlightOfferComponent {
  flightOffer: any;
  flightId: any = 1;
  ratings: any[] = [];
  averageRating: number = 0;

  constructor(
    private route: ActivatedRoute,
    private flightOfferDataService: FlightOfferDataService, // Assumed service to fetch flight data
    private ratingService: RatingService
  ) {}

  ngOnInit(): void {
    // Get the flight offer data from the shared service
    this.flightOffer = this.flightOfferDataService.getFlightOfferData();
    this.loadRatings();
  }
  loadRatings(): void {
    this.ratingService.getRatings(this.flightId).subscribe((data: any[]) => {
     
      this.ratings = data.filter(rating => rating.delete === 'F');

      const total = this.ratings.reduce((sum, rating) => sum + rating.rating, 0);
      this.averageRating = this.ratings.length > 0 ? Math.round(total / this.ratings.length) : 0;
      console.log(this.averageRating);
      console.log(this.ratings);
    });
  }
}
