import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'prettyTime'
})
export class PrettyTimePipe implements PipeTransform {

  transform(value: number, ...args: unknown[]): unknown {
    if (value < 1000) {
      return `${value}ms`;
    }
    
    const secs = Math.floor((value / 1000) % 60);
    if (value < 60000) {
      return `${secs}s`;
    }

    const mins = Math.floor((value / (60 * 1000)) % 60);
    if (value < 3600000) {
      return `${mins}m ${secs}s`;
    }

    const hours = Math.floor(value / (3600 * 1000));
    return `${hours}h ${mins}m ${secs}s`;
  }

}
