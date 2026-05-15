export const api_url = 'http://localhost:10000/api'

export enum Language {
  PYTHON = ".py",
  SH = ".sh",
  BASH = ".bash" ,
  POWERSHELL = ".ps1",
  POWERSHELL_MODULE = ".psm1"
}

export enum MatchType {
  EXACT = "EXACT",
  STARTS_WITH = "STARTS_WITH",
  ENDS_WITH = "ENDS_WITH",
  CONTAINS = "CONTAINS",
  REGEX = "REGEX",
}

export interface ActiveAlert {
  id: number
  externalEventId: string
  subject: string,
  message: string,
  source: string,
  originType: string,
  acknowledged: boolean,
  createdAt: Date,
  actions: ActionResponse[],
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

export interface MyJWTPayload {
  role: "ADMINISTRATOR" | "TECHNICIAN";
  firstname: string,
  surname: string,
  sub: string;
  iat: number;
  exp: number;
}

export const undefinedAlertsFilters = {
  severity: undefined,
  message: undefined,
  subject: undefined,
  source: undefined,
  origin: undefined,
  ack: undefined,
  unack: undefined,
  createdDateFrom: undefined,
  createdDateTo: undefined,
  closedDateFrom: undefined,
  closedDateTo: undefined
}

export interface AlertHistoryFilters {
  severity?: number[]
  message?: string
  subject?: string
  source?: string
  origin?: string[]
  ack? : boolean
  unack? : boolean
  createdDateFrom?: string
  createdDateTo?: string
  closedDateFrom?: string
  closedDateTo?: string
}

export interface LibraryPluginFilters {
  name?: string,
  language?: Language,
  creator?: string,
  tags?: string[],
  maxWeight?: number,
}

export const undefinedLibraryFilters = {
  name: undefined,
  language: undefined,
  creator: undefined,
}

export interface User {
  id?: number,
  firstname: string,
  surname: string,
  active?: boolean,
  email: string,
  role?: "ADMINISTRATOR" | "TECHNICIAN"
  groups?: string[]
}

export const blankUser = {
  firstname: '',
  surname: '',
  email: '',
  role: "TECHNICIAN",
  groups: []
}

export interface UserGroup {
  id: number,
  name: string,
}

export interface UserGroupStats extends UserGroup {
  userCount: number,
  ruleCount: number
}

export interface MyPlugin
{
  name: string,
  fullName: string
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
  fileName: string,
  creator: string,
  language: Language,
  description?: string,
  code?: string,
  weight: number,
  createdAt: Date,
  tags: string[],
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



export interface GroupDetails {
  id?: number,
  name: string,
  rules: Rule[],
  users: User[]
}



export interface Rule {
  id?: number
  userGroup?: UserGroup
  userGroupId?: number,
  sourcePattern: string
  sourceType: MatchType
  contentPattern: string
  contentType: MatchType
  subjectPattern: string
  subjectMatchType: MatchType
  originPattern: string
  originMatchType: MatchType
  minSeverity: number
  playSound: boolean
}

export const initialRule = (userGroupId?: number): Rule =>{
  return {
    userGroupId: userGroupId,
    sourcePattern: '',
    sourceType: MatchType.CONTAINS,
    contentPattern: '',
    contentType: MatchType.CONTAINS,
    subjectPattern: '',
    subjectMatchType: MatchType.CONTAINS,
    originPattern: '',
    originMatchType: MatchType.CONTAINS,
    minSeverity: 0,
    playSound: false,

  }
}

export const InitialGroupDetails: GroupDetails = {
  name: '',
  rules: Array.of(initialRule()),
  users: []
}

export const isBash = (lang: string)  =>
  lang === Language.SH || lang === Language.BASH

export const isPython = (lang: string)  =>
  lang === Language.PYTHON

export const isPowerShell = (lang: string)  =>
  lang === Language.POWERSHELL_MODULE || lang === Language.POWERSHELL
