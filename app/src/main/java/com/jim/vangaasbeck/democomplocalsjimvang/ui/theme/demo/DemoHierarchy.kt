package com.jim.vangaasbeck.democomplocalsjimvang.ui.theme.demo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jim.vangaasbeck.hierarchymodifier.modifier.HighlightableHierarchy
import com.jim.vangaasbeck.hierarchymodifier.modifier.HighlightableNode
import com.jim.vangaasbeck.hierarchymodifier.modifier.LocalHighlightTextColor
import com.jim.vangaasbeck.hierarchymodifier.modifier.parentChainHighlighter

/**
 * Helper composable that applies highlight color to text, without prop-drilling.
 */
@Composable
private fun HighlightableText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    style: TextStyle = TextStyle.Default
) {
    val highlightColor = LocalHighlightTextColor.current
    val finalColor = if (highlightColor != Color.Unspecified) highlightColor else color

    Text(
        text = text,
        modifier = modifier,
        color = finalColor,
        fontSize = fontSize,
        fontWeight = fontWeight,
        style = style
    )
}

@Composable
fun DemoHierarchyScreen() {
    HighlightableHierarchy {
        // Root
        HighlightableNode(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            highlightChildren = true
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    HighlightableText(
                        "Hierarchy Highlighting Without Prop-Drilling Demo",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Instructions
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    HighlightableText(
                        "Click any text below. Parents turn Blue. Children turn Yellow.",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                // Main Demo
                HighlightableNode(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    highlightChildren = true
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(2.dp, Color.Gray.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Root node text
                            HighlightableText(
                                "Root",
                                modifier = Modifier.parentChainHighlighter(highlightChildren = true),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )

                            HorizontalDivider()

                            // Branch 1
                            HighlightableNode(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp),
                                highlightChildren = true
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    HighlightableText(
                                        "Parent A",
                                        modifier = Modifier.parentChainHighlighter(highlightChildren = true),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    // Children of Parent A
                                    Column(
                                        modifier = Modifier.padding(start = 16.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        HighlightableNode(
                                            modifier = Modifier.padding(2.dp),
                                            highlightChildren = true
                                        ) {
                                            HighlightableText(
                                                "• Child A-1",
                                                modifier = Modifier.parentChainHighlighter(
                                                    highlightChildren = true
                                                )
                                            )
                                        }

                                        HighlightableNode(
                                            modifier = Modifier.padding(2.dp),
                                            highlightChildren = true
                                        ) {
                                            HighlightableText(
                                                "• Child A-2",
                                                modifier = Modifier.parentChainHighlighter(
                                                    highlightChildren = true
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            // Branch 2
                            HighlightableNode(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp),
                                highlightChildren = true
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    HighlightableText(
                                        "Parent B",
                                        modifier = Modifier.parentChainHighlighter(highlightChildren = true),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    // Children of Parent B
                                    Column(
                                        modifier = Modifier.padding(start = 16.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        HighlightableNode(
                                            modifier = Modifier.padding(2.dp),
                                            highlightChildren = true
                                        ) {
                                            HighlightableText(
                                                "- Child B-1",
                                                modifier = Modifier.parentChainHighlighter(
                                                    highlightChildren = true
                                                )
                                            )
                                        }

                                        HighlightableNode(
                                            modifier = Modifier.padding(2.dp),
                                            highlightChildren = true
                                        ) {
                                            HighlightableText(
                                                "- Child B-2",
                                                modifier = Modifier.parentChainHighlighter(
                                                    highlightChildren = true
                                                )
                                            )
                                        }

                                        // Nested grandchild
                                        HighlightableNode(
                                            modifier = Modifier.padding(start = 16.dp, top = 4.dp),
                                            highlightChildren = true
                                        ) {
                                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                HighlightableText(
                                                    "Grandchild",
                                                    modifier = Modifier.parentChainHighlighter(
                                                        highlightChildren = true
                                                    ),
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Medium
                                                )

                                                HighlightableNode(
                                                    modifier = Modifier
                                                        .padding(start = 16.dp)
                                                        .padding(2.dp),
                                                    highlightChildren = true
                                                ) {
                                                    HighlightableText(
                                                        "Great-grandchild",
                                                        modifier = Modifier.parentChainHighlighter(
                                                            highlightChildren = true
                                                        ),
                                                        fontSize = 14.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Legend
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    HighlightableText(
                        "CompositionLocals are used to supply the necessary properties throughout the " +
                                "view hierarchy without prop-drilling. Can also be used to pass event callbacks " +
                                "without event-drilling.",
                        modifier = Modifier
                            .parentChainHighlighter(highlightChildren = true)
                            .padding(12.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}
