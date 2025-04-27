import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FlightOfferDataService } from './flight-offer-data.service';
import { FlightOffer } from '../flight-search-page/flight-search.model';
import { RatingService } from './rating.service';
import { BanedUserRating } from '../banned-ratings/banned-ratings.service';
@Component({
  selector: 'app-flight-offer',
  standalone: false,
  templateUrl: './flight-offer.component.html',
  styleUrl: './flight-offer.component.css'
})
export class FlightOfferComponent {
  selectedFlightOffer: FlightOffer | null = null;
  flightId: any = 1;
  ratings: any[] = [];
  averageRating: number = 0;

  constructor(
    private route: ActivatedRoute,
    private flightOfferDataService: FlightOfferDataService, // Assumed service to fetch flight data
    private router: Router,
    private ratingService: RatingService,
    private bannedUserRatingService: BanedUserRating
  ) {}

  ngOnInit(): void {
    // Get the flight offer data from the shared service
    // this.flightOffer = this.flightOfferDataService.getFlightOfferData();
    this.loadRatings();
    const navigation = this.router.lastSuccessfulNavigation;
    if (navigation?.extras?.state) {
      this.selectedFlightOffer = navigation.extras.state['selectedFlightOffer'];
      console.log(navigation.extras.state['selectedFlightOffer'])
    }
  }

  makeBooking(): void {
    console.log(this.selectedFlightOffer)
    this.router.navigate(['/makebooking'], {
      state: { selectedFlightOffer: this.selectedFlightOffer }
    });
    
  }
  loadRatings(): void {
    this.ratingService.getRatings(this.flightId).subscribe((data: any[]) => {
      this.ratings = data.filter(rating => rating.delete === 'F');
  

      const userIds = [...new Set(this.ratings.map(rating => rating.userId))];
  

      console.log(userIds)
      const userDetailsPromises = userIds.map((userId: number) =>
        this.bannedUserRatingService.getUser(userId).toPromise() 
      );
      
  
      Promise.all(userDetailsPromises)
        .then(userDetailsResponses => {
          const userDetailsMap = new Map<number, any>();
          userDetailsResponses.forEach((user: any) => {
            console.log('Data:', user);
            const newdata = JSON.parse(user);
            console.log(user)
            userDetailsMap.set(newdata.id, newdata.username); 
          });
          console.log(userDetailsMap)
  
          this.ratings = this.ratings.map(rating => {
            const user = userDetailsMap.get(rating.userId);
            console.log('Data:', user);
            return {
              ...rating,
              user, 
            };
          });
  
          const total = this.ratings.reduce((sum, rating) => sum + rating.rating, 0);
          this.averageRating = this.ratings.length > 0 ? Math.round(total / this.ratings.length) : 0;
  

          console.log(this.averageRating);
          console.log(this.ratings);
        })
        .catch(err => {
          console.error("Error fetching user details:", err);
        });
    });
  }
  
}
