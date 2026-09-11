package com.birthdaywish

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.stringResource
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

private data class Balloon(
    val x: Float,
    val y: Float,
    val color: Color,
    val driftX: Float,
    val driftY: Float
)

private data class Confetti(
    val color: Color,
    val rotation: Float,
    val rotSpeed: Float,
    val seed: Float
)

private val balloonPalette = listOf(
    Color(0xFFFF6B6B), Color(0xFF4ECDC4), Color(0xFFFFE66D),
    Color(0xFFA8E6CF), Color(0xFFDDA0DD), Color(0xFFFF8884),
    Color(0xFF88D8B0), Color(0xFFFFD93D), Color(0xFFF38181),
    Color(0xFFAA96DA), Color(0xFFFCBAD3), Color(0xFFA3D8F4)
)

private val confettiPalette = listOf(
    Color(0xFFFF6B6B), Color(0xFF4ECDC4), Color(0xFFFFE66D),
    Color(0xFFA8E6CF), Color(0xFFDDA0DD), Color(0xFFFF8884),
    Color(0xFF88D8B0), Color(0xFFFFD93D)
)

@Composable
fun BirthdayScreen() {
    val balloons = remember {
        List(12) { index ->
            Balloon(
                x = Random.nextFloat() * 1000f,
                y = Random.nextFloat() * 1200f,
                color = balloonPalette[index % balloonPalette.size],
                driftX = (Random.nextFloat() - 0.5f) * 2f,
                driftY = 0.12f + Random.nextFloat() * 0.18f
            )
        }
    }

    val confetti = remember {
        List(80) { index ->
            Confetti(
                color = confettiPalette[index % confettiPalette.size],
                rotation = Random.nextFloat() * 360f,
                rotSpeed = (Random.nextFloat() - 0.5f) * 360f,
                seed = Random.nextFloat() * 1000f
            )
        }
    }

    var celebrating by remember { mutableStateOf(false) }

    // Auto-trigger celebration 1.5 seconds after launch
    LaunchedEffect(Unit) {
        Handler(Looper.getMainLooper()).postDelayed({
            celebrating = true
        }, 1500L)
    }

    val scale by animateFloatAsState(
        targetValue = if (celebrating) 1.15f else 1f,
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
                    colors = listOf(
                        Color(0xFF1A1A2E), Color(0xFF16213E), Color(0xFF0F3460)
                    ),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawBalloons(balloons)
            if (celebrating) {
                drawConfetti(confetti)
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.wish_main),
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFE66D),
                textAlign = TextAlign.Center,
                modifier = Modifier.scale(scale)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.wish_sub),
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.scale(scale)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.wish_subtitle),
                fontSize = 17.sp,
                color = Color(0xFFA8E6CF),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            Box(
                modifier = Modifier
                    .size(90.dp)
                    .scale(scale)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFFFF6B6B), Color(0xFFFFE66D))
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🎂", fontSize = 44.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.tap_hint),
                fontSize = 13.sp,
                color = Color(0xFFAAAAAA),
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun DrawScope.drawBalloons(balloons: List<Balloon>) {
    val time = System.currentTimeMillis() * 0.001f
    balloons.forEach { b ->
        val x = ((b.x + b.driftX * size.width * 0.35f * time) %
                (size.width + 160f) + (size.width + 160f)) %
                (size.width + 160f) - 80f
        val y = ((b.y - time * b.driftY * size.height) %
                (size.height + 220f) + (size.height + 220f)) %
                (size.height + 220f) - 110f
        drawBalloon(Offset(x, y), b.color)
    }
}

private fun DrawScope.drawBalloon(center: Offset, color: Color) {
    val w = 30f
    val h = 44f
    val top = center.y - h / 2f
    val bottom = center.y + h / 2f
    val c = color.copy(alpha = 0.75f)

    drawOval(
        rect = Rect.fromLTRB(
            center.x - w / 2f, top + 7f,
            center.x + w / 2f, bottom - 1f
        ),
        color = c
    )

    drawLine(
        color = c,
        start = Offset(center.x, bottom - 1f),
        end = Offset(center.x, bottom + 8f),
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawConfetti(particles: List<Confetti>) {
    val time = System.currentTimeMillis() * 0.001f
    particles.forEach { p ->
        val phase = time * 2f + p.seed
        val screenX = ((phase * 50f + p.rotation * 2f) % size.width)
        val screenY = ((phase * 80f + p.rotation) % size.height)
        val alpha = 1f - (screenY / size.height)
        val rot = p.rotation + p.rotSpeed * time

        drawContext.canvas.save()
        drawContext.canvas.translate(screenX, screenY)
        drawContext.canvas.rotate(rot)
        drawRect(
            color = p.color.copy(alpha = alpha.coerceIn(0f, 1f)),
            topLeft = Offset(-5f, -9f),
            size = Size(10f, 18f)
        )
        drawContext.canvas.restore()
    }
}
