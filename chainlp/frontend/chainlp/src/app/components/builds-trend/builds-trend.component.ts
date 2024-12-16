import { Component, Input } from '@angular/core';
import { ChartData, ChartOptions } from 'chart.js';
import { Page } from '../../model/page.model';
import { Build } from '../../model/build.model';
import { DateTimeService } from '../../services/date-time.service';

@Component({
  selector: 'app-builds-trend',
  templateUrl: './builds-trend.component.html',
  styleUrl: './builds-trend.component.scss'
})
export class BuildsTrendComponent {

  @Input()
  builds: Page<Build>;
  
  /* build trend chart */
  trendChartType: any = 'bar';
  trendData: ChartData<any> = {
    labels: [],
    datasets: [
      { label: 'Passed', data: [], backgroundColor: 'rgba(112, 203, 94, .75)' },
      { label: 'Failed', data: [], backgroundColor: 'rgba(240, 105, 132, .75)' }
    ]
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
      }
    }
  };

  constructor(private datetimeService: DateTimeService) { }

  ngOnInit() {
    this.showTrends();
  }

  showTrends() {
    const builds:Build[] = this.builds.content;
    const data: any = [];
    for (let i = 0; i < builds.length; i++) {
      const dt = this.datetimeService.formatDate(builds[i].startedAt, 'MM/D');
      data[dt] = {
        passed: 0,
        failed: 0
      };
    }
    for (let i = 0; i < builds.length; i++) {
      const dt = this.datetimeService.formatDate(builds[i].startedAt, 'MM/D');
      if (builds[i].result == 'PASSED') {
        data[dt]['passed']++;
      } else {
        data[dt]['failed']++;
      }
    }
    for (let o in data) {
      this.trendData.labels?.push(o);
      this.trendData.datasets[0].data.push(data[o].passed);
      this.trendData.datasets[1].data.push(data[o].failed);
    }
  }

}
