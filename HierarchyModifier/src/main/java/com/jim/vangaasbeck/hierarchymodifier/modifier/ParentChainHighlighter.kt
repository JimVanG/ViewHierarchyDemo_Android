package com.jim.vangaasbeck.hierarchymodifier.modifier

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color


/*
 * We create CompositionLocals to supply the necessary properties throughout the
 * view hierarchy without prop-drilling. Can also be used to pass event callbacks
 * without event-drilling.
 *
 * Difference between the two types of CompositionLocals:
 *
 * - Use compositionLocalOf for the property values that are being passed through the hierarchy that
 * change often (i.e. changing the color with each click event).
 * - If the value doesn't change at runtime, we use staticCompositionLocalOf, so that it won't
 * trigger recomposition - typically used when a node ONLY calls a function rather than reading a value.
 *
 */

/**
 * Use [compositionLocalOf] since this value changes on click.
 */
val LocalHierarchyNode = compositionLocalOf<ComposeNode?> { null }

/**
 * Use [compositionLocalOf] since this value changes on click.
 */
val LocalHighlightState = compositionLocalOf<ColourState> {
    error("ColourState not provided")
}

/**
 * Use [staticCompositionLocalOf] for the map of child nodes because
 * the value never changes so there's not need to trigger recomposition.
 */
val LocalChildrenRegistry = staticCompositionLocalOf<ChildNodeMap> {
    error("ChildNodeMap not provided")
}

/**
 * Use [compositionLocalOf] since this value changes on click.
 */
val LocalHighlightTextColor = compositionLocalOf { Color.Unspecified }

/**
 * Use [staticCompositionLocalOf] for the event sink because
 * the instance never changes, only the handlers registered within it change.
 */
val LocalHierarchyEventSink = staticCompositionLocalOf<HierarchyEventSink> {
    error("HierarchyEventSink not provided")
}

/**
 * Reusable Composable wrapper that allows nodes to pass properties and events throughout the view hierarchy,
 * highlighting the content and handling events without prop-drilling or event-drilling.
 *
 * Usage:
 * ```
 * HighlightableNode(
 *     modifier = Modifier.padding(8.dp),
 *     parentHighlightColor: Color
 *     childHighlightColor: Color
 *     highlightChildren = true,
 *     onEvent = { event ->
 *         // Handle events from children
 *         when (event.eventType) {
 *             "custom_action" -> {
 *                 println("Received: ${event.data}")
 *                 true // handled
 *             }
 *             else -> false // not handled, continue propagation
 *         }
 *     }
 * ) {
 *     Text("Click me", modifier = Modifier.highlightableClick())
 * }
 * ```
 */
@Composable
fun HighlightableNode(
    modifier: Modifier = Modifier,
    parentHighlightColor: Color = Color.Blue, // Blue for parents
    childHighlightColor: Color = Color.Green, // Green for children
    highlightChildren: Boolean = false,
    onEvent: EventHandler? = null,
    content: @Composable () -> Unit
) {
    val currentNode = LocalHierarchyNode.current
    val highlightState = LocalHighlightState.current
    val childrenRegistry = LocalChildrenRegistry.current
    val eventSink = LocalHierarchyEventSink.current

    // Create a new node for this composable
    val node = remember(currentNode) {
        ComposeNode(parent = currentNode)
    }

    // Register this node with its parent
    LaunchedEffect(node.id, currentNode?.id) {
        childrenRegistry.registerChild(currentNode?.id, node.id)
    }

    // Register event handler if provided, and unregister when disposed
    DisposableEffect(node.id, onEvent) {
        if (onEvent != null) {
            eventSink.registerHandler(node.id, onEvent)
        }
        onDispose {
            if (onEvent != null) {
                eventSink.unregisterHandler(node.id, onEvent)
            }
        }
    }

    // Set text color based on highlight state, the color toggles based on the state.
    val textColor = when {
        highlightState.highlightedParents.contains(node.id) -> {
            // Just log the first 8 chars of the UUID so the logs aren't too long.
            Log.d(
                "ParentChainHighlighter.HighlightableNode",
                "Node (${node.id.take(8)}) - PARENT highlighted"
            )
            parentHighlightColor
        }

        highlightChildren && highlightState.highlightedChildren.contains(node.id) -> {
            // Just log the first 8 chars of the UUID so the logs aren't too long.
            Log.d(
                "ParentChainHighlighter.HighlightableNode",
                "Node (${node.id.take(8)}) - CHILD highlighted"
            )
            childHighlightColor
        }

        else -> Color.Unspecified
    }

    CompositionLocalProvider(
        LocalHierarchyNode provides node,
        LocalHighlightTextColor provides textColor
    ) {
        Box(
            modifier = modifier.then(Modifier)
        ) {
            content()
        }
    }
}


/**
 * Modifier extension that adds click handling to highlight the hierarchy and
 * optionally dispatch events up the parent chain.
 *
 * Must be used inside a HighlightableNode wrapper. Reads the current node from
 * CompositionLocal and triggers highlighting on click. Can also dispatch custom
 * events that bubble up through parent nodes.
 *
 * Usage:
 * ```
 * HighlightableNode {
 *     Text(
 *         "Click me",
 *         modifier = Modifier.parentChainHighlighter(
 *             highlightChildren = true,
 *             eventType = "show_toast",
 *             eventData = "some data"
 *         )
 *     )
 * }
 * ```
 */
@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.parentChainHighlighter(
    highlightChildren: Boolean = false,
    eventType: String? = null,
    eventData: Any? = null
): Modifier = composed {
    val currentNode = LocalHierarchyNode.current
    val highlightState = LocalHighlightState.current
    val childrenRegistry = LocalChildrenRegistry.current
    val eventSink = LocalHierarchyEventSink.current

    this.clickable {

        if (currentNode != null) {
            // Dispatch event first if eventType is provided
            if (eventType != null) {
                val event = HierarchyEvent(
                    source = currentNode.id,
                    eventType = eventType,
                    data = eventData
                )
                val parentChain = currentNode.getParentChain()
                eventSink.dispatchEvent(event, parentChain)
            }

            // Then handle highlighting
            // Toggle: if this node is already highlighted, clear all highlights
            if (highlightState.highlightedParents.contains(currentNode.id)) {
                highlightState.clearHighlights()
            } else {
                val parentChain = currentNode.getParentChain()
                val children = if (highlightChildren) {
                    childrenRegistry.getDescendants(currentNode.id)
                } else {
                    emptyList()
                }
                highlightState.highlightChain(parentChain, children)
            }
        }
    }
}

/**
 * Root Wrapper for supporting highlighting the view hierarchy and event dispatching.
 */
@Composable
fun HighlightableHierarchy(
    content: @Composable () -> Unit
) {
    val colourState = remember { ColourState() }
    val childNodeMap = remember { ChildNodeMap() }
    val eventSink = remember { HierarchyEventSink() }

    CompositionLocalProvider(
        LocalHighlightState provides colourState,
        LocalChildrenRegistry provides childNodeMap,
        LocalHierarchyEventSink provides eventSink,
        LocalHierarchyNode provides null
    ) {
        content()
    }
}
