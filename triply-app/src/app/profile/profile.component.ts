import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
  standalone: false,
})
export class ProfileComponent {
  showResetPassword = false;
  currentPassword = '';
  newPassword = '';
  confirmPassword = '';
  resetError = '';
  resetSuccess = '';

  private readonly API_URL = environment.apiUrl + '/auth/reset-password';

  constructor(private http: HttpClient) {}

  resetPassword() {
    if (!this.currentPassword || !this.newPassword || !this.confirmPassword) {
      this.resetError = "All password fields are required.";
      return;
    }
    if (this.newPassword !== this.confirmPassword) {
      this.resetError = "Passwords do not match";
      return;
    }
    this.resetError = '';
    this.resetSuccess = '';
    this.http.post(this.API_URL, {
        currentPassword: this.currentPassword,
        newPassword: this.newPassword
      }, {
        withCredentials: true
      })
      .subscribe({
        next: () => this.resetSuccess = "Password updated successfully!",
        error: err => this.resetError = err.error?.message || err.error || "Error resetting password"
      });
  }
  }
