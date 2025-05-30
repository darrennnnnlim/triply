import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpResponse,
} from '@angular/common/http';
import {
  Observable,
  throwError,
  switchMap,
  catchError,
  BehaviorSubject,
  filter,
  take,
  tap,
} from 'rxjs';
import { AuthService } from '../auth/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private refreshTokenSubject: BehaviorSubject<boolean> =
    new BehaviorSubject<boolean>(false);

  constructor(private authService: AuthService) {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    let authReq = req.clone({});

    // Check for CSRF token
    const csrfToken = this.getCsrfTokenFromCookie();
    if (csrfToken) {
      authReq = authReq.clone({
        setHeaders: { 'X-XSRF-TOKEN': csrfToken },
      });
    }

    console.log('Intercepted');

    return next.handle(authReq).pipe(
      tap((event) => {
        if (event instanceof HttpResponse) {
          const newCsrfToken = event.headers.get('X-XSRF-TOKEN');
          if (newCsrfToken) {
            this.storeCsrfTokenInCookie(newCsrfToken);
          }
        }
      }),
      catchError((error) => {
        const isTokenExpired = error.status === 401;
        if (
          (isTokenExpired || error.status === 403) &&
          !req.url.includes('auth/refresh')
        ) {
          return this.handle403Error(req, next);
        }
        return throwError(() => error);
      })
    );
  }

  private handle403Error(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    if (!this.isRefreshing) {
      this.isRefreshing = true;
      this.refreshTokenSubject.next(false);

      return this.authService.refreshToken().pipe(
        switchMap((success) => {
          this.isRefreshing = false;
          if (success) {
            this.refreshTokenSubject.next(true);
            return next.handle(this.addTokenToRequest(request));
          }
          return throwError(() => new Error('Refresh failed'));
        }),
        catchError((error) => {
          this.isRefreshing = false;
          this.authService.logout();
          return throwError(() => error);
        })
      );
    }

    return this.refreshTokenSubject.pipe(
      filter((success) => success),
      take(1),
      switchMap(() => next.handle(this.addTokenToRequest(request)))
    );
  }

  private addTokenToRequest(request: HttpRequest<any>): HttpRequest<any> {
    const csrfToken = this.getCsrfTokenFromCookie();
    return request.clone({
      setHeaders: {
        'X-XSRF-TOKEN': csrfToken || '',
      },
    });
  }

  private getCsrfTokenFromCookie(): string | null {
    const match = document.cookie.match(/XSRF-TOKEN=([^;]+)/);
    return match ? decodeURIComponent(match[1]) : null;
  }

  private storeCsrfTokenInCookie(token: string): void {
    document.cookie = `XSRF-TOKEN=${encodeURIComponent(
      token
    )}; path=/; Secure; SameSite=Strict`;
  }
}
