package com.example.myapplication.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.size
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color

//vertical stripes
fun Modifier.verticalStripes(patternColor: Color = Color.Black.copy(alpha = 0.15f)): Modifier =
    this.then(
        Modifier.drawBehind {
            val stripeWidth = size.width / 12f
            for (i in 0..12) {
                drawRect(
                    color = patternColor,
                    topLeft = androidx.compose.ui.geometry.Offset(x = i * stripeWidth, y = 0f),
                    size = androidx.compose.ui.geometry.Size(stripeWidth / 2f, size.height)
                )
            }
        }
    )

//horizontal stripes
fun Modifier.horizontalStripes(patternColor: Color = Color.Black.copy(alpha = 0.15f)): Modifier =
    this.then(
        Modifier.drawBehind {
            val stripeHeight = size.height / 12f
            for (i in 0..12) {
                drawRect(
                    color = patternColor,
                    topLeft = androidx.compose.ui.geometry.Offset(x = 0f, y = i * stripeHeight),
                    size = androidx.compose.ui.geometry.Size(size.width, stripeHeight / 2f)
                )
            }
        }
    )

//checker pattern
fun Modifier.checkerPattern(patternColor: Color = Color.Black.copy(alpha = 0.15f)): Modifier =
    this.then(
        Modifier.drawBehind {
            val cellSize = size.width / 10f
            for (row in 0..10) {
                for (col in 0..10) {
                    if ((row + col) % 2 == 0) {
                        drawRect(
                            color = patternColor,
                            topLeft = androidx.compose.ui.geometry.Offset(
                                x = col * cellSize,
                                y = row * cellSize
                            ),
                            size = androidx.compose.ui.geometry.Size(cellSize, cellSize)
                        )
                    }
                }
            }
        }
    )

