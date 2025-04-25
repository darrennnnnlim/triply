# Adding Email to Frontend Registration

This document outlines the steps required to add an email field to the user registration process in the frontend.

## 1. Update `app/auth/login/login.component.ts`

Modify the `loginForm` FormGroup to include an `email` control with appropriate validators.

```typescript
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  standalone: false,
})
export class LoginComponent {
  loginForm: FormGroup;
  errorMessage: string = '';
  isLoading: boolean = false;
  isLoginMode: boolean = true; // true = login, false = register

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    public activeModal: NgbActiveModal
  ) {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]], // Add email control
      role: ['USER', Validators.required],
      confirmPassword: [''], // will only be required in register mode
    });
  }

  switchMode(): void {
    this.isLoginMode = !this.isLoginMode;
    this.errorMessage = '';
    if (this.isLoginMode) {
      this.loginForm.get('confirmPassword')?.clearValidators();
      this.loginForm.get('email')?.clearValidators(); // Clear email validator in login mode
    } else {
      this.loginForm
        .get('confirmPassword')
        ?.setValidators([Validators.required]);
      this.loginForm
        .get('email')
        ?.setValidators([Validators.required, Validators.email]); // Set email validator in register mode
    }
    this.loginForm.get('confirmPassword')?.updateValueAndValidity();
    this.loginForm.get('email')?.updateValueAndValidity();
  }

  onSubmit(): void {
    if (this.loginForm.invalid) return;

    this.isLoading = true;
    this.errorMessage = '';

    const { username, password, email, role, confirmPassword } = this.loginForm.value; // Include email

    if (!this.isLoginMode && password !== confirmPassword) {
      this.errorMessage = 'Passwords do not match';
      this.isLoading = false;
      return;
    }

    if (this.isLoginMode) {
      this.authService.login({ username, password, role }).subscribe({
        next: () => {
          this.activeModal.close('Login successful');
          this.router.navigate(['/']);
          this.isLoading = false;
        },
        error: (err) => {
          console.log(err);
          this.errorMessage = err.error?.message || 'Invalid login';
          this.isLoading = false;
        },
      });
    } else {
      this.authService.register({ username, password, email }).subscribe({ // Include email
        next: () => {
          this.activeModal.close('Registration successful');
          this.router.navigate(['/']);
          this.isLoading = false;
        },
        error: (err) => {
          console.log(err);
          this.errorMessage = err.error?.message || 'Registration failed';
          this.isLoading = false;
        },
      });
    }
  }
}
```

## 2. Update `app/auth/login/login.component.html`

Add an email input field to the registration form, with appropriate styling and validation.

```html
<div class="modal-header">
  <h4 class="modal-title" id="modal-basic-title">
    {{ isLoginMode ? "Login" : "Register" }}
  </h4>
  <button
    type="button"
    class="btn-close"
    aria-label="Close"
    (click)="activeModal.dismiss('Cross click')"
  ></button>
</div>

<div class="modal-body">
  <form [formGroup]="loginForm" (ngSubmit)="onSubmit()">
    <div class="mb-3">
      <label for="username" class="form-label">Username:</label>
      <input
        type="text"
        id="username"
        formControlName="username"
        class="form-control"
        required
      />
    </div>

    <div class="mb-3">
      <label for="password" class="form-label">Password:</label>
      <input
        type="password"
        id="password"
        formControlName="password"
        class="form-control"
        required
      />
    </div>

    <div *ngIf="!isLoginMode" class="mb-3">
      <label for="email" class="form-label">Email:</label>
      <input
        type="email"
        id="email"
        formControlName="email"
        class="form-control"
        required
      />
    </div>

    <div *ngIf="!isLoginMode" class="mb-3">
      <label for="confirmPassword" class="form-label">Confirm Password:</label>
      <input
        type="password"
        id="confirmPassword"
        formControlName="confirmPassword"
        class="form-control"
        required
      />
    </div>

    <!-- Optional error message -->
    <div *ngIf="errorMessage" class="alert alert-danger">
      {{ errorMessage }}
    </div>
  </form>
</div>

<div class="modal-footer d-flex justify-content-between">
  <button type="button" class="btn btn-link p-0" (click)="switchMode()">
    {{ isLoginMode ? "Create an account" : "Back to login" }}
  </button>

  <div>
    <button
      type="button"
      class="btn btn-secondary rounded-pill border-0"
      (click)="activeModal.dismiss('cancel click')"
    >
      Cancel
    </button>
    <button
      type="submit"
      class="btn btn-primary rounded-pill border-0"
      [disabled]="isLoading"
      (click)="onSubmit()"
    >
      {{ isLoginMode ? "Login" : "Register" }}
    </button>
  </div>
</div>
```

## 3. Update `app/auth/auth.service.ts`

Modify the `register()` method to accept the email in the `credentials` parameter.

```typescript
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

  register(credentials: { // Update register method
    username: string;
    password: string;
    email: string;
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