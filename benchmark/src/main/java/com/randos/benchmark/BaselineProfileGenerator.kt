package com.randos.benchmark

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RequiresApi(Build.VERSION_CODES.P)
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {

    /*
    Command for generating baseline profile:
    ./gradlew :benchmark:pixel2Api31BenchmarkAndroidTest -P android.testInstrumentationRunnerArguments.androidx.benchmark.enabledRules=BaselineProfile
     */
    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() {
        rule.collect(
            packageName = "com.randos.musicvibe",
            includeInStartupProfile = true
        ) {
            startActivityAndWait()
        }
    }
}