import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { ApiService } from '../core/api.service';
import { Day } from '../core/models';
import { DaysPage } from './days-page';

describe('DaysPage', () => {
  const day: Day = {
    date: '2026-10-06',
    steps: 8412,
    sleepScore: 72,
    activityCount: 1,
    activityMinutes: 69,
    entries: [],
    valuesXp: 155,
    entriesXp: 0,
    slotBonus: 0,
    caloriePenalty: 0,
    totalXp: 155,
  };

  let api: { getDays: ReturnType<typeof vi.fn>; saveDay: ReturnType<typeof vi.fn> };
  let fixture: ComponentFixture<DaysPage>;
  let page: HTMLElement;

  beforeEach(async () => {
    api = { getDays: vi.fn(() => of([day])), saveDay: vi.fn(() => of(day)) };
    TestBed.configureTestingModule({
      imports: [DaysPage],
      providers: [{ provide: ApiService, useValue: api }],
    });
    fixture = TestBed.createComponent(DaysPage);
    page = fixture.nativeElement;
    await fixture.whenStable();
  });

  async function setValue(selector: string, value: string): Promise<void> {
    const input = page.querySelector<HTMLInputElement>(selector)!;
    input.value = value;
    input.dispatchEvent(new Event('input'));
    await fixture.whenStable();
  }

  async function submit(): Promise<void> {
    page.querySelector<HTMLButtonElement>('button[type="submit"]')!.click();
    await fixture.whenStable();
  }

  it('shows all days in a table', () => {
    const row = page.querySelector('tbody tr')!;
    expect(row.textContent).toContain('06.10.2026');
    expect(row.textContent).toContain('8412');
    expect(row.textContent).toContain('1 (69 Min.)');
    expect(row.textContent).toContain('155');
  });

  it('explains bonus and penalty of a day', async () => {
    api.getDays.mockReturnValue(of([{ ...day, slotBonus: 30, caloriePenalty: -20, totalXp: 165 }]));
    fixture = TestBed.createComponent(DaysPage);
    page = fixture.nativeElement;
    await fixture.whenStable();

    expect(page.querySelector('tbody tr')?.textContent).toContain('+30 Tagesbonus, −20 zu viel gegessen');
  });

  it('rejects decimal steps', async () => {
    await setValue('#steps', '1000.5');
    await submit();

    expect(api.saveDay).not.toHaveBeenCalled();
    expect(page.textContent).toContain('Bitte eine ganze Zahl eingeben.');
  });

  it('needs steps or a sleep score', async () => {
    await submit();

    expect(api.saveDay).not.toHaveBeenCalled();
    expect(page.textContent).toContain('Bitte Schritte oder Schlaf-Score angeben.');
  });

  it('checks the ranges', async () => {
    await setValue('#sleepScore', '101');
    await submit();

    expect(api.saveDay).not.toHaveBeenCalled();
    expect(page.textContent).toContain('Höchstens 100.');
  });

  it('accepts 0 steps as a value', async () => {
    await setValue('#date', '2026-10-06');
    await setValue('#steps', '0');
    await submit();

    expect(api.saveDay).toHaveBeenCalledWith({
      date: '2026-10-06',
      steps: 0,
      sleepScore: null,
      activityCount: null,
      activityMinutes: null,
    });
    expect(page.querySelector('[role="status"]')?.textContent).toContain('155 XP');
  });
});
