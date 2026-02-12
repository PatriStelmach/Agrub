export interface User
{
  id: number,
  name: string,
  role: "admin" | "technician"
  groups: string[]
  avatar: string | File
}
