import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import {
  HTTP_INTERCEPTORS,
  HttpClientModule,
  provideHttpClient,
  withInterceptorsFromDi,
} from '@angular/common/http';
import { AppComponent } from './app.component';
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { routes } from './app.routes';
import { RouterModule } from '@angular/router';
import { LoginModule } from './auth/login/login.module';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HeaderModule } from './header/header.module';
import { ViewBookingModule } from './booking/viewbooking/viewbooking.module';
import { MakeBookingModule } from './booking/makebooking/makebooking.module';
import { ConfirmedBookingModule } from './booking/confirmedbooking/confirmedbooking.module';

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule.forRoot(routes),
    HeaderModule,
    ViewBookingModule,
    MakeBookingModule,
    ConfirmedBookingModule,
  ],
  providers: [
    provideHttpClient(
      withInterceptorsFromDi() // Enable DI-based interceptors
    ),
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true,
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
