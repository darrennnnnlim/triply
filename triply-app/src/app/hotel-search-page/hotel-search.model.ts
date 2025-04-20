export interface HotelOffer {
  totalPrice: number;
  checkInDate: string;
  checkOutDate: string;
  capacity: number;
  location: string;

  hotelId: number;
  hotelName: string;
  hotelRoomTypeId: number;
  hotelRoomTypeName: string; 
}

export interface HotelSearchDTO {
    location: string;
    checkInDate: string;
    checkOutDate: string;
    maxPrice: number;
}

export interface Hotel {
  name: string;
  location: string;
}

export interface Pricing {
  totalPrice: number;
}