package com.randos.logger

import android.util.Log

/**
 * Enumeration representing different levels of logging.
 *
 * This enum defines various log levels that indicate the severity of logged messages.
 * Each log level corresponds to a specific type of log message, ranging from the most
 * detailed (VERBOSE) to the most critical (WTF - What a Terrible Failure).
 *
 * @property VERBOSE Verbose level indicating detailed logging.
 * @property DEBUG Debug level for logging messages useful during development.
 * @property INFO Informational level for general logging purposes.
 * @property WARNING Warning level indicating potential issues that require attention.
 * @property ERROR Error level indicating critical failures that need immediate attention.
 * @property WTF Critical level indicating unexpected and severe failures.
 */
private enum class LogLevel {
    VERBOSE,
    DEBUG,
    INFO,
    WARNING,
    ERROR,
    WTF // What a Terrible Failure ;)
}

/**
 * Utility class for logging messages at different levels of severity.
 *
 * This class provides methods to log messages at various levels, from verbose debugging information
 * to critical error and failure messages. It uses the Android `Log` class for logging operations
 * and supports sending critical logs to analytics for monitoring and analysis.
 *
 * Example usage:
 * ```
 * class MyClass {
 *     fun someMethod() {
 *         Logger.d(this@MyClass, "Debug message", null)
 *         Logger.e(this@MyClass, "Error message", exception)
 *     }
 * }
 * ```
 */
class Logger {

