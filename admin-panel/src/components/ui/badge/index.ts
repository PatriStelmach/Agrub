import type { VariantProps } from "class-variance-authority"
import { cva } from "class-variance-authority"

export { default as Badge } from "./Badge.vue"

export const badgeVariants = cva(
  "inline-flex items-center justify-center rounded-full border px-2 py-0.5 text-xs font-medium w-fit whitespace-nowrap shrink-0 [&>svg]:size-3 gap-1 [&>svg]:pointer-events-none focus-visible:border-ring focus-visible:ring-ring/50 focus-visible:ring-[3px] aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructive transition-[color,box-shadow] overflow-hidden",
  {
    variants: {
      variant: {
        default:
          "border-transparent bg-primary text-primary-foreground [a&]:hover:bg-primary/90",
        secondary:
          "border-transparent bg-secondary text-secondary-foreground [a&]:hover:bg-secondary/90",
        destructive:
         "border-transparent bg-destructive text-white [a&]:hover:bg-destructive/90 focus-visible:ring-destructive/20 dark:focus-visible:ring-destructive/40 dark:bg-destructive/60",
        outline:
          "text-foreground [a&]:hover:bg-accent [a&]:hover:text-accent-foreground",
        groups:
          "border-transparent text-primary bg-badge/80 hover:bg-badge/50 cursor-pointer hover:scale-105 transition-all duration-50" +
          " hover:shadow-badge hover:shadow-md mr-1 my-1 text-sm xl:text-md xl:text-lg ",
        tags:
          "border-transparent text-primary bg-accent hover:bg-badge2 cursor-pointer hover:scale-105 transition-all duration-50" +
          " hover:shadow-badge2 hover:shadow-md mr-1 my-1 text-sm xl:text-lg 2xl:text-lg",
        source:
          "border-transparent text-primary bg-accent hover:bg-badge1 cursor-pointer hover:scale-105 transition-all duration-50" +
          " hover:shadow-badge1 hover:shadow-md text-md lg:text-lg 2xl:text-xl "
      },
    },
    defaultVariants: {
      variant: "default",
    },
  },
)
export type BadgeVariants = VariantProps<typeof badgeVariants>
