import { Injectable } from '@angular/core';
import moment from 'moment';

@Injectable({
  providedIn: 'root'
})
export class DateTimeService {

  constructor() { }

  public formatMillis(millis: number, pattern: string): string {
    return moment(millis).format(pattern);
  }

  public formatDate(date: Date, pattern: string): string {
    return moment(date).format(pattern);
  }

}
