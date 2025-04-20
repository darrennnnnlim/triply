import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class HotelOfferDataService {

  private hotelOfferData: any; // To hold the hotel offer data temporarily

  constructor() {}

  // Set the hotel offer data
  setHotelOfferData(data: any): void {
    this.hotelOfferData = data;
  }

  // Get the hotel offer data
  getHotelOfferData(): any {
    return this.hotelOfferData;
  }
}