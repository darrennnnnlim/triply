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
    });
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      return;
    }
    this.isLoading = true;
    this.errorMessage = '';

    const { username, password, role } = this.loginForm.value;
    this.authService.login({ username, password, role }).subscribe({
      next: () => {
        this.activeModal.close('Login successful');
        this.router.navigate(['/']);
        this.isLoading = false;
      },
      error: (err) => {
        console.log(err);
        this.errorMessage =
          err.error?.message || 'Invalid username or password';
        this.isLoading = false;
      },
    });
  }
}
