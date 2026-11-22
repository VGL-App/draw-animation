package com.vgl.draw.animation.core.lessons.content

import com.vgl.draw.animation.core.lessons.editItem
import com.vgl.draw.animation.core.lessons.editedSample
import com.vgl.draw.animation.core.lessons.resourceText
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** index.json is a contract with the content team (plan T-048, section 3.1): the sample file is the reference. */
class LessonIndexFormatTest {
    private fun sampleIndex(): LessonIndex {
        val result = LessonIndexFormat.decode(resourceText("index-v1.json"), "v1")
        assertTrue("$result", result is IndexDecodeResult.Ok)
        return (result as IndexDecodeResult.Ok).index
    }

    @Test
    fun decode_v1Sample_returnsIndex() {
        val index = sampleIndex()
        assertEquals(listOf(LessonGroup("basics", "Basics"), LessonGroup("motion", "Motion")), index.groups)
        assertEquals(
            listOf(
                LessonEntry(
                    id = "walk-in-place",
                    groupId = "motion",
                    title = "Walk in place",
                    minutes = 8,
                    frameCount = 16,
                    stepCount = 5,
                    thumb = "walk-in-place/thumb.webp",
                    pack = "walk-in-place/lesson.zip",
                ),
                LessonEntry(
                    id = "bouncing-ball",
                    groupId = "basics",
                    title = "Bouncing ball",
                    minutes = 5,
                    frameCount = 4,
                    stepCount = 3,
                    thumb = "bouncing-ball/thumb.webp",
                    pack = "bouncing-ball/lesson.zip",
                ),
                LessonEntry(
                    id = "squash-and-stretch",
                    groupId = "basics",
                    title = "Squash and stretch",
                    minutes = 6,
                    frameCount = 12,
                    stepCount = 6,
                    thumb = "squash-and-stretch/thumb.webp",
                    pack = "squash-and-stretch/lesson.zip",
                ),
            ),
            index.lessons,
        )
    }

    @Test
    fun sampleIndex_orderAndNumbers() {
        val index = sampleIndex()
        assertEquals(listOf("bouncing-ball", "squash-and-stretch", "walk-in-place"), index.ordered.map { it.id })
        assertEquals(1, index.numberInGroup("bouncing-ball"))
        assertEquals(2, index.numberInGroup("squash-and-stretch"))
        assertEquals(1, index.numberInGroup("walk-in-place"))
        assertEquals(1, index.libraryNumber("bouncing-ball"))
        assertEquals(2, index.libraryNumber("squash-and-stretch"))
        assertEquals(3, index.libraryNumber("walk-in-place"))
        assertEquals("squash-and-stretch", index.next("bouncing-ball")?.id)
        assertEquals("walk-in-place", index.next("squash-and-stretch")?.id)
        assertNull(index.next("walk-in-place"))
        assertEquals(listOf("bouncing-ball", "squash-and-stretch"), index.lessonsIn("basics").map { it.id })
        assertEquals(1, index.completedCount("basics", setOf("bouncing-ball", "walk-in-place")))
        assertEquals("bouncing-ball", index.entry("bouncing-ball")?.id)
        assertNull(index.next("no-such-lesson"))
        assertNull(index.entry("no-such-lesson"))
        assertNull(index.libraryNumber("no-such-lesson"))
        assertNull(index.numberInGroup("no-such-lesson"))
    }

    @Test
    fun decode_unknownField_isIgnored() {
        val text = editedSample("index-v1.json") { root ->
            root["extra"] = JsonPrimitive(1)
            root.editItem("lessons", 0) { it["extra"] = JsonPrimitive(1) }
        }
        assertTrue(LessonIndexFormat.decode(text, "v1") is IndexDecodeResult.Ok)
    }

    @Test
    fun decode_invalid_returnsInvalid() {
        // Control: the sample itself is valid.
        assertTrue(LessonIndexFormat.decode(resourceText("index-v1.json"), "v1") is IndexDecodeResult.Ok)

        fun lesson(index: Int, change: (MutableMap<String, JsonElement>) -> Unit) =
            editedSample("index-v1.json") { root -> root.editItem("lessons", index, change) }

        // Lesson 1 of the array is bouncing-ball, lesson 0 is walk-in-place (the only lesson of motion).
        val cases = linkedMapOf(
            "broken JSON" to "{",
            "empty text" to "",
            "no formatVersion" to editedSample("index-v1.json") { it.remove("formatVersion") },
            "no groups" to editedSample("index-v1.json") { it["groups"] = JsonArray(emptyList()) },
            "duplicate group id" to editedSample("index-v1.json") { root ->
                root.editItem("groups", 1) { it["id"] = JsonPrimitive("basics") }
            },
            "duplicate lesson id" to lesson(2) { it["id"] = JsonPrimitive("bouncing-ball") },
            "upper-case id" to lesson(1) { it["id"] = JsonPrimitive("Bouncing") },
            "double dash id" to lesson(1) { it["id"] = JsonPrimitive("a--b") },
            "path id" to lesson(1) { it["id"] = JsonPrimitive("../x") },
            "41-character id" to lesson(1) { it["id"] = JsonPrimitive("a".repeat(41)) },
            "blank title" to lesson(1) { it["title"] = JsonPrimitive(" ") },
            "unknown group" to lesson(1) { it["group"] = JsonPrimitive("effects") },
            "empty group" to lesson(0) { it["group"] = JsonPrimitive("basics") },
            "minutes 0" to lesson(1) { it["minutes"] = JsonPrimitive(0) },
            "minutes 121" to lesson(1) { it["minutes"] = JsonPrimitive(121) },
            "frameCount 0" to lesson(1) { it["frameCount"] = JsonPrimitive(0) },
            "stepCount 0" to lesson(1) { it["stepCount"] = JsonPrimitive(0) },
            "stepCount 51" to lesson(1) { it["stepCount"] = JsonPrimitive(51) },
            "no pack" to lesson(1) { it.remove("pack") },
            "pack outside" to lesson(1) { it["pack"] = JsonPrimitive("../x.zip") },
            "hidden thumb" to lesson(1) { it["thumb"] = JsonPrimitive(".hidden/thumb.webp") },
            "pack of another lesson" to lesson(1) { it["pack"] = JsonPrimitive("walk-in-place/lesson.zip") },
            "thumb png" to lesson(1) { it["thumb"] = JsonPrimitive("bouncing-ball/thumb.png") },
            "pack other name" to lesson(1) { it["pack"] = JsonPrimitive("bouncing-ball/pack.zip") },
        )
        for ((name, text) in cases) {
            val result = LessonIndexFormat.decode(text, "v1")
            assertTrue("$name: $result", result is IndexDecodeResult.Invalid)
        }

        val newer = LessonIndexFormat.decode(
            editedSample("index-v1.json") { it["formatVersion"] = JsonPrimitive(2) },
            "v1",
        )
        assertTrue("$newer", newer is IndexDecodeResult.Invalid)
        assertTrue("$newer", (newer as IndexDecodeResult.Invalid).reason.contains("newer"))
    }
}
