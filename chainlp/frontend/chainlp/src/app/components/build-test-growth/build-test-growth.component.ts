import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';
import { ChartData, ChartOptions } from 'chart.js';
import { Page } from '../../model/page.model';
import { Build } from '../../model/build.model';

@Component({
  selector: 'app-build-test-growth',
  templateUrl: './build-test-growth.component.html',
  styleUrl: './build-test-growth.component.scss'
})
export class BuildTestGrowthComponent {

  @Input()
  builds: Page<Build>;

  @Input()
  maxHeight: number = 125;

  /* build trend chart */
  chartType: any = 'line';
  data: ChartData<any> = {
    labels: [],
    datasets: [
      { 
        label: 'Count of tests', 
        data: []
      }
    ]
  };
  options: ChartOptions<any> = {
    responsive: true,
    borderWidth: 2, 
    pointRadius: 1, 
    maintainAspectRatio: false,
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

  constructor(private router: Router) {}

  ngOnInit() {
    this.showTrends();
  }

  showTrends() {
    const builds: Build[] = this.builds.content;
    for (let i = 0; i < builds.length; i++) {
      if (builds[i].buildstats.length > 0) {
        this.data.labels?.push('#' + builds[i].id);
        this.data.datasets[0].data.push(builds[i].buildstats[0].total);
      }
    }
  }

}
