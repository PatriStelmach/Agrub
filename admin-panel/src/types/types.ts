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

export interface LogObject
{
  id: number
  header: string,
  content: string,
  source: string,
  technicianGroups?: string[],
  createdAt: Date,
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
  lastModifiedBy: string,
  language: "python" | "bash" | "PowerShell",
  description?: string,
  code: string,
  weight: number,
  createdAt?: Date,
  tags: string[]
  runningIntervals?: number,
  addedAt?: Date,
  updatedAt?: Date,
  on?: boolean,
  type?: "log" | "alert"
}
//
// export interface Plugin
// {
//   id: number,
//   name: string,
//   lastModifiedBy: user(pewnie id, więc może być number)
//   description?: string,
//   code?: string,
//   tags?: string[]
//   runningIntervals?: number (w sekundach)
//   updatedAt: (api musi zmienić na now())
//   on?: boolean,
//   type?: "log" | "alert"
// }

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
