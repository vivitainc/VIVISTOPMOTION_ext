package co.vivita.viviware.stopmotion_mpgenerator

import android.app.ProgressDialog
import android.content.Context
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL
import com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS
import com.arthenica.mobileffmpeg.FFmpeg
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

/**
 * Created by kashimoto on 2017/03/24.
 */
object MovieUtils {
    private const val TAG = "MovieUtils"
    const val SOURCE_FILE_FORMAT = "source%04d.jpg"

    fun generateMovieWithFfmpeg(
        context: Context,
        projectName: String,
        projectDir: File,
        listener: OnCompleteGenerateListener
    ) {
        try {
            val curDate = System.currentTimeMillis()
            // 保存場所
            val mpegDir = context.cacheDir
            val mpegFile = File(mpegDir, projectName + "_" + curDate + ".mp4")

            // トータル枚数は？
            val max = projectDir.listFiles().filter { it.name.startsWith("source") }.size
            Log.w(TAG, "total pages : $max")

            // ダイアログをだす
            val progressDialog = ProgressDialog(context)
            progressDialog.setTitle(context.getString(R.string.msg_create_movie))
            progressDialog.setCancelable(false)
            progressDialog.show()

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    Log.w(TAG, "source file : $projectDir/$SOURCE_FILE_FORMAT")
                    val rc =
                        FFmpeg.execute("-f image2 -r 8 -analyzeduration 2147483647 -probesize 2147483647 -i $projectDir/$SOURCE_FILE_FORMAT -an -vcodec libx264 $mpegFile")

                    when (rc) {
                        RETURN_CODE_SUCCESS -> {
                            Log.w(TAG, "ffmpeg rc success")
                            listener.onCompleteGenerateMovie(mpegFile)
                        }
                        RETURN_CODE_CANCEL -> {
                            Log.w(TAG, "ffmpeg rc cancel")
                        }
                        else -> {
                            Log.w(TAG, "ffmpeg rc else")
                        }
                    }
                } catch (e: Exception) { // Handle if FFmpeg is already running
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) { // Handle if FFmpeg is already running
            e.printStackTrace()
        }
    }

    interface OnCompleteGenerateListener {
        fun onCompleteGenerateMovie(file: File)
    }
}