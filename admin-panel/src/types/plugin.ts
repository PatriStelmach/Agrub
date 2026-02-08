export interface Plugin
{
  id: number,
  name: string,
  creator: string,
  language: "python" | "bash" | "PowerShell",
  description?: string,
  weight: number,
  createdAt?: Date,
  tags: string[]
  runningIntervals?: string,
  addedAt?: Date,
  updatedAt?: Date,
  on?: boolean,
}
