import { Component } from '@angular/core';
import { AdminService } from './admin.service';
import { UserRoleDTO } from './user.dto';
import { EventEmitter, Input, Output } from '@angular/core';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { AuthService } from '../auth/auth.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css'],
  standalone: false,
})
export class AdminComponent {
  authState$!: Observable<{
    isLoggedIn: boolean;
    username?: string;
    role?: string;
  }>;

  constructor(
    private adminService: AdminService,
    private router: Router,
    public authService: AuthService
  ) {
    this.authState$ = this.authService.authState$ as Observable<{
      isLoggedIn: boolean;
      username?: string;
      role?: string;
    }>;
  }

  response: string | null = null;
  errorMessage: string | null = null;
  usersWithRoles: UserRoleDTO[] = [];
  isDialogOpen = false;
  userToBanId: number | null = null;
  userToBan: string | null = null;
  isAdmin = false;
  currentUsername: string | null = null;
  bannedUsers: UserRoleDTO[] = [];
  showBannedUsers = false;
  userToAction = '';
  userIdToAction: number | null = null;
  dialogAction: 'ban' | 'unban' | 'promote' | 'demote' = 'ban';
  searchUsername: string = '';
  currentUserId: number | null = null;

  ngOnInit(): void {
    this.authService.initAuthStateFromBackend();
    this.authState$.subscribe((authState) => {
      // console.log('Auth state:', authState);
    });
    this.adminService.getCurrentUser().subscribe({
      next: (data) => {
        // console.log('Data:', data);
        const newdata = JSON.parse(data);
        this.currentUsername = newdata.username;
        // Fetch all users with roles
        this.adminService.getUsersWithRoles().subscribe({
          next: (data) => {
            this.usersWithRoles = data;
            // Find the current user's ID
            this.currentUserId =
              this.usersWithRoles.find(
                (user) => user.username === this.currentUsername
              )?.id ?? null;

            // Check if the current user is an admin
            this.isAdmin = this.usersWithRoles.some(
              (user) =>
                user.username === this.currentUsername &&
                user.roleName === 'ROLE_ADMIN'
            );
            if (!this.isAdmin) {
              this.router.navigate(['/home']);
            }
          },
          error: (error) => {
            console.error('Error fetching users with roles:', error);
            this.router.navigate(['/home']);
          },
        });
      },
      error: (error) => {
        console.error('Error fetching current user:', error);
        this.router.navigate(['/login']);
      },
    });
  }

  promoteUser(userId: number): void {
    this.adminService.promoteUser(userId).subscribe({
      next: () => this.fetchUsersWithRoles(),
      error: (error) => console.error(`Error promoting user:`, error),
    });
  }

  demoteUser(userId: number): void {
    this.adminService.demoteUser(userId).subscribe({
      next: () => this.fetchUsersWithRoles(),
      error: (error) => console.error(`Error demoting user:`, error),
    });
  }

  searchUsers() {
    if (this.showBannedUsers) {
      this.adminService
        .searchBannedUsersByUsername(this.searchUsername)
        .subscribe({
          next: (data) => (this.bannedUsers = data),
          error: (err) => console.error(err),
        });
    } else {
      this.adminService.searchUsersByUsername(this.searchUsername).subscribe({
        next: (data) => (this.usersWithRoles = data),
        error: (err) => console.error(err),
      });
    }
  }

  clearSearch() {
    this.searchUsername = '';
    this.loadUsers();
  }

  toggleView() {
    this.showBannedUsers = !this.showBannedUsers;
    if (this.showBannedUsers) {
      this.router.navigate(['admin/banned']);
    } else {
      this.router.navigate(['admin']);
    }
  }

  banUser(userId: number): void {
    this.adminService.banUser(userId).subscribe({
      next: () => {
        // console.log(`User with ID ${userId} has been banned.`);
        this.fetchUsersWithRoles();
      },
      error: (error) => {
        console.error(`Error banning user with ID ${userId}:`, error);
      },
    });
  }

  cancelAction() {
    this.isDialogOpen = false;
    this.userIdToAction = null;
  }

  unbanUser(userId: number): void {
    this.adminService.unbanUser(userId).subscribe({
      next: () => {
        // console.log(`User with ID ${userId} has been unbanned.`);
        this.fetchUsersWithRoles();
      },
      error: (error) => {
        console.error(`Error unbanning user with ID ${userId}:`, error);
      },
    });
  }

  loadUsers() {
    this.adminService.getUsersWithRoles().subscribe({
      next: (data) => {
        this.usersWithRoles = data;
      },
      error: (error) => {
        console.error('Error fetching users with roles:', error);
      },
    });
  }

  fetchUsersWithRoles(): void {
    this.adminService.getUsersWithRoles().subscribe({
      next: (users) => {
        this.usersWithRoles = users;
      },
      error: (error) => {
        console.error('Error fetching users:', error);
      },
    });
  }

  confirmAction(): void {
    if (this.userIdToAction !== null) {
      if (this.dialogAction === 'ban') {
        this.banUser(this.userIdToAction);
      } else if (this.dialogAction === 'unban') {
        this.unbanUser(this.userIdToAction);
      } else if (this.dialogAction === 'promote') {
        this.promoteUser(this.userIdToAction);
      } else if (this.dialogAction === 'demote') {
        this.demoteUser(this.userIdToAction);
      }
    }
    this.isDialogOpen = false;
  }

  openActionDialog(
    userId: number,
    username: string,
    action: 'ban' | 'unban' | 'promote' | 'demote'
  ): void {
    this.userIdToAction = userId;
    this.userToAction = username;
    this.isDialogOpen = true;
    this.dialogAction = action;
  }
}
