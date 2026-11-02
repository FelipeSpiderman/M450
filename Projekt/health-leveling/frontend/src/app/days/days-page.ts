import { DatePipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ApiService } from '../core/api.service';
import { today } from '../core/dates';
import { errorMessage, fieldError } from '../core/messages';
import { Day } from '../core/models';
import { atLeastOne, notInFuture, wholeNumber } from '../core/validators';
import { XpChart } from './xp-chart';

@Component({
  selector: 'app-days-page',
  imports: [ReactiveFormsModule, DatePipe, XpChart],
  templateUrl: './days-page.html',
})
export class DaysPage implements OnInit {
  private readonly api = inject(ApiService);

  protected readonly fieldError = fieldError;
  protected readonly days = signal<Day[]>([]);
  protected readonly message = signal<string | null>(null);
  protected readonly error = signal<string | null>(null);

  protected readonly form = new FormGroup(
    {
      date: new FormControl(today(), { nonNullable: true, validators: [Validators.required, notInFuture] }),
      steps: new FormControl<number | null>(null, [Validators.min(0), Validators.max(100000), wholeNumber]),
      sleepScore: new FormControl<number | null>(null, [Validators.min(0), Validators.max(100), wholeNumber]),
    },
    { validators: atLeastOne('steps', 'sleepScore') },
  );

  ngOnInit(): void {
    this.loadDays();
  }

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const value = this.form.getRawValue();
    this.api
      .saveDay({ ...value, activityCount: null, activityMinutes: null })
      .subscribe({
        next: (day) => {
          this.message.set(`Tag gespeichert: ${day.totalXp} XP an diesem Tag`);
          this.error.set(null);
          this.form.reset({ date: value.date });
          this.loadDays();
        },
        error: (error) => {
          this.error.set(errorMessage(error));
          this.message.set(null);
        },
      });
  }

  /** Explains the bonus and penalty of the day review, e.g. "+30 Tagesbonus". */
  protected breakdown(day: Day): string {
    const parts: string[] = [];
    if (day.slotBonus > 0) {
      parts.push(`+${day.slotBonus} Tagesbonus`);
    }
    if (day.caloriePenalty < 0) {
      parts.push(`−${Math.abs(day.caloriePenalty)} zu viel gegessen`);
    }
    return parts.join(', ');
  }

  private loadDays(): void {
    this.api.getDays().subscribe({
      next: (days) => this.days.set(days),
      error: (error) => this.error.set(errorMessage(error)),
    });
  }
}
