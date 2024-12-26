import { Component, Input } from '@angular/core';
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
    }
  };

  constructor() { }

  ngOnInit() {
    this.showTrends();
  }

  showTrends() {
    const builds:Build[] = this.builds.content;
    for (let i = 0; i < builds.length; i++) {
      this.data.labels?.push('#' + builds[i].id);
      this.data.datasets[0].data.push(builds[i].buildstats[0].total);
    }
  }

}
