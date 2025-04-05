import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Rating } from '../history/history.model';

@Injectable({ providedIn: 'root' })
export class BanedUserRating {
    private readonly API_URL = 'http://localhost:8080/api/v1/booking';
    private readonly RATINGS_URL = 'http://localhost:8080/api/v1/ratings';

    constructor(private http: HttpClient) {}

    getAllRatings(): Observable<any> {
    return this.http.get(`${this.RATINGS_URL}/allRatings`);
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

    putRating(userId: number, flightId: number | null | undefined, hotelId: number | null | undefined): Observable<string> {
        if (flightId != null && flightId != undefined){
            return this.http.put<string>(`${this.RATINGS_URL}/banRating/${userId}?flightId=${flightId}`, {
                withCredentials: true,
              });
        }
        else{
            return this.http.put<string>(`${this.RATINGS_URL}/banRating/${userId}?hotelId=${hotelId}`, {
                withCredentials: true,
              });
        }
        
      }
}