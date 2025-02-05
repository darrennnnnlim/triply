import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

interface AuthResponse {
  accessToken: string;
  refreshToken: string;
}

// @Injectable({ providedIn: 'root' })
@Injectable()
export class AuthService {
  private readonly API_URL = 'http://localhost:8080/api/v1/auth';
  private currentRole: string | null = null;

  constructor(private http: HttpClient) {}

  login(credentials: {
    username: string;
    password: string;
    role?: string;
  }): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.API_URL}/login`, credentials)
      .pipe(
        tap((response) => {
          this.storeTokens(response.accessToken, response.refreshToken);
          this.currentRole = credentials.role || 'USER'; // Default role if not provided
        })
      );
  }

  register(credentials: {
    username: string;
    password: string;
  }): Observable<any> {
    return this.http.post<any>(`${this.API_URL}/register`, credentials);
  }

  refreshToken(refreshToken: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API_URL}/refresh`, {
      refreshToken,
      role: this.currentRole, // Include role from stored value
    });
  }

  storeTokens(accessToken: string, refreshToken: string): void {
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
  }

  getAccessToken(): string | null {
    return localStorage.getItem('accessToken');
  }

  getRefreshToken(): string | null {
    return localStorage.getItem('refreshToken');
  }

  logout(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
  }
}
