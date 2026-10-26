import { Component, inject, signal } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../core/api.service';
import { errorMessage } from '../core/messages';
import { ImportResult } from '../core/models';

@Component({
  selector: 'app-import-page',
  templateUrl: './import-page.html',
})
export class ImportPage {
  private readonly api = inject(ApiService);

  protected readonly file = signal<File | null>(null);
  protected readonly busy = signal(false);
  protected readonly message = signal<string | null>(null);
  protected readonly error = signal<string | null>(null);

  protected selectFile(event: Event): void {
    this.file.set((event.target as HTMLInputElement).files?.[0] ?? null);
  }

  protected importFile(): void {
    const file = this.file();
    if (file) {
      this.run(this.api.importFile(file), 'Import abgeschlossen');
    }
  }

  protected sync(): void {
    this.run(this.api.sync(), 'Synchronisation abgeschlossen');
  }

  private run(request: Observable<ImportResult>, title: string): void {
    this.busy.set(true);
    this.message.set(null);
    this.error.set(null);
    request.subscribe({
      next: (result) => {
        this.message.set(`${title}: ${result.created} neue Tage, ${result.updated} aktualisiert`);
        this.busy.set(false);
      },
      error: (error) => {
        this.error.set(errorMessage(error));
        this.busy.set(false);
      },
    });
  }
}
