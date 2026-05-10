export function dateParser(date: Date | string) {
  const d = new Date(date);
  const pad = (n: number) => String(n).padStart(2, '0');
  const hours = pad(d.getHours());
  const minutes = pad(d.getMinutes());
  const seconds = pad(d.getSeconds());
  const day = pad(d.getDate());
  const month = pad(d.getUTCMonth() + 1);
  const year = d.getUTCFullYear();
  return {
    hours,
    minutes,
    seconds,
    day,
    month,
    year,
    fullDate: `${day}/${month}/${year} - ${hours}:${minutes}:${seconds}`,
    apiDate: `${year}-${month}-${day}T${hours}:${minutes}:${seconds}Z`,
    toDate: d,
  }
}
