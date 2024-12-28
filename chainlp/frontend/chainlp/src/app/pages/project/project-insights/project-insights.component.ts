import { Component, OnDestroy, OnInit, ViewChild } from "@angular/core";
import { Router } from "@angular/router";
import { Subject, takeUntil } from 'rxjs';
import { ChartData, ChartOptions } from 'chart.js';
import * as moment from 'moment';
import { ErrorHandlerService } from '../../../services/error-handler.service';
import { Page } from '../../../model/page.model';
import { ProjectService } from "../../../services/project.service";
import { Project } from "../../../model/project.model";
import { BuildService } from "../../../services/build.service";
import { Build } from "../../../model/build.model";
import { BaseChartDirective } from "ng2-charts";

@Component({
  selector: 'app-project-insights',
  templateUrl: './project-insights.component.html',
  styleUrl: './project-insights.component.scss'
})
export class ProjectInsightsComponent implements OnInit, OnDestroy {

  @ViewChild(BaseChartDirective) chart?: BaseChartDirective;
  
  private readonly _destroy$: Subject<any> = new Subject<any>();
  private readonly _build$: Subject<any> = new Subject<any>();
  private readonly _del$: Subject<any> = new Subject<any>();

  moment: any = moment;

  q: string;
  error: any;
  projectPage: Page<Project>;

  constructor(private router: Router,
    private projectService: ProjectService, 
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
    this.data = {
      labels: [20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1],
      datasets: []
    };
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
        project.builds = response;
        this.appendToCountsTrend(project, response.content);
        this.chart?.update();
        console.log(this.data)
      },
      error: (err) => {
        this.error = this.errorService.getError(err);
      }
    });
  }

  /* time taken trend */
  chartType: any = 'line';
  data: ChartData<any> = {
    labels: [],
    datasets: []
  };
  options: ChartOptions<any> = {
    responsive: true,
    borderWidth: 2, 
    pointRadius: 1, 
    maintainAspectRatio: false,
    scales: {
      x: {
        grid: {
          display: true
        },
        ticks: {
          display: false
        }
      }
    },
    plugins: {
      legend: {
        
      }
    }
  };

  appendToCountsTrend(project: Project, builds: Build[]) {
    const data: any[] = [];
    const dataset = {
      label: project.name,
      data: data,
      borderColor: this.getRandColor(Math.floor(Math.random() * 20)),
      cubicInterpolationMode: 'monotone',
      tension: 0.4
    };
    this.data.datasets.push(dataset);
    for (let build of builds) {
      if (build.buildstats.length > 0) {
        const n: number = build.durationMs / 1000 / 60;
        dataset.data.push(n);
      }
    }
  }

  rand(max: number) {
    return Math.floor(Math.random() * max);
  }

  getRandColor(brightness: any) {
    return "rgb(" + this.rand(256) + "," + this.rand(256) + "," + this.rand(256) + ")";
  }

}
