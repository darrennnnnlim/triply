export interface Rating {
    userId: number;       
    flightId?: number | null;    
    hotelId?: number | null;     
    rating: number; 
    type: String;      
  }
  