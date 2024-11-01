import { Component, OnInit } from "@angular/core";
import { Subject, takeUntil } from 'rxjs';
import { ErrorHandlerService } from '../../../services/error-handler.service';
import { Page } from '../../../model/page.model';
import { ProjectService } from "../../../services/project.service";
import { Project } from "../../../model/project.model";

@Component({
  selector: 'app-project-listing',
  templateUrl: './project-listing.component.html',
  styleUrl: './project-listing.component.scss'
})
export class ProjectListingComponent implements OnInit {

  private _destroy$: Subject<any> = new Subject<any>();

  error: any;
  projects: Page<Project>;

  constructor(private projectService: ProjectService, 
    private errorService: ErrorHandlerService) { }
  
  ngOnInit(): void {
    this.findAllProjects();
  }

  findAllProjects(pageNumber: number = 0): void {
    this.projectService.findAll(pageNumber, 9)
      .pipe(takeUntil(this._destroy$))
        .subscribe({
          next: (response: Page<Project>) => {
            this.projects = response;
            console.log(response)
          },
          error: (err) => {
            this.error = this.errorService.getError(err);
          }
        });
  }

}
