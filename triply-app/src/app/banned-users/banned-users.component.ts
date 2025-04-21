import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PopupComponent } from '../popup/popup.component';

@Component({
  selector: 'app-banned-users',
  imports: [CommonModule, PopupComponent],
  templateUrl: './banned-users.component.html',
  styleUrl: './banned-users.component.css'
})
export class BannedUsersComponent {
  bannedUsers = [
    {id: 1, name: "Hello", email: "test@mail.com"},
    {id: 2, name: "kelvin", email: "kelvin@mail.com"}
  ]
  isPopup = false;
  popupMsg = "";
  unbanUser (id: number){
    this.bannedUsers = this.bannedUsers.filter(user => user.id !== id);
    console.log("user with ID: " + id + " has been unbanned");
    this.isPopup = !this.isPopup;
    this.popupMsg = "user with ID: " + id + " has been unbanned";
  }
}
