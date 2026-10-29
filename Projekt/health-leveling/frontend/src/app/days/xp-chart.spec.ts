import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Day } from '../core/models';
import { XpChart, niceMax } from './xp-chart';

describe('niceMax', () => {
  it('rounds up to 1, 2 or 5 times a power of ten', () => {
    expect(niceMax(0)).toBe(10);
    expect(niceMax(7)).toBe(10);
    expect(niceMax(155)).toBe(200);
    expect(niceMax(200)).toBe(200);
    expect(niceMax(301)).toBe(500);
    expect(niceMax(501)).toBe(1000);
  });
});

describe('XpChart', () => {
  let fixture: ComponentFixture<XpChart>;
  let chart: HTMLElement;

  const day = (date: string, totalXp: number): Day => ({
    date,
    steps: null,
    sleepScore: null,
    activityCount: null,
    activityMinutes: null,
    entries: [],
    valuesXp: totalXp,
    entriesXp: 0,
    slotBonus: 0,
    caloriePenalty: 0,
    totalXp,
  });

  async function render(days: Day[]): Promise<void> {
    fixture = TestBed.createComponent(XpChart);
    fixture.componentRef.setInput('days', days);
    fixture.componentRef.setInput('endDate', '2026-10-14');
    chart = fixture.nativeElement;
    await fixture.whenStable();
  }

  const columns = () => Array.from(chart.querySelectorAll<HTMLElement>('.chart-column'));
  const bar = (index: number) => columns()[index].querySelector<HTMLElement>('.chart-bar');

  it('shows one column per day for the last 14 days, oldest first', async () => {
    await render([]);

    expect(columns().length).toBe(14);
    expect(columns()[0].getAttribute('aria-label')).toContain('01.10.2026');
    expect(columns()[13].getAttribute('aria-label')).toContain('14.10.2026');
  });

  it('draws bars relative to a clean maximum', async () => {
    // max 155 -> axis goes to 200
    await render([day('2026-10-14', 155), day('2026-10-13', 100)]);

    expect(bar(13)?.style.height).toBe('77.5%');
    expect(bar(12)?.style.height).toBe('50%');
    expect(chart.querySelector('.chart-grid span')?.textContent).toBe('200');
  });

  it('draws no bar for days without data', async () => {
    await render([day('2026-10-14', 50)]);

    expect(bar(0)).toBeNull();
    expect(columns()[0].getAttribute('aria-label')).toContain('keine Daten');
  });

  it('draws negative days below the zero line', async () => {
    await render([day('2026-10-14', 90), day('2026-10-13', -10)]);

    // axis from -10 to 100: zero line at 9.09 %
    expect(bar(12)?.classList).toContain('negative');
    expect(bar(12)?.style.bottom).toBe('0%');
    expect(parseFloat(bar(13)!.style.bottom)).toBeCloseTo(9.09, 1);
  });

  it('shows the value on hover and keyboard focus', async () => {
    await render([day('2026-10-14', 155)]);

    columns()[13].dispatchEvent(new Event('focus'));
    await fixture.whenStable();
    expect(chart.querySelector('.chart-tooltip')?.textContent).toContain('155 XP');

    columns()[13].dispatchEvent(new Event('blur'));
    columns()[12].dispatchEvent(new Event('mouseenter'));
    await fixture.whenStable();
    expect(chart.querySelector('.chart-tooltip')?.textContent).toContain('keine Daten');
  });
});
