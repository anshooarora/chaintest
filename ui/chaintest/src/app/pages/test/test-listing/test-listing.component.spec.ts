import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TestListingComponent } from './test-listing.component';

describe('TestListingComponent', () => {
  let component: TestListingComponent;
  let fixture: ComponentFixture<TestListingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TestListingComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(TestListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
