import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BaseService } from './base.service';
import { Cache } from '../model/cache.model';

@Injectable({
  providedIn: 'root'
})
export class CacheService extends BaseService<Cache> {

  constructor(http: HttpClient) {
    super('/cache', http);
  }

  clear(name: string): Observable<any> {
    return this.http.delete(`${this._path}/${name}`);
  }

  clearAll(): Observable<any> {
    return this.http.delete(`${this._path}`);
  }

}
