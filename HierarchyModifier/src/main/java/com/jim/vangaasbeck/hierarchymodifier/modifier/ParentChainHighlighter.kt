package com.jim.vangaasbeck.hierarchymodifier.modifier

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color


/*
 * We create CompositionLocals to supply the necessary properties throughout the
 * view hierarchy without prop-drilling. Can also be used to pass event callbacks
 * without event-drilling.
 *
 * Use compositionLocalOf since all the property values being passed through the hierarchy
 * change often (with each click event). If the value didn't change at runtime, we would use staticCompositionLocalOf,
 * so that it wouldn't trigger recomposition, but this value changes with every click, so use compositionLocalOf.
 */

val LocalHierarchyNode = compositionLocalOf<ComposeNode?> { null }

// CompositionLocal for the highlight state.
val LocalHighlightState = compositionLocalOf<ColourState> {
    error("ColourState not provided")
}

// CompositionLocal for the highlight text color.
val LocalHighlightTextColor = compositionLocalOf { Color.Unspecified }

/**
 * Composable wrapper that allows nodes to pass properties up to parent views in the hierarchy,
 * highlighting the content.
 *
 * Usage:
 * ```
 * HighlightableNode(
 *     modifier = Modifier.padding(8.dp),
 *     parentHighlightColor: Color = Color(0x..),
 *     childHighlightColor: Color = Color(0x..),
 *     highlightChildren = true // planning for supporting highlighting children
 * ) {
 *     Text("Click me", modifier = Modifier.highlightableClick())
 * }
 * ```
 */
@Composable
fun HighlightableNode(
    modifier: Modifier = Modifier,
    parentHighlightColor: Color = Color(red = 81, green = 57, blue = 235), // Blue for parents
    childHighlightColor: Color = Color(red = 213, green = 248, blue = 89), // Yellow for children
    highlightChildren: Boolean = false,
    content: @Composable () -> Unit
) {
    val currentNode = LocalHierarchyNode.current
    val highlightState = LocalHighlightState.current

    // Create a new node for this composable
    val node = remember(currentNode) {
        ComposeNode(parent = currentNode)
    }


    // Determine text color based on highlight state
    val textColor = when {
        highlightState.highlightedParents.contains(node.id) -> {
            // Just log the first 8 chars of the UUID so the logs aren't too long.
            Log.d(
                "ParentChainHighlighter.HighlightableNode",
                "Node (${node.id.take(8)}) - PARENT highlighted"
            )
            parentHighlightColor
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
 * Modifier extension that adds click handling to highlight the hierarchy.
 *
 * Must be used inside a HighlightableNode wrapper. Reads the current node from
 * CompositionLocal and triggers highlighting on click.
 *
 * Note: Uses `composed` to access CompositionLocal values (required for reading
 * LocalHierarchyNode, LocalHighlightState).
 *
 * Usage:
 * ```
 * HighlightableNode {
 *     Text("Click me", modifier = Modifier.parentChainHighlighter(highlightChildren = true))
 * }
 * ```
 */
@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.parentChainHighlighter(highlightChildren: Boolean = false): Modifier = composed {
    val currentNode = LocalHierarchyNode.current
    val highlightState = LocalHighlightState.current

    this.clickable {
        if (currentNode != null) {
            // Toggle: if this node is already highlighted, clear all highlights
            if (highlightState.highlightedParents.contains(currentNode.id)) {
                highlightState.clearHighlights()
            } else {
                val parentChain = currentNode.getParentChain()
                highlightState.highlightChain(parentChain)
            }
        }
    }
}

// Root wrapper
@Composable
fun HighlightableHierarchy(
    content: @Composable () -> Unit
) {
    val colourState = remember { ColourState() }

    CompositionLocalProvider(
        LocalHighlightState provides colourState,
        LocalHierarchyNode provides null
    ) {
        content()
    }
}
