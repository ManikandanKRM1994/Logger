package com.krm.logger

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.krm.logger.log.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        Log.d("Tag", "I'm a log which you don't see easily, hehe")
        Log.d("json content", "{ \"key\": 3, \n \"value\": something}")
        Log.d("error", "There is a crash somewhere or any warning")

        Logger.addLogAdapter(AndroidLogAdapter())
        Logger.d("message")

        Logger.clearLogAdapters()


        var formatStrategy: FormatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false)
            .methodCount(0)
            .methodOffset(3)
            .tag("My custom tag")
            .build()

        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))

        Logger.addLogAdapter(object : AndroidLogAdapter() {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })

        Logger.addLogAdapter(DiskLogAdapter())


        Logger.w("no thread info and only 1 method")

        Logger.clearLogAdapters()
        formatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false)
            .methodCount(0)
            .build()

        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))
        Logger.i("no thread info and method info")

        Logger.t("tag").e("Custom tag for only one use")

        Logger.json("{ \"key\": 3, \"value\": something}")

        Logger.d(listOf("foo", "bar"))

        val map: MutableMap<String, String> =
            HashMap()
        map["key"] = "value"
        map["key1"] = "value2"

        Logger.d(map)

        Logger.clearLogAdapters()
        formatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false)
            .methodCount(0)
            .tag("MyTag")
            .build()
        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))

        Logger.w("my log message with my tag")

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
