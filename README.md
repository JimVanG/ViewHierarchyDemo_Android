# Hierarchy Highlighting Demo

Android Compose app demonstrating parent chain highlighting without prop-drilling.

## What It Does

Click any text in the app to highlight all parent nodes in blue. Uses `CompositionLocal` to avoid passing callbacks through every component.

## Project Structure

- `app/` - Main application
- `HierarchyModifier/` - Reusable highlighting module

## Running

1. Open in Android Studio
2. Sync Gradle
3. Run on device/emulator