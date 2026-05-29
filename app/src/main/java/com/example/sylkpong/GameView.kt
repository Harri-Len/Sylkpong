package com.example.sylkpong

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.annotation.RequiresApi
import androidx.core.graphics.toColorInt

@RequiresApi(Build.VERSION_CODES.O)
class GameView (context: Context) : SurfaceView(context), SurfaceHolder.Callback {
    enum class GameState {
        LOADING,
        PLAYING,
        PAUSED,
        GAMEOVER
    }
    private val playButtonRect = android.graphics.RectF()
    private var gameLoop: GameLoop? = null
    var currentState = GameState.LOADING
    var currentLoad = 0L
    var maxLoad = 250L
    var ballX = 200f
    var ballY = 200f
    var ballPaint = Paint().apply {
        color = Color.RED
    }
    var ballRadius = 25f
    var speedX = 15f
    var speedY = 15f
    var playerX = 300f
    var playerY = height / 2 + 1500f
    var paddleWidth = 75f
    var paddleHeight = 15f
    var paddlePaint = Paint().apply {
        color = Color.GREEN
    }
    var score = 0L
    var eater_font: Typeface? = null

    init {
        try {
            eater_font = Typeface.createFromAsset(context.assets, "fonts/eater.ttf")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        holder.addCallback(this)
        focusable = FOCUSABLE
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        gameLoop = GameLoop(this, holder)
        gameLoop?.startLoop()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        gameLoop?.stopLoop()
    }
    fun update() {
        when (currentState) {
            GameState.LOADING -> {}
            GameState.PAUSED -> {}
            GameState.PLAYING -> playState()
            GameState.GAMEOVER -> {}
        }
    }

    fun playState() {
        ballX += speedX
        ballY += speedY
        if (ballX - ballRadius <= 0 || ballX + ballRadius > width) {
            speedX = -speedX
        }
        if (ballY - ballRadius <= 0) {
            speedY = -speedY
        }
        if (ballY + ballRadius >= playerY && ballY - ballRadius <= playerY + paddleHeight) {
            if (ballX + ballRadius >= playerX && ballX - ballRadius <= playerX + paddleWidth) {
                ballY = playerY - ballRadius
                speedY = -speedY
                score++
            }
        }
        if (ballY >= height) {
            print("Game Over")
            currentState = GameState.GAMEOVER
        }


    }
    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        when (currentState) {
            GameState.LOADING -> drawLoadingScreen(canvas)
            GameState.PLAYING -> drawPlayingScreen(canvas)
            GameState.PAUSED -> {}
            GameState.GAMEOVER -> drawGameOverlay(canvas)
        }
    }

    fun drawGameOverlay(canvas: Canvas) {
        val boxWidth = width * 0.7f
        val boxHeight = height * 0.4f
        val left = width / 2f - (boxWidth / 2)
        val top = height / 2 - (boxHeight / 2)
        val right = width / 2f + (boxWidth / 2)
        val bottom = height / 2 + (boxHeight / 2)
        val overlayPaint = Paint().apply {
            color =("#5F5C5C5E".toColorInt())
            style = Paint.Style.FILL
        }
        canvas.drawRect(left, top, right, bottom, overlayPaint)
        val textPaint = Paint().apply {
            color = ("#5B1515EE".toColorInt())
            textAlign = Paint.Align.CENTER
            textSize = 55f
            typeface = eater_font
            isAntiAlias = true
        }
        canvas.drawText("GAME OVER", width / 2f, (top + ((bottom - top) * 0.2f)), textPaint)
    }
    fun drawPlayingScreen(canvas: Canvas) {
        canvas.drawColor(Color.BLACK)
        val textPaint = Paint().apply {
            color = Color.MAGENTA
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("Welcome to Sylkpong", width / 2f, height / 2f, textPaint)
        canvas.drawCircle(ballX, ballY, ballRadius, ballPaint)
        canvas.drawRect(playerX, playerY, playerX + paddleWidth, playerY + paddleHeight, paddlePaint)
    }
    fun drawLoadingScreen(canvas: Canvas) {
        canvas.drawColor(Color.BLACK)
        val textPaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 55f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        val padding = height / 2f + 15f
        val buttonSize = 45f
        val left = width / 2f - buttonSize - 25
        val top = padding
        val bottom = buttonSize + padding
        val right = width / 2f + buttonSize + 25
        canvas.drawText("SylkPong", width / 2f, height / 2f, textPaint)
        val buttonColor = Paint().apply {
            color = Color.GREEN
        }
        playButtonRect.set(
            left,
            top,
            right,
            bottom,
        )
        canvas.drawRoundRect(playButtonRect, 15f, 15f, buttonColor)
        val textColor = Paint().apply {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("PLAY", width / 2f, top + 23f, textColor)

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                if (currentState == GameState.LOADING) {
                    if (playButtonRect.contains(touchX, touchY)) {
                        currentState = GameState.PLAYING
                        return true
                    }
                }
                playerX = touchX + (paddleWidth / 2)
                if (playerX < 0) playerX = 0f
                if (playerX + paddleWidth > width) playerX = width - paddleWidth
            }
        }
        return true
    }
}