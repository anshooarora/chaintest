import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Page } from '../model/page.model';

@Injectable({
  providedIn: 'root'
})
export abstract class BaseService<T> {

  private _path: string;
  private _api_endpoint: URL;  

  protected constructor(uri: string, private http: HttpClient) {
    this._path = environment.apiVersion + uri;
    this._api_endpoint = new URL(this._path, environment.apiURL);
  }

  findAll(page: number = 0, pageSize: number = 20, sort: string = 'desc'): Observable<Page<T>> {
    const params = new HttpParams()
      .set('page', page)
      .set('sort', 'id,' + sort)
      .set('size', pageSize);
    return this.http.get<Page<T>>(this._api_endpoint.href, { params: params });
  }

  find(id: number) {
    return this.http.get<T>(this._api_endpoint.href + `/${id}`);
  }

  save(t: T): Observable<T> {
    return this.http.post<T>(this._api_endpoint.href, t);
  }

  update(t: T): Observable<T> {
    return this.http.put<T>(this._api_endpoint.href, t);
  }

  delete(t: any): Observable<any> {
    const url = new URL(t.id.toString(), this._api_endpoint);
    return this.http.delete<any>(url.href);
  }

}
