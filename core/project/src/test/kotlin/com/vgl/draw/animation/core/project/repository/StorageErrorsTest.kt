package com.vgl.draw.animation.core.project.repository

import java.io.IOException
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Telling a full disk from other write errors by the message, as Android and the JVM report it (plan T-002). */
class StorageErrorsTest {
    @Test
    fun androidEnospcMessage_isOutOfSpace() {
        assertTrue(isOutOfSpace(IOException("write failed: ENOSPC (No space left on device)")))
    }

    @Test
    fun jvmMessage_isOutOfSpace() {
        assertTrue(isOutOfSpace(IOException("No space left on device")))
    }

    @Test
    fun messageInCause_isOutOfSpace() {
        assertTrue(isOutOfSpace(IOException("x", IOException("No space left on device"))))
    }

    @Test
    fun otherMessage_isNotOutOfSpace() {
        assertFalse(isOutOfSpace(IOException("Could not replace a project file")))
    }

    @Test
    fun noMessage_isNotOutOfSpace() {
        assertFalse(isOutOfSpace(IOException(null as String?)))
    }

    @Test
    fun causeLoop_returnsFalseWithoutHanging() {
        assertFalse(isOutOfSpace(SelfCausedException()))
    }

    private class SelfCausedException : IOException("loop") {
        override val cause: Throwable get() = this
    }
}
