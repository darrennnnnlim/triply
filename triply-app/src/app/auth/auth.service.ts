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
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly API_URL = environment.apiUrl + '/auth';
  private userSubject: BehaviorSubject<User | null> =
    new BehaviorSubject<User | null>(null);

  private authState = new BehaviorSubject<{
    isLoggedIn: boolean;
    username?: string;
    role?: string;
  }>({ isLoggedIn: false });
  private isLoggedInSubject = new BehaviorSubject<boolean>(false);
  isLoggedIn$ = this.isLoggedInSubject.asObservable();
  authState$ = this.authState.asObservable();

  constructor(private http: HttpClient) {}

  login(credentials: {
    username: string;
    password: string;
  }): Observable<void> {
    console.log('Sending login request to:', `${this.API_URL}/login`);
    console.log('With credentials:', credentials);
    return this.http
      .post<LoginResponse>(`${this.API_URL}/login`, credentials, {
        // Temporarily disabled withCredentials for testing
        withCredentials: true,
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        }
      }).pipe(
        tap(response => console.log('Login response:', response)),
        catchError(error => {
          console.error('Login error:', error);
          return throwError(() => error);
        })
      )
      .pipe(
        tap((response) => {
          this.authState.next({
            isLoggedIn: true,
            username: response.username,
            role: response.role,
          });
          // Persist to localStorage
          localStorage.setItem('user_role', response.role);
          localStorage.setItem('username', response.username);
        }),
        map(() => undefined)
      );
  }

  initAuthStateFromBackend() {
    this.http
      .get<{ loggedIn: boolean; username?: string; role?: string }>(
        `${this.API_URL}/check-session`,
        { withCredentials: true }
      )
      .subscribe((response) => {
        let role = response.role;
        if (!role) {
          role = localStorage.getItem('user_role') || '';
        }
        this.authState.next({
          isLoggedIn: response.loggedIn,
          username: response.username,
          role: response.role,
        });
        // Optionally, persist to localStorage for instant reloads
        if (response.role && response.username) {
          localStorage.setItem('user_role', response.role);
          localStorage.setItem('username', response.username);
        }
      });
  }

  register(credentials: { // Update register method
    username: string;
    password: string;
    email: string;
  }): Observable<any> {
    return this.http.post<any>(`${this.API_URL}/register`, credentials).pipe(
      catchError((error) => {
        let errorMessage = 'Registration failed';
        if (error.error instanceof ErrorEvent) {
          // client-side error
          errorMessage = `Error: ${error.error.message}`;
        } else {
          // server-side error
          errorMessage = `Error Code: ${error.status}\nMessage: ${error.error.message}`;
        }
        return throwError(() => errorMessage);
      })
    );
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
      .post<void>(
        `${this.API_URL}/logout`,
        {},
        { withCredentials: true, responseType: 'text' as 'json' }
      )
      .pipe(
        tap(() => {
          this.authState.next({ isLoggedIn: false });
          localStorage.removeItem('user_role');
          localStorage.removeItem('username');
        })
      );
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('access_token');
  }

  isAdmin(): boolean {
    return localStorage.getItem('user_role') === 'ROLE_ADMIN';
  }

  getUserId(): Observable<number> {
    return this.http.get<number>(`${this.API_URL}/userid`, {
      withCredentials: true,
    });
  }
}
