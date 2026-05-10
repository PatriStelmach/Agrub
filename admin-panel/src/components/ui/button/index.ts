import type { VariantProps } from "class-variance-authority"
import { cva } from "class-variance-authority"

export { default as Button } from "./Button.vue"

export const buttonVariants = cva(
  "inline-flex cursor-pointer items-center justify-center gap-2 whitespace-nowrap rounded-md text-sm font-medium  duration-25 disabled:pointer-events-none disabled:opacity-50 [&_svg]:pointer-events-none [&_svg:not([class*='size-'])]:size-4 shrink-0 [&_svg]:shrink-0 outline-none focus-visible:border-ring focus-visible:ring-ring/50 focus-visible:ring-[3px] aria-invalid:ring-red-badge/20 aria-invalid:ring-red-badge/40 aria-invalid:border-red-badge",
  {
    variants: {
      variant: {
        default:
          " bg-primary text-primary-foreground hover:bg-primary/90",
        outline:
          " border bg-background shadow-xs hover:bg-accent hover:text-accent-foreground bg-input/30 border-input hover:bg-input",
        secondary:
          " bg-secondary text-secondary-foreground hover:bg-secondary/80",
        ghost:
          " hover:bg-accent hover:text-accent-foreground hover:bg-accent/50",
        link: "cursor-pointer text-primary underline-offset-4 hover:underline",



        green_outline:
        "bg-background border font-bold border-input  hover:bg-green-badge/60 bg-input/30 text-green-badge/90 light:hover:bg-green-badge/70 hover:border-green-badge hover:text-primary",
        yellow_outline:
          "bg-background border font-bold border-input  bg-input/30 text-yellow-badge/90 light:hover:bg-yellow-badge/70 hover:bg-yellow-badge/80 hover:border-yellow-badge hover:text-primary",
        red_outline:
          "bg-background border font-bold border-input hover:bg-red-badge/70 light:hover:bg-red-badge/80  bg-input/30 text-red-badge  hover:border-red-badge hover:text-primary",
        blue_outline:
          "bg-background border font-bold border-input hover:bg-blue-badge/50  bg-input/30 text-blue-badge/90 hover:bg-blue-badge/80 hover:border-blue-badge hover:text-primary",
        orange_outline:
          "bg-background border font-bold border-input  bg-input/30 text-orange-badge/90 light: hover:bg-orange-badge/60 hover:bg-orange-badge/70 hover:border-orange-badge hover:text-primary",



        green_inside:
        "bg-background bg-input/30 border-green-badge border-2 text-green-badge  hover:shadow-[0_0_10px_1px] hover:shadow-green-badge ",
        orange_inside:
          "bg-background bg-input/30 border-orange-badge border-2 text-orange-badge  hover:shadow-[0_0_10px_1px] hover:shadow-orange-badge ",
        red_inside:
          "bg-background bg-input/30 border-red-badge border-2 text-red-badge   hover:shadow-[0_0_10px_1px] hover:shadow-red-badge ",
        yellow_inside:
          "bg-background bg-input/30 border-yellow-badge border-2 text-yellow-badge   hover:shadow-[0_0_10px_1px] hover:shadow-yellow-badge ",
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
