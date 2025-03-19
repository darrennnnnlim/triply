import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class FlightOfferDataService {

  private flightOfferData: any; // To hold the flight offer data temporarily

  constructor() {}

  // Set the flight offer data
  setFlightOfferData(data: any): void {
    this.flightOfferData = data;
  }

  // Get the flight offer data
  getFlightOfferData(): any {
    return this.flightOfferData;
  }
}