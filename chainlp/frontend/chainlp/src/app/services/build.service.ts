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

}
