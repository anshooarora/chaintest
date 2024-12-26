import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BaseService } from './base.service';
import { Test } from '../model/test.model';
import { environment } from '../../environments/environment';
import { Page } from '../model/page.model';

@Injectable({
  providedIn: 'root'
})
export class TestService extends BaseService<Test> {

  constructor(http: HttpClient) {
    super('/tests', http);
  }

  search(id: number = 0, name: string = '', projectId: number = 0, buildId: number = 0, depth: number = -1, result: string = '', tags: string = '', error: string = '', pageNum: number = 0, op: string = 'AND') {
    let params = new HttpParams();
    if (id > 0) {
      params = params.set('id', id);
    }
    if (name) {
      params = params.set('name', name);
    }
    if (projectId > 0) {
      params = params.set('projectId', projectId);
    }
    if (buildId > 0) {
      params = params.set('buildId', buildId);
    }
    if (depth >= 0) {
      params = params.set('depth', depth);
    }
    if (result) {
      params = params.set('result', result);
    }
    if (tags) {
      params = params.set('tags', tags);
    }
    if (error) {
      params = params.set('error', error);
    }
    params = params.set('op', op);
    params = params.set('page', pageNum);
    params = params.set('sort', 'id,desc');
    return super.query(params)
  }

  history(id: number) {
    return this.http.get<Page<Test>>(`${this._api_endpoint.href}/${id}/history`);
  }

}
