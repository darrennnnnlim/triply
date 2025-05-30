import { FlightOffer } from "../flight-search-page/flight-search.model";
import { HotelOffer } from "../hotel-search-page/hotel-search.model";

export interface FlightBooking {
  id?: number;
  flightId: number;
  flightClassId: number;
  bookingId?: number;
  userId: number;
  departureDate: string; // ISO string format
}

export interface HotelBooking {
  id?: number;
  hotelId: number;
  hotelRoomTypeId: number;
  bookingId?: number;
  userId: number;
  checkIn: string; // ISO string format
  checkOut: string; // ISO string format
}

export interface FlightBookingAddon {
  id?: number;
  flightBookingId?: number;
  flightAddonId: number;
  price?: number;
  quantity: number;
}

export interface HotelBookingAddon {
  id?: number;
  hotelBookingId?: number;
  hotelAddonId: number;
  quantity: number;
  totalPrice?: number;
}

export interface Booking {
  id?: number;
  userId: number;
  finalPrice?: number;
  status?: string;
  bookingTime?: string;
  flightBooking?: FlightBooking;
  hotelBooking?: HotelBooking;
  flightBookingAddon?: FlightBookingAddon[];
  hotelBookingAddon?: HotelBookingAddon[];
}

export interface FlightAddon {
  id?: number;
  name: string;
}

export interface HotelAddon {
  id?: number;
  name: string;
  price: number;
}

export interface ConfirmedBooking {
  bookingFlightInfo: FlightOffer;
  bookingHotelInfo: HotelOffer;
  bookingPayload: Booking;
  bookingResponse: any;
}