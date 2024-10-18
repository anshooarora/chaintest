import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseService } from './base.service';
import { Tag } from '../model/tag.model';

@Injectable({
  providedIn: 'root'
})
export class TagService extends BaseService<Tag> {

  constructor(http: HttpClient) {
    super('/tags', http);
  }

}
