package com.example.customui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.Paint
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.core.graphics.withRotation
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin


data class ScaleStyle(
    val scaleWidth: Dp = 150.dp,
    val radius: Dp = 550.dp,
    val normalLineColor: Color = Color.LightGray,
    val fiveStepLineColor: Color = Color.Green,
    val tenStepLineColor: Color = Color.Black,
    val normalLineLength: Dp = 15.dp,
    val fiveStepLineLength: Dp = 25.dp,
    val tenStepLineLength: Dp = 35.dp,
    val scaleIndicatorColor: Color = Color.Green,
    val scaleIndicatorLength: Dp = 60.dp,
    val textSize: TextUnit = 18.sp,
)

sealed class LineType {
    data object Normal : LineType()
    data object FiveStep : LineType()
    data object TenStep : LineType()
}

@Composable
fun ScalePreview() {

    var weight by remember {
        mutableIntStateOf(0)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Weight: $weight KG",
            color = Color.Green,
            fontSize = 25.sp,
            modifier = Modifier.align(Alignment.Center),
            fontWeight = FontWeight.Bold
        )

        Scale(modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .align(Alignment.BottomCenter)
            .fillMaxSize(), onWeightChange = {
            weight = it
        })
    }
}

/**
 * A composable function that renders a customizable weight scale.
 * This scale allows the user to drag and change the weight value,
 * which is reflected visually on a circular scale.
 *
 * @param modifier Modifier to be applied to the Canvas.
 * @param style A ScaleStyle object that defines the visual style of the scale.
 * @param minWeight The minimum weight value displayed on the scale.
 * @param maxWeight The maximum weight value displayed on the scale.
 * @param initialWeight The initial weight value displayed on the scale.
 * @param onWeightChange Callback function that gets triggered when the weight value changes.
 *
 * Example Usage:
 * ```
 * Scale(
 *     modifier = Modifier.fillMaxSize(),
 *     style = ScaleStyle(),
 *     minWeight = 50,
 *     maxWeight = 300,
 *     initialWeight = 68,
 *     onWeightChange = { newWeight ->
 *         println("New weight: $newWeight")
 *     }
 * )
 * ```
 */
