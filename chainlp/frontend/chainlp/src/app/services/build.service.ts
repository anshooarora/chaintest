import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BaseService } from './base.service';
import { Build } from '../model/build.model';
import { Page } from '../model/page.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BuildService extends BaseService<Build> {

  constructor(http: HttpClient) {
    super('/builds', http);
  }

  findByProjectId(projectId: number = 0, page: number = 0, pageSize: number = 20, sort: string = ''): Observable<Page<Build>> {
    const params = new HttpParams()
      .set('projectId', projectId)
      .set('page', page)
      .set('sort', sort)
      .set('size', pageSize);
      return this.query(params);
  }

  q(projectId: number, result: string = '', startedAfter: number = -1, endedBefore: number = -1, page: number = 0, pageSize: number = 20, sort: string = ''): Observable<Page<Build>> {
    const params = new HttpParams()
      .set('projectId', projectId)
      .set('result', result)
      .set('startedAfter', startedAfter)
      .set('endedBefore', endedBefore)
      .set('page', page)
      .set('sort', sort)
      .set('size', pageSize);

    return this.query(params);
  }

  findByDisplayId(projectId: number, displayId: number): Observable<Page<Build>> {
    const params = new HttpParams()
      .set('projectId', projectId)
      .set('displayId', displayId);
      return this.query(params);
  }

}
