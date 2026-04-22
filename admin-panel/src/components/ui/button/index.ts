import type { VariantProps } from "class-variance-authority"
import { cva } from "class-variance-authority"

export { default as Button } from "./Button.vue"

export const buttonVariants = cva(
  "inline-flex cursor-pointer items-center justify-center gap-2 whitespace-nowrap rounded-md text-sm font-medium transition-all duration-50 disabled:pointer-events-none disabled:opacity-50 [&_svg]:pointer-events-none [&_svg:not([class*='size-'])]:size-4 shrink-0 [&_svg]:shrink-0 outline-none focus-visible:border-ring focus-visible:ring-ring/50 focus-visible:ring-[3px] aria-invalid:ring-badge2/20 dark:aria-invalid:ring-badge2/40 aria-invalid:border-badge2",
  {
    variants: {
      variant: {
        default:
          " bg-primary text-primary-foreground hover:bg-primary/90",
        destructive:
          " bg-badge2 text-white hover:bg-badge2/90 focus-visible:ring-badge2/20 dark:focus-visible:ring-badge2/40 dark:bg-badge2/60",
        outline:
          " border bg-background shadow-xs hover:bg-accent hover:text-accent-foreground dark:bg-input/30 dark:border-input dark:hover:bg-input",
        secondary:
          " bg-secondary text-secondary-foreground hover:bg-secondary/80",
        ghost:
          " hover:bg-accent hover:text-accent-foreground dark:hover:bg-accent/50",
        link: "cursor-pointer text-primary underline-offset-4 hover:underline",
        green:
        " bg-green-600 hover:bg-badge1 text-white ",
        green_outline:
        "bg-background border font-bold dark:border-input  dark:bg-input/30 text-badge1/90 hover:bg-badge1/70 hover:border-badge1 hover:text-primary",
        yellow_outline:
          "bg-background border font-bold dark:border-input  dark:bg-input/30 text-yellow-500/90 hover:bg-yellow-500/80 hover:border-yellow-500 hover:text-primary",
        red_outline:
          "bg-background border font-bold dark:border-input  dark:bg-input/30 text-badge2 hover:bg-badge2 hover:border-red-500 hover:text-primary",
        cyan_outline:
          "bg-background border font-bold dark:border-input  dark:bg-input/30 text-cyan-500/90 hover:bg-cyan-500/80 hover:border-cyan-500 hover:text-primary",
        orange_outline:
          "bg-background border font-bold dark:border-input  dark:bg-input/30 text-amber-500/90 hover:bg-amber-500/80 hover:border-amber-500 hover:text-primary",
        green_inside:
        "bg-background dark:bg-input/30 dark:border-badge1 border-2 text-badge1  hover:shadow-[0_0_10px_1px] hover:shadow-badge1 ",
        orange_inside:
          "bg-background dark:bg-input/30 dark:border-amber-600 border-2 text-amber-600  hover:shadow-[0_0_10px_1px] hover:shadow-amber-600 ",
        red_inside:
          "bg-background dark:bg-input/30 dark:border-badge2 border-2 text-badge2   hover:shadow-[0_0_10px_1px] hover:shadow-badge2 ",
        yellow_inside:
          "bg-background dark:bg-input/30 dark:border-yellow-500 border-2 text-yellow-500   hover:shadow-[0_0_10px_1px] hover:shadow-yellow-500 ",
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
