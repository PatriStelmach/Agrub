export interface Alert
{
  id: number
  header: string,
  source: string,
  status: "In Process" | "Done",
  priority: "low" | "medium" | "high",
  technicianGroups?: string[],
  createdAt: Date,
  closedAt?: Date,
}
