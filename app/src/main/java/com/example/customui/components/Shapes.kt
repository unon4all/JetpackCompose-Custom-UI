package com.example.customui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun MyCanvas(modifier: Modifier = Modifier) {

    Canvas(
        modifier = modifier
            .padding(16.dp)
            .size(200.dp)
    ) {

        drawRect(
            color = Color.Black, size = size
        )

        drawRect(
            color = Color.Red,
            topLeft = Offset(100f, 50f),
            size = Size(100f, 100f),
            style = Stroke(width = 1.dp.toPx(), cap = StrokeCap.Round)
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.Red, Color.Yellow), center = center, radius = 150f
            ),
            radius = 100f,
            center = Offset(size.width / 2, size.height / 2),
        )

        drawArc(
            color = Color.Green,
            startAngle = 0f,
            sweepAngle = 270f,
            useCenter = false,
            size = Size(200f, 200f),
            style = Stroke(width = 2.dp.toPx())
        )

        drawOval(
            color = Color.Blue, topLeft = Offset(100f, 100f), size = Size(200f, 300f)
        )

        drawLine(
            color = Color.Magenta,
            start = Offset(0f, 0f),
            end = Offset(size.width / 2, size.height / 2),
            strokeWidth = 5.dp.toPx()
        )

    }

}