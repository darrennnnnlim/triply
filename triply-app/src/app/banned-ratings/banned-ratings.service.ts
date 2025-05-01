import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class BanedUserRating {
    private readonly RATINGS_URL = environment.apiUrl + '/ratings';
    private readonly API_URL = environment.apiUrl + '/admin';

    constructor(private http: HttpClient) {}

    getAllRatings(): Observable<any> {
    return this.http.get(`${this.RATINGS_URL}/allRatings`, { withCredentials: true });
    }

    getFlightDetails(flightId: number): Observable<any> {
    return this.http.get(`${environment.apiUrl}/flight/${flightId}`, { withCredentials: true });
    }

    getHotelDetails(hotelId: number): Observable<any> {
    return this.http.get(`${environment.apiUrl}/hotel/${hotelId}`, { withCredentials: true });
    }

    getUser(userId: number): Observable<string> {
      return this.http.get(`${this.API_URL}/user/${userId}`, {
        withCredentials: true,
        responseType: 'text',
      });
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