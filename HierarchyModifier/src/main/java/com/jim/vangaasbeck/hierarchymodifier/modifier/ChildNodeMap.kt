package com.jim.vangaasbeck.hierarchymodifier.modifier

import androidx.compose.runtime.mutableStateMapOf
import kotlin.collections.forEach
import kotlin.collections.getOrPut
import kotlin.collections.isNotEmpty

class ChildNodeMap {
    private val childrenMap = mutableStateMapOf<String, MutableSet<String>>()

    fun registerChild(parentId: String?, childId: String) {
        if (parentId != null) {
            childrenMap.getOrPut(parentId) { mutableSetOf() }.add(childId)
        }
    }

    /**
     * Use Breadth First Search (BFS) for finding all the child nodes
     * in the view hierarchy.
     */
    fun getDescendants(nodeId: String): List<String> {
        val descendants = mutableListOf<String>()
        val queue = ArrayDeque<String>()
        queue.add(nodeId)

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            childrenMap[current]?.forEach { child ->
                descendants.add(child)
                queue.add(child)
            }
        }

        return descendants
    }
}