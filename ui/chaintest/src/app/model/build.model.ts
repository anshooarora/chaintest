import { ExecutionStage } from "./execution-stage.model";
import { Page } from "./page.model";
import { Result } from "./result.model";
import { RunStats } from "./run-stats.model";
import { Tag } from "./tag.model";

export class Build extends Page<Build> {
  id: number;
  runStats: RunStats[];
  tagStats: any;
  startedAt: Date;
  endedAt: Date;
  durationMs: number;
  executionStage: ExecutionStage;
  testRunner: string;
  name: string;
  result: Result;
  tags: Tag[];
  bdd: boolean;
}