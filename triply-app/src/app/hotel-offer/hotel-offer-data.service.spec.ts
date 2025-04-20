import { TestBed } from '@angular/core/testing';

import { HotelOfferDataService } from './hotel-offer-data.service';

describe('HotelOfferDataService', () => {
  let service: HotelOfferDataService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HotelOfferDataService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
