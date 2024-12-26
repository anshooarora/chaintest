import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { Chart, ChartData, ChartOptions, LegendItem } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';
import AnnotationPlugin from "chartjs-plugin-annotation";
import { BuildService } from '../../../services/build.service';
import { ErrorHandlerService } from '../../../services/error-handler.service';
import { Page } from '../../../model/page.model';
import { Build } from '../../../model/build.model';
import { TagStats } from '../../../model/tag-stats.model';

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
  readonly q: any = {
    result: '',
    dateFrom: '',
    dateTo: ''
  }

  /* build tests chart */
  chartType: any = 'doughnut';
  statsTitle: string = 'Tests';
  stats: ChartData<any> = {
    labels: ['Passed', 'Failed', 'Skipped'],
    datasets: [
      {
        data: [],
        backgroundColor: ['rgb(140, 197, 83)', 'rgb(233,80,113)', 'rgb(221, 91, 96)'],
        borderColor: 'transparent'
      }
    ]
  };

  options: ChartOptions<any> = {
    responsive: true,
    cutout: '65%',
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
      },
      annotation: {
        annotations: [{
          borderWidth: 0,
          label: {
            content: '',
            display: true,
            backgroundColor: 'transparent',
            color: '#aaa'
          }
        }]
      }
    }
  };

  constructor(private route: ActivatedRoute,
    private buildService: BuildService,
    private errorService: ErrorHandlerService) { 
      Chart.register(AnnotationPlugin); 
    }

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

  private getBuildStats(build: Build) {
    if (build.bdd) {
      return {
        buildstats: build.buildstats.filter(x => x.depth == 1),
        tagstats: build.tagStats?.filter(x => x.depth == 1),
        statsTitle: 'Scenarios'
      };
    } else {
      if (build.buildstats.length === 3) {
        return {
          buildstats: build.buildstats.filter(x => x.depth == 2),
          tagstats: build.tagStats?.filter(x => x.depth == 2),
          statsTitle: 'Methods'
        };
      }
      if (build.buildstats.length === 2) {
        return {
          buildstats: build.buildstats.filter(x => x.depth == 1),
          tagstats: build.tagStats?.filter(x => x.depth == 1),
          statsTitle: 'Methods'
        };
      }
      return {
        buildstats: build.buildstats.filter(x => x.depth == 0),
        tagstats: build.tagStats?.filter(x => x.depth == 0),
        statsTitle: 'Tests'
      };
    }
  }

  showSelectedBuildMetrics(build: Build) {
    this.tagstats = [];
    this.stats.datasets[0].data = [];

    if (!build || !build.buildstats || !build.buildstats.length) {
      return;
    }

    const { buildstats: buildstats, tagstats, statsTitle } = this.getBuildStats(build);
    this.options.plugins.annotation.annotations[0].label.content = `${Math.floor(buildstats[0].passed / buildstats[0].total * 100)}%`;
    this.tagstats = tagstats;
    this.statsTitle = statsTitle;

    if (buildstats.length > 0) {
      this.stats.datasets[0].data.push(buildstats[0].passed, buildstats[0].failed, buildstats[0].skipped);
    }

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

  reset(): void {
    this.q.status = '';
    this.q.dateFrom = '';
    this.q.dateTo = '';

    this.builds = new Page<Build>();
    this.findBuilds();
  }

  search(): void {
    this.builds = new Page<Build>();
    let dateFrom = -1;
    if (this.q.dateFrom) {
      const date = new Date(this.q.dateFrom.year, this.q.dateFrom.month - 1, this.q.dateFrom.day, 0, 0, 0, 0);
      dateFrom = date.getTime();
    }
    let dateTo = -1;
    if (this.q.dateTo) {
      const date = new Date(this.q.dateTo.year, this.q.dateTo.month - 1, this.q.dateTo.day, 0, 0, 0, 0);
      dateTo = date.getTime();
    }
    console.log(this.q)
    
    this.buildService.q(this.projectId, this.q.result, dateFrom, dateTo, 0, 20, 'id,desc')
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

}
