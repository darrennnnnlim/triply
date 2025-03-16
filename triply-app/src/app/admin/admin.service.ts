import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserRoleDTO } from './user.dto';
import { tap } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class AdminService {
  private readonly API_URL = 'http://localhost:8080/api/v1/admin';

  constructor(private http: HttpClient) {}

  test(): Observable<any> {
    return this.http.get(`${this.API_URL}/test`, {
      withCredentials: true,
      responseType: 'text',
    });
  }

  postTest(): Observable<any> {
    return this.http.post(
      `${this.API_URL}/test`,
      {},
      {
        withCredentials: true,
        responseType: 'text',
      }
    );
  }

  getUsersWithRoles(): Observable<UserRoleDTO[]> {
    return this.http.get<UserRoleDTO[]>(`${this.API_URL}/users`, {
      withCredentials: true,
    });
  }

  getAdmins(): Observable<UserRoleDTO[]> {
    return this.http.get<UserRoleDTO[]>(`${this.API_URL}/admins`, {
      withCredentials: true,
    });
  }

  getCurrentUser(): Observable<string> {
    return this.http.get(`${this.API_URL}/currentuser`, {
      withCredentials: true,
      responseType: 'text',
    });
  }

  banUser(userId: number): Observable<string> {
    return this.http.post(
      `${this.API_URL}/ban/${userId}`,
      {},
      {
        withCredentials: true,
        responseType: 'text',
      }
    );
  }

  unbanUser(userId: number): Observable<string> {
    return this.http.post(
      `${this.API_URL}/unban/${userId}`,
      {},
      {
        withCredentials: true,
        responseType: 'text',
      }
    );
  }

  getBannedUsers(): Observable<UserRoleDTO[]> {
    return this.http
      .get<UserRoleDTO[]>(`${this.API_URL}/isBanned`, {
        withCredentials: true,
      });
  }
  
  
}
