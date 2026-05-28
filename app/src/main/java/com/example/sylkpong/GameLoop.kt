import android.graphics.Canvas
import android.os.Build
import android.view.SurfaceHolder
import androidx.annotation.RequiresApi

class GameLoop (private val gameView: GameView, private val surfaceHolder: SurfaceHolder) : Thread() {
    private var isRunning = false
    private val targetFPS = 60
    private val targetTime = (1000L / targetFPS)

    fun startLoop() {
        isRunning = true
        start()
    }
    fun stopLoop() {
        isRunning = false
        try {
            join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun run() {
        var startTime: Long
        var timeMillis: Long
        var waitTime: Long
        var canvas: Canvas?
        while (isRunning) {
            startTime = System.currentTimeMillis()
            canvas = null
            try {
                canvas = surfaceHolder.lockCanvas()
                synchronized(surfaceHolder) {
                    gameView.update()
                    if (canvas != null) {
                        gameView.draw(canvas)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if(canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            timeMillis = System.currentTimeMillis()
            waitTime = targetTime - timeMillis
            if (waitTime > 0) {
                try {
                    sleep(waitTime)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }

        }
    }
}