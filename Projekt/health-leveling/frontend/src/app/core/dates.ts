// Dates are handled as local time strings, the same format the backend uses.

/** Today as "yyyy-MM-dd" in local time. */
export function today(now = new Date()): string {
  return localDateTime(now).substring(0, 10);
}

/** Now as "yyyy-MM-ddTHH:mm" in local time (format of <input type="datetime-local">). */
export function localDateTime(now = new Date()): string {
  // toISOString() is UTC, so shift by the timezone offset first
  const local = new Date(now.getTime() - now.getTimezoneOffset() * 60000);
  return local.toISOString().substring(0, 16);
}
