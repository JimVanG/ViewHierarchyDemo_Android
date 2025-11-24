package com.jim.vangaasbeck.hierarchymodifier.modifier

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
class ColourState {
    var highlightedParents by mutableStateOf<Set<String>>(emptySet())
        private set

    var highlightedChildren by mutableStateOf<Set<String>>(emptySet())
        private set

    fun highlightChain(parentIds: List<String>, childIds: List<String> = emptyList()) {
        Log.d(
            "ColourState.highlightChain",
            "Highlighting - Parents: (${parentIds.size}, Children: ${childIds.size}.)."
        )
        highlightedParents = parentIds.toSet()
        highlightedChildren = childIds.toSet()
    }

    fun clearHighlights() {
        Log.d(
            "ColourState.clearHighlights",
            "Clearing Highlights."
        )
        highlightedParents = emptySet()
        highlightedChildren = emptySet()
    }
}