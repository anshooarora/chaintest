import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { ChartData, ChartOptions, LegendItem } from 'chart.js';
import { BuildService } from '../../../services/build.service';
import { ErrorHandlerService } from '../../../services/error-handler.service';
import { Build } from '../../../model/build.model';
import { TestService } from '../../../services/test.service';
import { Page } from '../../../model/page.model';
import { Test } from '../../../model/test.model';

@Component({
  selector: 'app-build',
  templateUrl: './build.component.html',
  styleUrl: './build.component.scss'
})
export class BuildComponent implements OnInit {

  private _destroy$: Subject<any> = new Subject<any>();

  buildId: number;
  build: Build;
  page: Page<Test>;
  error: string;
  result: string = 'FAILED';

  /* charts */
  chartType: any = 'doughnut';

  depth0Data: ChartData<any> = {
    labels: ['Passed', 'Failed', 'Skipped'],
    datasets: [
      {
        data: [],
        backgroundColor: ['rgb(89, 199, 125)', 'rgb(240, 105, 132)', 'rgb(233, 210, 113)']
      }
    ]
  };

  depth1Data: ChartData<any> = {
    labels: ['Passed', 'Failed', 'Skipped'],
    datasets: [
      {
        data: [],
        backgroundColor: ['rgb(89, 199, 125)', 'rgb(240, 105, 132)', 'rgb(233, 210, 113)']
      }
    ]
  };

  depth2Data: ChartData<any> = {
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
    private _testService: TestService,
    private _errorService: ErrorHandlerService) { }

  ngOnInit(): void {
    let id = this.route.snapshot.paramMap.get('buildId') || '0';
    this.buildId = parseInt(id);
    this.findBuild(this.buildId);
    this.findTests(this.buildId);
  }

  ngOnDestroy(): void {
    this._destroy$.next(null);
    this._destroy$.complete();
  }

  findBuild(buildId: number): void {
    this._buildService.find(buildId)
    .pipe(takeUntil(this._destroy$))
    .subscribe({
      next: (build: Build) => {
        this.build = build;
        this.computeMetrics(build);
        console.log(build)
      },
      error: (err) => {
        this.error = this._errorService.getError(err);
      }
    });
  }

  private computeMetrics(build: Build) {
    const depth0 = build.runStats.filter(x => x.depth == 0);
    if (depth0.length) {
      this.depth0Data.datasets[0].data.push(depth0[0].passed, depth0[0].failed, depth0[0].skipped);
    }
    const depth1 = build.runStats.filter(x => x.depth == 1);
    if (depth1.length) {
      this.depth1Data.datasets[0].data.push(depth1[0].passed, depth1[0].failed, depth1[0].skipped);
    }
    const depth2 = build.runStats.filter(x => x.depth == 2);
    if (depth2.length) {
      this.depth2Data.datasets[0].data.push(depth2[0].passed, depth2[0].failed, depth2[0].skipped);
    }
  }

  findTests(buildId: number): void {
    this._testService.search(0, '', this.buildId, 0, this.result)
    .pipe(takeUntil(this._destroy$))
    .subscribe({
      next: (page: Page<Test>) => {
        this.page = page;
        console.log(page)
      },
      error: (err) => {
        this.error = this._errorService.getError(err);
      }
    });
  }
}
