import { Component, OnDestroy, OnInit } from "@angular/core";
import { Subject, takeUntil } from 'rxjs';
import * as moment from 'moment';
import { ErrorHandlerService } from '../../../services/error-handler.service';
import { Page } from '../../../model/page.model';
import { ProjectService } from "../../../services/project.service";
import { Project } from "../../../model/project.model";
import { BuildService } from "../../../services/build.service";
import { Build } from "../../../model/build.model";

@Component({
  selector: 'app-project-listing',
  templateUrl: './project-listing.component.html',
  styleUrl: './project-listing.component.scss'
})
export class ProjectListingComponent implements OnInit, OnDestroy {

  private _destroy$: Subject<any> = new Subject<any>();
  private _build$: Subject<any> = new Subject<any>();

  moment: any = moment;

  q: string;
  error: any;
  projectPage: Page<Project>;

  constructor(private projectService: ProjectService, 
    private buildService: BuildService,
    private errorService: ErrorHandlerService) { }
  
  ngOnInit(): void {
    this.findAllProjects();
  }

  ngOnDestroy(): void {
    this._destroy$.next(null);
    this._destroy$.complete();
  }

  findAllProjects(pageNumber: number = 0): void {
    this.projectService.findAll(pageNumber, 9)
    .pipe(takeUntil(this._destroy$))
      .subscribe({
        next: (response: Page<Project>) => {
          this.projectPage = response;
          response.content.forEach(project => {
            project.display = true;
            this.findBuilds(project);
          });
        },
        error: (err) => {
          this.error = this.errorService.getError(err);
        }
    });
  }

  findBuilds(project: Project): void {
    this.buildService.findByProjectId(project.id, 0, 15, 'id,desc')
    .pipe(takeUntil(this._build$))
      .subscribe({
        next: (response: Page<Build>) => {
          console.log(response)
          project.builds = response;
        },
        error: (err) => {
          this.error = this.errorService.getError(err);
        }
    });
  }

  search(): void {
    this.projectPage.content.forEach(x => {
      x.display = true;
      let includesTag = false;
      let matchesTestRunner = false;
      if (x.builds.content.length) {
        includesTag = x.builds.content.flatMap(ts => ts.tagStats).some(t => t.name.includes(this.q));
        matchesTestRunner = x.builds.content[0].testRunner.includes(this.q);
      }
      if (!x.name.includes(this.q) && !includesTag && !matchesTestRunner) {
        x.display = false;
      }
    })
  }

}
