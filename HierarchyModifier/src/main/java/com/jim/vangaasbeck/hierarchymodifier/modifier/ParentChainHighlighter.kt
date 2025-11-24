package com.jim.vangaasbeck.hierarchymodifier.modifier

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
 * Use compositionLocalOf for the property values that are being passed through the hierarchy that
 * change often (like with each click event). If the value doesn't change at runtime, we use staticCompositionLocalOf,
 * so that it won't trigger recomposition.
 *
 */

/**
 * Use [compositionLocalOf] since this value changes on click.
 */
val LocalHierarchyNode = compositionLocalOf<ComposeNode?> { null }

/**
 * Use [compositionLocalOf] since this value changes on click.
 */val LocalHighlightState = compositionLocalOf<ColourState> {
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
 */val LocalHighlightTextColor = compositionLocalOf { Color.Unspecified }

/**
 * Composable wrapper that allows nodes to pass properties throughout the view hierarchy,
 * highlighting the content.
 *
 * Usage:
 * ```
 * HighlightableNode(
 *     modifier = Modifier.padding(8.dp),
 *     parentHighlightColor: Color
 *     childHighlightColor: Color
 *     highlightChildren = true
 * ) {
 *     Text("Click me", modifier = Modifier.highlightableClick())
 * }
 * ```
 */
@Composable
fun HighlightableNode(
    modifier: Modifier = Modifier,
    parentHighlightColor: Color = Color.Blue, // Blue for parents
    childHighlightColor: Color = Color.Yellow, // Yellow for children
    highlightChildren: Boolean = false,
    content: @Composable () -> Unit
) {
    val currentNode = LocalHierarchyNode.current
    val highlightState = LocalHighlightState.current
    val childrenRegistry = LocalChildrenRegistry.current

    // Create a new node for this composable
    val node = remember(currentNode) {
        ComposeNode(parent = currentNode)
    }

    // Register this node with its parent
    LaunchedEffect(node.id, currentNode?.id) {
        childrenRegistry.registerChild(currentNode?.id, node.id)
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
 * Modifier extension that adds click handling to highlight the hierarchy.
 *
 * Must be used inside a HighlightableNode wrapper. Reads the current node from
 * CompositionLocal and triggers highlighting on click.
 *
 * Usage:
 * ```
 * HighlightableNode {
 *     Text("Click me", modifier = Modifier.parentChainHighlighter(highlightChildren = true))
 * }
 * ```
 */
@SuppressLint("UnnecessaryComposedModifier")
fun Modifier.parentChainHighlighter(
    highlightChildren: Boolean = false
): Modifier = composed {
    val currentNode = LocalHierarchyNode.current
    val highlightState = LocalHighlightState.current
    val childrenRegistry = LocalChildrenRegistry.current

    this.clickable {
        if (currentNode != null) {
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
 * Root Wrapper for supporting highlighting the view hierarchy.
 */
@Composable
fun HighlightableHierarchy(
    content: @Composable () -> Unit
) {
    val colourState = remember { ColourState() }
    val childNodeMap = remember { ChildNodeMap() }

    CompositionLocalProvider(
        LocalHighlightState provides colourState,
        LocalChildrenRegistry provides childNodeMap,
        LocalHierarchyNode provides null
    ) {
        content()
    }
}
