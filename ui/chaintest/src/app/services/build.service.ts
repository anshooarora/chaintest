import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseService } from './base.service';
import { Build } from '../model/build.model';

@Injectable({
  providedIn: 'root'
})
export class BuildService extends BaseService<Build> {

  constructor(http: HttpClient) {
    super('/builds', http);
  }

}
