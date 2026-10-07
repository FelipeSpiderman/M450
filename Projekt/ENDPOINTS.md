# Google Health API – Endpoint Reference (read your own data)

Base URL: `https://health.googleapis.com/v4`

Every request needs these headers:

```
Authorization: Bearer <ACCESS_TOKEN>
Accept: application/json
```

`users/me` always means "the account that signed in" (you).

> **How to try it:** see [`api-explorer/README.md`](api-explorer/README.md). It signs you in once and saves real responses to `api-explorer/samples/`.

---

## 1. Authentication

### OAuth URLs

| What | URL |
|---|---|
| Authorization (sign-in / consent) | `https://accounts.google.com/o/oauth2/v2/auth` |
| Token exchange & refresh | `https://oauth2.googleapis.com/token` |

### Read-only scopes (request all of these to read everything)

```
https://www.googleapis.com/auth/googlehealth.activity_and_fitness.readonly
https://www.googleapis.com/auth/googlehealth.health_metrics_and_measurements.readonly
https://www.googleapis.com/auth/googlehealth.sleep.readonly
https://www.googleapis.com/auth/googlehealth.nutrition.readonly
https://www.googleapis.com/auth/googlehealth.ecg.readonly
https://www.googleapis.com/auth/googlehealth.irn.readonly
```

When authorizing, add `access_type=offline` and `prompt=consent` so you get a **refresh token**.

### Get a new access token automatically (no sign-in needed)

Access tokens expire after ~1 hour. Use the refresh token to get a new one:

```bash
curl -X POST https://oauth2.googleapis.com/token \
  -d client_id=YOUR_CLIENT_ID \
  -d client_secret=YOUR_CLIENT_SECRET \
  -d refresh_token=YOUR_REFRESH_TOKEN \
  -d grant_type=refresh_token
```

Response contains a fresh `access_token`. Do this in your code before each batch of requests.

> ⚠️ While your app is in **Testing** mode, refresh tokens expire after **7 days**
> (`refresh_token_expires_in: 604799`). After that, sign in again once to get a new one.

> 🔒 Never commit `client_secret` or `refresh_token` to Git. Use environment variables.

---

## 2. Account endpoints

| Purpose | Method | Endpoint |
|---|---|---|
| Your Health user ID (check account is linked) | GET | `/users/me/identity` |
| Paired devices (model, battery, last sync) | GET | `/users/me/pairedDevices` |

---

## 3. The 4 ways to read a data type

Replace `{type}` with a value from the table in section 4.

| Method | HTTP | Endpoint | Use it for |
|---|---|---|---|
| **list** | GET | `/users/me/dataTypes/{type}/dataPoints` | Raw detailed data points |
| **reconcile** | GET | `/users/me/dataTypes/{type}/dataPoints:reconcile` | Same, but deduplicated across devices (recommended) |
| **rollUp** | POST | `/users/me/dataTypes/{type}/dataPoints:rollUp` | Aggregates per time window (e.g. per hour) |
| **dailyRollUp** | POST | `/users/me/dataTypes/{type}/dataPoints:dailyRollUp` | Aggregates per calendar day (best for dashboards) |
| **get** | GET | `/users/me/dataTypes/{type}/dataPoints/{id}` | One single data point by ID |

### rollUp body (physical UTC time)

```json
{
  "range": { "startTime": "2026-10-01T00:00:00Z", "endTime": "2026-10-02T00:00:00Z" },
  "windowSize": "3600s"
}
```

### dailyRollUp body (local calendar days)

```json
{
  "range": {
    "start": { "date": { "year": 2026, "month": 10, "day": 1 }, "time": {} },
    "end":   { "date": { "year": 2026, "month": 10, "day": 7 }, "time": {} }
  },
  "windowSizeDays": 1
}
```

---

## 4. All readable data types

Endpoint name uses **kebab-case** (`body-fat`). Filters use **snake_case** (`body_fat`).

### Activity & fitness — scope `activity_and_fitness.readonly`

| Data | `{type}` | Supported reads |
|---|---|---|
| Steps | `steps` | list, reconcile, rollUp, dailyRollUp |
| Distance | `distance` | list, reconcile, rollUp, dailyRollUp |
| Floors | `floors` | reconcile, rollUp, dailyRollUp |
| Altitude | `altitude` | list, reconcile, rollUp, dailyRollUp |
| Active energy burned | `active-energy-burned` | list, reconcile, rollUp, dailyRollUp |
| Total calories | `total-calories` | rollUp, dailyRollUp |
| Active minutes | `active-minutes` | list, reconcile, rollUp, dailyRollUp |
| Active zone minutes | `active-zone-minutes` | list, reconcile, rollUp, dailyRollUp |
| Activity level | `activity-level` | list, reconcile |
| Sedentary periods | `sedentary-period` | list, reconcile, rollUp, dailyRollUp |
| Time in heart rate zone | `time-in-heart-rate-zone` | list, reconcile, rollUp, dailyRollUp |
| Calories in heart rate zone | `calories-in-heart-rate-zone` | rollUp, dailyRollUp |
| Workouts / exercise | `exercise` | list, get, reconcile |
| Swim lengths | `swim-lengths-data` | list, reconcile, rollUp, dailyRollUp |
| VO2 max | `vo2-max` | list, reconcile |
| Daily VO2 max | `daily-vo2-max` | list, reconcile |
| Run VO2 max | `run-vo2-max` | list, reconcile, rollUp, dailyRollUp |

