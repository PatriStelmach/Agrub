import {toTypedSchema} from "@vee-validate/zod";
import z from "zod";
import type {MonitoringSystemsConfig} from "@/types/types.ts";
import {useForm} from "vee-validate";

export const nagiosSchema = toTypedSchema(
  z.object({
    nagios_url: z.string().url('Must be a valid URL'),
    nagios_enabled: z.boolean(),
    nagios_user: z.string().min(1, 'User is required'),
    nagios_password_SECRET: z.string()
  .optional()
  .refine(val => val === undefined || val.length > 0, {
    message: 'Password cannot be empty'
  }),
    nagios_password_confirm_SECRET: z.string()
  .optional()
  .refine(val => val === undefined || val.length > 0, {
    message: 'Password cannot be empty'
  }),
  })
)

export const wazuhSchema = toTypedSchema(
  z.object({
    wazuh_url: z.string().url('Must be a valid URL'),
    wazuh_enabled: z.boolean(),
    wazuh_user: z.string().min(1, 'User is required'),
    wazuh_password_SECRET: z.string()
  .optional()
  .refine(val => val === undefined || val.length > 0, {
    message: 'Password cannot be empty'
  }),
    wazuh_password_new_SECRET: z.string()
  .optional()
  .refine(val => val === undefined || val.length > 0, {
    message: 'Password cannot be empty'
  }),
    wazuh_min_active_level: z.number()
      .min(0, '0 is minimal severity level')
      .max(16, '16 is maximal severity level'),
  })
)

export const zabbixSchema = toTypedSchema(
  z.object({
    zabbix_url: z.string().url('Must be a valid URL'),
    zabbix_enabled: z.boolean(),
    zabbix_password_SECRET: z.string()
      .optional()
      .refine(val => val === undefined || val.length > 0, {
        message: 'API token cannot be empty'
      }),
    zabbix_password_new_SECRET: z.string()
      .optional()
      .refine(val => val === undefined || val.length > 0, {
        message: 'API token cannot be empty'
      })
  })
)

export const loginSchema = toTypedSchema(
  z.object({
    email: z
      .string()
      .email('Invalid email address'),
    password: z
      .string()
      .min(4, 'Password must be at least 4 characters.')
  }),
)

const schemaMap = {
  nagios: nagiosSchema,
  wazuh: wazuhSchema,
  zabbix: zabbixSchema,
}

export const initialValuesMap = {
  nagios: (s: MonitoringSystemsConfig) => ({
    nagios_url: s.url,
    nagios_enabled: s.enabled,
    nagios_user: s.user,
  }),
  wazuh: (s: MonitoringSystemsConfig) => ({
    wazuh_url: s.url,
    wazuh_enabled: s.enabled,
    wazuh_user: s.user,
    wazuh_min_active_level: s.min_active_level,
  }),
  zabbix: (s: MonitoringSystemsConfig) => ({
    zabbix_url: s.url,
    zabbix_enabled: s.enabled,
  }),
}

export const useSystemForm = (system: MonitoringSystemsConfig) => {
  const name = system.name as keyof typeof schemaMap
  return useForm({
    validationSchema: schemaMap[name],
    initialValues: initialValuesMap[name](system) as any,
  })
}
