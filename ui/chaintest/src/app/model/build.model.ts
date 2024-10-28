import { ExecutionStage } from "./execution-stage.model";
import { Page } from "./page.model";
import { Result } from "./result.model";
import { Tag } from "./tag.model";

export class Build extends Page<Build> {
  id: number;
  runStats: any;
  tagStats: any;
  startedAt: Date;
  endedAt: Date;
  durationMs: number;
  executionStage: ExecutionStage;
  testRunner: string;
  name: string;
  result: Result;
  tags: Tag[];
}