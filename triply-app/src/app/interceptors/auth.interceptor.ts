import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor, HttpResponse } from '@angular/common/http';
import { Observable, throwError, switchMap, catchError, BehaviorSubject, filter, take, tap } from 'rxjs';
import { AuthService } from '../auth/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private refreshTokenSubject: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    let authReq = req.clone({
        withCredentials: true,
    });

    const csrfToken = this.getCsrfTokenFromCookie();
    if (csrfToken) {
        authReq = authReq.clone({
            setHeaders: {
                'X-XSRF-TOKEN': csrfToken
            },
            withCredentials: true
        });
    }

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
          if (error.status === 401 && !req.url.includes('auth/refresh')) {
            return this.handle401Error(req, next);
          }
          return throwError(() => error);
        })
      );
  }

  private handle401Error(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (!this.isRefreshing) {
      this.isRefreshing = true;
      this.refreshTokenSubject.next(false);

      return this.authService.refreshToken().pipe(
        switchMap(() => {
            this.isRefreshing = false;
            this.refreshTokenSubject.next(true);

            return next.handle(request);
        }),
        catchError((err) => {
            this.isRefreshing = false;
            this.authService.logout();
            return throwError(() => err);
        })
      )
    }

    return this.refreshTokenSubject.pipe(
      filter(token => token !== null),
      take(1),
      switchMap(() => next.handle(request))
    );
  }

  private getCsrfTokenFromCookie(): string | null {
    const match = document.cookie.match(/XSRF-TOKEN=([^;]+)/);
    return match ? decodeURIComponent(match[1]) : null;
  }

  private storeCsrfTokenInCookie(token: string): void {
    document.cookie = `XSRF-TOKEN=${encodeURIComponent(token)}; path=/; Secure; SameSite=Strict`;
  }
}