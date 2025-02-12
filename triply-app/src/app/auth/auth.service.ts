import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

@Injectable()
export class AuthService {
  private readonly API_URL = 'http://localhost:8080/api/v1/auth';

  constructor(private http: HttpClient) {}

  login(credentials: {
    username: string;
    password: string;
    role?: string;
  }): Observable<any> {
    return this.http.post(`${this.API_URL}/login`, credentials);
  }

  register(credentials: {
    username: string;
    password: string;
  }): Observable<any> {
    return this.http.post<any>(`${this.API_URL}/register`, credentials);
  }

  refreshToken(): Observable<any> {
    return this.http.post(
      `${this.API_URL}/refresh`,
      {},
      { withCredentials: true }
    );
  }

  logout(): Observable<void> {
    return this.http.post<void>(
      `${this.API_URL}/logout`,
      {},
      { withCredentials: true }
    );
  }
}
