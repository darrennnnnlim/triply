import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { BookingComponent } from './booking.component';
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { AuthInterceptor } from '../interceptors/auth.interceptor';
import { BookingService } from './booking.service';

@NgModule({
  declarations: [BookingComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule.forChild([{ path: '', component: BookingComponent }]),
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
    BookingService
  ],
  exports: [BookingComponent],
})
export class BookingModule {}
