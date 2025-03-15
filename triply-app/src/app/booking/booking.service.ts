import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class BookingService {
  private readonly API_URL = 'http://localhost:8080/api/v1/booking';

  constructor(private http: HttpClient) {}

  test(): Observable<any> {
    // return this.http.get(`${this.API_URL}/test`, { withCredentials: true });
    return this.http.get(`${this.API_URL}/test`, { withCredentials: true, responseType: 'text' });

  }
  postTest(): Observable<any> {
    return this.http.post(
      `${this.API_URL}/test`,
      {},
      { withCredentials: true }
    );
  }
}
