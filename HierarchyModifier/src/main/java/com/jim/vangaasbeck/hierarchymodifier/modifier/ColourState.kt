package com.jim.vangaasbeck.hierarchymodifier.modifier

import android.util.Log
import androidx.compose.runtime.*

@Stable
class ColourState {
    var highlightedParents by mutableStateOf<Set<String>>(emptySet())
        private set

    fun highlightChain(parentIds: List<String>) {
        Log.d("ColourState.highlightChain",
            "Highlighting - Parents: (${parentIds.size}).")
        highlightedParents = parentIds.toSet()
    }

    fun clearHighlights() {
        Log.d("ColourState.clearHighlights",
            "Clearing Highlights.")
        highlightedParents = emptySet()
    }
}