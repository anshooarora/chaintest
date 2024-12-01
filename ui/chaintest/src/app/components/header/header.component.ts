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
  pageNum: number = 0;

  constructor(private testService: TestService, private errorService: ErrorHandlerService) { }

  openEnd(content: TemplateRef<any>) {
    this.offcanvasService.open(content, { position: 'start', panelClass: 'w-75' });
  }

  search(content: TemplateRef<any>, open: boolean = true, num: number = 0): void {
    if (!this.searchTerm) {
      return;
    }

    if (open) {
      this.openEnd(content);
    }

    this.pageNum = num;
    
    this.testService.search(0, this.searchTerm, 0, -1, '', '', this.searchTerm, this.pageNum, 'OR')
    .pipe(takeUntil(this._destroy$))
    .subscribe({
      next: (tests: Page<Test>) => {
        if (this.pageNum == 0) {
          this.page = tests;
        } else {
          this.page.content.push(...tests.content);
        }
        console.log(this.page)
      },
      error: (err) => {
        this.error = this.errorService.getError(err);
      }
    });
  }

}
