import {toTypedSchema} from "@vee-validate/zod";
import z from "zod";

export const nagiosSchema = toTypedSchema(
  z.object({
    nagios_url: z.string().url('Must be a valid URL'),
    nagios_enabled: z.boolean(),
    nagios_user: z.string().min(1, 'User is required'),
    nagios_pass_SECRET: z.string().min(1, 'Password is required'),
  })
)

export const wazuhSchema = toTypedSchema(
  z.object({
    wazuh_url: z.string().url('Must be a valid URL'),
    wazuh_enabled: z.boolean(),
    wazuh_user: z.string().min(1, 'User is required'),
    wazuh_password_SECRET: z.string().min(1, 'Password is required'),
    wazuh_min_active_level: z.number().min(0, '0 is minimal severity level').max(16, '16 is maximal severity level'),
  })
)

export const zabbixSchema = toTypedSchema(
  z.object({
    zabbix_url: z.string().url('Must be a valid URL'),
    zabbix_enabled: z.boolean(),
    zabbix_api_token_SECRET: z.string().nonempty( 'Api token cannot be empty'),
  })
)
