package com.krm.logger.log

import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import com.krm.logger.log.DiskLogStrategy.WriteHandler
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CsvFormatStrategy private constructor(builder: Builder) :
    FormatStrategy {
    private val date: Date
    private val dateFormat: SimpleDateFormat
    private val logStrategy: LogStrategy
    private val tag: String?
    override fun log(
        priority: Int,
        tag: String?,
        message: String
    ) {
        var sMessage = message
        Utils.checkNotNull(sMessage)
        val mTag = formatTag(tag)
        date.time = System.currentTimeMillis()
        val builder = StringBuilder()
        builder.append(date.time.toString())
        builder.append(SEPARATOR)
        builder.append(dateFormat.format(date))
        builder.append(SEPARATOR)
        builder.append(Utils.logLevel(priority))
        builder.append(SEPARATOR)
        builder.append(mTag)
        if (sMessage.contains(NEW_LINE!!)) {
            sMessage = sMessage.replace(
                NEW_LINE.toRegex(),
                NEW_LINE_REPLACEMENT
            )
        }
        builder.append(SEPARATOR)
        builder.append(sMessage)
        builder.append(NEW_LINE)
        logStrategy.log(priority, mTag, builder.toString())
    }

    private fun formatTag(tag: String?): String? {
        return if (!Utils.isEmpty(tag) && !Utils.equals(
                this.tag,
                tag
            )
        ) {
            this.tag + "-" + tag
        } else this.tag
    }

    class Builder {
        var date: Date? = null
        var dateFormat: SimpleDateFormat? = null
        var logStrategy: LogStrategy? = null
        var tag: String? = "PRETTY_LOGGER"
        fun date(`val`: Date?): Builder {
            date = `val`
            return this
        }

        fun dateFormat(`val`: SimpleDateFormat?): Builder {
            dateFormat = `val`
            return this
        }

        fun logStrategy(`val`: LogStrategy?): Builder {
            logStrategy = `val`
            return this
        }

        fun tag(tag: String?): Builder {
            this.tag = tag
            return this
        }

        fun build(): CsvFormatStrategy {
            if (date == null) {
                date = Date()
            }
            if (dateFormat == null) {
                dateFormat =
                    SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS", Locale.UK)
            }
            if (logStrategy == null) {
                val diskPath =
                    Environment.getExternalStorageDirectory().absolutePath
                val folder = diskPath + File.separatorChar + "logger"
                val ht = HandlerThread("AndroidFileLogger.$folder")
                ht.start()
                val handler: Handler = WriteHandler(
                    ht.looper,
                    folder,
                    MAX_BYTES
                )
                logStrategy = DiskLogStrategy(handler)
            }
            return CsvFormatStrategy(this)
        }

        companion object {
            private const val MAX_BYTES = 500 * 1024
        }
    }

    companion object {
        private val NEW_LINE = System.getProperty("line.separator")
        private const val NEW_LINE_REPLACEMENT = " <br> "
        private const val SEPARATOR = ","
        @JvmStatic
        fun newBuilder(): Builder {
            return Builder()
        }
    }

    init {
        Utils.checkNotNull(builder)
        date = builder.date!!
        dateFormat = builder.dateFormat!!
        logStrategy = builder.logStrategy!!
        tag = builder.tag
    }
}