import { Component, inject, TemplateRef } from '@angular/core';
import { NgbOffcanvas } from '@ng-bootstrap/ng-bootstrap';
import { TestService } from '../../services/test.service';
import { Subject, takeUntil } from 'rxjs';
import { Test } from '../../model/test.model';
import { Page } from '../../model/page.model';
import { ErrorHandlerService } from '../../services/error-handler.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {

  private offcanvasService = inject(NgbOffcanvas);
  private _destroy$: Subject<any> = new Subject<any>();
    
  searchEnabled: boolean;
  searchTerm: string;
  error: any;
  page: Page<Test>;

  constructor(private testService: TestService, private errorService: ErrorHandlerService) { }

  openEnd(content: TemplateRef<any>) {
    this.offcanvasService.open(content, { position: 'start', panelClass: 'w-75' });
  }

  search(content: TemplateRef<any>): void {
    if (!this.searchTerm) {
      return;
    }

    this.openEnd(content);

    this.testService.search(0, this.searchTerm)
    .pipe(takeUntil(this._destroy$))
    .subscribe({
      next: (tests: Page<Test>) => {
        this.page = tests;
        console.log(tests)
      },
      error: (err) => {
        this.error = this.errorService.getError(err);
      }
    });
  }

}
