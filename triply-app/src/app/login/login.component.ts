import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [MatCardModule],
  template: `
    <h2>Login</h2>
    <p>Login to your account!</p>
  `,
})
export class LoginComponent {}