import type { VariantProps } from "class-variance-authority"
import { cva } from "class-variance-authority"

export { default as Badge } from "./Badge.vue"

export const badgeVariants = cva(
  "hover:shadow-md text-xs transition-all! duration-150! lg:text-sm md:pt-1 lg:pt-0.5 lg:border-3 2xl:text-md  inline-flex items-center justify-center rounded-full border px-2 py-0.5 font-medium w-fit whitespace-nowrap shrink-0 [&>svg]:size-3 gap-1 [&>svg]:pointer-events-none focus-visible:border-ring focus-visible:ring-ring/50 focus-visible:ring-[3px] aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructive transition-[color,box-shadow] overflow-hidden",
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
          "text-foreground  [a&]:hover:bg-accent [a&]:hover:text-accent-foreground",
        null:
          "border-red-badge/50 border-2  text-red-badge  bg-red-badge/20  text-xs",
        tags:
          "border-blue-badge hover:-translate-y-[1px] border-2 text-blue-badge bg-blue-badge/20 hover:bg-blue-badge/10 cursor-pointer  " +
          " hover:scale-102 hover:shadow-blue-badge  mr-1 my-1 ",
        source:
          "border-transparent  border text-primary bg-input  " +
          " ",
        origin:
          "border-blue-badge border-2  bg-blue-badge/20 hover:bg-transparent cursor-pointer " +
          " hover:shadow-blue-badge  text-blue-badge hover:scale-102 font-semibold",
        ack_type:"border-green-badge/50 border-2  text-green-badge  bg-green-badge/20  text-xs",
        number:
        " items-start  bg-green-badge/70 h-5 min-w-5 px-1 py-0.5! lg:py-0! rounded-full lg:text-xs! f tabular-nums text-primary"
      },
    },
    defaultVariants: {
      variant: "default",
    },
  },
)
export type BadgeVariants = VariantProps<typeof badgeVariants>
