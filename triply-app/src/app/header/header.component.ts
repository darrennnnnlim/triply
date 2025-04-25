import { Component, ElementRef, HostListener, ViewChild } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { LoginComponent } from '../auth/login/login.component';
import { AuthService } from '../auth/auth.service';
import { User } from '../auth/auth.model';
import { Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
  standalone: false,
})
export class HeaderComponent {
  isHamburgerMenuVisible = false;
  isLoggedIn = false;
  userInitials = '';
  private destroy$ = new Subject<void>();

  @ViewChild('hamburgerMenu', { static: false }) hamburgerMenu!: ElementRef;
  @ViewChild('hamburgerButton', { static: false }) hamburgerButton!: ElementRef;

  constructor(
    private modalService: NgbModal,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.authService.authState$
      .pipe(takeUntil(this.destroy$))
      .subscribe((state) => {
        this.isLoggedIn = state.isLoggedIn;
        this.userInitials = state.username
          ? this.getUserInitials(state.username)
          : '';
      });

    // Single initial check
    this.authService.checkSession().subscribe();
  }

  getUserInitials(name: string): string {
    const names = name.split(' ');
    let initials = '';
    names.forEach((n) => {
      if (n && n.length > 0) {
        initials += n.charAt(0);
      }
    });
    return initials.toUpperCase();
  }

  openLoginModal(isLoginMode: boolean = true): void {
    // Open LoginComponent as the modal content.
    const modalRef = this.modalService.open(LoginComponent, {
      ariaLabelledBy: 'modal-basic-title',
    });
    modalRef.componentInstance.isLoginMode = isLoginMode;
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

  logout(): void {
    this.authService.logout().subscribe({
      next: () => {
        this.isLoggedIn = false;
        this.userInitials = '';
        this.router.navigate(['/']);
      },
      error: (error) => {
        console.error('Logout failed', error);
      },
    });
  }
}
