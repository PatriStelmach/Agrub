export const api_url = 'http://localhost:10000/api'

export interface AlertObject
{
  id: number
  header: string,
  source: string,
  status: "Sent" | "In Process" | "Done",
  severity: string,
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
  severity: string,
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
  name: string,
  fileName: string
  creator: string,
  language: ".py" | ".sh" | ".bash" | ".ps1"  | ".psm1",
  description?: string,
  code?: string,
  weight: number,
  tags: string[]
  cronExpression: string
  updatedAt: Date,
  active: boolean,
  log: boolean
}

export interface MyPluginsFromApi
{
  active: boolean
  creator: string,
  isLog: boolean,
  name: string,
  fileName: string,
  language: string,
  updatedAt: Date,
  weight: number,
  tags: string[],
  cronExpression: string,
}

export interface PluginDetails
{
  description: string,
  code: string,
}

export interface ImportPlugin {
  name: string,
  creator: string,
  language: ".py" | ".sh" | ".bash" | ".ps1"  | ".psm1",
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
  description?: string,
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

