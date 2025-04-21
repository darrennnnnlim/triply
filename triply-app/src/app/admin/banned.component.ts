import { Component, OnInit } from '@angular/core';
import { AdminService } from './admin.service';
import { UserRoleDTO } from './user.dto';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-banned',
  templateUrl: './banned.component.html',
  styleUrls: ['./banned.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule],
})

export class BannedComponent implements OnInit {
  bannedUsers: UserRoleDTO[] = [];
  searchUsername: string = '';

  constructor(private adminService: AdminService, private router: Router) {}

  ngOnInit(): void {
    this.fetchBannedUsers();
  }

  toggleView(): void {
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.router.navigate(['/admin']);
    });
  }

  searchUsers(): void {
    this.adminService
      .searchBannedUsersByUsername(this.searchUsername)
      .subscribe({
        next: (users) => (this.bannedUsers = users),
        error: (error) => console.error(error),
      });
  }

  clearSearch(): void {
    this.searchUsername = '';
    this.fetchBannedUsers();
  }

  fetchBannedUsers(): void {
    this.adminService.getBannedUsers().subscribe({
      next: (users) => (this.bannedUsers = users),
      error: (error) => console.error(error),
    });
  }
}