@Composable
fun Scale(
    modifier: Modifier = Modifier,
    style: ScaleStyle = ScaleStyle(),
    minWeight: Int = 50,
    maxWeight: Int = 300,
    initialWeight: Int = 68,
    onWeightChange: (Int) -> Unit
) {
    val radius = style.radius
    val scaleWidth = style.scaleWidth
    var center by remember { mutableStateOf(Offset.Zero) }
    var circleCenter by remember { mutableStateOf(Offset.Zero) }
    var angle by remember { mutableFloatStateOf(0f) }
    var dragStartedAngle by remember { mutableFloatStateOf(0f) }
    var oldAngle by remember { mutableFloatStateOf(angle) }

    Canvas(
        modifier = modifier
            .pointerInput(true) {
                detectDragGestures(
                    onDragStart = { offset ->
                        // Equation 1: Calculates the drag start angle
                        // -atan2(deltaX, deltaY) converts the cartesian coordinates to an angle in radians
                        // which is then converted to degrees by multiplying with (180 / PI).
                        // Example:
                        // If circleCenter is at (100, 100) and offset (touch point) is at (120, 80),
                        // atan2(100 - 120, 100 - 80) gives an angle in radians which is converted to degrees.
                        dragStartedAngle = -atan2(
                            circleCenter.x - offset.x,
                            circleCenter.y - offset.y
                        ) * (180f / PI.toFloat())
                    },
                    onDragEnd = { oldAngle = angle }
                ) { change, _ ->
                    // Equation 2: Calculates the touch angle during dragging
                    // -atan2(deltaX, deltaY) converts the cartesian coordinates to an angle in radians
                    // which is then converted to degrees by multiplying with (180 / PI).
                    // Example:
                    // If circleCenter is at (100, 100) and change.position (new touch point) is at (130, 70),
                    // atan2(100 - 130, 100 - 70) gives an angle in radians which is converted to degrees.
                    val touchAngle = -atan2(
                        circleCenter.x - change.position.x,
                        circleCenter.y - change.position.y
                    ) * (180f / PI.toFloat())

                    // Equation 3: Calculates the new angle based on the change in touch angle
                    // newAngle = oldAngle + (touchAngle - dragStartedAngle)
                    // Example:
                    // If oldAngle is 20 degrees, dragStartedAngle is -30 degrees, and touchAngle is -10 degrees,
                    // newAngle will be 20 + (-10 - (-30)) = 40 degrees.
                    val newAngle = oldAngle + (touchAngle - dragStartedAngle)

                    // Equation 4: Constrains the new angle within the min and max weight range
                    // angle = newAngle.coerceIn(min, max)
                    // Example:
                    // If initialWeight is 68, minWeight is 50, and maxWeight is 300,
                    // the angle will be constrained to the range of -232 to 18 degrees.
                    angle = newAngle.coerceIn(
                        minimumValue = initialWeight - maxWeight.toFloat(),
                        maximumValue = initialWeight - minWeight.toFloat()
                    )
                    // Updates the weight based on the angle
                    onWeightChange((initialWeight - angle).roundToInt())
                }
            }
    ) {
        center = this.center
        circleCenter = Offset(
            center.x,
            scaleWidth.toPx() / 2f + radius.toPx()
        )
        val outerRadius = radius.toPx() + scaleWidth.toPx() / 2f
        val innerRadius = radius.toPx() - scaleWidth.toPx() / 2f

        drawContext.canvas.nativeCanvas.apply {
            drawCircle(
                circleCenter.x,
                circleCenter.y,
                radius.toPx(),
                Paint().apply {
                    strokeWidth = scaleWidth.toPx()
                    color = android.graphics.Color.WHITE
                    setStyle(Paint.Style.STROKE)
                    setShadowLayer(60f, 0f, 0f, android.graphics.Color.argb(50, 0, 0, 0))
                }
            )
        }

        // Draw lines
        for (i in minWeight..maxWeight) {
            // Equation 5: Converts weight index to radians for positioning
            // angleInRad = (index - initialWeight + angle - 90) * (PI / 180)
            // Example:
            // If i is 100, initialWeight is 68, and angle is 20 degrees,
            // angleInRad = (100 - 68 + 20 - 90) * (PI / 180) = -38 * (PI / 180) radians.
            val angleInRad = (i - initialWeight + angle - 90) * ((PI / 180f).toFloat())

            val lineType = when {
                i % 10 == 0 -> LineType.TenStep
                i % 5 == 0 -> LineType.FiveStep
                else -> LineType.Normal
            }
            val lineLength = when (lineType) {
                LineType.Normal -> style.normalLineLength.toPx()
                LineType.FiveStep -> style.fiveStepLineLength.toPx()
                LineType.TenStep -> style.tenStepLineLength.toPx()
            }
            val lineColor = when (lineType) {
                LineType.Normal -> style.normalLineColor
                LineType.FiveStep -> style.fiveStepLineColor
                LineType.TenStep -> style.tenStepLineColor
            }

            // Equation 6: Calculates the start and end points for each line
            // lineStart.x = (outerRadius - lineLength) * cos(angleInRad) + circleCenter.x
            // lineStart.y = (outerRadius - lineLength) * sin(angleInRad) + circleCenter.y
            // lineEnd.x = outerRadius * cos(angleInRad) + circleCenter.x
            // lineEnd.y = outerRadius * sin(angleInRad) + circleCenter.y
            // Example:
            // If outerRadius is 100 pixels and lineLength is 10 pixels,
            // for angleInRad = -0.66 radians (approx -38 degrees),
            // lineStart = (90 * cos(-0.66) + 100, 90 * sin(-0.66) + 100)
            // lineEnd = (100 * cos(-0.66) + 100, 100 * sin(-0.66) + 100)
            val lineStart = Offset(
                x = (outerRadius - lineLength) * cos(angleInRad) + circleCenter.x,
                y = (outerRadius - lineLength) * sin(angleInRad) + circleCenter.y
            )
            val lineEnd = Offset(
                x = outerRadius * cos(angleInRad) + circleCenter.x,
                y = outerRadius * sin(angleInRad) + circleCenter.y
            )

            drawContext.canvas.nativeCanvas.apply {
                if (lineType is LineType.TenStep) {
                    val textRadius = outerRadius - lineLength - 5.dp.toPx() - style.textSize.toPx()
                    val x = textRadius * cos(angleInRad) + circleCenter.x
                    val y = textRadius * sin(angleInRad) + circleCenter.y
                    withRotation(
                        degrees = angleInRad * (180f / PI.toFloat()) + 90f,
                        pivotX = x,
                        pivotY = y
                    ) {
                        drawText(
                            abs(i).toString(),
                            x,
                            y,
                            Paint().apply {
                                textSize = style.textSize.toPx()
                                textAlign = Paint.Align.CENTER
                            }
                        )
                    }
                }
            }
            drawLine(
                color = lineColor,
                start = lineStart,
                end = lineEnd,
                strokeWidth = 1.dp.toPx()
            )
        }
        // Draw scale indicator
        val middleTop = Offset(
            x = circleCenter.x,
            y = circleCenter.y - innerRadius - style.scaleIndicatorLength.toPx()
        )
        val bottomLeft = Offset(
            x = circleCenter.x - 4f,
            y = circleCenter.y - innerRadius
        )
        val bottomRight = Offset(
            x = circleCenter.x + 4f,
            y = circleCenter.y - innerRadius
        )
        val indicator = Path().apply {
            moveTo(middleTop.x, middleTop.y)
            lineTo(bottomLeft.x, bottomLeft.y)
            lineTo(bottomRight.x, bottomRight.y)
            lineTo(middleTop.x, middleTop.y)
        }
        drawPath(
            path = indicator,
            color = style.scaleIndicatorColor
        )
    }
}
