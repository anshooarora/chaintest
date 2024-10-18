import { Page } from "./page.model";
import { Tag } from "./tag.model";

export class Build extends Page<Build> {
  id: number;
  stats: any;
  startedAt: Date;
  endedAt: Date;
  durationMs: number;
  testRunner: string;
  name: string;
  result: string;
  tags: Tag[];
}