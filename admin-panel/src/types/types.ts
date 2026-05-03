export const api_url = 'http://localhost:10000/api'

export interface ActiveAlert {
  id: number
  externalEventId: string
  subject: string,
  message: string,
  source: string,
  originType: string,
  acknowledged: boolean,
  createdAt: Date,
  //actions: ActionResponse[],
  severity: 0 | 1 | 2 | 3 | 4 | 5,
}


export interface HistoryAlert extends ActiveAlert {
  closedAt: Date
}

export interface Actions {
  id: number
  alertId?: number
  author: string,
  ack?: boolean,
  message?: string,
  newSeverity?: 0 | 1 | 2 | 3 | 4 | 5,
}

export interface ActionResponse extends Actions {
  actionType: string
  problemId: number,
  createdAt: Date,
}

export interface AlertDetails {
  message?: string,
  subject?: string,
  severity?: 0 | 1 | 2 | 3 | 4 | 5
}




export interface User {
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
  severity: 0 | 1 | 2 | 3 | 4 | 5,
}

export interface MyPluginsFromApi
{
  active: boolean
  creator: string,
  severity: 0 | 1 | 2 | 3 | 4 | 5,
  name: string,
  fileName: string,
  language: Language,
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


export enum Language {
  PYTHON = ".py",
  SH = ".sh",
  BASH = ".bash",
  POWERSHELL = ".ps1",
  POWERSHELL_MODULE = ".psm1"
}
