import type { VariantProps } from "class-variance-authority"
import { cva } from "class-variance-authority"

export { default as Button } from "./Button.vue"

export const buttonVariants = cva(
  "inline-flex cursor-pointer items-center justify-center gap-2 whitespace-nowrap rounded-md text-sm font-medium  duration-100 disabled:pointer-events-none disabled:opacity-50 [&_svg]:pointer-events-none [&_svg:not([class*='size-'])]:size-4 shrink-0 [&_svg]:shrink-0 outline-none focus-visible:border-ring focus-visible:ring-ring/50 focus-visible:ring-[3px] aria-invalid:ring-red-badge/20 dark:aria-invalid:ring-red-badge/40 aria-invalid:border-red-badge",
  {
    variants: {
      variant: {
        default:
          " bg-primary text-primary-foreground hover:bg-primary/90",
        destructive:
          " bg-red-badge text-white hover:bg-red-badge/90 focus-visible:ring-red-badge/20 dark:focus-visible:ring-red-badge/40 dark:bg-red-badge/60",
        outline:
          " border bg-background shadow-xs hover:bg-accent hover:text-accent-foreground dark:bg-input/30 dark:border-input dark:hover:bg-input",
        secondary:
          " bg-secondary text-secondary-foreground hover:bg-secondary/80",
        ghost:
          " hover:bg-accent hover:text-accent-foreground dark:hover:bg-accent/50",
        link: "cursor-pointer text-primary underline-offset-4 hover:underline",
        green:
        " bg-green-600 hover:bg-green-badge text-white ",
        green_outline:
        "bg-background border font-bold dark:border-input   dark:bg-input/30 text-green-badge/90 hover:bg-green-badge/70 hover:border-green-badge hover:text-primary",
        yellow_outline:
          "bg-background border font-bold dark:border-input  dark:bg-input/30 text-yellow-badge/90 hover:bg-yellow-badge/80 hover:border-yellow-badge hover:text-primary",
        red_outline:
          "bg-background border font-bold dark:border-input  dark:bg-input/30 text-red-badge hover:bg-red-badge hover:border-red-badge hover:text-primary",
        blue_outline:
          "bg-background border font-bold dark:border-input  dark:bg-input/30 text-blue-badge/90 hover:bg-blue-badge/80 hover:border-blue-badge hover:text-primary",
        orange_outline:
          "bg-background border font-bold dark:border-input  dark:bg-input/30 text-orange-badge/90 hover:bg-orange-badge/80 hover:border-orange-badge hover:text-primary",
        green_inside:
        "bg-background dark:bg-input/30 dark:border-green-badge border-2 text-green-badge  hover:shadow-[0_0_10px_1px] hover:shadow-green-badge ",
        orange_inside:
          "bg-background dark:bg-input/30 dark:border-orange-badge border-2 text-orange-badge  hover:shadow-[0_0_10px_1px] hover:shadow-orange-badge ",
        red_inside:
          "bg-background dark:bg-input/30 dark:border-red-badge border-2 text-red-badge   hover:shadow-[0_0_10px_1px] hover:shadow-red-badge ",
        yellow_inside:
          "bg-background dark:bg-input/30 dark:border-yellow-badge border-2 text-yellow-badge   hover:shadow-[0_0_10px_1px] hover:shadow-yellow-badge ",
      },
      size: {
        "default": "h-9 px-4 py-2 has-[>svg]:px-3",
        "sm": "h-8 rounded-md gap-1.5 px-3 has-[>svg]:px-2.5",
        "lg": "h-10 rounded-md px-6 has-[>svg]:px-4",
        "icon": "size-9",
        "icon-sm": "size-8",
        "icon-lg": "size-10",
      },
    },
    defaultVariants: {
      variant: "default",
      size: "default",
    },
  },
)
export type ButtonVariants = VariantProps<typeof buttonVariants>
