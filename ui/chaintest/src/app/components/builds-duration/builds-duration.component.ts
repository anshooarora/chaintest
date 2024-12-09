import { Component, Input } from '@angular/core';
import { ChartData, ChartOptions } from 'chart.js';
import { Page } from '../../model/page.model';
import { Build } from '../../model/build.model';
import { DateTimeService } from '../../services/date-time.service';

@Component({
  selector: 'app-builds-duration',
  templateUrl: './builds-duration.component.html',
  styleUrl: './builds-duration.component.scss'
})
export class BuildsDurationComponent {

  @Input()
  builds: Page<Build>;

  /* build trend chart */
  chartType: any = 'line';
  data: ChartData<any> = {
    labels: [],
    datasets: [
      { label: 'Duration', data: [], borderWidth: 1, pointRadius: 3 }
    ]
  };
  options: ChartOptions<any> = {
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      x: {
        grid: {
          display: false,
        }
      },
      y: {
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
    for (let i = 0; i < builds.length; i++) {
      this.data.labels?.push('#' + builds[i].id);
      this.data.datasets[0].data.push(builds[i].durationMs / 1000);
    }
  }

}
