import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RatingService {

  private apiUrl = 'http://localhost:8080/api/v1/ratings/flight'; 

  constructor(private http: HttpClient) { }

  getRatings(flightId: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${flightId}`);
  }
}
