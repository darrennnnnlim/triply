import { Routes } from '@angular/router';
import { AppComponent } from './app.component';
import { FlightComponent } from './flight/flight.component';

export const routes: Routes = [
    { path: '', component: AppComponent },  
    { path: 'flight', component: FlightComponent }, 
];
