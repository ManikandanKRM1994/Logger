package com.krm.logger.log

import android.os.Handler
import android.os.Looper
import android.os.Message
import java.io.File
import java.io.FileWriter
import java.io.IOException

class DiskLogStrategy(handler: Handler) : LogStrategy {
    private val handler: Handler = Utils.checkNotNull(handler)
    override fun log(priority: Int, tag: String?, message: String) {
        Utils.checkNotNull(message)
        handler.sendMessage(handler.obtainMessage(priority, message))
    }

    internal class WriteHandler(
        looper: Looper,
        folder: String,
        private val maxFileSize: Int
    ) : Handler(Utils.checkNotNull(looper)) {
        private val folder: String = Utils.checkNotNull(folder)
        override fun handleMessage(msg: Message) {
            val content = msg.obj as String
            var fileWriter: FileWriter? = null
            val logFile = getLogFile(folder, "logs")
            try {
                fileWriter = FileWriter(logFile, true)
                writeLog(fileWriter, content)
                fileWriter.flush()
                fileWriter.close()
            } catch (e: IOException) {
                if (fileWriter != null) {
                    try {
                        fileWriter.flush()
                        fileWriter.close()
                    } catch (e1: IOException) {
                    }
                }
            }
        }

        @Throws(IOException::class)
        private fun writeLog(fileWriter: FileWriter, content: String) {
            Utils.checkNotNull(fileWriter)
            Utils.checkNotNull(content)
            fileWriter.append(content)
        }

        private fun getLogFile(folderName: String, fileName: String): File {
            Utils.checkNotNull(folderName)
            Utils.checkNotNull(fileName)
            val folder = File(folderName)
            if (!folder.exists()) {
                folder.mkdirs()
            }
            var newFileCount = 0
            var newFile: File
            var existingFile: File? = null
            newFile =
                File(folder, String.format("%s_%s.csv", fileName, newFileCount))
            while (newFile.exists()) {
                existingFile = newFile
                newFileCount++
                newFile =
                    File(folder, String.format("%s_%s.csv", fileName, newFileCount))
            }
            return if (existingFile != null) {
                if (existingFile.length() >= maxFileSize) {
                    newFile
                } else existingFile
            } else newFile
        }

    }

}