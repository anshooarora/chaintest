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
import { PrettyTimePipe } from '../../../pipes/pretty-time.pipe';

@Component({
  selector: 'app-build',
  templateUrl: './build.component.html',
  styleUrl: './build.component.scss',
  providers: [PrettyTimePipe]
})
export class BuildComponent implements OnInit, OnDestroy {

  @ViewChild(BaseChartDirective) chart?: BaseChartDirective;
    
  private _destroy$: Subject<any> = new Subject<any>();
  private _body$: Subject<any> = new Subject<any>();
  private _history$: Subject<any> = new Subject<any>();
  private _titles: any = {
    'cucumber-jvm': ['Feature', 'Scenario', 'Step'],
    'junit': ['Class', 'Method'],
    'junit-jupiter': ['Class', 'Method'],
    'junit5': ['Class', 'Method'],
    'testng': ['Suite', 'Class', 'Method']
  };

  projectId: number;
  buildId: number;
  buildDisplayId: number;
  build: Build;
  page: Page<Test>;
  displaySummary: boolean = true;
  buildDepth: number = 0;
  error: string;
  pageNum: number = 0;
  tagDepth: number = 0;
  testCount: number | undefined = 0;

  /* filter */
  statusFilterHierarchy: string[];

  constructor(private route: ActivatedRoute,
    private _buildService: BuildService,
    private _testService: TestService,
    private _errorService: ErrorHandlerService) { 
      Chart.register(AnnotationPlugin); 
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(val => {
      this.projectId = parseInt(this.route.snapshot.paramMap.get('projectId') || '0');
      let id = this.route.snapshot.paramMap.get('buildId') || '0';
      if (this.buildDisplayId == parseInt(id)) {
        return;
      }
      this.resetAll();
      this.buildDisplayId = parseInt(id);
      this.findBuild();
    });
  }

  private resetAll(): void {
    this.page = new Page<Test>();
    this.pageNum = 0;
    this.build = new Build();
    [this.depth0, this.depth1, this.depth2].forEach(depthData => {
      depthData.datasets[0].data = [];
    });
  }

  ngOnDestroy(): void {
    this._destroy$.next(null);
    this._destroy$.complete();
    this._body$.next(null);
    this._body$.complete();
  }
  
  /* charts */
  chartType: any = 'doughnut';

  toggleDisplaySummary(): void {
    this.displaySummary = !this.displaySummary;
  }

  dataset: ChartDataset<any> = {
    backgroundColor: ['rgb(140, 197, 83)', 'rgb(233,80,113)', 'rgb(221, 91, 96)'],
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
      maintainAspectRatio: false,
      cutout: '70%',
      animation: {
        duration: 250
      },
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
              color: '#aaa'
            }
          }]
        }
      }
    };
  }

  findBuild(): void {
    this._buildService.findByDisplayId(this.projectId, this.buildDisplayId)
    .pipe(takeUntil(this._destroy$))
    .subscribe({
      next: (page: Page<Build>) => {
        const build = page.content[0];
        this.buildId = build.id;
        this.findTests();
        if (build.testRunner.indexOf('cucumber') > -1) {
          this.tagDepth = 1;
        } else {
          this.tagDepth = build.buildstats.length - 1;
        }

        this.testCount = build.buildstats.find(x => x.depth == 0)?.total;

        this.build = build;
        this.buildDepth = build.buildstats.length;
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
    this._testService.search(0, '', this.build.projectId, this.buildId, 0, '', '', '', pageNum, 'AND', 'id,asc')
    .pipe(takeUntil(this._destroy$))
    .subscribe({
      next: (page: Page<Test>) => {
        this.page = append ? { ...page, content: [...this.page.content, ...page.content] } : page;
        console.log(this.page);
        this.findTestsHistory();
      },
      error: (err) => {
        this.error = this._errorService.getError(err);
      }
    });
  }

  findTestsByStatus(result: string, depth: number): void {
    const test = new Test();
    test.buildId = this.buildId;
    test.depth = 0;
    if (depth === 0) {
      test.result = result;
    }
    if (depth >= 1) {
      test.result = '';
      test.children = [new Test()];
      test.children[0].buildId = this.buildId;
      if (depth >= 2) {
        test.children[0].result = '';
        test.children[0].children = [new Test()];
        test.children[0].children[0].depth = 2;
        test.children[0].children[0].result = result;
        test.children[0].children[0].buildId = this.buildId;
      } else {
        test.children[0].depth = 1;
        test.children[0].result = result;
      }
    }
    this._testService.withBody(test, 0, 'id,asc')
    .pipe(takeUntil(this._body$))
    .subscribe({
      next: (page: Page<Test>) => {
        this.page = page;
        console.log(this.page);
        this.findTestsHistory();
      },
      error: (err) => {
        this.error = this._errorService.getError(err);
      }
    });
  }

  findTestsHistory(): void {
    this.page.content.forEach(test => {
      if (!test.history) {
        this._testService.history(test.id)
        .pipe(takeUntil(this._history$))
        .subscribe({
          next: (page: Page<Test>) => {
            console.log(page);
            test.history = page;
          },
          error: (err) => {
            this.error = this._errorService.getError(err);
          }
        });
      }
    });
  }

  findAllTests(): void {
    this.findTests(0, false);
  }

  loadNextPage(): void {
    this.pageNum++;
    this.findTests(this.pageNum, true);
  }

  filterTag(tag: string): void {
    this.page = new Page<Test>();
    this._testService.search(0, '', -1, this.buildId, 0, '', tag, '', 0, 'AND')
    .pipe(takeUntil(this._destroy$))
    .subscribe({
      next: (page: Page<Test>) => {
        this.page = page;
        console.log(this.page);
      },
      error: (err) => {
        this.error = this._errorService.getError(err);
      }
    });
  }

  getTitles(): string[] {
    let titles = [];
    if (this.build.testRunner == 'testng' && this.build.buildstats.length == 2) {
      titles = this._titles['testng'].slice(1);
    } else {
      titles = this._titles[this.build.testRunner];
    }
    if (this.build.testRunner == 'cucumber-jvm') {
      this.statusFilterHierarchy = this._titles['cucumber-jvm'].slice(0, -1);
    } else {
      this.statusFilterHierarchy = this._titles[this.build.testRunner];
    }
    return titles;
  }
}
