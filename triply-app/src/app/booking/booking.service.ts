import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Booking } from './booking.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class BookingService {
  private readonly API_URL = environment.apiUrl + '/booking';

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

  createBooking(booking: Booking): Observable<Booking> {
    return this.http.post<Booking>(`${this.API_URL}/createBooking`, booking, {
      withCredentials: true,
    });
  }

  getBookings(): Observable<any[]> {
    return this.http.get<any[]>(`${this.API_URL}/bookings`, {
      withCredentials: true,
    });
  }

  cancelBooking(bookingId: number): Observable<any> {
    return this.http.put(`${this.API_URL}/cancel`, bookingId, {
      withCredentials: true,
    });
  }
}
