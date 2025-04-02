import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Rating } from './history.model';
import { environment } from '../../environments/environment';


@Injectable({ providedIn: 'root' })
export class HistoryService {
  private readonly API_URL = environment.apiUrl + '/booking';
  private readonly RATINGS_URL = environment.apiUrl + '/ratings';

  constructor(private http: HttpClient) {}

  getFlightBookings(userId: number): Observable<any> {
    return this.http.get(`${this.API_URL}/flight/user/${userId}`);
  }

  getHotelBookings(userId: number): Observable<any> {
    return this.http.get(`${this.API_URL}/hotel/user/${userId}`);
  }

  getFlightDetails(flightId: number): Observable<any> {
    return this.http.get(environment.apiUrl + `/flight/${flightId}`);
  }

  getHotelDetails(hotelId: number): Observable<any> {
    return this.http.get(environment.apiUrl + `/hotel/${hotelId}`);
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
