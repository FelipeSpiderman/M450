import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
  AchievementStatus,
  DailyValues,
  Day,
  Entry,
  EntryRequest,
  ImportResult,
  Profile,
} from './models';

/** The only place that talks to the backend. In development /api is forwarded via proxy.conf.json. */
@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);

  getProfile(): Observable<Profile> {
    return this.http.get<Profile>('/api/profile');
  }

  getAchievements(): Observable<AchievementStatus[]> {
    return this.http.get<AchievementStatus[]>('/api/achievements');
  }

  getDays(): Observable<Day[]> {
    return this.http.get<Day[]>('/api/days');
  }

  saveDay(values: DailyValues): Observable<Day> {
    return this.http.post<Day>('/api/days', values);
  }

  getEntries(date: string): Observable<Entry[]> {
    return this.http.get<Entry[]>('/api/entries', { params: { date } });
  }

  createEntry(entry: EntryRequest): Observable<Entry> {
    return this.http.post<Entry>('/api/entries', entry);
  }

  deleteEntry(id: number): Observable<void> {
    return this.http.delete<void>(`/api/entries/${id}`);
  }

  importFile(file: File): Observable<ImportResult> {
    const form = new FormData();
    form.append('file', file);
    return this.http.post<ImportResult>('/api/import', form);
  }

  sync(): Observable<ImportResult> {
    return this.http.post<ImportResult>('/api/sync', null);
  }
}
