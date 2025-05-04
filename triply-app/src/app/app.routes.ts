import { Routes } from '@angular/router';
import { HistoryComponent } from './history/history.component';
import { BannedUsersComponent } from './banned-users/banned-users.component';
import { ProfileComponent } from './profile/profile.component';
import { BannedRatingsComponent } from './banned-ratings/banned-ratings.component';

export const routes: Routes = [
  {
    path: 'login',
    loadChildren: () =>
      import('./auth/login/login.module').then((m) => m.LoginModule),
  },
  {
    path: '',
    loadChildren: () => import('./home/home.module').then((m) => m.HomeModule),
  },
  {
    path: 'profile',
    loadChildren: () =>
      import('./profile/profile.module').then((m) => m.ProfileModule),
  },
  {
    path: 'makebooking',
    loadChildren: () =>
      import('./booking/makebooking/makebooking.module').then(
        (m) => m.MakeBookingModule
      ),
  },
  {
    path: 'viewbooking',
    loadChildren: () =>
      import('./booking/viewbooking/viewbooking.module').then(
        (m) => m.ViewBookingModule
      ),
  },
  {
    path: 'admin',
    loadChildren: () =>
      import('./admin/admin.module').then((m) => m.AdminModule),
  },
  {
    path: 'flight-search',
    loadChildren: () =>
      import('./flight-search-page/flight-search-page.module').then(
        (m) => m.FlightSearchPageModule
      ),
  },
  {
    path: 'flight-offer', // Dynamic route for flight offer details
    loadChildren: () =>
      import('./flight-offer/flight-offer.module').then(
        (m) => m.FlightOfferModule
      ),
  },
  {
    path: 'hotel-search',
    loadChildren: () =>
      import('./hotel-search-page/hotel-search-page.module').then(
        (m) => m.HotelSearchPageModule
      ),
  },
  {
    path: 'hotel-offer', // Dynamic route for hotel offer details
    loadChildren: () =>
      import('./hotel-offer/hotel-offer.module').then(
        (m) => m.HotelOfferModule
      ),
  },
  { path: 'rateBooking', component: HistoryComponent },
  { path: 'banned', component: BannedRatingsComponent },
  {
    path: 'confirmedbooking',
    loadChildren: () =>
      import('./booking/confirmedbooking/confirmedbooking.module').then(
        (m) => m.ConfirmedBookingModule
      ),
  },
  {
    path: '**',
    redirectTo: '',
  },
];
