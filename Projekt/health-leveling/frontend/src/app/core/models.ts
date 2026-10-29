// Same shapes as the JSON of the backend (see backend/README.md)

export type EntryType = 'ACTIVITY' | 'MEAL' | 'DRINK' | 'WEIGHT';

export interface Profile {
  level: number;
  totalXp: number;
  xpIntoLevel: number;
  xpForNextLevel: number;
  progressPercent: number;
  currentStreak: number;
}

export interface AchievementStatus {
  achievement: string;
  title: string;
  description: string;
  unlocked: boolean;
}

export interface EntryRequest {
  type: EntryType;
  timestamp: string;
  name: string | null;
  durationMinutes: number | null;
  calories: number | null;
  amountMl: number | null;
  weightKg: number | null;
}

export interface Entry extends EntryRequest {
  id: number;
  xp: number;
}

export interface DailyValues {
  date: string;
  steps: number | null;
  sleepScore: number | null;
  activityCount: number | null;
  activityMinutes: number | null;
}

export interface Day extends DailyValues {
  entries: Entry[];
  valuesXp: number;
  entriesXp: number;
  slotBonus: number;
  caloriePenalty: number;
  totalXp: number;
}

export interface ImportResult {
  created: number;
  updated: number;
}