### Health metrics & vitals — scope `health_metrics_and_measurements.readonly`

| Data | `{type}` | Supported reads |
|---|---|---|
| Heart rate | `heart-rate` | list, reconcile, rollUp, dailyRollUp |
| Daily resting heart rate | `daily-resting-heart-rate` | list, reconcile |
| Daily heart rate zones | `daily-heart-rate-zones` | list, reconcile |
| Heart rate variability | `heart-rate-variability` | list, reconcile |
| Daily heart rate variability | `daily-heart-rate-variability` | list, reconcile |
| Oxygen saturation (SpO2) | `oxygen-saturation` | list, reconcile |
| Daily oxygen saturation | `daily-oxygen-saturation` | list, reconcile |
| Daily respiratory rate | `daily-respiratory-rate` | list, reconcile |
| Respiratory rate (sleep) | `respiratory-rate-sleep-summary` | list, reconcile |
| Sleep temperature | `daily-sleep-temperature-derivations` | list, reconcile |
| Skin temperature (**v4beta**, Pixel Watch 4+) | `skin-temperature-sensors` | list |
| Core body temperature | `core-body-temperature` | list, get, reconcile, rollUp, dailyRollUp |
| Blood glucose | `blood-glucose` | list, get, reconcile, rollUp, dailyRollUp |
| Weight | `weight` | list, get, reconcile, rollUp, dailyRollUp |
| Body fat | `body-fat` | list, get, reconcile, rollUp, dailyRollUp |
| Height | `height` | list, get, reconcile |

### Sleep — scope `sleep.readonly`

| Data | `{type}` | Supported reads |
|---|---|---|
| Sleep sessions (stages, summary) | `sleep` | list, get, reconcile |

### Nutrition — scope `nutrition.readonly`

| Data | `{type}` | Supported reads |
|---|---|---|
| Nutrition log | `nutrition-log` | list, get, reconcile, rollUp, dailyRollUp |
| Hydration log | `hydration-log` | list, get, reconcile, rollUp, dailyRollUp |
| Food | `food` | list, get |
| Food measurement units | `food-measurement-unit` | list, get |

### Special scopes

| Data | `{type}` | Scope | Supported reads |
|---|---|---|---|
| ECG | `electrocardiogram` | `ecg.readonly` | list |
| Irregular rhythm notifications | `irregular-rhythm-notification` | `irn.readonly` | list |

> Not readable (write-only via API): `menstrual-period`, `ovulation-test`, `moods`, `symptoms`.

---

## 5. Filtering by time

Interval data (steps, exercise, sleep, …):

```
GET /users/me/dataTypes/steps/dataPoints?filter=steps.interval.civil_start_time >= "2026-10-01T00:00:00"
```

Sample data (weight, body fat, heart rate, …):

```
GET /users/me/dataTypes/body-fat/dataPoints?filter=body_fat.sample_time.physical_time >= "2026-10-01T00:00:00Z"
```

URL-encode the filter in code (spaces → `%20`, quotes → `%22`).

Only wearable data (no manual entries) on `reconcile`:

```
?dataSourceFamily=users/me/dataSourceFamilies/google-wearables
```

---

## 6. Limits to remember

- **Pagination:** responses include `nextPageToken`. Pass it as `?pageToken=...` to get the next page.
  `exercise` and `sleep` return max **25** items per page; most others up to 10,000.
- **rollUp / dailyRollUp range:** max **14 days** for `heart-rate`, `active-minutes`,
  `total-calories`, `calories-in-heart-rate-zone`; max **90 days** for everything else.
- **rollUp windowSize:** at least `"1s"`; use `"60s"` or larger for 1-minute types like steps.
- **Rate limits:** on `429` or `504`, wait and retry with exponential backoff.
- Data appears only after the Google Health app has **synced**.

---

## 7. Quick test with curl

```bash
TOKEN="ya29...."
curl -H "Authorization: Bearer $TOKEN" -H "Accept: application/json" \
  https://health.googleapis.com/v4/users/me/dataTypes/sleep/dataPoints:reconcile
```

Docs: https://developers.google.com/health/data-types · https://developers.google.com/health/endpoints