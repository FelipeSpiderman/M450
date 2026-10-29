import { HttpErrorResponse } from '@angular/common/http';
import { FormControl, ValidatorFn, Validators } from '@angular/forms';
import { errorMessage, fieldError } from './messages';

describe('fieldError', () => {
  const touched = (value: unknown, validator: ValidatorFn) => {
    const control = new FormControl(value, validator);
    control.markAsTouched();
    return control;
  };

  it('shows nothing for untouched fields', () => {
    expect(fieldError(new FormControl('', Validators.required))).toBeNull();
  });

  it('shows nothing for valid fields', () => {
    expect(fieldError(touched(5, Validators.min(1)))).toBeNull();
  });

  it('explains what is wrong', () => {
    expect(fieldError(touched('', Validators.required))).toBe('Bitte ausfüllen.');
    expect(fieldError(touched(0, Validators.min(1)))).toBe('Mindestens 1.');
    expect(fieldError(touched(101, Validators.max(100)))).toBe('Höchstens 100.');
    expect(fieldError(touched(1.5, () => ({ wholeNumber: true })))).toBe('Bitte eine ganze Zahl eingeben.');
  });
});

describe('errorMessage', () => {
  it('uses the detail sent by the backend', () => {
    const error = new HttpErrorResponse({ status: 400, error: { detail: 'calories is required for MEAL' } });

    expect(errorMessage(error)).toBe('calories is required for MEAL');
  });

  it('explains when the backend is not reachable', () => {
    expect(errorMessage(new HttpErrorResponse({ status: 0 }))).toContain('nicht erreichbar');
    expect(errorMessage(new HttpErrorResponse({ status: 504 }))).toContain('nicht erreichbar');
  });

  it('falls back to the status code', () => {
    expect(errorMessage(new HttpErrorResponse({ status: 404 }))).toContain('404');
  });
});
