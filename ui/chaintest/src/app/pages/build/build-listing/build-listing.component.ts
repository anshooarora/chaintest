import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { ChartData, ChartOptions, LegendItem } from 'chart.js';
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
export class BuildListingComponent implements OnInit {

  private _destroy$: Subject<any> = new Subject<any>();

  projectId: number;

  error: any;
  tagDisplayLimit: number = 5;
  builds: Page<Build>;
  selectedBuild: Build;
  tagstats: TagStats[];
  pageNum: number = 0;

  /* chart */
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
    private _buildService: BuildService,
    private _errorService: ErrorHandlerService) { }

  ngOnInit(): void {
    const projectId = this.route.snapshot.paramMap.get('projectId') || '0';
    this.projectId = parseInt(projectId);
    this.findBuilds();
  }

  ngOnDestroy(): void {
    this._destroy$.next(null);
    this._destroy$.complete();
  }

  findBuilds(page: number = 0, pageSize: number = 5): void {
    this._buildService.findByProjectId(this.projectId, page, pageSize, 'id,desc')
      .pipe(takeUntil(this._destroy$))
      .subscribe({
        next: (builds: Page<Build>) => {
          if (!this.builds) {
            console.log('not builds')
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
          this.error = this._errorService.getError(err);
        }
      });
  }

  loadNextPage(): void {
    this.pageNum++;
    this.findBuilds(this.pageNum);
  }

  selectBuild(build: Build) {
    this.selectedBuild = build;
    this.showMetrics(build);
  }

  showMetrics(build: Build) {
    this.tagstats = [];
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
  }

  paginate($event: any) {
    this.findBuilds($event);
  }

}
