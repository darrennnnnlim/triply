import { Component } from '@angular/core';
import { AdminService } from './admin.service';
import { UserRoleDTO } from './user.dto';
import { EventEmitter, Input, Output } from '@angular/core';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css'],
  standalone: false,
})
export class AdminComponent {
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
  dialogAction: 'ban' | 'unban' = 'ban';

  constructor(private adminService: AdminService, private router: Router) {}
  private readonly API_URL = 'http://localhost:8080/api/v1/admin';

  ngOnInit(): void {
    this.adminService.getCurrentUser().subscribe({
      next: (username) => {
        this.currentUsername = username;
        // console.log('Current username:', this.currentUsername);

        // Fetch all users with roles
        this.adminService.getUsersWithRoles().subscribe({
          next: (data) => {
            // console.log('Users with roles:', data);
            this.usersWithRoles = data;

            // Check if the current user is an admin
            this.isAdmin = this.usersWithRoles.some(
              (user) =>
                user.username === this.currentUsername &&
                user.roleName === 'ROLE_ADMIN'
            );
            // console.log('Is admin?', this.isAdmin);

            if (!this.isAdmin) {
              this.router.navigate(['/home']);
            }
          },
          error: (error) => {
            console.error('Error fetching users with roles:', error);
          },
        });
      },
      error: (error) => {
        console.error('Error fetching current user:', error);
      },
    });
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

  openActionDialog(
    userId: number,
    username: string,
    action: 'ban' | 'unban'
  ): void {
    this.userIdToAction = userId;
    this.userToAction = username;
    this.isDialogOpen = true;
    this.dialogAction = action;
  }

  cancelAction() {
    this.isDialogOpen = false;
    this.userIdToAction = null;
  }

  confirmAction(): void {
    if (this.userIdToAction !== null) {
      if (this.dialogAction === 'ban') {
        this.banUser(this.userIdToAction);
      } else if (this.dialogAction === 'unban') {
        this.unbanUser(this.userIdToAction);
      }
    }
    this.isDialogOpen = false;
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
}
