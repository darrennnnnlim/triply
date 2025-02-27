import { TestBed } from '@angular/core/testing';

import { FlightOfferDataService } from './flight-offer-data.service';

describe('FlightOfferDataService', () => {
  let service: FlightOfferDataService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FlightOfferDataService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
