package co.vivita.viviware.stopmotion_mpgenerator

import android.app.ProgressDialog
import android.content.Context
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.arthenica.mobileffmpeg.Config.*
import com.arthenica.mobileffmpeg.FFmpeg
import java.io.File
import java.io.IOException

/**
 * Created by kashimoto on 2017/03/24.
 */
object MovieUtils {
    private const val TAG = "MovieUtils"
    private var sFfmpeg: FFmpeg? = null
    private var sMpegEncodeSuccess = false
    const val SOURCE_FILE_FORMAT = "source%04d.jpg"

    fun standbyFfmpeg(context: Context?) {
//        sFfmpeg = FFmpeg.getInstance(context)
//        try {
//            sFfmpeg!!.loadBinary(object : LoadBinaryResponseHandler() {
//                override fun onStart() {
//                    Log.w(TAG, "load onStart")
//                }
//
//                override fun onFailure() {
//                    Toast.makeText(context, R.string.msg_error_init_ffmpeg, Toast.LENGTH_LONG)
//                        .show()
//                    Log.w(TAG, "load onFailure")
//                }
//
//                override fun onSuccess() {
//                    Log.w(TAG, "load onSuccess")
//                }
//
//                override fun onFinish() {
//                    Log.w(TAG, "load onFinish")
//                }
//            })
//        } catch (e: FFmpegNotSupportedException) { // Handle if FFmpeg is not supported by device
//        }
    }

    fun generateMovieWithFfmpeg(
        context: Context,
        projectName: String,
        projectDir: File,
        listener: OnCompleteGenerateListener
    ) {
        if (sFfmpeg == null) {
            Toast.makeText(context, R.string.msg_failed_to_init_ffmpeg, Toast.LENGTH_LONG).show()
            return
        }
        try {
            val fileDir = context.filesDir.absolutePath
            val curDate = System.currentTimeMillis()
            // 保存場所
            val mpegDir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
            val mpegFile = File(mpegDir, projectName + "_" + curDate + ".mp4")
            val cmdStr =
                "-f image2 -r 8 -analyzeduration 2147483647 -probesize 2147483647 -i $projectDir/$SOURCE_FILE_FORMAT -an -vcodec libx264 $mpegFile"
            //            String cmdStr = "-f image2 -r 2 -mPlaySeqNum /sdcard/source%03d.png -an -vcodec libx264 /sdcard/video4.mp4";
            val cmd = cmdStr.split(" ").toTypedArray()

            // トータル枚数は？
            val max = projectDir.listFiles().filter { it.name.startsWith("source") }.size
            Log.w(TAG, "total pages : $max")

            // ダイアログをだす
            val progressDialog = ProgressDialog(context)
            progressDialog.setTitle(context.getString(R.string.msg_create_movie))
            progressDialog.setCancelable(false)
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            progressDialog.show()

            val rc = FFmpeg.execute("-f image2 -r 8 -analyzeduration 2147483647 -probesize 2147483647 -i $projectDir/$SOURCE_FILE_FORMAT -an -vcodec libx264 $mpegFile")

            when (rc) {
                RETURN_CODE_SUCCESS -> {

                }
                RETURN_CODE_CANCEL -> {

                }
            }
//            {
//                override fun onStart() {
//                    Log.w(TAG, "onStart")
//                }
//
//                override fun onProgress(message: String) {
//                    Log.w(TAG, "onProgress : $message")
//                    try {
//                        if (message.startsWith("frame=")) {
//                            val progress = Integer.parseInt(
//                                message.substring(
//                                    6,
//                                    message.indexOf("fps=")
//                                ).trim()
//                            )
//                            Log.w(TAG, "progress $progress")
//                            progressDialog.setMax(max)
//                            progressDialog.setProgress(progress)
//                        }
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }
//
//                override fun onFailure(message: String) {
//                    Log.w(TAG, "onFailure : $message")
//                    sMpegEncodeSuccess = false
//                }
//
//                override fun onSuccess(message: String) {
//                    Log.w(TAG, "onSuccess : $message")
//                    sMpegEncodeSuccess = true
//                }
//
//                override fun onFinish() {
//                    Log.w(
//                        TAG,
//                        "onFinish $sMpegEncodeSuccess, path $mpegFile"
//                    )
//                    progressDialog.dismiss()
//                    if (sMpegEncodeSuccess) {
//                        listener.onCompleteGenerateMovie(mpegFile)
//                    } else {
//                        Toast.makeText(
//                            context,
//                            R.string.msg_failed_generate_mpeg,
//                            Toast.LENGTH_LONG
//                        ).show()
//                    }
//                }
//            })
        } catch (e: Exception) { // Handle if FFmpeg is already running
        }
    }

    @Deprecated("")
    private fun tmp() { //        try {
//            SequenceEncoder enc = new SequenceEncoder(new File(getContext().getFilesDir() + "/aaa.mp4"));
//            for(Bitmap bmp : mBitmapList) {
//                Log.w(TAG, "bmp add ");
//                SequenceEncoder enc2 = new SequenceEncoder(new File(getContext().getFilesDir() + "/aaa2.mp4"));
//                enc.encodeImage(bmp);
//            }
//            enc.finish();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Deprecated("")
    private fun tmp2() {
        try {
            val mMediaCodec = MediaCodec.createEncoderByType("video/avc")
            val mMediaFormat = MediaFormat.createVideoFormat("video/avc", 320, 240)
            mMediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 125000)
            mMediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 15)
            mMediaFormat.setInteger(
                MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar
            )
            mMediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5)
            mMediaCodec.configure(mMediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            mMediaCodec.start()
            val mInputBuffers = mMediaCodec.inputBuffers
            //            for (Bitmap image : mBitmapList) {
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream); // image is the bitmap
//                byte[] input = byteArrayOutputStream.toByteArray();
//
//                int inputBufferIndex = mMediaCodec.dequeueInputBuffer(-1);
//                if (inputBufferIndex >= 0) {
//                    ByteBuffer inputBuffer = mInputBuffers[inputBufferIndex];
//                    inputBuffer.clear();
//                    inputBuffer.put(input);
//                    mMediaCodec.queueInputBuffer(inputBufferIndex, 0, input.length, 0, 0);
//                }
//            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    interface OnCompleteGenerateListener {
        fun onCompleteGenerateMovie(file: File)
    }
}