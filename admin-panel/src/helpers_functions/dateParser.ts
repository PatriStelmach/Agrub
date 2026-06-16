import { getLocalTimeZone, CalendarDateTime, ZonedDateTime, CalendarDate}
  from '@internationalized/date'

const tz = getLocalTimeZone()
export function dateParser(date: Date | string) {
  const d = new Date(date);
  const pad = (n: number) => String(n).padStart(2, '0');
  const hours = pad(d.getHours());
  const minutes = pad(d.getMinutes());
  const seconds = pad(d.getSeconds());
  const day = pad(d.getDate());
  const weekDayString = ['SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT'];
  const weekday = () => {
    return weekDayString[d.getDay()]
  }
  const month = pad(d.getMonth() + 1);
  const year = d.getUTCFullYear();

  return {
    hours,
    minutes,
    seconds,
    day,
    month,
    year,
    weekday,
    dayMonthYear: `${day}/${month}/${year}`,
    fullTime: `${hours}:${minutes}:${seconds}`,
    fullDate: `${day}/${month}/${year} - ${hours}:${minutes}:${seconds}`,
    apiDate: `${year}-${month}-${day}T${hours}:${minutes}:${seconds}Z`,
    toDate: d,
  }
}

export const toApiDate =
  (date: CalendarDate | CalendarDateTime | ZonedDateTime) => {
  return date ? dateParser(date.toDate(tz)).apiDate : undefined
}
