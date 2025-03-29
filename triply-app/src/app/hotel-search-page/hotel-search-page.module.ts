import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
// import { FormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { HotelSearchPageComponent } from './hotel-search-page.component';
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { AuthInterceptor } from '../interceptors/auth.interceptor';
import { HotelSearchPageService } from './hotel-search-page.service';

@NgModule({
  declarations: [HotelSearchPageComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule.forChild([{ path: '', component: HotelSearchPageComponent }]),
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
    HotelSearchPageService
  ],
  exports: [HotelSearchPageComponent],
})
export class HotelSearchPageModule {}
