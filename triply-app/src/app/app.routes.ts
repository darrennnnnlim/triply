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
    loadChildren: () =>
      import('./home/home.module').then((m) => m.HomeModule),
  },
  {
    path: 'booking',
    loadChildren: () =>
      import('./booking/booking.module').then((m) => m.BookingModule),
  },
  {
      path: 'search',
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
