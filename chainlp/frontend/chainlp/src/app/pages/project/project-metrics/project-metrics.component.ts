import { Component, inject, OnDestroy, OnInit, TemplateRef } from '@angular/core';
import { Subject, takeUntil } from 'rxjs';
import { ActivatedRoute, Router, RouterEvent, RoutesRecognized } from '@angular/router';
import { NgbOffcanvas } from '@ng-bootstrap/ng-bootstrap';
import { TestService } from '../../../services/test.service';
import { Page } from '../../../model/page.model';
import { Test } from '../../../model/test.model';
import { ErrorHandlerService } from '../../../services/error-handler.service';
import { PrettyTimePipe } from "../../../pipes/pretty-time.pipe";
import { BuildService } from '../../../services/build.service';
import { Build } from '../../../model/build.model';

@Component({
  selector: 'app-project-metrics',
  templateUrl: './project-metrics.component.html',
  styleUrl: './project-metrics.component.scss',
  providers: [PrettyTimePipe]
})
export class ProjectMetricsComponent implements OnInit, OnDestroy {

  private _builds$: Subject<any> = new Subject<any>();
  private _failingTests$: Subject<any> = new Subject<any>();
  private offcanvasService = inject(NgbOffcanvas);
  
  projectId: number = 0;
  buildPage: Page<Build>;
  failingTestsPage: Page<Test>;
  selectedTest: Test;
  error: any;

  constructor(private route: ActivatedRoute,
    private buildService: BuildService, 
    private testService: TestService, 
    private errorService: ErrorHandlerService) {
  }

  ngOnInit(): void {
    this.projectId = this.route.snapshot.params['projectId'];
    this.findBuilds();
    this.findFailingTests();
  }

  ngOnDestroy(): void {
    this._builds$.next(null);
    this._builds$.complete();
    this._failingTests$.next(null);
    this._failingTests$.complete();
  }

  findBuilds(page: number = 0, pageSize: number = 20): void {
    this.buildService.findByProjectId(this.projectId, page, pageSize, 'id,desc')
    .pipe(takeUntil(this._builds$))
    .subscribe({
      next: (buildPage: Page<Build>) => {
        this.buildPage = buildPage;
        console.log(this.buildPage)
      },
      error: (err) => {
        this.error = this.errorService.getError(err);
      }
    });
  }

  findFailingTests(pageNum: number = 0): void {
    this.testService.search(0, '', this.projectId, -1, 0, 'FAILED', '', '', pageNum)
    .pipe(takeUntil(this._failingTests$))
    .subscribe({
      next: (page: Page<Test>) => {
        this.failingTestsPage = page;
        console.log(page)
      },
      error: (err) => {
        this.error = this.errorService.getError(err);
      }
    });
  }

  openEnd(content: TemplateRef<any>) {
    this.offcanvasService.open(content, { position: 'start', panelClass: 'w-75' });
  }

}
