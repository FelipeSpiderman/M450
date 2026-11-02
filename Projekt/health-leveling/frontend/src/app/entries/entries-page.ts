import { Component, OnInit, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ApiService } from '../core/api.service';
import { localDateTime, today } from '../core/dates';
import { errorMessage, fieldError } from '../core/messages';
import { Entry, EntryRequest, EntryType } from '../core/models';
import { notInFuture, wholeNumber } from '../core/validators';

type Field = 'name' | 'durationMinutes' | 'calories' | 'amountMl' | 'weightKg';

/** Which fields each type shows. The required ones are the same as in the backend. */
const FIELDS: Record<EntryType, { required: Field[]; optional: Field[] }> = {
  ACTIVITY: { required: ['name', 'durationMinutes'], optional: ['calories'] },
  MEAL: { required: ['calories'], optional: ['name'] },
  DRINK: { required: ['amountMl'], optional: ['name'] },
  WEIGHT: { required: ['weightKg'], optional: [] },
};

export const TYPE_LABELS: Record<EntryType, string> = {
  ACTIVITY: 'Aktivität',
  MEAL: 'Mahlzeit',
  DRINK: 'Getränk',
  WEIGHT: 'Gewicht',
};

@Component({
  selector: 'app-entries-page',
  imports: [ReactiveFormsModule],
  templateUrl: './entries-page.html',
})
export class EntriesPage implements OnInit {
  private readonly api = inject(ApiService);

  protected readonly types = Object.keys(TYPE_LABELS) as EntryType[];
  protected readonly typeLabels = TYPE_LABELS;
  protected readonly fieldError = fieldError;

  protected readonly date = signal(today());
  protected readonly entries = signal<Entry[]>([]);
  protected readonly message = signal<string | null>(null);
  protected readonly error = signal<string | null>(null);

  // the ranges are the same as in the backend, "required" is added per type
  protected readonly form = new FormGroup({
    type: new FormControl<EntryType>('ACTIVITY', { nonNullable: true }),
    timestamp: new FormControl(localDateTime(), {
      nonNullable: true,
      validators: [Validators.required, notInFuture],
    }),
    name: new FormControl<string | null>(null, Validators.maxLength(100)),
    durationMinutes: new FormControl<number | null>(null, [Validators.min(1), Validators.max(1440), wholeNumber]),
    calories: new FormControl<number | null>(null, [Validators.min(0), Validators.max(5000), wholeNumber]),
    amountMl: new FormControl<number | null>(null, [Validators.min(1), Validators.max(5000), wholeNumber]),
    weightKg: new FormControl<number | null>(null, [Validators.min(20), Validators.max(400)]),
  });

  constructor() {
    this.applyRequiredFields(this.form.controls.type.value);
    this.form.controls.type.valueChanges.subscribe((type) => this.applyRequiredFields(type));
  }

  ngOnInit(): void {
    this.loadEntries();
  }

  protected shows(field: Field): boolean {
    const fields = FIELDS[this.form.controls.type.value];
    return fields.required.includes(field) || fields.optional.includes(field);
  }

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const value = this.form.getRawValue();
    // fields of other types are sent as null
    const pick = <T>(field: Field, fieldValue: T) => (this.shows(field) ? fieldValue : null);
    const request: EntryRequest = {
      type: value.type,
      timestamp: value.timestamp,
      name: pick('name', value.name?.trim() || null),
      durationMinutes: pick('durationMinutes', value.durationMinutes),
      calories: pick('calories', value.calories),
      amountMl: pick('amountMl', value.amountMl),
      weightKg: pick('weightKg', value.weightKg),
    };

    this.api.createEntry(request).subscribe({
      next: (entry) => {
        this.message.set(`${TYPE_LABELS[entry.type]} gespeichert: +${entry.xp} XP`);
        this.error.set(null);
        this.form.reset({ type: value.type, timestamp: localDateTime() });
        this.date.set(entry.timestamp.substring(0, 10));
        this.loadEntries();
      },
      error: (error) => {
        this.error.set(errorMessage(error));
        this.message.set(null);
      },
    });
  }

  protected changeDate(event: Event): void {
    this.date.set((event.target as HTMLInputElement).value);
    this.loadEntries();
  }

  protected delete(entry: Entry): void {
    if (!confirm(`${TYPE_LABELS[entry.type]} von ${entry.timestamp.substring(11, 16)} Uhr löschen?`)) {
      return;
    }
    this.api.deleteEntry(entry.id).subscribe({
      next: () => this.loadEntries(),
      error: (error) => this.error.set(errorMessage(error)),
    });
  }

  protected describe(entry: Entry): string {
    const name = entry.name ? `${entry.name}, ` : '';
    switch (entry.type) {
      case 'ACTIVITY':
        return `${name}${entry.durationMinutes} Min.`;
      case 'MEAL':
        return `${name}${entry.calories} kcal`;
      case 'DRINK':
        return `${name}${entry.amountMl} ml`;
      case 'WEIGHT':
        return `${entry.weightKg} kg`;
    }
  }

  private loadEntries(): void {
    this.api.getEntries(this.date()).subscribe({
      next: (entries) => this.entries.set(entries),
      error: (error) => this.error.set(errorMessage(error)),
    });
  }

  private applyRequiredFields(type: EntryType): void {
    for (const [field, control] of Object.entries(this.form.controls)) {
      if (FIELDS[type].required.includes(field as Field)) {
        control.addValidators(Validators.required);
      } else {
        control.removeValidators(Validators.required);
      }
      control.updateValueAndValidity({ emitEvent: false });
    }
  }
}
