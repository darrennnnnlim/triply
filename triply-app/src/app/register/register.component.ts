import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [MatCardModule], // Import MatCardModule if using Material components
  template: `
    <mat-card>
      <h2>Register</h2>
      <p>Register for an account!</p>
    </mat-card>
  `,
})
export class RegisterComponent {}