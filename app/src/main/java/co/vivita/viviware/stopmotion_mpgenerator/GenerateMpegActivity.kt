package co.vivita.viviware.stopmotion_mpgenerator

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File


class GenerateMpegActivity : AppCompatActivity(), MovieUtils.OnCompleteGenerateListener {

    private var projectDir: File? = null
    val TAG = "GenerateMpegActivity"

    lateinit var projectName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // request permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_CODE_PERMISSION
            )
            return
        }

        projectName = intent.getStringExtra(INTENT_EX_PROJECT_NAME) ?: "no_name"

        // 全消去
        cacheDir.listFiles()?.all {
            it.deleteRecursively()
        }

        // intent のURIを見て動画生成開始
        intent.getParcelableExtra<Uri>(MediaStore.EXTRA_OUTPUT)?.let { returnUri ->
            Log.w(TAG, "zip uri = $returnUri")

            getContentResolver().openInputStream(returnUri)?.use { inputStream ->
                val tmpDir = File(cacheDir, "tmp")
                tmpDir.mkdirs()
                val recvFile = File(tmpDir, "tekitou.zip")
                recvFile.outputStream().use { outputStream ->
                    // ファイルにコピーされる
                    inputStream.copyTo(outputStream)
                    projectDir = ZipUtils.expand(recvFile.absolutePath, cacheDir.absolutePath)
                    projectDir?.also {
                        startEncodeMpeg(it)
                    }
                }
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE_PERMISSION
                )
            } else {
                Toast.makeText(this, getString(R.string.msg_please_retry), Toast.LENGTH_LONG).show()
            }
        }
    }

    fun startEncodeMpeg(projectDir: File) {
        MovieUtils.standbyFfmpeg(this)
        MovieUtils.generateMovieWithFfmpeg(this, projectName, projectDir, this)
    }

    override fun onCompleteGenerateMovie(file: File) {
        val intent = Intent()
        val uri = FileProvider.getUriForFile(this, packageName + ".fileprovider", file)
        grantUriPermission("co.vivita.viviware.stopmotion.mgd", uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        grantUriPermission("co.vivita.viviware.stopmotion.mgd.dev", uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        intent.putExtra(INTENT_EX_MPEG_NAME, file.name)
        setResult(RESULT_OK, intent)
        finish()
    }

    companion object {
        val REQUEST_CODE_PERMISSION = 0
        val INTENT_EX_PROJECT_NAME = "intent_ex_project_name"
        val INTENT_EX_PROJECT_DIR = "intent_ex_project_dir"
        val INTENT_EX_MPEG_NAME = "intent_ex_mpeg_name"
        val INTENT_EX_GENERATED_FILEPATH = "intent_ex_generated_filepath"
    }

}
