import { HttpErrorResponse } from '@angular/common/http';
import { AbstractControl } from '@angular/forms';

/** German message for the first error of a form field, or null if it is valid or untouched. */
export function fieldError(control: AbstractControl | null): string | null {
  if (!control || !control.errors || !control.touched) {
    return null;
  }
  const errors = control.errors;
  if (errors['required']) return 'Bitte ausfüllen.';
  if (errors['min']) return `Mindestens ${errors['min'].min}.`;
  if (errors['max']) return `Höchstens ${errors['max'].max}.`;
  if (errors['maxlength']) return `Höchstens ${errors['maxlength'].requiredLength} Zeichen.`;
  if (errors['future']) return 'Darf nicht in der Zukunft liegen.';
  return 'Ungültiger Wert.';
}

/** Readable message for a failed request. The backend sends the reason in "detail". */
export function errorMessage(error: unknown): string {
  if (error instanceof HttpErrorResponse) {
    const detail = error.error?.detail;
    if (detail) {
      return detail;
    }
    // 0 = no connection, 5xx without detail = the dev-server proxy could not reach the backend
    if (error.status === 0 || error.status >= 500) {
      return 'Das Backend ist nicht erreichbar. Läuft es auf Port 8080?';
    }
    return `Unerwarteter Fehler (Status ${error.status}).`;
  }
  return 'Unerwarteter Fehler.';
}
