import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Workspace } from '../model/workspace.model';
import { BaseService } from './base.service';

@Injectable({
  providedIn: 'root'
})
export class WorkspaceService extends BaseService<Workspace> {

  constructor(http: HttpClient) {
    super('/workspaces', http);
  }

}
