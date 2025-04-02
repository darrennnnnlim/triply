import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

interface SearchDTO {
    origin: string;
    destination: string;
    departureDate: string;
    arrivalDate: string;
    maxPrice: number;
}

@Injectable({ providedIn: 'root' })
export class FlightSearchPageService {
//   private readonly API_URL = 'http://localhost:8080/api/v1/flightsearch';

  private readonly API_URL = environment.apiUrl + '/flightsearch'

  constructor(private http: HttpClient) {}

  test(): Observable<any> {
    return this.http.get(`${this.API_URL}/test`, { withCredentials: true });
  }

  searchFlights(searchRequest: SearchDTO): Observable<any> {
    return this.http.post<any>(this.API_URL, searchRequest, {
      withCredentials: true 
    });
  }
}
