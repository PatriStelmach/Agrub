import type {Plugin} from "@/types/plugin.ts"
export interface MyPlugin extends Plugin
{
  runningIntervals: string,
  addedAt: Date,
  updatedAt: Date,
  on: boolean,
}
