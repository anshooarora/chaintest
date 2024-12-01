import { Component, Input } from '@angular/core';
import { Test } from '../../model/test.model';

@Component({
  selector: 'app-feature',
  templateUrl: './feature.component.html',
  styleUrl: './feature.component.scss'
})
export class FeatureComponent {

  @Input()
  feature: Test;

}