    companion object {

        private val criticalLogLevels = setOf(LogLevel.WARNING, LogLevel.ERROR, LogLevel.WTF)

        /**
         * Logs a verbose message along with the caller's file name and line number.
         *
         * This method should be used to log verbose messages that provide detailed information about
         * the application's behavior. These logs are typically used for debugging and are not usually
         * necessary in a production environment. The `caller` parameter should be the reference to
         * the enclosing class, typically passed using `this@EnclosingClass` to ensure the log correctly
         * identifies the location where the log was triggered.
         *
         * Example usage:
         * ```
         * class MyClass {
         *     fun someMethod() {
         *         Logger.v(this@MyClass, "This is a verbose log message")
         *     }
         * }
         * ```
         *
         * @param caller The reference to the enclosing class (use `this@EnclosingClass`).
         * @param msg The message to log.
         */
        fun v(caller: Any, msg: String) {
            log(caller, msg, null, LogLevel.VERBOSE)
        }

        /**
         * Logs a verbose message along with the caller's file name and line number.
         *
         * This method should be used to log verbose messages that provide detailed information about
         * the application's behavior. These logs are typically used for debugging and are not usually
         * necessary in a production environment. The `caller` parameter should be the reference to
         * the enclosing class, typically passed using `this@EnclosingClass` to ensure the log correctly
         * identifies the location where the log was triggered.
         *
         * Example usage:
         * ```
         * class MyClass {
         *     fun someMethod() {
         *         Logger.v(this@MyClass, "This is a verbose log message", null)
         *     }
         * }
         * ```
         *
         * @param caller The reference to the enclosing class (use `this@EnclosingClass`).
         * @param msg The message to log.
         * @param tr A throwable to log.
         */
        fun v(caller: Any, msg: String, tr: Throwable) {
            log(caller, msg, tr, LogLevel.VERBOSE)
        }

        /**
         * Logs a debug message along with the caller's file name and line number.
         *
         * This method should be used to log debug messages that are useful for development and debugging
         * purposes. These logs can provide insights into the application's state and behavior during
         * development. The `caller` parameter should be the reference to the enclosing class, typically
         * passed using `this@EnclosingClass` to ensure the log correctly identifies the location where the
         * log was triggered.
         *
         * Example usage:
         * ```
         * class MyClass {
         *     fun someMethod() {
         *         try {
         *             // Some code that might throw an exception
         *         } catch (e: Exception) {
         *             Logger.d(this@MyClass, "A debug message")
         *         }
         *     }
         * }
         * ```
         *
         * @param caller The reference to the enclosing class (use `this@EnclosingClass`).
         * @param msg The message to log.
         */
        fun d(caller: Any, msg: String) {
            log(caller, msg, null, LogLevel.DEBUG)
        }

        /**
         * Logs a debug message along with the caller's file name and line number.
         *
         * This method should be used to log debug messages that are useful for development and debugging
         * purposes. These logs can provide insights into the application's state and behavior during
         * development. The `caller` parameter should be the reference to the enclosing class, typically
         * passed using `this@EnclosingClass` to ensure the log correctly identifies the location where the
         * log was triggered.
         *
         * Example usage:
         * ```
         * class MyClass {
         *     fun someMethod() {
         *         try {
         *             // Some code that might throw an exception
         *         } catch (e: Exception) {
         *             Logger.d(this@MyClass, "A debug message", e)
         *         }
         *     }
         * }
         * ```
         *
         * @param caller The reference to the enclosing class (use `this@EnclosingClass`).
         * @param msg The message to log.
         * @param tr A throwable to log.
         */
        fun d(caller: Any, msg: String, tr: Throwable) {
            log(caller, msg, tr, LogLevel.DEBUG)
        }

        /**
         * Logs an info message along with the caller's file name and line number.
         *
         * This method should be used to log informational messages that are useful for tracking the flow
         * of the application. The `caller` parameter should be the reference to the enclosing class,
         * typically passed using `this@EnclosingClass` to ensure the log correctly identifies the location
         * where the log was triggered.
         *
         * Example usage:
         * ```
         * class MyClass {
         *     fun updateName() {
         *         val response = // some network request
         *         if(response.isSuccessful()) {
         *              Logger.i(this@MyClass, "User name updated successfully.")
         *         }
         *     }
         * }
         * ```
         *
         * @param caller The reference to the enclosing class (use `this@EnclosingClass`).
         * @param msg The message to log.
         */
        fun i(caller: Any, msg: String) {
            log(caller, msg, null, LogLevel.INFO)
        }


        /**
         * Logs an info message along with the caller's file name and line number.
         *
         * This method should be used to log informational messages that are useful for tracking the flow
         * of the application. The `caller` parameter should be the reference to the enclosing class,
         * typically passed using `this@EnclosingClass` to ensure the log correctly identifies the location
         * where the log was triggered.
         *
         * Example usage:
         * ```
         * class MyClass {
         *     fun updateName() {
         *         val response = // some network request
         *         if(response.isSuccessful()) {
         *              Logger.i(this@MyClass, "User name updated successfully.")
         *         }
         *     }
         * }
         * ```
         *
         * @param caller The reference to the enclosing class (use `this@EnclosingClass`).
         * @param msg The message to log.
         * @param tr A throwable to log.
         */
        fun i(caller: Any, msg: String, tr: Throwable) {
            log(caller, msg, tr, LogLevel.INFO)
        }

        /**
         * Logs a warning message along with the caller's file name and line number.
         *
         * This method should be used to log warning messages that indicate a potential issue in the
         * application. Warnings are used to highlight situations that are not necessarily errors but may
         * require attention. The `caller` parameter should be the reference to the enclosing class,
         * typically passed using `this@EnclosingClass` to ensure the log correctly identifies the location
         * where the log was triggered.
         *
         * Example usage:
         * ```
         * class MyClass {
         *     fun someMethod() {
         *         try {
         *             // Some code that might throw an exception
         *         } catch (e: Exception) {
         *             Logger.w(this@MyClass, "A warning message")
         *         }
         *     }
         * }
         * ```
         *
         * @param caller The reference to the enclosing class (use `this@EnclosingClass`).
         * @param msg The message to log.
         */
        fun w(caller: Any, msg: String) {
            log(caller, msg, null, LogLevel.WARNING)
        }

        /**
         * Logs a warning message along with the caller's file name and line number.
         *
         * This method should be used to log warning messages that indicate a potential issue in the
         * application. Warnings are used to highlight situations that are not necessarily errors but may
         * require attention. The `caller` parameter should be the reference to the enclosing class,
         * typically passed using `this@EnclosingClass` to ensure the log correctly identifies the location
         * where the log was triggered.
         *
         * Example usage:
         * ```
         * class MyClass {
         *     fun someMethod() {
         *         try {
         *             // Some code that might throw an exception
         *         } catch (e: Exception) {
         *             Logger.w(this@MyClass, "A warning message", e)
         *         }
         *     }
         * }
         * ```
         *
         * @param caller The reference to the enclosing class (use `this@EnclosingClass`).
         * @param msg The message to log.
         * @param tr A throwable to log.
         */
        fun w(caller: Any, msg: String, tr: Throwable) {
            log(caller, msg, tr, LogLevel.WARNING)
        }

        /**
         * Logs an error message along with the caller's file name and line number.
         *
         * This method should be used to log error messages that indicate a critical issue or unexpected
         * behavior in the application. Errors typically require immediate attention and might indicate
         * failures that need to be addressed. The `caller` parameter should be the reference to the
         * enclosing class, typically passed using `this@EnclosingClass` to ensure the log correctly
         * identifies the location where the log was triggered.
         *
         * Example usage:
         * ```
         * class MyClass {
         *     fun someMethod() {
         *         try {
         *             // Some code that might throw an exception
         *         } catch (e: Exception) {
         *             Logger.e(this@MyClass, "An error occurred")
         *         }
         *     }
         * }
         * ```
         *
         * @param caller The reference to the enclosing class (use `this@EnclosingClass`).
         * @param msg The message to log.
         */
        fun e(caller: Any, msg: String) {
            log(caller, msg, null, LogLevel.ERROR)
        }

        /**
         * Logs an error message along with the caller's file name and line number.
         *
         * This method should be used to log error messages that indicate a critical issue or unexpected
         * behavior in the application. Errors typically require immediate attention and might indicate
         * failures that need to be addressed. The `caller` parameter should be the reference to the
         * enclosing class, typically passed using `this@EnclosingClass` to ensure the log correctly
         * identifies the location where the log was triggered.
         *
         * Example usage:
         * ```
         * class MyClass {
         *     fun someMethod() {
         *         try {
         *             // Some code that might throw an exception
         *         } catch (e: Exception) {
         *             Logger.e(this@MyClass, "An error occurred", e)
         *         }
         *     }
         * }
         * ```
         *
         * @param caller The reference to the enclosing class (use `this@EnclosingClass`).
         * @param msg The message to log.
         * @param tr The throwable that caused the error.
         */
        fun e(caller: Any, msg: String, tr: Throwable) {
            log(caller, msg, tr, LogLevel.ERROR)
        }

        /**
         * Logs a message at an unexpected critical level ("What a Terrible Failure") along with
         * the caller's file name and line number.
         *
         * This method should be used to log messages that indicate a critical and unexpected failure
         * in the application, where immediate attention is required. The `caller` parameter should
         * be the reference to the enclosing class, typically passed using `this@EnclosingClass`
         * to ensure the log correctly identifies the location where the log was triggered.
         *
         * Example usage:
         * ```
         * class MyClass {
         *     fun someMethod() {
         *         try {
         *             // Some critical operation that might fail
         *         } catch (e: Exception) {
         *             Logger.wtf(this@MyClass, "Critical failure occurred")
         *         }
         *     }
         * }
         * ```
         *
         * @param caller The reference to the enclosing class (use `this@EnclosingClass`).
         * @param msg The message to log.
         */
        fun wtf(caller: Any, msg: String) {
            log(caller, msg, null, LogLevel.WTF)
        }

        /**
         * Logs a message at an unexpected critical level ("What a Terrible Failure") along with
         * the caller's file name and line number.
         *
         * This method should be used to log messages that indicate a critical and unexpected failure
         * in the application, where immediate attention is required. The `caller` parameter should
         * be the reference to the enclosing class, typically passed using `this@EnclosingClass`
         * to ensure the log correctly identifies the location where the log was triggered.
         *
         * Example usage:
         * ```
         * class MyClass {
         *     fun someMethod() {
         *         try {
         *             // Some critical operation that might fail
         *         } catch (e: Exception) {
         *             Logger.wtf(this@MyClass, "Critical failure occurred", e)
         *         }
         *     }
         * }
         * ```
         *
         * @param caller The reference to the enclosing class (use `this@EnclosingClass`).
         * @param msg The message to log.
         * @param tr The throwable that caused the critical failure.
         */
        fun wtf(caller: Any, msg: String, tr: Throwable) {
            log(caller, msg, tr, LogLevel.WTF)
        }

        /**
         * This method logs messages along with optional throwable information at the specified
         * log level. It uses the Android `Log` class to output logs based on the provided `logLevel`.
         *
         * @param caller The reference to the enclosing class.
         * @param msg The message to log.
         * @param tr An optional throwable to log. Pass `null` if no throwable is present.
         * @param logLevel The log level indicating the severity of the log message.
         */
        private fun log(caller: Any, msg: String, tr: Throwable?, logLevel: LogLevel) {
            val tag = caller::class.simpleName
            val message = "$msg ${getLoggingLocation(caller)}"
            when (logLevel) {
                LogLevel.VERBOSE -> {
                    Log.v(tag, message, tr)
                }

                LogLevel.DEBUG -> {
                    Log.d(tag, message, tr)
                }

                LogLevel.INFO -> {
                    Log.i(tag, message, tr)
                }

                LogLevel.WARNING -> {
                    Log.w(tag, message, tr)
                }

                LogLevel.ERROR -> {
                    Log.e(tag, message, tr)
                }

                LogLevel.WTF -> {
                    Log.wtf(tag, message, tr)
                }
            }

            if (logLevel in criticalLogLevels) {
                // Send these logs to analytics
                println("Sending logs to analytics")
            }
        }

        /**
         * This method retrieves the location from which it is called in terms of method name,
         * file name, and line number. It is useful for logging purposes to identify where a log
         * statement is triggered within the codebase.
         *
         * @param caller The reference to the enclosing class.
         * @return A string containing the method name, file name, and line number where the log
         *         was triggered, formatted as "methodName(fileName:lineNumber)". Returns an empty
         *         string if the location cannot be determined.
         */
        private fun getLoggingLocation(caller: Any): String {
            val exceptions = Thread.currentThread().stackTrace
            for (exception in exceptions) {
                if (exception.className == caller::class.java.name) {
                    return "${exception.methodName}(${exception.fileName}:${exception.lineNumber})"
                }
            }
            return ""
        }
    }
}