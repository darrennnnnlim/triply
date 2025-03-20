import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common'; 

@Component({
  selector: 'rating',
  templateUrl: './rating.component.html',
  styleUrls: ['./rating.component.css'],
  imports: [CommonModule]
})
export class RatingComponent { 
  @Input() currentRating: number = 0; 
  @Input() totalStars: number = 5;   
  @Output() ratingChange = new EventEmitter<number>();  

  rate: number = 0; 
  stars: number[] = [];   

  ngOnInit() {
    this.stars = new Array(this.totalStars).fill(0);
  }

  setRating(rating: number): void {
    this.currentRating = rating;
    this.ratingChange.emit(this.currentRating);
  }

  hoverRating(rating: number): void {
    this.rate = rating;
  }
}
