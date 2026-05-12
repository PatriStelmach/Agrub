import type { User } from '@/types/types'

export const usersData: User[] = [
  {
    id: 1,
    firstname: "Anna",
    surname: "Kowalska",
    active: true,
    email: "anna.kowalska@alert.pl",
    role: "ADMINISTRATOR",
    groups: ["ADMINISTRATOR"],
  },
  {
    id: 2,
    firstname: "Piotr",
    surname: "Nowak",
    active: false,
    email: "piotr.nowak@alert.pl",
    role: "TECHNICIAN",
    groups: ["Technical", "Service", "System ADMINISTRATOR"],
  },
  {
    id: 3,
    firstname: "Ewa",
    surname: "Wiśniewska",
    active: true,
    email: "ewa.wisniewska@alert.pl",
    role: "ADMINISTRATOR",
    groups: ["ADMINISTRATOR"],
  },
  {
    id: 4,
    firstname: "Jan",
    surname: "Dąbrowski",
    active: true,
    email: "jan.dabrowski@alert.pl",
    role: "TECHNICIAN",
    groups: ["Technical", "Network"],
  },

]

