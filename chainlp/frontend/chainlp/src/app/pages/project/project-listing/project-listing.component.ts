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

  private readonly _destroy$: Subject<any> = new Subject<any>();
  private readonly _build$: Subject<any> = new Subject<any>();
  private readonly _del$: Subject<any> = new Subject<any>();

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
    this._build$.next(null);
    this._build$.complete();
  }

  findAllProjects(pageNumber: number = 0): void {
    this.projectService.findAll(pageNumber, 9)
    .pipe(takeUntil(this._destroy$))
      .subscribe({
        next: (response: Page<Project>) => {
          response.content.sort((a,b) => a.name > b.name ? 1 : -1)
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
        // enable for build executions trend:
        // const archive = this.builds;
        // this.builds = [];
        // this.builds.push(...response.content, ...archive);
      },
      error: (err) => {
        this.error = this.errorService.getError(err);
      }
    });
  }
  
  search(): void {
    const query = this.q.toLowerCase();
    this.projectPage.content.forEach(project => {
      project.display = project.name.toLowerCase().includes(query) ||
        project.builds.content.some(build => 
          build.testRunner.toLowerCase().includes(query) ||
          build.tagStats.some(tag => tag.name.toLowerCase().includes(query))
        );
    });
  }

  del(project: Project): void {
    this.projectService.delete(project)
    .pipe(takeUntil(this._del$))
    .subscribe({
      next: () => {
        this.projectPage.content = this.projectPage.content.filter(x => x.id !== project.id);
      },
      error: (err) => {
        this.error = this.errorService.getError(err);
      }
    });
  }

}
