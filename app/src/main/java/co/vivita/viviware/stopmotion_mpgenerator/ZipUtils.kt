package co.vivita.viviware.stopmotion_mpgenerator

import android.util.Log
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object ZipUtils {

    private fun getFiles(topDir: File, parentDir: File, files: MutableList<Pair<String, File>>) {

        // ファイル取得対象フォルダ直下のファイル,ディレクトリを走査
        for (f in parentDir.listFiles()) {

            // ファイルの場合はファイル一覧に追加
            if (f.isFile) {
                files.add(Pair(topDir.name + "/" + f.name, f))
                // ディレクトリの場合は再帰処理
            } else if (f.isDirectory) {
                getFiles(topDir, f, files)
            }
        }
    }

    fun compress(parentDir: File, outputFile: String) {

        val inputFiles = ArrayList<Pair<String, File>>()
        getFiles(parentDir, parentDir, inputFiles)

        var inputStream: InputStream? = null
        var zos: ZipOutputStream
        val buf = ByteArray(1024)
        try {
            zos = ZipOutputStream(FileOutputStream(outputFile))
            for (i in inputFiles.indices) {
                try {
                    inputStream = FileInputStream(inputFiles[i].second)
                    // Setting Filename
                    val filename = inputFiles[i].first
                    // ZIPエントリを作成
                    val ze = ZipEntry(filename)
                    // 作成したZIPエントリを登録
                    zos.putNextEntry(ze)
                    // 入力ストリームからZIP形式の出力ストリームへ書き出す
                    var len = 0
                    while (inputStream.read(buf).also({ len = it }) != -1) {
                        zos.write(buf, 0, len)
                    }
                    // 入力ストリームを閉じる
                    inputStream.close()
                    // エントリをクローズする
                    zos.closeEntry()
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
            // 出力ストリームを閉じる
            zos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * @return 展開したDir
     */
    fun expand(filePath: String, expandDir: String): File? {
        val input = ZipInputStream(BufferedInputStream(FileInputStream(filePath)))
        var zipEntry: ZipEntry?
        var data: Int
        var expandedDir: File? = null
        while (input.getNextEntry().also { zipEntry = it } != null) {

            // 出力用ファイルストリームの生成
            Log.i(TAG, "zipEntry : $zipEntry")
            val dir = File(expandDir + "/" + zipEntry!!.getName())
            dir.parentFile.mkdirs()
            expandedDir = dir.parentFile
            val output = BufferedOutputStream(FileOutputStream(expandDir + "/" + zipEntry!!.getName()))

            // エントリの内容を出力
            output.write(input.readBytes())
            output.close()
        }
        return expandedDir
    }

    val TAG = "ZipUtils"
}
