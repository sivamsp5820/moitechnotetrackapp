# Moi Note Tracker (Native Android App)

A pure **Native Android mobile application** built with **Kotlin**, **Jetpack Compose**, and **Material 3**, strictly designed in a **Minimalist Monochrome Matte Dark UI** (`#0A0A0A` canvas, `#141414` cards, crisp white text and high-contrast stage indicators).

Synchronizes multi-PC function tracking data in real time with the desktop **Moi Software** (`moitechv4new`) via **Aiven Cloud MySQL** (`pc_function_tracking` table).

---

## Key Features

1. **Monochrome Matte Dark Design**:
   - Zero colorful fills, pure high-contrast matte dark palette matching the reference interface.
   - Smooth status indicators, responsive cards, and clean typography.

2. **Real-Time 4-Stage Workflow Checklist**:
   - **Typed**: Mark when data entry is complete.
   - **Print**: Mark when note/order printing is done.
   - **Given for Binding**: Track binding handoff status.
   - **Given to Customer**: Mark final customer delivery.

3. **Customer Communication Quick Actions**:
   - 1-Tap Phone Call (`tel:` intent).
   - 1-Tap WhatsApp Direct Chat (`https://api.whatsapp.com/send?phone=...`).

4. **Multi-PC System Identification & Nicknames**:
   - Automatically tracks source computer hostname (e.g. `DESKTOP-FNTCP8N`).
   - Assign user-friendly nicknames directly from mobile (e.g. "Counter 1", "Billing Desk").

5. **Offline Support & Resync**:
   - All functions are cached locally via `SharedPreferences`.
   - Work offline without losing data; optimistic UI updates.

---

## Project Structure

```
D:/Others/moinotetrackapp/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/moitech/notetracker/
│   │   │   ├── MainActivity.kt
│   │   │   ├── MainAppScreen.kt
│   │   │   ├── data/
│   │   │   │   ├── api/ (ApiService.kt, RetrofitClient.kt)
│   │   │   │   ├── local/ (LocalStorage.kt)
│   │   │   │   └── model/ (TrackedFunction.kt, ClientItem.kt, FilterState.kt, ApiResponse.kt)
│   │   │   └── ui/
│   │   │       ├── components/ (BottomNavBar.kt, NoteCard.kt, KPICard.kt, FilterBottomSheet.kt)
│   │   │       ├── screens/ (HomeScreen.kt, NotesScreen.kt, NoteDetailScreen.kt, ClientsScreen.kt, SettingsScreen.kt)
│   │   │       └── theme/ (Color.kt, Theme.kt)
│   │   └── res/
│   │       ├── drawable/
│   │       ├── mipmap-anydpi-v26/
│   │       └── values/ (strings.xml, colors.xml, themes.xml)
│   ├── build.gradle
│   └── proguard-rules.pro
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── .github/workflows/
│   └── build-apk.yml (GitHub Actions Release APK Builder)
├── build.gradle
├── gradlew
├── gradlew.bat
├── settings.gradle
└── README.md
```

---

## Automated GitHub Actions APK Build

Pushing this repository to GitHub will automatically trigger `.github/workflows/build-apk.yml`:
1. Compiles the native release APK via `./gradlew assembleRelease`.
2. Uploads the release APK as a downloadable artifact.
3. Automatically publishes a GitHub Release when tags or manual triggers are run.

---

## Building Locally

```bash
# On Windows
./gradlew.bat assembleRelease

# On Linux / macOS
./gradlew assembleRelease
```

The generated APK will be at:
`app/build/outputs/apk/release/app-release.apk`
