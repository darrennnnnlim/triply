import { Component, OnInit } from '@angular/core';
import { AdminService } from './admin.service';
import { UserRoleDTO } from './user.dto';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-banned',
  templateUrl: './banned.component.html',
  styleUrls: ['./banned.component.css'],
  standalone: true,
  imports: [CommonModule]
})
export class BannedComponent implements OnInit {
  bannedUsers: UserRoleDTO[] = [];

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.fetchBannedUsers();
  }

  fetchBannedUsers(): void {
    this.adminService.getBannedUsers().subscribe({
      next: (users) => {
        // console.log('Fetched banned users:', users);
        this.bannedUsers = users;
      },
      error: (error) => {
        console.error('Error fetching banned users:', error);
      }
    });
  }
  
}
