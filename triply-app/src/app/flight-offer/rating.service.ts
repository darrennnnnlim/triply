import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
@Injectable({
  providedIn: 'root'
})
export class RatingService {
  
  private apiUrl = environment.apiUrl + '/ratings/airline'; 

  constructor(private http: HttpClient) { }

  getRatings(flightId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${flightId}`, {withCredentials: true});
  }
}
