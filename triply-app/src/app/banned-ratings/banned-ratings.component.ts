import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PopupComponent } from '../popup/popup.component';
import { BanedUserRating } from './banned-ratings.service';
import moment from 'moment';

@Component({
  selector: 'app-banned-ratings',
  imports: [CommonModule, PopupComponent],
  templateUrl: './banned-ratings.component.html',
  styleUrl: './banned-ratings.component.css'
})
export class BannedRatingsComponent {
  showDeleted = true;
  banned: any= []; 
  active: any = [];
  counter: number = 1;
  isPopup = false;
  popupMsg = "";

  ngOnInit(): void {
    const userId = 1;

    this.loadBookingData();
  }
  banORunban (userid: number, flightHotelId: number, type: string, action: string){
    this.banned = this.banned.filter((user: { id: number; }) => user.id !== userid);
    this.isPopup = !this.isPopup;
    if (type == "Flight"){
      this.updateRating(userid, flightHotelId, null);
    }
    else{
      this.updateRating(userid, null, flightHotelId);
    }
    
    console.log(action)
    if (action == "ban"){
      this.popupMsg = "user with ID: " + userid + " has been banned";
    }
    else{
      this.popupMsg = "user with ID: " + userid + " has been unbanned";
    }
   
    
  }
  constructor(private ratingService: BanedUserRating) {}

  loadBookingData(): void {

    this.ratingService.getAllRatings().subscribe(allRatings => {

      const flightRatings = allRatings.filter((rating: { flightId: number; }) => rating.flightId);
      const hotelRatings = allRatings.filter((rating: { hotelId: number; }) => rating.hotelId);
  
    
      const flightIds = [...new Set(flightRatings.map((rating: any) => rating.flightId))] as number[]; 
      const flightDetailsPromises = flightIds.map((flightId: number) =>
        this.ratingService.getFlightDetails(flightId).toPromise()
      );
      
      const hotelIds = [...new Set(hotelRatings.map((rating: any) => rating.hotelId))] as number[]; 
      const hotelDetailsPromises = hotelIds.map((hotelId: number) =>
        this.ratingService.getHotelDetails(hotelId).toPromise()
      );
  
      
      Promise.all([...flightDetailsPromises, ...hotelDetailsPromises]).then(responses => {
        const [flightDetailsResponses, hotelDetailsResponses] = [
          responses.slice(0, flightDetailsPromises.length),
          responses.slice(flightDetailsPromises.length)
        ];
       
        const flightResults = flightRatings.map((rating: { rating: number, userId: number, delete: string;}, index:  number) => {
          const flightDetails = flightDetailsResponses[index];
          const rateType = rating.rating ? 'exist' : 'new';
          return {
            flightDetails,
            user: rating.userId,
            delete: rating.delete,
            rating: rating.rating,
            rateType,
            type: 'Flight',
          };
        });
        
        const hotelResults = hotelRatings.map((rating: { rating: number, userId: number, delete: string; }, index: number) => {
          const hotelDetails = hotelDetailsResponses[index];
          const rateType = rating.rating ? 'exist' : 'new';
          return {
            hotelDetails,
            user: rating.userId,
            delete: rating.delete,
            rating: rating.rating,
            rateType,
            type: 'Hotel',
          };
        });
        
        this.addItemsToList([...flightResults, ...hotelResults]);
      }).catch(err => {
        console.error("Error fetching details:", err);
      });
    });
  }
  

  addItemsToList(results: any[]): void {
    console.log(results)
    results.forEach((result: any) => {
      if (result.type === 'Flight' && result.delete == 'T') {
        this.banned.push({
          name: result.flightDetails.airline,
          arrivalTime: convertDateTime(result.flightDetails.arrivalTime),
          departureTime: convertDateTime(result.flightDetails.departureTime),
          flightNumber: result.flightDetails.flightNumber,
          seatClass: result.seatClass,
          seatNumber: result.seatNumber,
          flightId: result.flightDetails.id,
          id: this.counter++,
          rating: result.rating || 0,
          rateType: result.rateType,
          type: result.type,
          user: result.user
        });
      } else if (result.type === 'Hotel' && result.delete == 'T') {
        this.banned.push({
          name: result.hotelDetails.name,
          checkInDate: convertDateTime(result.hotelDetails.checkInDate),
          checkOutDate: convertDateTime(result.hotelDetails.checkOutDate),
          hotelId: result.hotelDetails.id,
          id: this.counter++,
          rating: result.rating || 0,
          rateType: result.rateType,
          type: result.type,
          description: result.hotelDetails.description,
          user: result.user
        });
      }
      else if (result.type === 'Flight' && result.delete == 'F') {
        this.active.push({
          name: result.flightDetails.airline,
          arrivalTime: convertDateTime(result.flightDetails.arrivalTime),
          departureTime: convertDateTime(result.flightDetails.departureTime),
          flightNumber: result.flightDetails.flightNumber,
          seatClass: result.seatClass,
          seatNumber: result.seatNumber,
          flightId: result.flightDetails.id,
          id: this.counter++,
          rating: result.rating || 0,
          rateType: result.rateType,
          type: result.type,
          user: result.user
        });
      }
      else{
        this.active.push({
          name: result.hotelDetails.name,
          checkInDate: convertDateTime(result.hotelDetails.checkInDate),
          checkOutDate: convertDateTime(result.hotelDetails.checkOutDate),
          hotelId: result.hotelDetails.id,
          id: this.counter++,
          rating: result.rating || 0,
          rateType: result.rateType,
          type: result.type,
          description: result.hotelDetails.description,
          user: result.user
        });
      }

    });

    console.log(this.banned);
    console.log(this.active)
  }

  updateRating(userId: number, flightId: number | null | undefined, hotelId: number | null | undefined): void {
      this.ratingService.putRating(userId, flightId, hotelId).subscribe({
        next: (res: any) => {
         console.log(res)
        },
        error: (err) => {
          console.error( `Error: ${err.status} - ${err.statusText}`);
        },
      });
    }

}
function convertDateTime(timestamp: string): string {
  return moment(timestamp).tz('Asia/Singapore').format('DD/MM/yyyy') + " " + moment(timestamp).tz('Asia/Singapore').format('HH:mm');
}
