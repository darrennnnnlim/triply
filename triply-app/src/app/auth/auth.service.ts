import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {
  BehaviorSubject,
  catchError,
  map,
  Observable,
  of,
  tap,
  throwError,
} from 'rxjs';
import { LoginResponse, User } from './auth.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly API_URL = 'http://localhost:8080/api/v1/auth';
  private userSubject: BehaviorSubject<User | null> =
    new BehaviorSubject<User | null>(null);
  private authState = new BehaviorSubject<{
    isLoggedIn: boolean;
    username?: string;
  }>({ isLoggedIn: false });
  private isLoggedInSubject = new BehaviorSubject<boolean>(false);
  isLoggedIn$ = this.isLoggedInSubject.asObservable();
  authState$ = this.authState.asObservable();

  constructor(private http: HttpClient) {}

  login(credentials: {
    username: string;
    password: string;
    role?: string;
  }): Observable<void> {
    return this.http
      .post<LoginResponse>(`${this.API_URL}/login`, credentials, {
        withCredentials: true,
      })
      .pipe(
        tap((response) => {
          this.authState.next({
            isLoggedIn: true,
            username: response.username, // Add username to login response
          });
        }),
        map(() => undefined)
      );
  }

  register(credentials: {
    username: string;
    password: string;
  }): Observable<any> {
    return this.http.post<any>(`${this.API_URL}/register`, credentials);
  }

  refreshToken(): Observable<boolean> {
    return this.http
      .post<{ accessToken: string }>(
        `${this.API_URL}/refresh`,
        {},
        { withCredentials: true }
      )
      .pipe(
        tap((response) => {
          this.authState.next({
            isLoggedIn: true,
            username: this.authState.value.username,
          });
        }),
        map(() => true),
        catchError(() => {
          this.logout();
          return of(false);
        })
      );
  }

  checkSession(): Observable<void> {
    return this.http
      .get<{ loggedIn: boolean; username?: string }>(
        `${this.API_URL}/check-session`,
        { withCredentials: true }
      )
      .pipe(
        tap((response) => {
          this.authState.next({
            isLoggedIn: response.loggedIn,
            username: response.username,
          });
        }),
        catchError((error) => {
          // Pass through to interceptor
          return throwError(() => error);
        }),
        map(() => undefined)
      );
  }

  logout(): Observable<void> {
    return this.http
      .post<void>(`${this.API_URL}/logout`, {}, { withCredentials: true })
      .pipe(
        tap(() => {
          this.isLoggedInSubject.next(false);
        })
      );
  }
}
