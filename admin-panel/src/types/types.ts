export interface AlertObject
{
  id: number
  header: string,
  source: string,
  status: "Sent" | "In Process" | "Done",
  priority: "low" | "medium" | "high",
  technicianGroups?: string[],
  createdAt: Date,
  closedAt?: Date,
}

export interface User
{
  id: number,
  firstname: string,
  surname: string,
  active: boolean,
  email?: string,
  role: "admin" | "technician"
  groups: string[]
  avatar?: string | File
}

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

export interface System
{
  id: number,
  name: string,
  img: string,
  openSource: boolean,
  description: string
}

export interface fileType
{
  id:number,
  file:File,
  progress:number
}

export type Paginable = Plugin | AlertObject;
