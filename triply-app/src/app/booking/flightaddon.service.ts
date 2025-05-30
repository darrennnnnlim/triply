import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class FlightAddonService {
  private readonly API_URL = environment.apiUrl + '/flightaddon';
  constructor(private http: HttpClient) {}

  searchAddonsByFlightId(flightId: number): Observable<any> {
    return this.http.get(`${this.API_URL}/${flightId}`, {
      withCredentials: true,
    });
  }
}
