import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule, Routes } from '@angular/router';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations'; // Import this!

import { MatButtonModule } from '@angular/material/button'; // Import MatButtonModule
import { MatCardModule } from '@angular/material/card'; // Import MatCardModule
import { MatToolbarModule } from '@angular/material/toolbar'; // Import MatToolbarModule (optional)

import { AppComponent } from './app.component';
import { RegisterComponent } from './register/register.component';
import { LoginComponent } from './login/login.component';

const appRoutes: Routes = [
  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginComponent },
];

@NgModule({
  declarations: [
    AppComponent,
    RegisterComponent,
    LoginComponent
  ],
  imports: [
    BrowserModule,
    RouterModule.forRoot(appRoutes),
    BrowserAnimationsModule, // Add BrowserAnimationsModule
    MatButtonModule, // Add MatButtonModule to imports
    MatCardModule, // Add MatCardModule to imports
    MatToolbarModule // Add MatToolbarModule to imports (optional)
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }