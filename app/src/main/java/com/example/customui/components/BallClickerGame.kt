package com.example.customui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random

@Composable
fun MainScreen() {
    var points by remember {
        mutableIntStateOf(0)
    }
    var isTimerRunning by remember {
        mutableStateOf(false)
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,

            ) {
            Text(
                text = "Points: $points", fontSize = 20.sp, fontWeight = FontWeight.Bold
            )
            Button(onClick = {
                isTimerRunning = !isTimerRunning
                points = 0
            }) {
                Text(text = if (isTimerRunning) "Reset" else "Start")
            }
            CountdownTimer(
                isTimerRunning = isTimerRunning
            ) {
                isTimerRunning = false
            }
        }
        BallClicker(
            enabled = isTimerRunning
        ) {
            points++
        }
    }
}


@Composable
fun CountdownTimer(
    time: Int = 30000,
    isTimerRunning: Boolean = false,
    onTimerFinished: () -> Unit = {},
) {

    var currentTime by remember {
        mutableIntStateOf(time)
    }

    LaunchedEffect(key1 = currentTime, key2 = isTimerRunning) {

        if (!isTimerRunning) {
            currentTime = time
            return@LaunchedEffect
        }

        if (currentTime > 0) {
            delay(1000L)
            currentTime -= 1000
        } else {
            onTimerFinished()
        }
    }

    Text(
        text = (currentTime / 1000).toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold
    )
}


@Composable
fun BallClicker(
    radius: Float = 100f,
    enabled: Boolean = false,
    color: Color = Red,
    onBallClick: () -> Unit = {},
) {

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {

        var ballPosition by remember {
            mutableStateOf(
                randomOffset(
                    radius = radius, width = constraints.maxWidth, height = constraints.maxHeight
                )
            )
        }

        Canvas(modifier = Modifier
            .fillMaxSize()
            .pointerInput(enabled) {
                if (!enabled) {
                    return@pointerInput
                }

                detectTapGestures {
                    val distance = sqrt(
                        (it.x - ballPosition.x).pow(2) + (it.y - ballPosition.y).pow(2)
                    )

                    if (distance <= radius) {
                        ballPosition = randomOffset(
                            radius = radius,
                            width = constraints.maxWidth,
                            height = constraints.maxHeight
                        )
                        onBallClick()
                    }
                }
            }) {

            drawCircle(
                color = color, radius = radius, center = ballPosition
            )

        }
    }
}


private fun randomOffset(
    radius: Float,
    width: Int,
    height: Int,
): Offset {
    return Offset(
        x = Random.nextInt(radius.roundToInt(), width - radius.roundToInt()).toFloat(),
        y = Random.nextInt(radius.roundToInt(), height - radius.roundToInt()).toFloat()
    )
}