import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RatingComponent } from '../rating/rating.component';
import axios from 'axios';
import moment from 'moment-timezone';

@Component({
  selector: 'app-history',
  imports: [RatingComponent, CommonModule],
  templateUrl: './history.component.html',
  styleUrl: './history.component.css'
})
export class HistoryComponent implements OnInit {
  items: any = [];
  // items = [
  //   // { id: 1, name: "Singapore Airline", desc: "SQ123", rating: 0 },
  //   // { id: 2, name: "Scoot", desc: "SCoot123", rating: 1 },
  //   // { id: 3, name: "Cathay Pacific", desc: "CP123", rating: 5 },
  //   // { id: 4, name: "Hotel 1", desc: "Best Hotel in SG", rating: 4 },
  //   // { id: 5, name: "Hotel 123", desc: "Best Hotel in SG", rating: 3 }
  // ]

  expandedId: number | null = null;

  async ngOnInit(): Promise<void> {

    const userId = 1;

    const bookingsResponse = await axios.get(`http://localhost:8080/api/v1/booking/flight/user/${userId}`);
    const bookings = bookingsResponse.data;


    const flightDetailsPromises = bookings.map((booking: any) =>
      axios.get(`http://localhost:8080/api/v1/flight/${booking.flightId}`)
    );


    const flightDetailsResponses = await Promise.all(flightDetailsPromises);


    const results = bookings.map((booking: any, index: number | number) => ({
      ...booking,
      flightDetails: flightDetailsResponses[index].data
    }));

    console.log(results);

    results.forEach((result: { flightDetails: { airline: any; arrivalTime: any; departureTime: any; flightNumber: any; }; seatClass: any; seatNumber: any; flightId: { flightId: any; }; bookingId: any; }) => {
      var name = result.flightDetails.airline;
      var arrivalTime = result.flightDetails.arrivalTime;
      var departureTime = result.flightDetails.departureTime;
      var flightNumber = result.flightDetails.flightNumber;
      var seatClass = result.seatClass;
      var seatNumber = result.seatNumber;
      var flightId = result.flightId.flightId;
      var bookingId = result.bookingId;


      this.items.push({
        "name": name,
        "arrivalTime": convertDateTime(arrivalTime),
        "departureTime": convertDateTime(departureTime),
        "flightNumber": flightNumber,
        "seatClass": seatClass,
        "seatNumber": seatNumber,
        "flightId": flightId,
        "id": bookingId,
        "rating": 0
      })
    });


    console.log(this.items)
  } catch(error: any) {
    console.error('Error fetching data', error);
  }


  toggleExpand(id: number) {
    this.expandedId = this.expandedId === id ? null : id;
  }

  onRatingChange(newRating: number, id: number): void {
    // this.rating = newRating;
    const item = this.items.find((item: { id: number; }) => item.id === id);
    if (item) {
      item.rating = newRating;
      console.log("Rating: " + item.rating);
      console.log("ID: " + id);
      // call the api
    }

    

  }
}

function convertDateTime(timestamp: string): string {
  return moment(timestamp).tz('Asia/Singapore').format('DD/MM/yyyy') + " " +moment(timestamp).tz('Asia/Singapore').format('HH:mm');
 }
