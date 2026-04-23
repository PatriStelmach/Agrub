export const api_url = 'http://localhost:10000/api'

export interface AlertObject
{
  id: number
  header: string,
  source: string,
  status: "Sent" | "In Process" | "Done",
  severity: Severity,
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
  role: "Administrator" | "Technician"
  groups: string[]
  avatar?: string
}

export interface MyPlugin
{
  name: string,
  fileName: string
  creator: string,
  language: Language
  description?: string,
  code?: string,
  weight: number,
  tags: string[]
  cronExpression: string
  updatedAt: Date,
  active: boolean,
  severity: Severity,
}

export interface MyPluginsFromApi
{
  active: boolean
  creator: string,
  severity: Severity,
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
  language: Language,
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
  language: Language,
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

export interface ApiResponse {
  success: boolean,
  message: string,
  code: number,
}

export enum Severity {
  UNKNOWN = 'Unknown',
  NOT_CLASSIFIED = 'Not classified',
  OK = 'Ok',
  INFORMATION = 'Information',
  LOW = 'Low',
  WARNING = 'Warning',
  MEDIUM = 'Medium',
  AVERAGE = 'Average',
  HIGH = 'High',
  DISASTER = 'Disaster',
  CRITICAL = 'Critical'
}

export enum Language {
  PYTHON = ".py",
  SH = ".sh",
  BASH = ".bash",
  POWERSHELL = ".ps1",
  POWERSHELL_MODULE = ".psm1"
}
