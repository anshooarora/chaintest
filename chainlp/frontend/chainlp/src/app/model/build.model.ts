import { ExecutionStage } from "./execution-stage.model";
import { Page } from "./page.model";
import { Result } from "./result.model";
import { BuildStats as BuildStats } from "./run-stats.model";
import { TagStats } from "./tag-stats.model";
import { Tag } from "./tag.model";

export class Build extends Page<Build> {
  id: number;
  projectId: number;
  projectName: string;
  buildstats: BuildStats[];
  tagStats: TagStats[];
  startedAt: Date;
  endedAt: Date;
  durationMs: number;
  executionStage: ExecutionStage;
  testRunner: string;
  name: string;
  result: string;
  tags: Tag[];
  bdd: boolean;
}