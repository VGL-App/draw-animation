package com.vgl.draw.animation.core.lessons.progress

import org.junit.Assert.assertEquals
import org.junit.Test

/** Progress bar (spec lessons, question 7) and the content version a lesson opens with (KB-4). */
class LessonProgressTest {
    private fun attempt(step: Int, stepCount: Int = 6, version: String = "v1") =
        LessonAttempt(version, step, stepCount, "6f1c2a3e-0000-4000-8000-000000000001", 0)

    @Test
    fun fraction_followsStepAndCount() {
        assertEquals(0.5f, attempt(step = 2).fraction, 0f)
        assertEquals(1f / 6, attempt(step = 0).fraction, 0f)
        assertEquals(1f, attempt(step = 5).fraction, 0f)
    }

    @Test
    fun packVersionFor_inProgressUsesAttemptVersion() {
        val inProgress = LessonProgress(inProgress = mapOf("bouncing-ball" to attempt(step = 1, version = "v1")))
        assertEquals("v1", packVersionFor("bouncing-ball", inProgress, "v2"))
        assertEquals("v2", packVersionFor("bouncing-ball", LessonProgress(), "v2"))
        val completed = LessonProgress(
            completed = mapOf("bouncing-ball" to CompletedLesson("6f1c2a3e-0000-4000-8000-000000000002")),
        )
        assertEquals("v2", packVersionFor("bouncing-ball", completed, "v2"))
    }
}
