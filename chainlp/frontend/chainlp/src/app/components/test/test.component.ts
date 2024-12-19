import { Component, Input } from '@angular/core';
import { Test } from '../../model/test.model';

@Component({
  selector: 'app-test',
  templateUrl: './test.component.html',
  styleUrl: './test.component.scss'
})
export class TestComponent {

  @Input()
  test: Test;

  @Input()
  bdd: boolean;

  @Input()
  showMeta: boolean;

}
