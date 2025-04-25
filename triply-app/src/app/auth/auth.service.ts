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
  // private authState = new BehaviorSubject<{
  //   isLoggedIn: boolean;
  //   username?: string;
  // }>({ isLoggedIn: false });
  private authState = new BehaviorSubject<{
    isLoggedIn: boolean;
    username?: string;
    role?: string;
  }>({ isLoggedIn: false });  
  private isLoggedInSubject = new BehaviorSubject<boolean>(false);
  isLoggedIn$ = this.isLoggedInSubject.asObservable();
  authState$ = this.authState.asObservable();

  constructor(private http: HttpClient) {}

  // login(credentials: {
  //   username: string;
  //   password: string;
  //   role?: string;
  // }): Observable<void> {
  //   return this.http
  //     .post<LoginResponse>(`${this.API_URL}/login`, credentials, {
  //       withCredentials: true,
  //     })
  //     .pipe(
  //       tap((response) => {
  //         this.authState.next({
  //           isLoggedIn: true,
  //           username: response.username, // Add username to login response
  //         });
  //       }),
  //       map(() => undefined)
  //     );
  // }
  login(credentials: { username: string; password: string; role?: string }): Observable<void> {
    return this.http
      .post<LoginResponse>(`${this.API_URL}/login`, credentials, { withCredentials: true })
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
    this.http.get<{ loggedIn: boolean; username?: string; role?: string }>(`${this.API_URL}/check-session`, { withCredentials: true })
      .subscribe(response => {
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

  // logout(): Observable<void> {
  //   return this.http
  //     .post<void>(`${this.API_URL}/logout`, {}, { withCredentials: true })
  //     .pipe(
  //       tap(() => {
  //         this.isLoggedInSubject.next(false);
  //       })
  //     );
  // }
  logout(): Observable<void> {
    return this.http
      .post<void>(`${this.API_URL}/logout`, {}, { withCredentials: true })
      .pipe(
        tap(() => {
          this.authState.next({ isLoggedIn: false });
          localStorage.removeItem('user_role');
          localStorage.removeItem('username');
          // Remove tokens/cookies if needed
        })
      );
  }
  

  isLoggedIn(): boolean {
    // Check for token/cookie or use your own logic
    return !!localStorage.getItem('access_token');
  }
  
  isAdmin(): boolean {
    // Example: check user role stored in localStorage or from a user object
    return localStorage.getItem('user_role') === 'ROLE_ADMIN';
  }
  
}
