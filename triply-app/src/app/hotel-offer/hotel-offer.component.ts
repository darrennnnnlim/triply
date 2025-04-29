import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HotelOfferDataService } from './hotel-offer-data.service';
import { HotelOffer } from '../hotel-search-page/hotel-search.model';
import { RatingService } from './rating.service';
import { BanedUserRating } from '../banned-ratings/banned-ratings.service';

@Component({
  selector: 'app-hotel-offer',
  standalone: false,
  templateUrl: './hotel-offer.component.html',
  styleUrl: './hotel-offer.component.css'
})
export class HotelOfferComponent {
  selectedHotelOffer: HotelOffer | null = null;
  hotelId: any = 1;
  ratings: any[] = [];
  averageRating: number = 0;

  constructor(
    private route: ActivatedRoute,
    private hotelOfferDataService: HotelOfferDataService, // assumed service to fetch hotel data
    private router: Router,
    private ratingService: RatingService,
    private bannedUserRatingService: BanedUserRating
  ) {}

  ngOnInit(): void {
    // Get the hotel offer data from the shared service
    // this.hotelOffer = this.hotelOfferDataService.getHotelOfferData();
    this.loadRatings();
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

  loadRatings(): void {
    this.ratingService.getRatings(this.hotelId).subscribe((data: any[]) => {
      this.ratings = data.filter(rating => rating.delete === 'F');
  
      const userIds = [...new Set(this.ratings.map(rating => rating.userId))];
  
      console.log(userIds);
  

      const userDetailsPromises = userIds.map((userId: number) =>
        this.bannedUserRatingService.getUser(userId).toPromise()
      );
  
      Promise.all(userDetailsPromises)
        .then(userDetailsResponses => {
          const userDetailsMap = new Map<number, string>();
          userDetailsResponses.forEach((user: any) => {
            console.log('Data:', user);
            const newdata = JSON.parse(user);
            console.log(newdata);
            userDetailsMap.set(newdata.id, newdata.username);
          });
  
          console.log(userDetailsMap);
  
          this.ratings = this.ratings.map(rating => {
            const user = userDetailsMap.get(rating.userId); 
            console.log('User:', user);
            return {
              ...rating,
              user, 
            };
          });
  
          const total = this.ratings.reduce((sum, rating) => sum + rating.rating, 0);
          this.averageRating = this.ratings.length > 0 ? Math.round(total / this.ratings.length) : 0;
  
          console.log('Average Rating:', this.averageRating);
          console.log('Ratings:', this.ratings);
        })
        .catch(err => {
          console.error("Error fetching user details:", err);
        });
    });
  }
  
}
