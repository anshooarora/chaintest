import { Component, OnDestroy, OnInit } from "@angular/core";
import { Subject, takeUntil } from 'rxjs';
import { TestService } from '../../../services/test.service';
import { Test } from '../../../model/test.model';
import { Page } from '../../../model/page.model';
import { ErrorHandlerService } from "../../../services/error-handler.service";

@Component({
  selector: 'app-test-listing',
  templateUrl: './test-listing.component.html',
  styleUrl: './test-listing.component.scss'
})
export class TestListingComponent implements OnInit, OnDestroy {

  private _destroy$: Subject<any> = new Subject<any>();

  error: any;
  page: Page<Test>;

  constructor(private testService: TestService, private errorService: ErrorHandlerService) { }

  ngOnInit(): void {
    this.findAll();
  }

  ngOnDestroy(): void {
    this._destroy$.unsubscribe();
    this._destroy$.complete();
  }

  findAll(pageNumber: number = 0): void {
    this.testService.search(undefined, undefined, 15, 0)
      .pipe(takeUntil(this._destroy$))
      .subscribe({
        next: (response: Page<Test>) => {
          this.page = response;
          console.log(response)
        },
        error: (err) => {
          this.error = this.errorService.getError(err);
        }
      });
  }

}
