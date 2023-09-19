package com.twiceyuan.wxapk

import android.os.PatternMatcher
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
//    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.twiceyuan.wxapk", appContext.packageName)
    }

    @Test
    fun testPatternMatcher(){
        // 如下三个点匹配的东西不同:
        // 第一个`.`, 和`*`一起, 匹配任意数量的任意字符
        // 第二个`.`, 匹配`.`
        // 第三个`.`, 匹配任意字符
        val pattern = PatternMatcher(".*.apk.1", PatternMatcher.PATTERN_SIMPLE_GLOB)
        assertTrue(pattern.match("fuck.apk.1"))
        assertTrue(pattern.match("fuck.apk_1"))
        assertFalse(pattern.match("fuck_apk_1"))
        assertTrue(pattern.match("/you/fuck.apk.1"))
    }
}