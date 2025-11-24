package com.jim.vangaasbeck.hierarchymodifier.modifier


import java.util.UUID

data class ComposeNode(
    val id: String = UUID.randomUUID().toString(),
    val parent: ComposeNode? = null
) {
    /**
     * Build the full parent chain from this node to the root so we can know which to highlight.
     */
    fun getParentChain(): List<String> {
        val chain = mutableListOf<String>()
        var current: ComposeNode? = this
        while (current != null) {
            chain.add(current.id)
            current = current.parent
        }
        return chain
    }
}
