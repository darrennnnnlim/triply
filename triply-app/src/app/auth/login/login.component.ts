import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  standalone: false,
})
export class LoginComponent {
  loginForm: FormGroup;
  errorMessage: string = '';
  isLoading: boolean = false;
  isLoginMode: boolean = true; // true = login, false = register

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    public activeModal: NgbActiveModal
  ) {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required],
      role: ['USER', Validators.required],
      confirmPassword: [''], // will only be required in register mode
    });
  }

  switchMode(): void {
    this.isLoginMode = !this.isLoginMode;
    this.errorMessage = '';
    if (this.isLoginMode) {
      this.loginForm.get('confirmPassword')?.clearValidators();
    } else {
      this.loginForm
        .get('confirmPassword')
        ?.setValidators([Validators.required]);
    }
    this.loginForm.get('confirmPassword')?.updateValueAndValidity();
  }

  onSubmit(): void {
    if (this.loginForm.invalid) return;

    this.isLoading = true;
    this.errorMessage = '';

    const { username, password, role, confirmPassword } = this.loginForm.value;

    if (!this.isLoginMode && password !== confirmPassword) {
      this.errorMessage = 'Passwords do not match';
      this.isLoading = false;
      return;
    }

    if (this.isLoginMode) {
      this.authService.login({ username, password, role }).subscribe({
        next: () => {
          this.activeModal.close('Login successful');
          this.router.navigate(['/']);
          this.isLoading = false;
        },
        error: (err) => {
          console.log(err);
          this.errorMessage = err.error?.message || 'Invalid login';
          this.isLoading = false;
        },
      });
    } else {
      this.authService.register({ username, password }).subscribe({
        next: () => {
          this.activeModal.close('Registration successful');
          this.router.navigate(['/']);
          this.isLoading = false;
        },
        error: (err) => {
          console.log(err);
          this.errorMessage = err.error?.message || 'Registration failed';
          this.isLoading = false;
        },
      });
    }
  }
}
