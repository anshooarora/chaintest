import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { Chart, ChartData, ChartDataset, ChartOptions, LegendItem } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';
import AnnotationPlugin from "chartjs-plugin-annotation";

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
export class BuildComponent implements OnInit, OnDestroy {

  @ViewChild(BaseChartDirective) chart?: BaseChartDirective;
    
  private _destroy$: Subject<any> = new Subject<any>();

  buildId: number;
  build: Build;
  page: Page<Test>;
  displayCharts: boolean = true;
  error: string;
  result: string = 'FAILED';
  pageNum: number = 0;

  constructor(private route: ActivatedRoute,
    private _buildService: BuildService,
    private _testService: TestService,
    private _errorService: ErrorHandlerService) { 
      Chart.register(AnnotationPlugin); 
    }

  ngOnInit(): void {
    let id = this.route.snapshot.paramMap.get('buildId') || '0';
    this.buildId = parseInt(id);
    this.findBuild();
    this.findTests();
  }

  ngOnDestroy(): void {
    this._destroy$.next(null);
    this._destroy$.complete();
  }
  
  /* charts */
  chartType: any = 'doughnut';

  toggleDisplayCharts(): void {
    this.displayCharts = !this.displayCharts;
  }

  dataset: ChartDataset<any> = {
    backgroundColor: ['rgb(89, 199, 125)', 'rgb(240, 105, 132)', 'rgb(233, 210, 113)'],
    borderColor: 'transparent'
  };

  depth0: ChartData<any> = {
    labels: ['Passed', 'Failed', 'Skipped'],
    datasets: [{
      data: [],
      ...this.dataset
    }]
  };

  depth1: ChartData<any> = {
    labels: ['Passed', 'Failed', 'Skipped'],
    datasets: [{
      data: [],
      ...this.dataset
    }]
  };

  depth2: ChartData<any> = {
    labels: ['Passed', 'Failed', 'Skipped'],
    datasets: [{
      data: [],
      ...this.dataset
    }]
  };

  getOptions(depth: number): ChartOptions<any> {
    const buildstats = this.build.buildstats.find(x => x.depth == depth);
    const label = buildstats ? Math.floor(buildstats.passed / buildstats.total * 100) : 0;
    return {
      responsive: true,
      cutout: '70%',
      plugins: {
        legend: {
          position: 'right',
          labels: {
            boxWidth: 15,
            filter: (item: LegendItem, chartData: ChartData<any>) => {
              const label = item.text.toUpperCase();
              const index = label === 'PASSED' ? 0 : label === 'FAILED' ? 1 : 2;
              return chartData.datasets[0].data[index] != 0;
            }
          }
        },
        annotation: {
          annotations: [{
            borderWidth: 0,
            label: {
              content: label + '%',
              display: true,
              backgroundColor: 'transparent',
              color: 'inherit'
            }
          }]
        }
      }
    };
  }

  findBuild(): void {
    this._buildService.find(this.buildId)
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
    [this.depth0, this.depth1, this.depth2].forEach((depthData, index) => {
      const buildstats = build.buildstats.filter(x => x.depth == index);
      if (buildstats.length) {
        depthData.datasets[0].data.push(buildstats[0].passed, buildstats[0].failed, buildstats[0].skipped);
      }
    });
    this.chart?.update();
  }
  
  findTests(pageNum: number = 0, append: boolean = false): void {
    this._testService.search(0, '', this.buildId, 0, this.result, '', '', pageNum)
    .pipe(takeUntil(this._destroy$))
    .subscribe({
      next: (page: Page<Test>) => {
        this.page = append ? { ...page, content: [...this.page.content, ...page.content] } : page;
        console.log(this.page);
      },
      error: (err) => {
        this.error = this._errorService.getError(err);
      }
    });
  }

  findAllTests(): void {
    this.result = '';
    this.findTests(0, false);
  }

  loadNextPage(): void {
    this.pageNum++;
    this.findTests(this.pageNum, true);
  }
}
