package com.birthdaywish

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateFloatAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                BirthdayScreen()
            }
        }
    }
}

private data class Balloon(val x: Float, val y: Float, val color: Color, val drift: Float)

@Composable
fun BirthdayScreen() {
    val balloons = remember {
        List(10) {
            Balloon(
                x = Random.nextFloat() * 400f + 40f,
                y = Random.nextFloat() * 900f + 200f,
                color = listOf(
                    Color(0xFFFF6B6B), Color(0xFF4ECDC4), Color(0xFFFFE66D),
                    Color(0xFFA8E6CF), Color(0xFFDDA0DD), Color(0xFFFF8884),
                    Color(0xFF88D8B0), Color(0xFFFFD93D), Color(0xFFF38181),
                    Color(0xFFAA96DA)
                )[it % 10],
                drift = Random.nextFloat() * 2f - 1f
            )
        }
    }

    var celebrating by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (celebrating) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "celebrate_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A1A2E), Color(0xFF16213E), Color(0xFF0F3460)),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val time = (System.currentTimeMillis() % 10_000) / 10_000f
            balloons.forEach { balloon ->
                val x = (balloon.x + balloon.drift * size.width * 0.6f * time) % size.width
                val y = (balloon.y - time * size.height * 0.2f) % (size.height + 200f) - 100f
                drawBalloon(Offset(x, y), balloon.color)
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🎉 শুভ জন্মদিন! 🎉",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFE66D),
                textAlign = TextAlign.Center,
                modifier = Modifier.scale(scale)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "তোমার জন্মদিনের শুভকামনা!",
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "তোমার দিনটা সুন্দর হোক 🎂✨",
                fontSize = 18.sp,
                color = Color(0xFFA8E6CF),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .scale(scale)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFFFF6B6B), Color(0xFFFFE66D))
                        ),
                        shape = androidx.compose.foundation.shape.CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🎂", fontSize = 42.sp)
            }
        }
    }
}

private fun DrawScope.drawBalloon(center: Offset, color: Color) {
    val width = 30f
    val height = 44f
    val top = center.y - height / 2f
    val bottom = center.y + height / 2f
    val balloonColor = color.copy(alpha = 0.7f)

    drawOval(
        rect = androidx.compose.ui.geometry.Rect.fromLTRB(
            center.x - width / 2f,
            top + 8f,
            center.x + width / 2f,
            bottom - 2f
        ),
        color = balloonColor
    )

    drawLine(
        color = balloonColor,
        start = Offset(center.x, bottom - 2f),
        end = Offset(center.x, bottom + 10f),
        strokeWidth = 2f,
        cap = StrokeCap.Round
    )
}
