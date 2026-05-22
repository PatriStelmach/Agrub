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
    wazuh_critical_level: z.number()
      .min(0, '0 is minimal critical level')
      .max(16, '16 is maximal critical level'),
    wazuh_warning_level: z.number()
      .min(0, '0 is minimal warning level')
      .max(16, '16 is maximal warning level'),
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

export const editCurrentUser = toTypedSchema(
  z.object({
    email: z.string().email('Invalid email address'),
    firstname: z.string().min(2, 'Firstname must be at least 2 characters.'),
    surname: z.string().min(2, 'Surname must be at least 2 characters.'),
    oldPassword: z
      .string()
      .min(4, 'Password must be at least 4 characters.'),
    newPassword: z
      .string()
      .min(4, 'Password must be at least 4 characters.'),
    confirmPassword: z
      .string()
      .min(4, 'Password must be at least 4 characters.')
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
  }))
