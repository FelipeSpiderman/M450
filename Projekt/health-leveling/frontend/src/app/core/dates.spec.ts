import { localDateTime, today } from './dates';

describe('dates', () => {
  it('formats today as yyyy-MM-dd in local time', () => {
    expect(today(new Date(2026, 9, 6, 0, 30))).toBe('2026-10-06');
  });

  it('formats local date and time for datetime-local inputs', () => {
    expect(localDateTime(new Date(2026, 9, 6, 23, 59))).toBe('2026-10-06T23:59');
  });
});
