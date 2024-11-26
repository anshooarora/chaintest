import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'prettyTime'
})
export class PrettyTimePipe implements PipeTransform {

  transform(value: number, ...args: unknown[]): unknown {
    if (1000 > value) {
      return `${value}ms`;
    }
    
    const millis = value % 1000;
    const secs = Math.floor((value / 1000) % 60);
    if (60000 > value) {
      return `${secs}s`;
    }

    const mins = Math.floor((value / (60 * 1000)) % 60);
    if (3600000 > value) {
      return `${mins}m ${secs}s`;
    }

    const hours = Math.floor((value / (3600 * 1000)) % 3600);
    return `${hours}h ${mins}m ${secs}s`;
  }

}
