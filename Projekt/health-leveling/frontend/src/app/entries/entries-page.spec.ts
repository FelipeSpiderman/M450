import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { ApiService } from '../core/api.service';
import { Entry } from '../core/models';
import { EntriesPage } from './entries-page';

describe('EntriesPage', () => {
  const savedRun: Entry = {
    id: 1,
    type: 'ACTIVITY',
    timestamp: '2026-10-06T07:30',
    name: 'Laufen',
    durationMinutes: 30,
    calories: null,
    amountMl: null,
    weightKg: null,
    xp: 40,
  };

  let api: { getEntries: ReturnType<typeof vi.fn>; createEntry: ReturnType<typeof vi.fn>; deleteEntry: ReturnType<typeof vi.fn> };
  let fixture: ComponentFixture<EntriesPage>;
  let page: HTMLElement;

  beforeEach(async () => {
    api = {
      getEntries: vi.fn(() => of([savedRun])),
      createEntry: vi.fn(() => of(savedRun)),
      deleteEntry: vi.fn(() => of(undefined)),
    };
    TestBed.configureTestingModule({
      imports: [EntriesPage],
      providers: [{ provide: ApiService, useValue: api }],
    });
    fixture = TestBed.createComponent(EntriesPage);
    page = fixture.nativeElement;
    await fixture.whenStable();
  });

  async function setValue(selector: string, value: string): Promise<void> {
    const input = page.querySelector<HTMLInputElement | HTMLSelectElement>(selector)!;
    input.value = value;
    input.dispatchEvent(new Event(input instanceof HTMLSelectElement ? 'change' : 'input'));
    await fixture.whenStable();
  }

  async function submit(): Promise<void> {
    page.querySelector<HTMLButtonElement>('button[type="submit"]')!.click();
    await fixture.whenStable();
  }

  it('shows the entries of the day', () => {
    expect(page.querySelector('.list')?.textContent).toContain('Laufen, 30 Min.');
    expect(page.querySelector('.list')?.textContent).toContain('+40 XP');
  });

  it('describes entries without repeating the type', async () => {
    api.getEntries.mockReturnValue(
      of([{ ...savedRun, id: 2, type: 'MEAL', name: null, durationMinutes: null, calories: 700, xp: 10 }]),
    );
    await setValue('input[type="date"]', '2026-10-06');
    page.querySelector('input[type="date"]')!.dispatchEvent(new Event('change'));
    await fixture.whenStable();

    expect(page.querySelector('.list li')?.textContent).toContain('Mahlzeit 700 kcal');
  });

  it('shows only the fields of the selected type', async () => {
    expect(page.querySelector('#durationMinutes')).not.toBeNull();
    expect(page.querySelector('#weightKg')).toBeNull();

    await setValue('#type', 'WEIGHT');

    expect(page.querySelector('#weightKg')).not.toBeNull();
    expect(page.querySelector('#durationMinutes')).toBeNull();
    expect(page.querySelector('#name')).toBeNull();
  });

  it('does not send an invalid form and explains the errors', async () => {
    await setValue('#durationMinutes', '0');
    await submit();

    expect(api.createEntry).not.toHaveBeenCalled();
    const errors = Array.from(page.querySelectorAll('.field-error')).map((e) => e.textContent?.trim());
    expect(errors).toContain('Bitte ausfüllen.');
    expect(errors).toContain('Mindestens 1.');
  });

  it('rejects a time in the future', async () => {
    await setValue('#timestamp', '2999-01-01T08:00');
    await submit();

    expect(page.textContent).toContain('Darf nicht in der Zukunft liegen.');
  });

  it('sends a valid activity and shows the XP', async () => {
    await setValue('#timestamp', '2026-10-06T07:30');
    await setValue('#name', ' Laufen ');
    await setValue('#durationMinutes', '30');
    await submit();

    expect(api.createEntry).toHaveBeenCalledWith({
      type: 'ACTIVITY',
      timestamp: '2026-10-06T07:30',
      name: 'Laufen',
      durationMinutes: 30,
      calories: null,
      amountMl: null,
      weightKg: null,
    });
    expect(page.querySelector('[role="status"]')?.textContent).toContain('+40 XP');
    expect(api.getEntries).toHaveBeenLastCalledWith('2026-10-06');
  });

  it('only sends the fields of the selected type', async () => {
    await setValue('#durationMinutes', '30');
    await setValue('#type', 'DRINK');
    await setValue('#amountMl', '500');
    await submit();

    expect(api.createEntry).toHaveBeenCalledWith(
      expect.objectContaining({ type: 'DRINK', amountMl: 500, durationMinutes: null }),
    );
  });

  it('shows the reason when the backend rejects the entry', async () => {
    api.createEntry.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 400, error: { detail: 'calories is required for MEAL' } })),
    );
    await setValue('#type', 'MEAL');
    await setValue('#calories', '600');
    await submit();

    expect(page.querySelector('[role="alert"]')?.textContent).toContain('calories is required for MEAL');
  });

  it('rejects decimals for minutes', async () => {
    await setValue('#durationMinutes', '30.5');
    await submit();

    expect(api.createEntry).not.toHaveBeenCalled();
    expect(page.textContent).toContain('Bitte eine ganze Zahl eingeben.');
  });

  it('deletes an entry after confirmation', async () => {
    vi.spyOn(window, 'confirm').mockReturnValue(true);

    page.querySelector<HTMLButtonElement>('.list button')!.click();
    await fixture.whenStable();

    expect(api.deleteEntry).toHaveBeenCalledWith(1);
  });

  it('keeps the entry when deleting is cancelled', async () => {
    vi.spyOn(window, 'confirm').mockReturnValue(false);

    page.querySelector<HTMLButtonElement>('.list button')!.click();
    await fixture.whenStable();

    expect(api.deleteEntry).not.toHaveBeenCalled();
  });
});
