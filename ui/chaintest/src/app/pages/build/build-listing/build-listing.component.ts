import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { ChartData, ChartOptions, LegendItem } from 'chart.js';
import { BuildService } from '../../../services/build.service';
import { ErrorHandlerService } from '../../../services/error-handler.service';
import { Page } from '../../../model/page.model';
import { Build } from '../../../model/build.model';
import { TagStats } from '../../../model/tag-stats.model';
import { BaseChartDirective } from 'ng2-charts';

@Component({
  selector: 'app-build-listing',
  templateUrl: './build-listing.component.html',
  styleUrl: './build-listing.component.scss'
})
export class BuildListingComponent implements OnInit, OnDestroy {

  @ViewChild(BaseChartDirective) chart?: BaseChartDirective;
  
  private _destroy$: Subject<any> = new Subject<any>();
  private _destroyDel$: Subject<any> = new Subject<any>();

  projectId: number;

  error: any;
  tagDisplayLimit: number = 5;
  builds: Page<Build>;
  selectedBuild: Build;
  tagstats: TagStats[];
  pageNum: number = 0;

  /* build tests chart */
  chartType: any = 'doughnut';
  statsTitle: string = 'Tests';
  stats: ChartData<any> = {
    labels: ['Passed', 'Failed', 'Skipped'],
    datasets: [
      {
        data: [],
        backgroundColor: ['rgb(89, 199, 125)', 'rgb(240, 105, 132)', 'rgb(233, 210, 113)']
      }
    ]
  };
  options: ChartOptions<any> = {
    responsive: true,
    plugins: {
      legend: {
        position: 'right',
        labels: {
          boxWidth: 15,
          filter: (item: LegendItem, chartData: ChartData<any>) => {
            const label = item.text.toUpperCase();
            let index = 2;
            label == 'PASSED' && (index = 0);
            label == 'FAILED' && (index = 1);
            return chartData.datasets[0].data[index] != 0;
          }
        }
      }
    }
  };

  constructor(private route: ActivatedRoute,
    private buildService: BuildService,
    private errorService: ErrorHandlerService) { }

  ngOnInit(): void {
    const projectId = this.route.snapshot.paramMap.get('projectId') || '0';
    this.projectId = parseInt(projectId);
    this.findBuilds();
  }

  ngOnDestroy(): void {
    this._destroy$.next(null);
    this._destroy$.complete();
    this._destroyDel$.next(null);
    this._destroyDel$.complete();
  }

  findBuilds(page: number = 0, pageSize: number = 20): void {
    this.buildService.findByProjectId(this.projectId, page, pageSize, 'id,desc')
      .pipe(takeUntil(this._destroy$))
      .subscribe({
        next: (builds: Page<Build>) => {
          if (!this.builds) {
            this.builds = builds;
            this.selectBuild(builds.content[0]);
          } else {
            this.builds.content.push(...builds.content);
            this.builds.first = builds.first;
            this.builds.last = builds.last;
          }
          console.log(this.builds)
        },
        error: (err) => {
          this.error = this.errorService.getError(err);
        }
      });
  }

  loadNextPage(): void {
    this.pageNum++;
    this.findBuilds(this.pageNum);
  }

  selectBuild(build: Build) {
    this.selectedBuild = build;
    this.showSelectedBuildMetrics(build);
  }

  showSelectedBuildMetrics(build: Build) {
    this.tagstats = [];
    this.stats.datasets[0].data = [];
    if (!build.runStats || !build.runStats.length) {
      return;
    }
    let runstats: any;
    if (build.bdd) {
      runstats = build.runStats.filter(x => x.depth == 1);
      this.tagstats = build.tagStats && build.tagStats.filter(x => x.depth == 1);
      this.statsTitle = 'Scenarios';
    } else {
      runstats = build.runStats.filter(x => x.depth == 0);
      this.tagstats = build.tagStats && build.tagStats.filter(x => x.depth == 0);
    }
    this.stats.datasets[0].data.push(runstats[0].passed, runstats[0].failed, runstats[0].skipped);
    this.chart?.update();
  }

  del(build: Build): void {
    this.buildService.delete(build)
    .pipe(takeUntil(this._destroy$))
    .subscribe({
      next: (response: any) => {
        console.log(response)
      },
      error: (err) => {
        this.error = this.errorService.getError(err);
      }
    });
  }

  paginate($event: any) {
    this.findBuilds($event);
  }

}
