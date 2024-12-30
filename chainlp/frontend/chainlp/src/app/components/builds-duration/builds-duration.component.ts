import { Component, Input } from '@angular/core';
import { ChartData, ChartOptions } from 'chart.js';
import { Page } from '../../model/page.model';
import { Build } from '../../model/build.model';
import { DateTimeService } from '../../services/date-time.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-builds-duration',
  templateUrl: './builds-duration.component.html',
  styleUrl: './builds-duration.component.scss'
})
export class BuildsDurationComponent {

  @Input()
  builds: Page<Build>;

  @Input()
  maxHeight: number = 125;

  @Input()
  minHeight: number = 125;

  /* build trend chart */
  chartType: any = 'line';
  data: ChartData<any> = {
    labels: [],
    datasets: [
      { 
        label: 'Duration', 
        data: [], 
        borderWidth: 2, 
        pointRadius: 1, 
        stepped: false, 
        fill: { 
          target: 'origin', 
          above: 'rgba(0, 123, 255, 0.1)',
          below: 'rgba(0, 123, 255, 0.1)',
        } 
      }
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
    },
    onClick: (e: any, elements: any) => {
      if (elements && elements.length > 0) {
        const idx = elements[0].index;
        const buildId = e.chart.data.labels[idx].replace('#', '');
        this.router.navigate(['/builds', buildId]);
      }
    }
  };

  constructor(private router: Router, private datetimeService: DateTimeService) { }
  

  ngOnInit() {
    this.showTrends();
  }

  showTrends() {
    const builds:Build[] = this.builds.content.slice().reverse();
    for (let i = 0; i < builds.length; i++) {
      this.data.labels?.push('#' + builds[i].id);
      this.data.datasets[0].data.push(builds[i].durationMs / 1000);
    }
  }

}
