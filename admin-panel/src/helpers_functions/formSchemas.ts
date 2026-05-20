import {toTypedSchema} from "@vee-validate/zod";
import z from "zod";

export const nagiosSchema = toTypedSchema(
  z.object({
    nagios_url: z.string().url('Must be a valid URL'),
    nagios_user: z.string().min(1, 'User is required').nullable(),
    nagios_password_SECRET: z.string()
  .optional()
  })
)

export const wazuhSchema = toTypedSchema(
  z.object({
    wazuh_url: z.string().url('Must be a valid URL'),
    wazuh_user: z.string().min(1, 'User is required'),
    wazuh_password_SECRET: z.string()
  .optional(),
    wazuh_min_active_level: z.number()
      .min(0, '0 is minimal severity level')
      .max(16, '16 is maximal severity level'),
  })
)

export const zabbixSchema = toTypedSchema(
  z.object({
    zabbix_url: z.string().url('Must be a valid URL'),
    zabbix_api_token_SECRET: z.string()
      .optional(),
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

