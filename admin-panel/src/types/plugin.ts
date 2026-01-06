export interface Plugin
{
  id: number,
  name: string,
  creator: string,
  language: string,
  description?: string,
  weight: number,
  createdAt?: Date,
  tags: string[]
}
