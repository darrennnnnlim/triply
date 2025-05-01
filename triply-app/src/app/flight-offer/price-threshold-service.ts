import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { PriceThresholdDialogData } from './price-threshold';
@Injectable({
  providedIn: 'root'
})
export class PriceThresholdService {
  
  private apiUrl = environment.apiUrl + '/priceThreshold'; 

  constructor(private http: HttpClient) { }

  setPriceThreshold(priceThresholdData: PriceThresholdDialogData): Observable<any> {
    return this.http.post<any>(this.apiUrl, priceThresholdData, {withCredentials: true});
  }
}
