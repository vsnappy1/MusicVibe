package com.randos.logger

import android.util.Log

object Logger {

    // ---- VERBOSE ----

    /**
     * Send a [LogType.VERBOSE] log message.
     * Note* For Tag It tries to get the class name where log statement is invoked.
     * @param msg The message you would like logged.
     * @return A positive value if the message was loggable.
     */
    @JvmStatic
    fun v(msg: String): Int {
        return log(LogType.VERBOSE, getClassName(), msg)
    }

    /**
     * Send a [LogType.VERBOSE] log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @return A positive value if the message was loggable.
     */
    @JvmStatic
    fun v(tag: String, msg: String): Int {
        return log(LogType.VERBOSE, tag, msg)
    }

    /**
     * Send a [LogType.VERBOSE] log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log.
     * @return A positive value if the message was loggable.
     */
    @JvmStatic
    fun v(tag: String, msg: String, tr: Throwable?): Int {
        return log(LogType.VERBOSE, tag, msg, tr)
    }

    // ---- DEBUG ----

    /**
     * Send a [LogType.DEBUG] log message.
     * Note* For Tag It tries to get the class name where log statement is invoked.
     * @param msg The message you would like logged.
     * @return A positive value if the message was loggable.
     */
    @JvmStatic
    fun d(msg: String): Int {
        return log(LogType.DEBUG, getClassName(), msg)
    }

    /**
     * Send a [LogType.DEBUG] log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @return A positive value if the message was loggable.
     */
    @JvmStatic
    fun d(tag: String, msg: String): Int {
        return log(LogType.DEBUG, tag, msg)
    }

    /**
     * Send a [LogType.DEBUG] log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log.
     * @return A positive value if the message was loggable.
     */
    @JvmStatic
    fun d(tag: String, msg: String, tr: Throwable?): Int {
        return log(LogType.DEBUG, tag, msg, tr)
    }

    // ---- INFO ----

    /**
     * Send a [LogType.INFO] log message.
     * Note* For Tag It tries to get the class name where log statement is invoked.
     * @param msg The message you would like logged.
     * @return A positive value if the message was loggable.
     */
    @JvmStatic
    fun i(msg: String): Int {
        return log(LogType.INFO, getClassName(), msg)
    }

    /**
     * Send an [LogType.INFO] log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @return A positive value if the message was loggable.
     */
    @JvmStatic
    fun i(tag: String, msg: String): Int {
        return log(LogType.INFO, tag, msg)
    }

    /**
     * Send a [LogType.INFO] log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log.
     */
    @JvmStatic
    fun i(tag: String, msg: String, tr: Throwable?): Int {
        return log(LogType.INFO, tag, msg, tr)
    }

    // ---- WARN ----

    /**
     * Send a [LogType.WARN] log message.
     * Note* For Tag It tries to get the class name where log statement is invoked.
     * @param msg The message you would like logged.
     * @return A positive value if the message was loggable.
     */
    @JvmStatic
    fun w(msg: String): Int {
        return log(LogType.WARN, getClassName(), msg)
    }

    /**
     * Send a [LogType.WARN] log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @return A positive value if the message was loggable.
     */
    @JvmStatic
    fun w(tag: String, msg: String): Int {
        return log(LogType.WARN, tag, msg)
    }

    /**
     * Send a [LogType.WARN] log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log.
     * @return A positive value if the message was loggable.
     */
    @JvmStatic
    fun w(tag: String, msg: String, tr: Throwable?): Int {
        return log(LogType.WARN, tag, msg, tr)
    }

    // ---- ERROR ----

    /**
     * Send a [LogType.ERROR] log message.
     * Note* For Tag It tries to get the class name where log statement is invoked.
     * @param msg The message you would like logged.
     * @return A positive value if the message was loggable.
     */
    @JvmStatic
    fun e(msg: String): Int {
        return log(LogType.ERROR, getClassName(), msg)
    }

    /**
     * Send an [LogType.ERROR] log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @return A positive value if the message was loggable.
     */
    @JvmStatic
    fun e(tag: String, msg: String): Int {
        return log(LogType.ERROR, tag, msg)
    }

    /**
     * Send a [LogType.ERROR] log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log.
     * @return A positive value if the message was loggable.
     */
    @JvmStatic
    fun e(tag: String, msg: String, tr: Throwable?): Int {
        return log(LogType.ERROR, tag, msg, tr)
    }

    private fun log(logType: LogType, tag: String, message: String, tr: Throwable? = null): Int {
        val msg = if (BuildConfig.DEBUG) "$message | ${getCallerLocation()}" else message
        val result = when (logType) {
            LogType.VERBOSE -> if (tr == null) Log.v(tag, msg) else Log.v(tag, message, tr)
            LogType.DEBUG -> if (tr == null) Log.d(tag, msg) else Log.d(tag, message, tr)
            LogType.INFO -> if (tr == null) Log.i(tag, msg) else Log.i(tag, message, tr)
            LogType.WARN -> if (tr == null) Log.w(tag, msg) else Log.w(tag, message, tr)
            LogType.ERROR -> if (tr == null) Log.e(tag, msg) else Log.e(tag, message, tr)
        }

        // If log type is error send this to crashlytics
        if (logType == LogType.ERROR) {
            println("Sending to crashlytics: $message")
        }
        return result
    }

    /**
     * This method retrieves the location from which it is called in terms of method name,
     * file name, and line number. It is useful for logging purposes to identify where a log
     * statement is triggered within the codebase.
     *
     * @return A string containing the method name, file name, and line number where the log
     *         was triggered. Returns an empty string if the location cannot be determined.
     */
    private fun getCallerLocation(): String {
        val exceptions = Thread.currentThread().stackTrace
        val exception = exceptions.getOrNull(6)
        return "[${exception?.methodName} - ${exception?.fileName}:${exception?.lineNumber}]"
    }

    /**
     * Tries to get the class name where log statement was invoked using stack trace.
     */
    private fun getClassName(): String {
        val exceptions = Thread.currentThread().stackTrace
        val exception = exceptions.getOrNull(6)
        return exception?.className?.split(".")?.last() ?: "Unknown ClassName"
    }

    private enum class LogType {
        VERBOSE, DEBUG, INFO, WARN, ERROR
    }
}