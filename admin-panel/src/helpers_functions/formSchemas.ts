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
    wazuh_password_SECRET: z.string().optional(),
    wazuh_critical_level: z
      .number()
      .int('Must be a positive integer')
      .min(0, '0 is minimal critical level')
      .max(15, '16 is maximal critical level'),
    wazuh_warning_level: z
      .number()
      .int('Must be a positive integer')
      .min(0, '0 is minimal warning level')
      .max(15, '16 is maximal warning level'),
    wazuh_info_as_alerts: z.boolean(),
  })
    .superRefine((data, ctx) => {
      if(data.wazuh_warning_level > data.wazuh_critical_level) {
        ctx.addIssue({
          code: z.ZodIssueCode.custom,
          message: 'Critical level must be higher or equeal to warning',
          path: ["wazuh_critical_level"],
        })
        ctx.addIssue({
          code: z.ZodIssueCode.custom,
          message: 'Warning level must be lower or equeal to critical',
          path: ["wazuh_warning_level"],
        })
      }
    })
)

export const zabbixSchema = toTypedSchema(
  z.object({
    zabbix_url: z.string().url('Must be a valid URL'),
    zabbix_api_token_SECRET: z.string()
      .optional(),
  })
)

export const alertLoginSchema = toTypedSchema(
  z.object({
    email: z
      .string()
      .email('Invalid email address'),
    password: z
      .string()
      .min(4, 'Password must be at least 4 characters.')
  }),
)

export const ADLoginSchema = toTypedSchema(
  z.object({
    email: z
      .string()
      .min(1,'Username is required'),
    password: z
      .string()
      .min(4, 'Password must be at least 4 characters.')
  }),
)

export const groupSchema = toTypedSchema(
  z.object({
    groupName:z
      .string()
      .min(1, 'Group name is required').max(30, 'At most 30 characters.'),
  })
)

export const createUserSchema = toTypedSchema(
  z.object({
    email: z
      .string()
      .email('Invalid email address'),
    firstname: z
      .string()
      .min(2, 'Firstname must be at least 2 characters.'),
    surname: z
      .string()
      .min(2, 'Surname must be at least 2 characters.'),
    password: z
      .string()
      .min(4, 'Password must be at least 4 characters.'),
    confirmPassword: z
      .string()
      .min(4, 'Password must be at least 4 characters.')
  })
    .refine((data) =>
      data.password === data.confirmPassword, {
      message: "Passwords don't match!",
      path: ["password"]
    }))

export const editUserSchema = toTypedSchema(
  z.object({
    email: z.string().email('Invalid email address'),
    firstname: z.string().min(2, 'Firstname must be at least 2 characters.'),
    surname: z.string().min(2, 'Surname must be at least 2 characters.'),
  })
)

export const editCurrentUserSchema = toTypedSchema(
  z.object({
    id: z.number(),
    email: z.string().email('Invalid email address'),
    firstname: z.string().min(2, 'Firstname must be at least 2 characters.'),
    surname: z.string().min(2, 'Surname must be at least 2 characters.'),
    autoLogoutMinutes: z
      .number()
      .int('Must be a positive integer')
      .min(1, "Cannot be lower than 1 minute"),
    groups: z.array(z.object({
      name: z.string(),
      id: z.number(),
    }))
  }))

export const changePasswordSchema = toTypedSchema(
  z.object({
    oldPassword: z
      .string()
      .min(8, 'Password must be at least 8 characters.'),
    newPassword: z
      .string()
      .min(8, 'Password must be at least 8 characters.'),
    confirmPassword: z
      .string()
      .min(8, 'Password must be at least 8 characters.'),
  }).superRefine((data,ctx) => {
    if(data.newPassword !== data.confirmPassword) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        message: "Passwords don't match!",
        path: ["confirmPassword"]
      })
    }
    if(data.newPassword === data.oldPassword) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        message: "New password must be different",
        path: ["newPassword"]
      })
    }
  })
)

