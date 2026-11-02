import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { ApiService } from '../core/api.service';
import { ProfilePage } from './profile-page';

describe('ProfilePage', () => {
  const profile = {
    level: 3,
    totalXp: 350,
    xpIntoLevel: 50,
    xpForNextLevel: 300,
    progressPercent: 16,
    currentStreak: 4,
  };
  const achievements = [
    { achievement: 'FIRST_ENTRY', title: 'First step', description: 'Log your first entry', unlocked: true },
    { achievement: 'LEVEL_5', title: 'Rising star', description: 'Reach level 5', unlocked: false },
  ];

  async function render(api: Partial<ApiService>): Promise<HTMLElement> {
    TestBed.configureTestingModule({
      imports: [ProfilePage],
      providers: [{ provide: ApiService, useValue: api }],
    });
    const fixture = TestBed.createComponent(ProfilePage);
    await fixture.whenStable();
    return fixture.nativeElement;
  }

  it('shows level, XP, progress bar and streak', async () => {
    const page = await render({ getProfile: () => of(profile), getAchievements: () => of([]) });

    expect(page.querySelector('.level')?.textContent).toContain('Level 3');
    expect(page.textContent).toContain('50 / 300 XP bis Level 4');
    expect(page.querySelector<HTMLElement>('.progress-bar')?.style.width).toBe('16%');
    expect(page.textContent).toContain('4 Tage');
  });

  it('shows which achievements are unlocked', async () => {
    const page = await render({ getProfile: () => of(profile), getAchievements: () => of(achievements) });

    const items = page.querySelectorAll('.achievements li');
    expect(items.length).toBe(2);
    expect(items[0].classList).toContain('unlocked');
    expect(items[1].textContent).toContain('Gesperrt');
  });

  it('shows a message when the backend is not reachable', async () => {
    const offline = () => throwError(() => new HttpErrorResponse({ status: 0 }));
    const page = await render({ getProfile: offline, getAchievements: offline });

    expect(page.querySelector('[role="alert"]')?.textContent).toContain('nicht erreichbar');
  });
});
