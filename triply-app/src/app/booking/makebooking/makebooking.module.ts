import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {
  HTTP_INTERCEPTORS,
  provideHttpClient,
  withInterceptorsFromDi,
} from '@angular/common/http';
import { MatCardModule } from '@angular/material/card';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTableModule } from '@angular/material/table';
import { MatRow } from '@angular/material/table';
import { MakeBookingComponent } from './makebooking.component';
import { AuthInterceptor } from '../../interceptors/auth.interceptor';
import { BookingService } from '../booking.service';
import { RouterModule } from '@angular/router';

@NgModule({
  declarations: [MakeBookingComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatListModule,
    MatIconModule,
    MatButtonModule,
    MatDividerModule,
    MatTabsModule,
    MatTableModule,
    MatRow,
    FormsModule,
    RouterModule.forChild([{ path: '', component: MakeBookingComponent }]),
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
    BookingService,
  ],
  exports: [MakeBookingComponent],
})
export class MakeBookingModule {}
