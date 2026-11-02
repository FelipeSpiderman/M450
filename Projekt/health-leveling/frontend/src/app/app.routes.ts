import { Routes } from '@angular/router';
import { DaysPage } from './days/days-page';
import { EntriesPage } from './entries/entries-page';
import { ImportPage } from './import/import-page';
import { ProfilePage } from './profile/profile-page';

export const routes: Routes = [
  { path: '', component: ProfilePage, title: 'Profil – Health Leveling' },
  { path: 'erfassen', component: EntriesPage, title: 'Erfassen – Health Leveling' },
  { path: 'tage', component: DaysPage, title: 'Tage – Health Leveling' },
  { path: 'import', component: ImportPage, title: 'Import – Health Leveling' },
  { path: '**', redirectTo: '' },
];
