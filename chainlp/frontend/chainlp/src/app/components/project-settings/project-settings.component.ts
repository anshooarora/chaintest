import { Component, inject, OnDestroy, OnInit, TemplateRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbOffcanvas } from '@ng-bootstrap/ng-bootstrap';
import { Subject, takeUntil } from 'rxjs';
import { ProjectService } from '../../services/project.service';
import { Project } from '../../model/project.model';
import { ErrorHandlerService } from '../../services/error-handler.service';

@Component({
  selector: 'app-project-settings',
  templateUrl: './project-settings.component.html',
  styleUrl: './project-settings.component.scss'
})
export class ProjectSettingsComponent implements OnInit, OnDestroy {

  private offcanvasService = inject(NgbOffcanvas);
  private _destroy$: Subject<any> = new Subject<any>();

  error: any;
  project: Project;
  isSaved: boolean = false;

  constructor(private route: ActivatedRoute,
    private projectService: ProjectService,
    private errorService: ErrorHandlerService) { }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('projectId');
    this.findProject(id);
  }

  ngOnDestroy(): void {
    this._destroy$.next(null);
    this._destroy$.complete();
  }

  findProject(id: any): void {
    this.projectService.find(id)
    .pipe(takeUntil(this._destroy$))
    .subscribe({
      next: (project: Project) => {
        this.project = project;
      },
      error: (err) => {
        this.error = this.errorService.getError(err);
      }
    });
  }

	open(content: TemplateRef<any>) {
		this.offcanvasService.open(content, { position: 'end', panelClass: 'w-50' }).result.then(
			(result) => {
				
			},
			(reason) => {
				
			},
		);
	}

  save() {
    this.projectService.update(this.project)
    .pipe(takeUntil(this._destroy$))
    .subscribe({
      next: (project: Project) => {
        this.project = project;
        this.isSaved = true;
        setTimeout(() => {
          this.isSaved = false;
        }, 2000);
      },
      error: (err) => {
        this.error = this.errorService.getError(err);
      }
    });
  }

}
