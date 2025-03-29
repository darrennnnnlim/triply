import { Routes } from '@angular/router';
import { HistoryComponent } from './history/history.component';
import { BannedUsersComponent } from './banned-users/banned-users.component';

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
    path: 'makebooking',
    loadChildren: () =>
      import('./booking/makebooking/makebooking.module').then(
        (m) => m.MakeBookingModule
      ),
  },
  {
    path: 'booking',
    loadChildren: () =>
      import('./booking/booking.module').then((m) => m.BookingModule),
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
            import('./flight-search-page/flight-search-page.module').then((m) => m.FlightSearchPageModule),
  },
  {
    path: 'flight-offer', // Dynamic route for flight offer details
    loadChildren: () =>
      import('./flight-offer/flight-offer.module').then((m) => m.FlightOfferModule),
  },
  { path: 'history', component: HistoryComponent },
  { path: 'banned', component: BannedUsersComponent },
  {
    path: '**',
    redirectTo: '',
  },
];
