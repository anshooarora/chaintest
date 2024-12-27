import { Build } from "./build.model";
import { Page } from "./page.model";

export class Project extends Page<Project> {
  id: number;
  name: string;
  createdAt: number;
  url: string;
  repositoryName: string;
  repositoryUrl: string;

  // used in project-listing.component
  builds: Page<Build>;
  display: boolean = true;
}
