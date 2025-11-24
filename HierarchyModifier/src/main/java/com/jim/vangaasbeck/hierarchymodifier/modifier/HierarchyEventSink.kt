package com.jim.vangaasbeck.hierarchymodifier.modifier

import android.util.Log
import androidx.compose.runtime.Stable

/**
 * Represents an event that can be dispatched through the hierarchy.
 *
 * @param source The ID of the node that originated the event.
 * @param eventType A string identifier for the event type (e.g., "show_toast", "custom_action")
 * purposely left open to String constants so the end-user can supply custom callbacks.
 * @param data Optional data payload associated with the event.
 */
data class HierarchyEvent(
    val source: String,
    val eventType: String,
    val data: Any? = null
)

/**
 * Callback function type for handling hierarchy events.
 * Returns true if the event was handled and should stop propagating, false to continue.
 */
typealias EventHandler = (HierarchyEvent) -> Boolean

/**
 * Event sink that manages event dispatching through the compose hierarchy.
 * Parents can register event handlers, and children can dispatch events that
 * bubble up through their parent chain.
 */
@Stable
class HierarchyEventSink {
    private val handlers = mutableMapOf<String, MutableList<EventHandler>>()

    /**
     * Register an event handler for a specific node.
     *
     * @param nodeId The ID of the node registering the handler
     * @param handler The callback function to invoke when events reach this node
     */
    fun registerHandler(nodeId: String, handler: EventHandler) {
        handlers.getOrPut(nodeId) { mutableListOf() }.add(handler)
        Log.d(
            "HierarchyEventSink.registerHandler",
            "Registered handler for node (${nodeId.take(8)})"
        )
    }

    /**
     * Unregister an event handler for a specific node.
     *
     * @param nodeId The ID of the node unregistering the handler
     * @param handler The callback function to remove
     */
    fun unregisterHandler(nodeId: String, handler: EventHandler) {
        handlers[nodeId]?.remove(handler)
        if (handlers[nodeId]?.isEmpty() == true) {
            handlers.remove(nodeId)
        }
        Log.d(
            "HierarchyEventSink.unregisterHandler",
            "Unregistered handler for node (${nodeId.take(8)})"
        )
    }

    /**
     * Dispatch an event through the parent chain.
     * The event will bubble up from the source node through all its parents,
     * calling registered handlers at each level until one handles it.
     *
     * @param event The event to dispatch
     * @param parentChain The list of node IDs from source to root
     */
    fun dispatchEvent(event: HierarchyEvent, parentChain: List<String>) {
        Log.d(
            "HierarchyEventSink.dispatchEvent",
            "Dispatching event '${event.eventType}' from node (${event.source.take(8)}) through chain of ${parentChain.size} nodes"
        )

        for (nodeId in parentChain) {
            val nodeHandlers = handlers[nodeId] ?: continue

            for (handler in nodeHandlers) {
                val handled = handler(event)
                if (handled) {
                    Log.d(
                        "HierarchyEventSink.dispatchEvent",
                        "Event '${event.eventType}' handled by node (${nodeId.take(8)})"
                    )
                    return
                }
            }
        }

        Log.d(
            "HierarchyEventSink.dispatchEvent",
            "Event '${event.eventType}' completed propagation without being handled"
        )
    }

    /**
     * Clear all registered handlers.
     *
     * Called when leaving a screen or in rare-cases.
     */
    fun clearHandlers() {
        handlers.clear()
        Log.d("HierarchyEventSink.clearHandlers", "Cleared all event handlers")
    }
}
