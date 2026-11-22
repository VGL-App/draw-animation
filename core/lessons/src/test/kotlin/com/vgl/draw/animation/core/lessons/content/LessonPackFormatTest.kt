package com.vgl.draw.animation.core.lessons.content

import com.vgl.draw.animation.core.lessons.editItem
import com.vgl.draw.animation.core.lessons.editedSample
import com.vgl.draw.animation.core.lessons.resourceText
import com.vgl.draw.animation.core.lessons.samplePackEntries
import java.io.File
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/** lesson.json and the pack layout are a contract with the content team and the editor (plan T-048, section 3.1). */
class LessonPackFormatTest {
    @get:Rule
    val tempFolder = TemporaryFolder()

    /** A folder holding the files of the sample pack, as extracted. */
    private fun samplePackDir(): File {
        val dir = tempFolder.newFolder()
        for ((name, bytes) in samplePackEntries()) {
            val file = File(dir, name)
            file.parentFile?.mkdirs()
            file.writeBytes(bytes)
        }
        return dir
    }

    private fun decode(text: String, dir: File = samplePackDir()) =
        LessonPackFormat.decode(text, "bouncing-ball", "v1", dir)

    @Test
    fun decode_v1Sample_returnsPack() {
        val dir = samplePackDir()
        val result = decode(resourceText("lesson-v1.json"), dir)
        assertTrue("$result", result is PackDecodeResult.Ok)
        val pack = (result as PackDecodeResult.Ok).pack
        assertEquals("bouncing-ball", pack.lessonId)
        assertEquals("v1", pack.contentVersion)
        assertEquals("9:16", pack.aspectRatio)
        assertEquals("720p", pack.resolution)
        assertEquals(12, pack.frameRate)
        assertEquals("white", pack.background)
        assertEquals(listOf(LessonLayer("Ball"), LessonLayer("Shadow")), pack.layers)
        assertEquals(listOf(1, 1, 2, 1), pack.frames.map { it.hold })
        assertEquals(
            listOf(
                LessonStep("Draw the ball", "Trace the red circle on frame 1.", 0, 0, true, 2),
                LessonStep("Squash it", "On frame 3, draw the ball flat where it hits the ground.", 2, 0, true, 0),
                LessonStep("Play it", "Tap play to watch your ball bounce.", 0, 0, false, 0),
            ),
            pack.steps,
        )
        assertEquals(dir, pack.dir)
        assertEquals(File(dir, "guides/0.png"), pack.guideFile(0))
        assertEquals(File(dir, "guides/1.png"), pack.guideFile(1))
        assertTrue(pack.guideFile(0)!!.isFile)
        assertNull(pack.guideFile(2))
        assertEquals(listOf(File(dir, "demo/0/0.png"), File(dir, "demo/0/1.png")), pack.demoFiles(0))
        assertEquals(emptyList<File>(), pack.demoFiles(1))
        assertEquals(File(dir, "cells/1/0.png"), pack.cellFile(1, 0))
        assertNull(pack.cellFile(0, 0))
    }

    @Test
    fun decode_invalid_returnsInvalid() {
        // Controls: the sample is valid, and a prebuilt cell is optional.
        assertTrue(decode(resourceText("lesson-v1.json")) is PackDecodeResult.Ok)
        val noCell = samplePackDir().also { File(it, "cells/1/0.png").delete() }
        assertTrue(decode(resourceText("lesson-v1.json"), noCell) is PackDecodeResult.Ok)

        fun root(change: (MutableMap<String, JsonElement>) -> Unit) = editedSample("lesson-v1.json", change)
        fun step(index: Int, change: (MutableMap<String, JsonElement>) -> Unit) =
            root { it.editItem("steps", index, change) }
        fun layers(count: Int) = JsonArray(List(count) { JsonObject(mapOf("name" to JsonPrimitive("L$it"))) })

        val textCases = linkedMapOf(
            "broken JSON" to "{",
            "formatVersion 2" to root { it["formatVersion"] = JsonPrimitive(2) },
            "other id" to root { it["id"] = JsonPrimitive("other") },
            "aspect ratio 4:5" to root { it["aspectRatio"] = JsonPrimitive("4:5") },
            "resolution 4k" to root { it["resolution"] = JsonPrimitive("4k") },
            "frame rate 10" to root { it["frameRate"] = JsonPrimitive(10) },
            "background red" to root { it["background"] = JsonPrimitive("red") },
            "0 layers" to root { it["layers"] = layers(0) },
            "6 layers" to root { it["layers"] = layers(6) },
            "blank layer name" to root { r -> r.editItem("layers", 0) { it["name"] = JsonPrimitive(" ") } },
            "0 frames" to root { it["frames"] = JsonArray(emptyList()) },
            "hold 0" to root { r -> r.editItem("frames", 0) { it["hold"] = JsonPrimitive(0) } },
            "hold 5" to root { r -> r.editItem("frames", 0) { it["hold"] = JsonPrimitive(5) } },
            "0 steps" to root { it["steps"] = JsonArray(emptyList()) },
            "frame 4" to step(2) { it["frame"] = JsonPrimitive(4) },
            "layer 2" to step(2) { it["layer"] = JsonPrimitive(2) },
            "demoFrameCount -1" to step(2) { it["demoFrameCount"] = JsonPrimitive(-1) },
            "demoFrameCount 121" to step(2) { it["demoFrameCount"] = JsonPrimitive(121) },
            "blank step title" to step(2) { it["title"] = JsonPrimitive("") },
        )
        for ((name, text) in textCases) {
            val result = decode(text)
            assertTrue("$name: $result", result is PackDecodeResult.Invalid)
        }

        for (missing in listOf("guides/1.png", "demo/0/1.png")) {
            val dir = samplePackDir().also { File(it, missing).delete() }
            val result = decode(resourceText("lesson-v1.json"), dir)
            assertTrue("missing $missing: $result", result is PackDecodeResult.Invalid)
        }
    }
}
