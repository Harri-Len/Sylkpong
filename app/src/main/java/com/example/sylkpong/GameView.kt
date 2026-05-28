import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.O)
class GameView (context: Context) : SurfaceView(context), SurfaceHolder.Callback {
    enum class GameState {
        LOADING,
        PLAYING,
        PAUSED
    }
    private val PlayButtonRect = android.graphics.RectF()
    private var gameLoop: GameLoop? = null
    var currentState = GameState.LOADING
    var currentLoad = 0L
    var maxLoad = 250L

    init {
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
            GameState.LOADING -> {
                currentLoad += 5
                if (currentLoad >= maxLoad) {
                    currentState = GameState.PLAYING
                }
            }
            GameState.PAUSED -> {}
            GameState.PLAYING -> {

            }
        }
    }
    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        when (currentState) {
            GameState.LOADING -> drawLoadingScreen(canvas)
            GameState.PLAYING -> drawPlayingScreen(canvas)
            GameState.PAUSED -> {}
        }
    }

    fun drawPlayingScreen(canvas: Canvas) {
        canvas.drawColor(Color.BLACK)
        val textPaint = Paint().apply {
            color = Color.MAGENTA
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("Welcome to Sylkpong", width / 2f, height / 2f, textPaint)
    }
    fun drawLoadingScreen(canvas: Canvas) {
        canvas.drawColor(Color.BLACK)
        val textPaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 35f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        val padding = 20f
        val buttonSize = 45f
        val left = width - buttonSize - padding
        val top = padding
        val bottom = buttonSize + padding
        val right = width - padding
        canvas.drawText("SylkPong", width / 2f, height / 2f, textPaint)
        val buttonColor = Paint().apply {
            color = Color.GREEN
        }
        PlayButtonRect.set(
            left,
            top,
            right,
            bottom,
        )
        canvas.drawRoundRect(PlayButtonRect, 15f, 15f, buttonColor)
        val textColor = Paint().apply {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("PLAY", left + 25, top + 10, textColor)

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (currentState == GameState.LOADING) return true
        val touchX = event.x
        val touchY = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                if (currentState == GameState.LOADING) {
                    if (PlayButtonRect.contains(touchX, touchY)) {
                        currentState = GameState.PLAYING
                    }
                }
            }
        }
        return true
    }
}