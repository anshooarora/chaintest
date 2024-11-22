import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
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

  findByProjectId(projectId: number = 0): Observable<Page<Build>> {
    return this.http.get<Page<Build>>(this._api_endpoint.href + `?projectId=${projectId}&sort=id,DESC`);
  }

}
