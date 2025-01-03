import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { ChartData, ChartOptions } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';
import { BehaviorSubject } from 'rxjs';
import ChartDataLabels from 'chartjs-plugin-datalabels';
import { Build } from '../../model/build.model';
import { DateTimeService } from '../../services/date-time.service';

@Component({
  selector: 'app-executions-trend',
  templateUrl: './executions-trend.component.html',
  styleUrl: './executions-trend.component.scss'
})
export class ExecutionsTrendComponent implements OnInit {

  @Input()
  builds$: BehaviorSubject<Build[]> = new BehaviorSubject<Build[]>([]);

  @ViewChild(BaseChartDirective) chart?: BaseChartDirective;

  /* sum of all datasets - chartjs plugin */
  totalizer: any = {
    id: 'totalizer',

    beforeUpdate: (chart: any) => {
      let totals: any = {};
      let utmost: number = 0;
  
      chart.data.datasets.forEach((dataset: any, datasetIndex: number) => {
        if (chart.isDatasetVisible(datasetIndex)) {
          utmost = datasetIndex;
          dataset.data.forEach((value: any, index: number) => {
            totals[index] = (totals[index] || 0) + value;
          })
        }
      })
  
      chart.$totalizer = {
        totals: totals,
        utmost: utmost
      }
    }
  }

  plugins = [ChartDataLabels, this.totalizer];
  builds: Build[] = [];

  /* chart */
  chartType: any = 'bar';
  data: ChartData<any> = {
    labels: [],
    datasets: []
  };
  trendChartOptions: ChartOptions<any> = {
    responsive: true,
    maintainAspectRatio: false,
    barPercentage: 0.05,
    scales: {
      x: {
        stacked: true,
        grid: {
          display: false,
        }
      },
      y: {
        stacked: true,
        grid: {
          display: false,
        }
      }
    },
    plugins: {
      legend: {
        display: false
      },
      datalabels: {
        formatter: (value: any, ctx: any) => {
          const total = ctx.chart.$totalizer.totals[ctx.dataIndex];
          return total + '\n';
        },
        display: function(ctx: any) {
          return ctx.datasetIndex === ctx.chart.$totalizer.utmost;
        },
        anchor: 'end',
      }
    }
  };

  constructor(private datetimeService: DateTimeService) {
  }

  ngOnInit() {
    this.builds$.subscribe((builds) => {
      this.builds = builds;
      this.showTrends();
    });
  }

  showTrends() {
    this.data = {
      labels: [],
      datasets: []
    };
    const data: any = {};
    this.builds.forEach((build) => {
      const dt = this.datetimeService.formatDate(build.startedAt, 'MMM DD');
      const project = build.projectName;
      if (dt in data) {
        if (data[dt].some((d: any) => d.project === project)) {
          data[dt].find((d: any) => d.project === project).executions++;
        } else {
          data[dt].push({
            project: project,
            executions: 1
          });
        }
      } else {
        data[dt] = [{
          project: project,
          executions: 1
        }]
      }
    });
    for (let o in data) {
      this.data.labels?.push(o);
      data[o].forEach((d: any) => {
        if (this.data.datasets.some((ds: any) => ds.label === d.project)) {
          this.data.datasets.find((ds: any) => ds.label === d.project).data.push(d.executions);
        } else {
          this.data.datasets.push({
            label: d.project,
            data: [d.executions],
            stack: 'combined'
          });
        }
      });
    }
  }
  
}
