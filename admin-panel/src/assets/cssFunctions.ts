export const inputText = 'text-sm!  xl:text-xl! 2xl:text-2xl!';
export const smallNameLabel = 'text-sm font-bold lg:text-md  xl:text-lg 2xl:text-2xl'
export const bigNameLabel = 'font-bold text-label text-lg xl:text-xl 2xl:text-2xl'
export const dataTable = "w-99/100 text-xs lg:text-sm xl:text-md 2xl:text:lg h-full mx-auto overflow-auto table-fixed"
export const tableCaption = "bg-background border-t-2 text-foreground sticky z-9 bottom-0 py-2 text-md lg:text-lg xl:text-xl 2xl:text:3xl"
export const tableHeaders = "hover:bg-background bg-background border-b-2! text-sm gap-0 space-0 "
export const tableDiv = " mt-[2vh] mx-[1%] w-98/100 relative overflow-auto max-md:max-h-[80vh] max-h-[90vh]"
export const topDiv = "relative max-h-[10vh] items-center align-middle"
export const topH1 = "text-center my-[2vh] font-bold text-label text-2xl xl:text-3xl 2xl:text-4xl border-b pb-[2vh]"
export const topButtonGroup = "absolute left-4 top-0 flex max-w-2/5 "
export const unwrappedItemClass = "bg-selected [&_td]:align-top  sticky h-26! [&_td]:pt-4! top-11 bottom-11 hover:bg-card z-9 cursor-auto"
export const tagsContainer = "max-h-30 mb-2  overflow-y-auto border-2 border-t-0! border-input p-2 rounded-b-md"
export const gridSkeletons = " px-6 py-2 grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-6 max-h-[90vh] overflow-y-auto"
export const gridCard = "shadow-[5px_5px_10px_1px] shadow-secondary border-2 my-2  min-h-44 flex  hover:border-blue-badge/50 hover:shadow-blue-badge/50 transition-all"
export const gridSystemCardUnwrapped =
  "shadow-[5px_5px_10px_1px]  border-2 my-2 flex transition-all  cursor-auto relative  border-blue-badge/50 shadow-blue-badge/50"
export const gridSystemCard =
  "shadow-[5px_5px_10px_1px] shadow-secondary border-2 my-2 cursor-auto relative h-110 flex  hover:border-blue-badge/50 hover:shadow-blue-badge/50 transition-all "

export const hoverListRow = (extra = '') => `odd:bg-accent/30 *:py-1 hover:bg-blue-badge/10 duration-0 ${extra}`.trim()
