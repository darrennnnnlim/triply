import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RatingComponent } from '../rating/rating.component';

@Component({
  selector: 'app-history',
   imports: [RatingComponent, CommonModule],
  templateUrl: './history.component.html',
  styleUrl: './history.component.css'
})
export class HistoryComponent {
  // rating: number = 0;

  items = [
    { id:1, name: "Singapore Airline", desc: "SQ123", rating: 0},
    { id:2, name: "Scoot", desc: "SCoot123", rating: 1},
    { id:3, name: "Cathay Pacific", desc: "CP123", rating: 5},
    { id:4, name: "Hotel 1", desc: "Best Hotel in SG", rating: 4},
    { id:5, name: "Hotel 123", desc: "Best Hotel in SG", rating: 3}
  ]

  expandedId: number | null= null;

  toggleExpand(id: number){
    this.expandedId = this.expandedId === id ? null: id;
  }
  
  onRatingChange(newRating: number, id: number): void {
    // this.rating = newRating;
    const item = this.items.find(item => item.id ===id);
    if (item){
      item.rating = newRating;
      console.log("Rating: "+ item.rating);
      console.log("ID: " + id);
    // call the api
    }
    

  }
}
