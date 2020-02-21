package com.krm.logger.log

interface LogStrategy {
    fun log(priority: Int, tag: String?, message: String)
}