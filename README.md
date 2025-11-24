# Hierarchy Highlighting Demo

Android Compose app demonstrating parent and child node highlighting without prop-drilling.

## What It Does

Click any text in the app to highlight all parent nodes (blue) and child nodes (yellow). Uses `CompositionLocal` to avoid passing callbacks through every component.

The Modifier extension method is in its own module to demonstrate it being used in a framework setting.

The Modifier extension method allows user to supply different color values and choose whether the children are highlighted or not.

Breadth First Search is used to find child nodes at runtime.

## Project Structure

- `app/` - Main application
- `HierarchyModifier/` - Reusable highlighting module

## Running

1. Open in Android Studio
2. Sync Gradle
3. Run on device/emulator