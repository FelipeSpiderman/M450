import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ApiService } from './api.service';
import { EntryRequest } from './models';

// No real backend: HttpTestingController catches every request
describe('ApiService', () => {
  let api: ApiService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    api = TestBed.inject(ApiService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('loads the profile', () => {
    let level = 0;
    api.getProfile().subscribe((profile) => (level = profile.level));

    http.expectOne('/api/profile').flush({ level: 3 });

    expect(level).toBe(3);
  });

  it('loads the entries of a day', () => {
    api.getEntries('2026-10-06').subscribe();

    const request = http.expectOne('/api/entries?date=2026-10-06');
    expect(request.request.method).toBe('GET');
    request.flush([]);
  });

  it('creates an entry with POST', () => {
    const entry: EntryRequest = {
      type: 'DRINK',
      timestamp: '2026-10-06T12:00',
      name: 'Wasser',
      durationMinutes: null,
      calories: null,
      amountMl: 500,
      weightKg: null,
    };
    api.createEntry(entry).subscribe();

    const request = http.expectOne('/api/entries');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(entry);
    request.flush({ ...entry, id: 1, xp: 10 });
  });

  it('deletes an entry', () => {
    api.deleteEntry(5).subscribe();

    expect(http.expectOne('/api/entries/5').request.method).toBe('DELETE');
  });

  it('saves daily values with POST', () => {
    api
      .saveDay({ date: '2026-10-06', steps: 8000, sleepScore: null, activityCount: null, activityMinutes: null })
      .subscribe();

    const request = http.expectOne('/api/days');
    expect(request.request.method).toBe('POST');
    expect(request.request.body.steps).toBe(8000);
  });

  it('uploads the import file as form data', () => {
    const file = new File(['content'], 'export.csv');
    api.importFile(file).subscribe();

    const request = http.expectOne('/api/import');
    expect(request.request.body instanceof FormData).toBe(true);
    expect((request.request.body as FormData).get('file')).toBe(file);
  });

  it('starts the sync with POST', () => {
    api.sync().subscribe();

    expect(http.expectOne('/api/sync').request.method).toBe('POST');
  });
});
