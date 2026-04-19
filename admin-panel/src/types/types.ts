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

export interface MyPlugin
{
  id: number,
  name: string,
  creator: string,
  language: "python" | "bash" | "PowerShell",
  description: string,
  code: string,
  weight: number,
  tags: string[]
  cronExpression: string
  updatedAt: Date,
  active: boolean,
  log: boolean
}

export interface ImportPlugin {
  name: string,
  creator: string,
  language: "python" | "bash" | "PowerShell",
  code: string,
  description: string,
  tags: string[],
  log: boolean
}

export interface LibraryPlugin
{
  id: number,
  name: string,
  creator: string,
  language: "python" | "bash" | "PowerShell",
  description: string,
  code?: string,
  weight: number,
  createdAt: Date,
  tags: string[],
  log?: boolean
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

