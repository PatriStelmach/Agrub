import MockAdapter from 'axios-mock-adapter'
import api from "../lib/axios.js";
import {ActiveAlert, Language, MyPlugin} from '../types/types.js'

export const adminUser = {
  id: 1,
  firstname: 'Admin',
  surname: 'Systemu',
  groups: [{id: 1, name:'ADMIN'}],
  autoLogoutMinutes: 1,
  lastPasswordChangeDate: '2026-05-15T10:00:00Z',
  sub: 'admin@pjatk.pl',
  role: 'ADMINISTRATOR'
}

export const techUser = {
  id: 2,
  firstname: 'Tech',
  surname: 'Systemu',
  groups: [{id: 2, name:'DATABASE'}],
  autoLogoutMinutes: 5,
  lastPasswordChangeDate: '2026-05-16T10:00:00Z',
  sub: 'tech@pjatk.pl',
  role: 'TECHNICIAN'
}

export const now = new Date()
export const later = new Date(now.getTime() + 60_000)
export const earlier = new Date(now.getTime() - 60_000)

export const mockPlugin: MyPlugin = {
  name: 'plugin',
  fullName: 'plugin.py',
  creator: 'admin',
  language: Language.PYTHON,
  description: 'custom plugin',
  code: 'print(f"Argument {i}: {arg}")',
  arguments: 'arg1',
  weight: 1,
  tags: ['python'],
  cronExpression: '1 1 * * * *',
  updatedAt: now,
  active: true,
  severity: 3,
}

export const mockAlert:ActiveAlert = {
  id: 1,
  subject: 'host down',
  message: 'test alert',
  severity: 5,
  isAcknowledged: false,
  actions: [
    {
      id: 1,
      ack: true,
      ackUpdate: true,
      author: 'admin@pjatk.pl',
      problemId: 1,
      createdAt: later
    }
  ],
  createdAt: now,
  originType: 'ZABBIX',
  source: 'localhost',
}

export const earlierAlert:ActiveAlert = {
  id: 2,
  subject: 'swap usage over 95%',
  message: 'too big swap',
  severity: 1,
  isAcknowledged: true,
  actions: [
    {
      id: 2,
      ack: false,
      ackUpdate: true,
      author: 'admin@pjatk.pl',
      problemId: 2,
      createdAt: now
    }
  ],
  createdAt: earlier,
  originType: 'WAZUH',
  source: 'localhost',
}

export const editedMockAlert:ActiveAlert = {
  id: 1,
  subject: 'host down',
  message: 'test alert',
  severity: 2,
  isAcknowledged: true,
  actions: [
    {
      id: 1,
      ack: true,
      ackUpdate: true,
      author: 'admin@pjatk.pl',
      problemId: 1,
      createdAt: later
    }
  ],
  createdAt: now,
  originType: 'ZABBIX',
  source: 'localhost',
}

export const mockGroup = {
  id: 1,
  name: 'ADMIN',
}

export const mockSecondGroup = {
  id: 2,
  name: 'DATABASES',
}

export const fullJWT = (isAdmin: boolean) => {
    const header = btoa(JSON.stringify({alg: 'HS256', typ: 'JWT'}))
    const body = btoa(JSON.stringify(isAdmin ? adminUser : techUser))
    const sig = 'fakesig'
  return `${header}.${body}.${sig}`
}

export const mock = new MockAdapter(api)
