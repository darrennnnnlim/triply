import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Rating } from './history.model';


@Injectable({ providedIn: 'root' })
export class HistoryService {
  private readonly API_URL = 'http://localhost:8080/api/v1/booking';
  private readonly RATINGS_URL = 'http://localhost:8080/api/v1/ratings';

  constructor(private http: HttpClient) {}

  getFlightBookings(userId: number): Observable<any> {
    return this.http.get(`${this.API_URL}/flight/user/${userId}`);
  }

  getHotelBookings(userId: number): Observable<any> {
    return this.http.get(`${this.API_URL}/hotel/user/${userId}`);
  }

  getFlightDetails(flightId: number): Observable<any> {
    return this.http.get(`http://localhost:8080/api/v1/flight/${flightId}`);
  }

  getHotelDetails(hotelId: number): Observable<any> {
    return this.http.get(`http://localhost:8080/api/v1/hotel/${hotelId}`);
  }

  getFlightRatings(userId: number): Observable<any> {
    return this.http.get(`${this.RATINGS_URL}/user/${userId}`);
  }

  getHotelRatings(userId: number): Observable<any> {
    return this.http.get(`${this.RATINGS_URL}/hotel/${userId}`);
  }

  postRating(rating: Rating): Observable<Rating> {
    return this.http.post<Rating>(`${this.RATINGS_URL}/submitRating`, rating, {
      withCredentials: true,
    });
  }
}