export const securitySettingSchema = toTypedSchema(  z.object({
    SECURITY_PASSWORD_LIFETIME_DAYS: z
      .number()
      .int('Must be a positive integer')
      .min(1, "Cannot be lower than 1 dat"),
    SECURITY_ACCESS_TOKEN_EXP_MINUTES: z
      .number()
      .int('Must be a positive integer')
      .min(1, "Cannot be lower than 1 hour"),
    SECURITY_REFRESH_TOKEN_EXP_HOURS: z
      .number()
      .int('Must be a positive integer')
      .min(1, "Cannot be lower than 1 minute"),
    SECURITY_AD_DOMAIN: z.string().url('Must be a valid URL'),
    SECURITY_AD_URL: z.string().url('Must be a valid URL'),
    SECURITY_LDAP_BASE_DN: z
      .string()
      .trim()
      .regex(/^(?:[A-Za-z][A-Za-z0-9-]*|(?:0|[1-9][0-9]*)(?:\.(?:0|[1-9][0-9]*))+)=(?:#(?:[0-9A-Fa-f]{2})+|(?:[^,=\+<>#;\\\"]|\\[,=\+<>#;\\\"\ ]|\\[0-9A-Fa-f]{2}|\"[^\"]*\")*(?:(?:[^,=\+<>#;\\\"\  ]|\\[,=\+<>#;\\\"\ ]|\\[0-9A-Fa-f]{2}|\"[^\"]*\")(?:[^,=\+<>#;\\\"]|\\[,=\+<>#;\\\"\ ]|\\[0-9A-Fa-f]{2}|\"[^\"]*\")*)?)(?:\s*[,;+]\s*(?:[A-Za-z][A-Za-z0-9-]*|(?:0|[1-9][0-9]*)(?:\.(?:0|[1-9][0-9]*))+)=(?:#(?:[0-9A-Fa-f]{2})+|(?:[^,=\+<>#;\\\"]|\\[,=\+<>#;\\\"\ ]|\\[0-9A-Fa-f]{2}|\"[^\"]*\")*(?:(?:[^,=\+<>#;\\\"\  ]|\\[,=\+<>#;\\\"\ ]|\\[0-9A-Fa-f]{2}|\"[^\"]*\")(?:[^,=\+<>#;\\\"]|\\[,=\+<>#;\\\"\ ]|\\[0-9A-Fa-f]{2}|\"[^\"]*\")*)?))*$/),
    SECURITY_LDAP_USER_DN_PATTERN: z
      .string()
      .regex(/^(?:[A-Za-z][A-Za-z0-9-]*|(?:0|[1-9][0-9]*)(?:\.(?:0|[1-9][0-9]*))+)=\{0\}(?:\s*[,;]\s*(?:[A-Za-z][A-Za-z0-9-]*|(?:0|[1-9][0-9]*)(?:\.(?:0|[1-9][0-9]*))+)=(?:#(?:[0-9A-Fa-f]{2})*|(?:[^,=+<>#;\\"]|\\[,=+<>#;\\" ]|\\[0-9A-Fa-f]{2})*))*$/)
  }))

export const smtpSettingSchema = toTypedSchema(
  z.object({
    smtp_host: z.string().url('Must be a valid URL'),
    smtp_port: z.enum(["25", "587", "465", "2525"]),
    smtp_user: z.string().email('Invalid email address'),
    smtp_password: z.string(),
    smtp_enabled: z.boolean(),
  }))

export const alertSettingSchema = toTypedSchema(
  z.object({
    external_system_sync_timer: z
      .number()
      .int('Must be a positive integer')
      .min(1, "Cannot be lower than 1 second"),
    scripts_execution_timeout_seconds: z
      .number()
      .int('Must be a positive integer')
      .min(1, "Cannot be lower than 1 second"),
  }))
