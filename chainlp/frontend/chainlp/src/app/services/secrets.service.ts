import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseService } from './base.service';
import { Secret } from '../model/secret.model';

@Injectable({
  providedIn: 'root'
})
export class SecretsService extends BaseService<Secret> {

  constructor(http: HttpClient) {
    super('/6ff57e5', http);
  }
  
}
