import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { localDateTime, today } from './dates';

/** For "yyyy-MM-dd" and "yyyy-MM-ddTHH:mm" values. The strings can be compared directly. */
export const notInFuture: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const value: string | null = control.value;
  if (!value) {
    return null;
  }
  const now = value.length === 10 ? today() : localDateTime();
  return value > now ? { future: true } : null;
};

/** Form group validator: at least one of the given fields must be filled. */
export function atLeastOne(...fields: string[]): ValidatorFn {
  return (group: AbstractControl): ValidationErrors | null => {
    const filled = fields.some((field) => {
      const value = group.get(field)?.value;
      return value !== null && value !== undefined && value !== '';
    });
    return filled ? null : { atLeastOne: true };
  };
}
