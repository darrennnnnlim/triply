export interface FlightOffer {

  origin: string;
  destination: string;
  departureDate: string; // ISO string format
  arrivalDate: string; // ISO string format
  flightId: number;
  flightNumber: string;

  flightClassId: number;
  flightClassName: string;
  airlineId: number;
  airlineName: string;
  
  flightPriceId: number;
  basePrice: number;
}

export interface SearchDTO {
    origin: string;
    destination: string;
    departureDate: string;
    arrivalDate: string;
    maxPrice: number;
}