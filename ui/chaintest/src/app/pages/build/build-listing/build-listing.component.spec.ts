import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BuildListingComponent } from './build-listing.component';

describe('BuildListingComponent', () => {
  let component: BuildListingComponent;
  let fixture: ComponentFixture<BuildListingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BuildListingComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(BuildListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
