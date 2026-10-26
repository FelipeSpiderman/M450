import { FormControl, FormGroup } from '@angular/forms';
import { localDateTime, today } from './dates';
import { atLeastOne, notInFuture } from './validators';

describe('notInFuture', () => {
  it('accepts empty values (required is checked separately)', () => {
    expect(notInFuture(new FormControl(''))).toBeNull();
  });

  it('accepts today and the past', () => {
    expect(notInFuture(new FormControl(today()))).toBeNull();
    expect(notInFuture(new FormControl('2026-01-01'))).toBeNull();
    expect(notInFuture(new FormControl(localDateTime()))).toBeNull();
  });

  it('rejects a future date', () => {
    expect(notInFuture(new FormControl('2999-01-01'))).toEqual({ future: true });
  });

  it('rejects a future date and time', () => {
    expect(notInFuture(new FormControl('2999-01-01T08:00'))).toEqual({ future: true });
  });
});

describe('atLeastOne', () => {
  const form = (steps: number | null, sleepScore: number | null) =>
    new FormGroup({ steps: new FormControl(steps), sleepScore: new FormControl(sleepScore) });

  it('rejects a group where all fields are empty', () => {
    expect(atLeastOne('steps', 'sleepScore')(form(null, null))).toEqual({ atLeastOne: true });
  });

  it('accepts one filled field, also 0', () => {
    expect(atLeastOne('steps', 'sleepScore')(form(0, null))).toBeNull();
    expect(atLeastOne('steps', 'sleepScore')(form(null, 80))).toBeNull();
  });
});
