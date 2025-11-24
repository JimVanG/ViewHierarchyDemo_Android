# Hierarchy Highlighting Demo

Android Compose app demonstrating parent and child node highlighting without prop-drilling, plus an event sink for bubbling events up the hierarchy.

## What It Does

- Click any text in the app to highlight all parent nodes (blue) and child nodes (green). Uses `CompositionLocal` to avoid passing callbacks through every component.
- The Modifier extension method is in its own module to demonstrate it being used in a framework setting.
- The Modifier extension method allows user to supply different color values and choose whether the children are highlighted or not.
- Breadth First Search (BFS) is used to find child nodes at runtime.
- Uses Event Sink to optionally dispatch custom events from child nodes that bubble up through the parent chain, allowing parent components to handle events without event-drilling.

## Project Structure

- `app/` - Demo application
- `HierarchyModifier/` - Reusable Component highlighting and event propagation module.
  - `HierarchyEventSink.kt` - Event registering/unregistering and dispatching.
  - `ParentChainHighlighter.kt` - Main modifier, CompositionLocals, and Composable Wrappers
  - `ComposeNode.kt` - Node hierarchy representation
  - `ChildNodeMap.kt` - Child node registry using BFS
  - `ColourState.kt` - Highlighting state management

## Features

### 1. Hierarchy Highlighting

Click (almost) any text to highlight the parent nodes (blue) and child nodes (green) - colours are customizable in end-client.

```kotlin
HighlightableHierarchy {
    HighlightableNode(highlightChildren = true) {
        Text(
            "Click me",
            modifier = Modifier.parentChainHighlighter(
                highlightChildren = true, 
                parentHighlightColor = Color.Purple,
                childHighlightColor = Color.Yellow
            )
        )
    }
}
```

### 2. Event Sink

Dispatch events from children that bubble up to parent handlers without event-drilling.

#### Basic Usage

**Parent registers an event handler:**
```kotlin
HighlightableNode(
    onEvent = { event ->
        when (event.eventType) {
            "show_toast" -> {
                Toast.makeText(context, event.data.toString(), Toast.LENGTH_SHORT).show()
                true // event was handled - can stop propagation
            }
            "super_important_event" -> {
                // make sure to handle the super important event
                true
            }
            else -> false // not handled - continue to next parent
        }
    }
) {
    // Child content
}
```

**Child dispatches an event:**
```kotlin
Text(
    "Click to send event",
    modifier = Modifier.parentChainHighlighter(
        highlightChildren = true,
        eventType = "show_toast",
        eventData = "Hello from child!"
    )
)
```

#### Event Handler Lifecycle

- **Registration**: Automatic in `HighlightableNode` when `onEvent` is composed
- **Cleanup**: Automatic when node leaves composition (using `DisposableEffect`)

#### Custom Event Types

Define your own event types:
```kotlin
enum class EventType(val eventValue: String) {
    SHOW_TOAST("show_toast"),
    SHOW_SNACKBAR("show_snackbar"),
    DATA_LOADED("data_loaded"),
    SUPER_IMPORTANT_EVENT("super_important_event")
}
```

## Running

1. Open in Android Studio
2. Sync Gradle
3. Run on device/emulator