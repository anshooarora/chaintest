import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseService } from './base.service';
import { Test } from '../model/test.model';

@Injectable({
  providedIn: 'root'
})
export class TestService extends BaseService<Test> {

  constructor(http: HttpClient) {
    super('/tests', http);
  }

}
