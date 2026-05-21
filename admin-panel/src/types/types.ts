export const api_url = '/api'

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
  ackUpdate: boolean,
  previousSeverity: 0 | 1 | 2 | 3 | 4 | 5,
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
  password?: string,
  active?: boolean,
  email: string,
  role?: "ADMINISTRATOR" | "TECHNICIAN"
  groups?: UserGroup[],
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

export interface SystemsStatus {
  wazuh_enabled: boolean
  nagios_enabled: boolean
  zabbix_enabled: boolean
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

//tak przychodzi z api
export interface WazuhConfig {
  name: string
  wazuh_user: string
  wazuh_password_SECRET?: string
  wazuh_url: string
  wazuh_enabled: boolean
  wazuh_min_active_level: number
}
//tak przychodzi z api
export interface NagiosConfig {
  name?: string
  nagios_url: string
  nagios_enabled: boolean
  nagios_user: string
  nagios_password_SECRET?: string
}
//tak przychodzi z api
export interface ZabbixConfig {
  name: string
  zabbix_enabled: boolean
  zabbix_url: string
  zabbix_api_token_SECRET?: string
}
//to jest moje po mapowaniu
export interface MonitoringSystemsConfig  {
  name: string
  user?: string
  passwordOrToken?: string
  url: string
  enabled: boolean
  min_active_level?: number
}

//tak przychodzi z api
export interface AlertSystemSettings {
  wazuh_user: string
  wazuh_password_SECRET?: string
  wazuh_url: string
  wazuh_enabled: boolean
  wazuh_min_active_level: number

  nagios_url: string
  nagios_enabled: boolean
  nagios_user: string
  nagios_password_SECRET?: string

  zabbix_enabled: boolean
  zabbix_url: string
  zabbix_api_token_SECRET?: string

  smtp_host: string
  smtp_port: string
  smtp_user: string
  smtp_password_SECRET?: string
  smtp_enabled: boolean

  external_system_sync_timer: string
  scripts_execution_timeout_seconds: string

  SECURITY_PASSWORD_LIFETIME_DAYS: string
  SECURITY_ACCESS_TOKEN_EXP_MINUTES: string
  SECURITY_REFRESH_TOKEN_EXP_HOURS: string
  SECURITY_AD_DOMAIN: string
  SECURITY_AD_URL: string
  SECURITY_LDAP_BASE_DN: string
  SECURITY_LDAP_USER_DN_PATTERN: string
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
  sourcePattern?: string
  sourceType?: MatchType
  contentPattern?: string
  contentType?: MatchType
  subjectPattern?: string
  subjectMatchType?: MatchType
  originPattern?: string
  originMatchType?: MatchType
  minSeverity: 0 | 1 | 2 | 3 | 4 | 5
  playSound: boolean
}

export const initialRule = (userGroupId: number): Rule =>{
  return {
    userGroupId: userGroupId,
    sourcePattern: undefined,
    sourceType: MatchType.CONTAINS,
    contentPattern: undefined,
    contentType: MatchType.CONTAINS,
    subjectPattern: undefined,
    subjectMatchType: MatchType.CONTAINS,
    originPattern: undefined,
    originMatchType: MatchType.CONTAINS,
    minSeverity: 0,
    playSound: false,
  }
}

export const initialGroupDetails: GroupDetails = {
  name: '',
  rules: [],
  users: []
}

export const isBash = (lang: string)  =>
  lang === Language.SH || lang === Language.BASH

export const isPython = (lang: string)  =>
  lang === Language.PYTHON

export const isPowerShell = (lang: string)  =>
  lang === Language.POWERSHELL_MODULE || lang === Language.POWERSHELL
