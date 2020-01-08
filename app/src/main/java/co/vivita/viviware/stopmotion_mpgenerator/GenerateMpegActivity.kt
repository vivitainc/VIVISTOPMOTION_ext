package co.vivita.viviware.stopmotion_mpgenerator

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File

class GenerateMpegActivity : AppCompatActivity(), MovieUtils.OnCompleteGenerateListener {

    val TAG = "GenerateMpegActivity"

    lateinit var projectName: String
    lateinit var projectDir: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        projectName = intent.getStringExtra(INTENT_EX_PROJECT_NAME) ?: "no_name"
        projectDir = intent.getStringExtra(INTENT_EX_PROJECT_DIR) ?: "no_name"

        // request permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSION)
        } else {
            startEncodeMpeg()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSION)
            } else {
                startEncodeMpeg()
            }
        }
    }

    fun startEncodeMpeg() {
        MovieUtils.standbyFfmpeg(this)
        MovieUtils.generateMovieWithFfmpeg(this, projectName, projectDir,this)
    }

    override fun onCompleteGenerateMovie(filePath: String) {
        val intent = Intent()
        intent.putExtra(INTENT_EX_GENERATED_FILEPATH, filePath)
        setResult(RESULT_OK, intent)
        finish()
    }

    companion object {
        val REQUEST_CODE_PERMISSION = 0
        val INTENT_EX_PROJECT_NAME = "intent_ex_project_name"
        val INTENT_EX_PROJECT_DIR = "intent_ex_project_dir"
        val INTENT_EX_GENERATED_FILEPATH = "intent_ex_generated_filepath"
    }

}
