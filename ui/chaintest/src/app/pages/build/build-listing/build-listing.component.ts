import { Component, OnInit } from '@angular/core';
import { Subject, takeUntil } from 'rxjs';
import { BuildService } from '../../../services/build.service';
import { ErrorHandlerService } from '../../../services/error-handler.service';
import { Page } from '../../../model/page.model';
import { Build } from '../../../model/build.model';

@Component({
  selector: 'app-build-listing',
  templateUrl: './build-listing.component.html',
  styleUrl: './build-listing.component.scss'
})
export class BuildListingComponent implements OnInit {

  private _destroy$: Subject<any> = new Subject<any>();

  error: any;
  tagDisplayLimit: number = 5;
  builds: Page<Build>;

  constructor(private _buildService: BuildService,
    private _errorService: ErrorHandlerService) { }

  ngOnInit(): void {
    this.findBuilds();
  }

  ngOnDestroy(): void {
    this._destroy$.next(null);
    this._destroy$.complete();
  }

  findBuilds(page: number = 0, pageSize: number = 5): void {
    this._buildService.findAll(page, pageSize)
      .pipe(takeUntil(this._destroy$))
      .subscribe({
        next: (builds: Page<Build>) => {
          this.builds = builds;
          console.log(builds)
        },
        error: (err) => {
          this.error = this._errorService.getError(err);
        }
      });
  }

}
