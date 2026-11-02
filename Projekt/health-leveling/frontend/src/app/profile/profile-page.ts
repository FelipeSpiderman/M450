import { Component, OnInit, inject, signal } from '@angular/core';
import { ApiService } from '../core/api.service';
import { errorMessage } from '../core/messages';
import { AchievementStatus, Profile } from '../core/models';

@Component({
  selector: 'app-profile-page',
  templateUrl: './profile-page.html',
})
export class ProfilePage implements OnInit {
  private readonly api = inject(ApiService);

  protected readonly profile = signal<Profile | null>(null);
  protected readonly achievements = signal<AchievementStatus[]>([]);
  protected readonly error = signal<string | null>(null);

  ngOnInit(): void {
    this.api.getProfile().subscribe({
      next: (profile) => this.profile.set(profile),
      error: (error) => this.error.set(errorMessage(error)),
    });
    this.api.getAchievements().subscribe({
      next: (achievements) => this.achievements.set(achievements),
      error: (error) => this.error.set(errorMessage(error)),
    });
  }
}
