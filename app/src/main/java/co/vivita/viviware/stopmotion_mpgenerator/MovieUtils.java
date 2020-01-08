package co.vivita.viviware.stopmotion_mpgenerator;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Movie;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by kashimoto on 2017/03/24.
 */

public class MovieUtils {
    private static final String TAG = "MovieUtils";
    private static FFmpeg sFfmpeg = null;
    private static boolean sMpegEncodeSuccess = false;
    public static final String SOURCE_FILE_FORMAT = "source%04d.jpg";

    public static void standbyFfmpeg(final Context context) {
        sFfmpeg = FFmpeg.getInstance(context);
        try {
            sFfmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.w(TAG, "load onStart");
                }

                @Override
                public void onFailure() {
                    Toast.makeText(context, R.string.msg_error_init_ffmpeg, Toast.LENGTH_LONG).show();
                    Log.w(TAG, "load onFailure");
                }

                @Override
                public void onSuccess() {
                    Log.w(TAG, "load onSuccess");
                }

                @Override
                public void onFinish() {
                    Log.w(TAG, "load onFinish");
                }
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
        }
    }

    public interface OnCompleteGenerateListener {
        void onCompleteGenerateMovie(String filePath);
    }

    public static void generateMovieWithFfmpeg(final Context context, String projectName, String projectDir, final OnCompleteGenerateListener listener) {

        if (sFfmpeg == null) {
            Toast.makeText(context, R.string.msg_failed_to_init_ffmpeg, Toast.LENGTH_LONG).show();
            return;
        }
        try {
            String fileDir = context.getFilesDir().getAbsolutePath();
            final long curDate = System.currentTimeMillis();

            // 保存場所
//            final String mpegFilePath = fileDir+"/video"+curDate + "_" + title +".mp4";
            final String mpegFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/" + curDate + "_" + projectName +".mp4";

            String cmdStr = "-f image2 -r 8 -analyzeduration 2147483647 -probesize 2147483647 -i " + projectDir + projectName +  "/" + SOURCE_FILE_FORMAT + " -an -vcodec libx264 " + mpegFilePath;
//            String cmdStr = "-f image2 -r 2 -mPlaySeqNum /sdcard/source%03d.png -an -vcodec libx264 /sdcard/video4.mp4";
            String[] cmd = cmdStr.split(" ");

            // ダイアログをだす
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle(context.getString(R.string.msg_create_movie));
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();

            sFfmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.w(TAG, "onStart");
                }

                @Override
                public void onProgress(String message) {
                    Log.w(TAG, "onProgress : " + message);
                }

                @Override
                public void onFailure(String message) {
                    Log.w(TAG, "onFailure : " + message);
                    sMpegEncodeSuccess = false;
                }

                @Override
                public void onSuccess(String message) {
                    Log.w(TAG, "onSuccess : " + message);
                    sMpegEncodeSuccess = true;
                }

                @Override
                public void onFinish() {
                    Log.w(TAG, "onFinish " + sMpegEncodeSuccess + ", path " + mpegFilePath);

                    progressDialog.dismiss();

                    if (sMpegEncodeSuccess) {
                        listener.onCompleteGenerateMovie(mpegFilePath);
                    } else {
                        Toast.makeText(context, R.string.msg_failed_generate_mpeg,Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
        }
    }

    /**
     * @deprecated
     */
    private static void tmp() {
//        try {
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

    /**
     * @deprecated
     */
    private static void tmp2() {
        try {
            MediaCodec mMediaCodec = MediaCodec.createEncoderByType("video/avc");
            MediaFormat mMediaFormat = MediaFormat.createVideoFormat("video/avc", 320, 240);
            mMediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 125000);
            mMediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 15);
            mMediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar);
            mMediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5);
            mMediaCodec.configure(mMediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mMediaCodec.start();
            ByteBuffer[] mInputBuffers = mMediaCodec.getInputBuffers();

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


