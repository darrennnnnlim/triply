import { Component } from '@angular/core';
import { RatingComponent } from '../rating/rating.component';

@Component({
  selector: 'flight',
  imports: [RatingComponent],
  templateUrl: './flight.component.html',
  styleUrl: './flight.component.css'
})
export class FlightComponent {

  rating: number = 0;

  onRatingChange(newRating: number): void {
    this.rating = newRating;
    console.log(this.rating);
    // call the api
  }

}
