import { Component, computed, input, signal } from '@angular/core';
import { today } from '../core/dates';
import { Day } from '../core/models';

const DAY_COUNT = 14;

interface Bar {
  date: string;
  label: string;
  xp: number | null;
  bottom: number;
  height: number;
}

/** Rounds up to a clean axis value: 1, 2 or 5 times a power of ten. */
export function niceMax(value: number): number {
  if (value <= 0) {
    return 10;
  }
  const power = 10 ** Math.floor(Math.log10(value));
  const step = [1, 2, 5, 10].find((factor) => factor * power >= value)!;
  return step * power;
}

/** Column chart with the XP of the last 14 days. The table below it shows the same values. */
@Component({
  selector: 'app-xp-chart',
  templateUrl: './xp-chart.html',
})
export class XpChart {
  readonly days = input.required<Day[]>();
  readonly endDate = input(today());

  protected readonly active = signal<Bar | null>(null);

  protected readonly chart = computed(() => {
    const xpByDate = new Map(this.days().map((day) => [day.date, day.totalXp]));
    const dates = lastDates(this.endDate(), DAY_COUNT);
    const values = dates.map((date) => xpByDate.get(date) ?? 0);

    // the axis goes below 0 only if a day has negative XP (calorie penalty)
    const max = niceMax(Math.max(...values));
    const min = Math.min(0, ...values);
    const position = (value: number) => ((value - min) / (max - min)) * 100;

    const bars: Bar[] = dates.map((date) => {
      const xp = xpByDate.get(date) ?? null;
      return {
        date,
        label: `${date.substring(8, 10)}.${date.substring(5, 7)}.`,
        xp,
        bottom: position(Math.min(xp ?? 0, 0)),
        height: (Math.abs(xp ?? 0) / (max - min)) * 100,
      };
    });
    // no label for a negative minimum, it would overlap the 0 (the value is in the tooltip and table)
    const ticks = [max, max / 2, 0].map((value) => ({
      value,
      position: position(value),
    }));
    return { bars, ticks };
  });

  protected describe(bar: Bar): string {
    const date = `${bar.label}${bar.date.substring(0, 4)}`;
    return bar.xp === null ? `${date}: keine Daten` : `${date}: ${bar.xp} XP`;
  }
}

/** The given number of dates ("yyyy-MM-dd") up to and including the end date. */
function lastDates(endDate: string, count: number): string[] {
  const end = new Date(`${endDate}T12:00`);
  return Array.from({ length: count }, (_, index) => {
    const date = new Date(end);
    date.setDate(end.getDate() - (count - 1 - index));
    return today(date);
  });
}
