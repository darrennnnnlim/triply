import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HotelSearchDTO } from './hotel-search.model';

@Injectable({ providedIn: 'root' })
export class HotelSearchPageService {

  private readonly API_URL = 'http://localhost:8080/api/v1/hotelsearch'

  constructor(private http: HttpClient) {}

  test(): Observable<any> {
    return this.http.get(`${this.API_URL}/test`, { withCredentials: true });
  }

  searchHotels(searchRequest: HotelSearchDTO): Observable<any> {
    return this.http.post<any>(this.API_URL, searchRequest, {
      withCredentials: true 
    });
  }
}
