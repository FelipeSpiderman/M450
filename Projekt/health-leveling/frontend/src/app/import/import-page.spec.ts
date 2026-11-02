import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { ApiService } from '../core/api.service';
import { ImportPage } from './import-page';

describe('ImportPage', () => {
  let api: { importFile: ReturnType<typeof vi.fn>; sync: ReturnType<typeof vi.fn> };
  let fixture: ComponentFixture<ImportPage>;
  let page: HTMLElement;

  beforeEach(async () => {
    api = {
      importFile: vi.fn(() => of({ created: 2, updated: 1 })),
      sync: vi.fn(() => of({ created: 7, updated: 0 })),
    };
    TestBed.configureTestingModule({
      imports: [ImportPage],
      providers: [{ provide: ApiService, useValue: api }],
    });
    fixture = TestBed.createComponent(ImportPage);
    page = fixture.nativeElement;
    await fixture.whenStable();
  });

  const importButton = () => page.querySelectorAll<HTMLButtonElement>('button')[0];
  const syncButton = () => page.querySelectorAll<HTMLButtonElement>('button')[1];

  async function selectFile(file: File): Promise<void> {
    const input = page.querySelector<HTMLInputElement>('#file')!;
    Object.defineProperty(input, 'files', { value: [file] });
    input.dispatchEvent(new Event('change'));
    await fixture.whenStable();
  }

  it('can only import when a file is selected', async () => {
    expect(importButton().disabled).toBe(true);

    await selectFile(new File(['content'], 'export.csv'));

    expect(importButton().disabled).toBe(false);
  });

  it('imports the file and shows the result', async () => {
    const file = new File(['content'], 'export.csv');
    await selectFile(file);

    importButton().click();
    await fixture.whenStable();

    expect(api.importFile).toHaveBeenCalledWith(file);
    expect(page.querySelector('[role="status"]')?.textContent).toContain('2 neue Tage, 1 aktualisiert');
  });

  it('shows why an import failed', async () => {
    api.importFile.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 400, error: { detail: 'Error on line 3: steps must be between 0 and 100000' } })),
    );
    await selectFile(new File(['content'], 'export.csv'));

    importButton().click();
    await fixture.whenStable();

    expect(page.querySelector('[role="alert"]')?.textContent).toContain('line 3');
  });

  it('starts the Google sync', async () => {
    syncButton().click();
    await fixture.whenStable();

    expect(api.sync).toHaveBeenCalled();
    expect(page.textContent).toContain('7 neue Tage');
  });

  it('explains when Google is not configured', async () => {
    api.sync.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 503, error: { detail: 'Google Health API is not configured' } })),
    );

    syncButton().click();
    await fixture.whenStable();

    expect(page.querySelector('[role="alert"]')?.textContent).toContain('not configured');
  });
});
