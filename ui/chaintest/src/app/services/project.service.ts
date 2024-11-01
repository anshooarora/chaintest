import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseService } from './base.service';
import { Project } from '../model/project.model';

@Injectable({
  providedIn: 'root'
})
export class ProjectService extends BaseService<Project> {

  constructor(http: HttpClient) {
    super('/projects', http);
  }

}
