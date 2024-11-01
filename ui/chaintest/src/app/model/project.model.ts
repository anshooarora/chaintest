import { Page } from "./page.model";

export class Project extends Page<Project> {
  id: number;
  name: string;
  createdAt: number;
}
