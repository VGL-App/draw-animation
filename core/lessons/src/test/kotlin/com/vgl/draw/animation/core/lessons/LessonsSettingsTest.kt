package com.vgl.draw.animation.core.lessons

import com.vgl.draw.animation.core.config.RemoteKey
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** The key and its default are decisions of spec lessons, section 8 and question 5 (plan T-048). */
class LessonsSettingsTest {
    @Test
    fun contentVersion_isStringV1() {
        val key = LessonsSettings.CONTENT_VERSION
        assertEquals("lessons_content_version", key.name)
        assertEquals(RemoteKey.Type.STRING, key.type)
        assertEquals("v1", key.default)
        assertTrue(key.description.isNotBlank())
        assertEquals(listOf(LessonsSettings.CONTENT_VERSION), LessonsSettings.all())
    }

    @Test
    fun contentVersionOrDefault_rejectsInvalid() {
        for (valid in listOf("v1", "v2", "v120")) {
            assertEquals(valid, contentVersionOrDefault(valid))
        }
        for (invalid in listOf("", "V2", "v2/", "v0", "2", "v1/../x", "v1.2")) {
            assertEquals("\"$invalid\"", "v1", contentVersionOrDefault(invalid))
        }
    }
}
