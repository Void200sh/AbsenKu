# AbsenKu - Mobile Attendance App

Native Android attendance app with camera selfie check-in, Supabase backend, offline-first architecture.

## Features

- **Camera Selfie Check-in/Check-out** - Capture photo with front camera (CameraX)
- **Supabase Backend** - PostgreSQL + Auth + REST API + Storage
- **Offline-First** - Room database queues records when offline, syncs when online
- **Material 3 Dark Neon Glass UI** - Dark mode first, glassmorphism, neon indigo accent
- **GitHub Actions Build** - Cloud APK build, zero phone load

## Tech Stack

- **Language**: Kotlin
- **UI**: Material 3, ViewBinding, RecyclerView
- **Camera**: CameraX (front camera, selfie capture)
- **Database**: Room (offline queue) + Supabase (PostgreSQL)
- **Networking**: Retrofit + OkHttp + Gson
- **DI**: Manual (Application class)
- **Build**: GitHub Actions (Gradle 8.5, JDK 17)

## Project Structure

```
AbsenKu/
├── app/
│   ├── src/main/
│   │   ├── java/com/absenku/
│   │   │   ├── AbsenKuApplication.kt
│   │   │   ├── data/
│   │   │   │   ├── AttendanceRecord.kt
│   │   │   │   ├── AttendanceDao.kt
│   │   │   │   ├── AttendanceDatabase.kt
│   │   │   │   └── AttendanceRepository.kt
│   │   │   ├── network/
│   │   │   │   └── SupabaseApi.kt
│   │   │   ├── ui/main/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   └── MainViewModel.kt
│   │   │   └── ui/checkin/
│   │   │       ├── CheckInActivity.kt
│   │   │       └── CheckInViewModel.kt
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   ├── drawable/
│   │   │   ├── values/
│   │   │   ├── values-night/
│   │   │   ├── mipmap-anydpi-v26/
│   │   │   ├── xml/
│   │   │   └── menu/
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── .github/workflows/build.yml
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── local.properties.example
```

## Setup

1. **Clone repo**
2. **Configure Supabase**: Copy `local.properties.example` → `local.properties`, fill in:
   ```properties
   SUPABASE_URL=https://your-project.supabase.co/
   SUPABASE_ANON_KEY=your-anon-key
   USER_ID=your-user-id
   ```
3. **Create Supabase table**:
   ```sql
   create table attendance_records (
       id bigserial primary key,
       user_id text not null,
       type text not null check (type in ('check_in', 'check_out')),
       timestamp timestamptz not null default now(),
       location text,
       photo_path text,
       synced boolean default false,
       server_id text
   );
   enable row level security on attendance_records;
   create policy "user_insert" on attendance_records for insert with check (auth.uid()::text = user_id);
   create policy "user_select" on attendance_records for select using (auth.uid()::text = user_id);
   ```
4. **Create Storage bucket**: `attendance_photos` (public or private with RLS)

## Build

**Local (requires JDK 17, Android SDK):**
```bash
./gradlew assembleDebug
```

**Cloud (GitHub Actions):**
- Push to `main` branch
- Workflow runs automatically
- APK artifact uploaded to Actions → Artifacts
- Tag a release (e.g., `v1.0.0`) to create GitHub Release with APK

## UI Design

**Dark Neon Glass** (Dark Mode First):
- Background: `#0A0A0C`
- Cards: `#1E1E20` with subtle border `#3A3A3E`
- Accent: Indigo `#4F46E5` with glow `#818CF8`
- Glassmorphism: Semi-transparent cards, subtle borders
- CTA: Full-width pill button, indigo, bottom docked

## Permissions

- `CAMERA` - Selfie capture
- `INTERNET` - Supabase API
- `READ/WRITE_EXTERNAL_STORAGE` - Photo storage (scoped to app directory)

## License

MIT