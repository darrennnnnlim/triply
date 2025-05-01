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
    
    const navigation = this.router.lastSuccessfulNavigation;
    if (navigation?.extras?.state) {
      this.selectedFlightOffer = navigation.extras.state['selectedFlightOffer'];
      console.log(navigation.extras.state['selectedFlightOffer'])
      this.loadRatings();
    }
  }

  makeBooking(): void {
    console.log(this.selectedFlightOffer)
    this.router.navigate(['/makebooking'], {
      state: { selectedFlightOffer: this.selectedFlightOffer }
    });
    
  }
  loadRatings(): void {
    console.log(this.selectedFlightOffer)
    if (this.selectedFlightOffer?.airlineId) {
        const airlineId: number = this.selectedFlightOffer.airlineId;
      
      this.ratingService.getRatings(airlineId).subscribe((data: any[]) => {
        this.ratings = data.filter(rating => rating.delete === 'F');
        const userIds = [...new Set(this.ratings.map(rating => rating.userId))];
        const userDetailsPromises = userIds.map((userId: number) =>
          this.bannedUserRatingService.getUser(userId).toPromise() 
        );        
    
        Promise.all(userDetailsPromises)
          .then(userDetailsResponses => {
            const userDetailsMap = new Map<number, any>();
            userDetailsResponses.forEach((user: any) => {
              const newdata = JSON.parse(user);
              userDetailsMap.set(newdata.id, newdata.username); 
            });
    
            this.ratings = this.ratings.map(rating => {
              const user = userDetailsMap.get(rating.userId);
              return {
                ...rating,
                user, 
              };
            });
    
            const total = this.ratings.reduce((sum, rating) => sum + rating.rating, 0);
            this.averageRating = this.ratings.length > 0 ? Math.round(total / this.ratings.length) : 0;
          })
          .catch(err => {
            console.error("Error fetching user details:", err);
          });
      });
    }
  }
  
}
