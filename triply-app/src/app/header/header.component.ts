import { Component, ElementRef, HostListener, ViewChild } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { LoginComponent } from '../auth/login/login.component';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
  standalone: false,
})
export class HeaderComponent {
  isHamburgerMenuVisible = false;

  @ViewChild('hamburgerMenu', { static: false }) hamburgerMenu!: ElementRef;
  @ViewChild('hamburgerButton', { static: false }) hamburgerButton!: ElementRef;

  constructor(private modalService: NgbModal) {}

  openLoginModal(): void {
    // Open LoginComponent as the modal content.
    this.modalService.open(LoginComponent, {
      ariaLabelledBy: 'modal-basic-title',
    });
  }

  toggleHamburgerMenu(): void {
    this.isHamburgerMenuVisible = !this.isHamburgerMenuVisible;
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    if (!this.isHamburgerMenuVisible) {
      return;
    }
    const target = event.target as HTMLElement;
    const clickedInsideMenu =
      this.hamburgerMenu?.nativeElement.contains(target);
    const clickedHamburgerButton =
      this.hamburgerButton?.nativeElement.contains(target);
    if (!clickedInsideMenu && !clickedHamburgerButton) {
      this.isHamburgerMenuVisible = false;
    }
  }
}
