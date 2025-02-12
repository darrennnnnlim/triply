import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HomeService } from './home.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  standalone: false
})
export class HomeComponent {
  response: string | null = null;
  errorMessage: string | null = null;

  constructor(private homeService: HomeService) {}

  testApi(): void {
    this.homeService.test().subscribe({
      next: (res: any) => {
        this.response = res;
        this.errorMessage = null;
      },
      error: (err) => {
        this.errorMessage = `Error: ${err.status} - ${err.statusText}`;
        console.error(err);
      }
    });
  }

  private getCsrfToken(): string | null {
    const match = document.cookie.match(/XSRF-TOKEN=([^;]+)/);
    return match ? decodeURIComponent(match[1]) : null;
  }
}
