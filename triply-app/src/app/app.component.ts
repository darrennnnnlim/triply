import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderModule } from './header/header.module';
import { CommonModule } from '@angular/common';
import { FooterModule } from './footer/footer.module';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CommonModule, HeaderModule, FooterModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  title = 'triply-app';  
}
