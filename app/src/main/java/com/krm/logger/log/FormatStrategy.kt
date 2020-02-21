package com.krm.logger.log

interface FormatStrategy {
    fun log(priority: Int, tag: String?, message: String)
}