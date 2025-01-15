import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Page } from '../model/page.model';

@Injectable({
  providedIn: 'root'
})
export abstract class BaseService<T> {

  protected _path: string;

  protected constructor(uri: string, protected http: HttpClient) {
    let baseUrl = environment.apiURL;
    if (baseUrl.startsWith('http')) {
      this._path = environment.apiURL + environment.apiVersion + uri;
    } else {
      this._path = environment.apiVersion + uri;
    } 
  }

  query(params: HttpParams): Observable<Page<T>> {
    return this.http.get<Page<T>>(this._path, { params: params });
  }

  body(obj: T, params: HttpParams): Observable<Page<T>> {
    return this.http.post<Page<T>>(this._path + '/q', obj, { params: params });
  }

  findAll(page: number = 0, pageSize: number = 20, sort: string = ''): Observable<Page<T>> {
    const params = new HttpParams()
      .set('page', page)
      .set('sort', sort)
      .set('size', pageSize);
    return this.query(params);
  }

  find(id: number) {
    return this.http.get<T>(this._path + `/${id}`);
  }

  save(t: T): Observable<T> {
    return this.http.post<T>(this._path, t);
  }

  update(t: T): Observable<T> {
    return this.http.put<T>(this._path, t);
  }

  delete(t: any): Observable<any> {
    const url = new URL(t.id.toString(), this._path + '/');
    return this.http.delete<any>(url.href);
  }

}
