import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PopupComponent } from '../popup/popup.component';
import { BanedUserRating } from './banned-ratings.service';
import moment from 'moment';
import { catchError, EMPTY, map, Observable, switchMap } from 'rxjs';
import { Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { AdminService } from '../admin/admin.service';
import { UserRoleDTO } from '../admin/user.dto';

@Component({
  selector: 'app-banned-ratings',
  imports: [CommonModule, PopupComponent],
  templateUrl: './banned-ratings.component.html',
  styleUrl: './banned-ratings.component.css'
})
export class BannedRatingsComponent {
  authState$!: Observable<{
    isLoggedIn: boolean;
    username?: string;
    role?: string;
  }>;

  isAdmin = false;
  currentUsername: string | null = null;
  usersWithRoles: UserRoleDTO[] = [];
  currentUserId: number | null = null;

  showDeleted = true;
  banned: any = [];
  active: any = [];
  counter: number = 1;
  isPopup = false;
  popupMsg = "";


  constructor(
    private ratingService: BanedUserRating, 
    private adminService: AdminService,
    private router: Router,
    public authService: AuthService) {
    this.authState$ = this.authService.authState$ as Observable<{
      isLoggedIn: boolean;
      username?: string;
      role?: string;
    }>;
  }

     ngOnInit(): void {
      // this.loadBookingData();
      this.fetchUser().subscribe({
        next: () => {
          this.loadBookingData();
        },
        error: (error: any) => {
          console.error('Failed to fetch user:', error);
        }
      });
    }
    
    fetchUser() {
      this.authService.initAuthStateFromBackend();
    
      return this.authState$.pipe(
        switchMap((authState) => {
          console.log('Auth state:', authState);
          
          return this.adminService.getCurrentUser(); 
        }),
        switchMap((data) => {
          console.log('Data:', data);
          const newdata = JSON.parse(data);
          this.currentUsername = newdata.username;
    
          return this.adminService.getUsersWithRoles();
        }),
        map((usersWithRoles) => {
          this.usersWithRoles = usersWithRoles;
    
          this.currentUserId = this.usersWithRoles.find(
            (user) => user.username === this.currentUsername
          )?.id ?? null;
    
          this.isAdmin = this.usersWithRoles.some(
            (user) =>
              user.username === this.currentUsername &&
              user.roleName === 'ROLE_ADMIN'
          );
    
          if (!this.isAdmin) {
            this.router.navigate(['/home']);
          }
        }),
        catchError((error) => {
          console.error('Error fetching current user or users with roles:', error);
          this.router.navigate(['/']);
          return EMPTY; 
        })
      );
    }

  banORunban(user: string, userid: number, flightHotelId: number, type: string, action: string) {
    this.banned = this.banned.filter((user: { id: number; }) => user.id !== userid);
    this.isPopup = !this.isPopup;
    if (type == "Flight") {
      this.updateRating(userid, flightHotelId, null);
    }
    else {
      this.updateRating(userid, null, flightHotelId);
    }

    console.log(action)
    if (action == "ban") {
      this.popupMsg =  user + " has been banned";
    }
    else {
      this.popupMsg = user + " has been unbanned";
    }

  }

  handlePopupClose(): void {
    console.log('Popup closed');
    this.isPopup = false;
    this.showDeleted = true;
    this.banned = [];
    this.active = [];
    this.counter = 1;
    this.isPopup = false;
    this.popupMsg = "";
    this.loadBookingData();
  }

  loadBookingData(): void {
    this.ratingService.getAllRatings().subscribe(allRatings => {
      const flightRatings = allRatings.filter((rating: { flightId: number }) => rating.flightId);
      const hotelRatings = allRatings.filter((rating: { hotelId: number }) => rating.hotelId);

      const flightIds = [...flightRatings.map((rating: any) => rating.flightId)] as number[];
      const hotelIds = [...hotelRatings.map((rating: any) => rating.hotelId)] as number[];
      
      const userIds = [
        ...new Set([
          ...flightRatings.map((rating: any) => rating.userId),
          ...hotelRatings.map((rating: any) => rating.userId),
        ]),
      ];
  
      const flightDetailsPromises = flightIds.map((flightId: number) =>
        this.ratingService.getFlightDetails(flightId).toPromise()
      );
      const hotelDetailsPromises = hotelIds.map((hotelId: number) =>
        this.ratingService.getHotelDetails(hotelId).toPromise()
      );
  
      const userDetailsPromises = userIds.map((userId: number) =>
        this.ratingService.getUser(userId).toPromise() 
      );
      console.log(userDetailsPromises);
  
      Promise.all([...flightDetailsPromises, ...hotelDetailsPromises, ...userDetailsPromises])
        .then(responses => {
          const [flightDetailsResponses, hotelDetailsResponses, ...userDetailsResponses] = [
            responses.slice(0, flightDetailsPromises.length),
            responses.slice(flightDetailsPromises.length, flightDetailsPromises.length + hotelDetailsPromises.length),
            ...responses.slice(flightDetailsPromises.length + hotelDetailsPromises.length),
          ];
          
          const userDetailsMap = new Map<number, any>();
          userDetailsResponses.forEach((data: any) => {
            const newdata = JSON.parse(data);
            const username = newdata.username;
            console.log(username)
            userDetailsMap.set(newdata.id, username); 
          });
  
          const flightResults = flightRatings.map((rating: { rating: number, userId: number, delete: string }, index: number) => {
            const flightDetails = flightDetailsResponses[index];
            const user = userDetailsMap.get(rating.userId);
            const rateType = rating.rating ? 'exist' : 'new';
            console.log(user)
            return {
              flightDetails,
              user, 
              delete: rating.delete,
              rating: rating.rating,
              userId:rating.userId,
              rateType,
              type: 'Flight',
            };
          });
  
          const hotelResults = hotelRatings.map((rating: { rating: number, userId: number, delete: string }, index: number) => {
            const hotelDetails = hotelDetailsResponses[index];
            const user = userDetailsMap.get(rating.userId);
            const rateType = rating.rating ? 'exist' : 'new';
            console.log(user)
            return {
              hotelDetails,
              user,
              delete: rating.delete,
              rating: rating.rating,
              userId:rating.userId,
              rateType,
              type: 'Hotel',
            };
          });
  
          this.addItemsToList([...flightResults, ...hotelResults]);
        })
        .catch(err => {
          console.error("Error fetching details:", err);
        });
    });
  }
  
  


  addItemsToList(results: any[]): void {
    console.log(results)
    let bannedTemp: any = []
    let activeTemp: any = []
    results.forEach((result: any) => {
      if (result.type === 'Flight' && result.delete == 'T') {
        bannedTemp.push({
          name: result.flightDetails.airline,
          arrivalTime: convertDateTime(result.flightDetails.arrivalTime),
          departureTime: convertDateTime(result.flightDetails.departureTime),
          flightNumber: result.flightDetails.flightNumber,
          seatClass: result.seatClass,
          seatNumber: result.seatNumber,
          flightId: result.flightDetails.id,
          id: this.counter++,
          rating: result.rating || 0,
          rateType: result.rateType,
          type: result.type,
          user: result.user,
          userId: result.userId
        });
      } else if (result.type === 'Hotel' && result.delete == 'T') {
        bannedTemp.push({
          name: result.hotelDetails.name,
          checkInDate: convertDateTime(result.hotelDetails.checkInDate),
          checkOutDate: convertDateTime(result.hotelDetails.checkOutDate),
          hotelId: result.hotelDetails.id,
          id: this.counter++,
          rating: result.rating || 0,
          rateType: result.rateType,
          type: result.type,
          description: result.hotelDetails.description,
          user: result.user,
          userId: result.userId
        });
      }
      else if (result.type === 'Flight' && result.delete == 'F') {
        activeTemp.push({
          name: result.flightDetails.airline,
          arrivalTime: convertDateTime(result.flightDetails.arrivalTime),
          departureTime: convertDateTime(result.flightDetails.departureTime),
          flightNumber: result.flightDetails.flightNumber,
          seatClass: result.seatClass,
          seatNumber: result.seatNumber,
          flightId: result.flightDetails.id,
          id: this.counter++,
          rating: result.rating || 0,
          rateType: result.rateType,
          type: result.type,
          user: result.user,
          userId: result.userId
        });
      }
      else {
        activeTemp.push({
          name: result.hotelDetails.name,
          checkInDate: convertDateTime(result.hotelDetails.checkInDate),
          checkOutDate: convertDateTime(result.hotelDetails.checkOutDate),
          hotelId: result.hotelDetails.id,
          id: this.counter++,
          rating: result.rating || 0,
          rateType: result.rateType,
          type: result.type,
          description: result.hotelDetails.description,
          user: result.user,
          userId: result.userId
        });
      }

    });
    
    this.banned = bannedTemp;
    this.active = activeTemp;
    console.log(this.banned);
    console.log(this.active)
  }

  updateRating(userId: number, flightId: number | null | undefined, hotelId: number | null | undefined): void {
    this.ratingService.putRating(userId, flightId, hotelId).subscribe({
      next: (res: any) => {
        console.log(res)
      },
      error: (err) => {
        console.error(`Error: ${err.status} - ${err.statusText}`);
      },
    });
  }

}
function convertDateTime(timestamp: string): string {
  return moment(timestamp).tz('Asia/Singapore').format('DD/MM/yyyy') + " " + moment(timestamp).tz('Asia/Singapore').format('HH:mm');
}
