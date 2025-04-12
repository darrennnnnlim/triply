export interface HotelOffer {
  hotel: Hotel;
  checkInDate: string;
  checkOutDate: string;
  pricing: Pricing
}

export interface HotelSearchDTO {
    location: string;
    checkInDate: string;
    checkOutDate: string;
}

export interface Hotel {
  name: string;
  location: string;
}

export interface Pricing {
  totalPrice: number;
}