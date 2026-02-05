export interface Alert
{
  id: number
  header: string,
  source: string,
  status: string,
  priority: string,
  technician?: string,
  createdAt: Date,
  closedAt?: Date,
}
