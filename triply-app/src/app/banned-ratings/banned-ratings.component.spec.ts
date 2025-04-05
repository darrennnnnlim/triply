import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BannedRatingsComponent } from './banned-ratings.component';

describe('BannedUsersComponent', () => {
  let component: BannedRatingsComponent;
  let fixture: ComponentFixture<BannedRatingsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BannedRatingsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BannedRatingsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
