package com.vgl.draw.animation.core.project.storage

import com.vgl.draw.animation.core.project.TestProjects
import com.vgl.draw.animation.core.project.model.CameraKey
import com.vgl.draw.animation.core.project.model.FrameRate
import com.vgl.draw.animation.core.project.model.Layer
import com.vgl.draw.animation.core.project.model.LayerId
import java.util.UUID
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class ProjectFormatTest {
    private val sample = TestProjects.v1SampleText()

    @Test
    fun decode_v1Sample_returnsFullProject() {
        assertEquals(ProjectReadResult.Ok(TestProjects.full()), ProjectFormat.decode(sample))
    }

    @Test
    fun encode_fullProject_matchesV1Sample() {
        assertEquals(
            Json.parseToJsonElement(sample),
            Json.parseToJsonElement(ProjectFormat.encode(TestProjects.full())),
        )
    }

    @Test
    fun encodeThenDecode_fullProject_returnsEqualProject() {
        val full = TestProjects.full()
        assertEquals(ProjectReadResult.Ok(full), ProjectFormat.decode(ProjectFormat.encode(full)))
    }

    @Test
    fun encode_writesCurrentFormatVersion() {
        val root = Json.parseToJsonElement(ProjectFormat.encode(TestProjects.full())).jsonObject
        val version = root.getValue("formatVersion").jsonPrimitive
        assertFalse(version.isString)
        assertEquals(1, version.int)
        assertEquals(ProjectFormat.CURRENT_FORMAT_VERSION, version.int)
    }

    @Test
    fun encode_frameRateAsNumber() {
        val project = TestProjects.full().copy(frameRate = FrameRate.FPS_12)
        val root = Json.parseToJsonElement(ProjectFormat.encode(project)).jsonObject
        val frameRate = root.getValue("project").jsonObject.getValue("frameRate").jsonPrimitive
        assertFalse(frameRate.isString)
        assertEquals(12, frameRate.int)
    }

    @Test
    fun encode_fullProject_isSmall() {
        val size = ProjectFormat.encode(TestProjects.full()).toByteArray().size
        assertTrue("size $size", size < 16_384)
    }

    @Test
    fun decode_newerFormatVersion_returnsNewerVersion() {
        val text = editSample(version = JsonPrimitive(2)) { project -> project["somethingNew"] = JsonPrimitive(1) }
        assertEquals(ProjectReadResult.NewerVersion(2, "My animation 3"), ProjectFormat.decode(text))
    }

    @Test
    fun decode_invalidJson_returnsCorruptWithoutName() {
        listOf("{not json", "", sample.substring(0, sample.length / 2)).forEach { text ->
            assertEquals(
                "decode(\"${text.take(20)}...\")",
                ProjectReadResult.Corrupt(null, CorruptReason.INVALID_JSON),
                ProjectFormat.decode(text),
            )
        }
    }

    @Test
    fun decode_missingVersion_keepsName() {
        val text = editSample(version = null) {}
        assertEquals(
            ProjectReadResult.Corrupt("My animation 3", CorruptReason.MISSING_VERSION),
            ProjectFormat.decode(text),
        )
    }

    @Test
    fun decode_versionNotNumber_keepsName() {
        val text = editSample(version = JsonPrimitive("one")) {}
        assertEquals(
            ProjectReadResult.Corrupt("My animation 3", CorruptReason.MISSING_VERSION),
            ProjectFormat.decode(text),
        )
    }

    @Test
    fun decode_unknownKey_returnsCorrupt() {
        val text = editSample { project -> project["extra"] = JsonPrimitive(1) }
        val result = ProjectFormat.decode(text)
        assertTrue("$result", result is ProjectReadResult.Corrupt)
        assertEquals(CorruptReason.INVALID_CONTENT, (result as ProjectReadResult.Corrupt).reason)
    }

    @Test
    fun decode_invalidContentWithName_keepsName() {
        val text = editSample { project -> project["frames"] = JsonArray(emptyList()) }
        assertEquals(
            ProjectReadResult.Corrupt("My animation 3", CorruptReason.INVALID_CONTENT),
            ProjectFormat.decode(text),
        )
    }

    @Test
    fun decode_ruleViolation_returnsCorrupt() {
        // Control: the unchanged sample is valid.
        assertTrue(ProjectFormat.decode(sample) is ProjectReadResult.Ok)

        val cases: Map<String, (MutableMap<String, JsonElement>) -> Unit> = mapOf(
            "no frame" to { p -> p["frames"] = JsonArray(emptyList()) },
            "no layer" to { p -> p["layers"] = JsonArray(emptyList()) },
            "11 layers" to { p ->
                p["layers"] = JsonArray(
                    (1..11).map { i ->
                        JsonObject(
                            mapOf(
                                "id" to JsonPrimitive(UUID.randomUUID().toString()),
                                "name" to JsonPrimitive("Layer $i"),
                                "visible" to JsonPrimitive(true),
                            ),
                        )
                    },
                )
            },
            "duplicate frame id" to { p ->
                p.editArrayItem("frames", 3) { it["id"] = p.arrayItem("frames", 0).getValue("id") }
            },
            "duplicate layer id" to { p ->
                p.editArrayItem("layers", 1) { it["id"] = p.arrayItem("layers", 0).getValue("id") }
            },
            "duplicate sound id" to { p ->
                p.editArrayItem("sounds", 1) { it["id"] = p.arrayItem("sounds", 0).getValue("id") }
            },
            "hold 0" to { p -> p.editArrayItem("frames", 0) { it["hold"] = JsonPrimitive(0) } },
            "hold 5" to { p -> p.editArrayItem("frames", 0) { it["hold"] = JsonPrimitive(5) } },
            "zoom 0" to { p ->
                p.editArrayItem("frames", 2) { frame ->
                    val key = frame.getValue("cameraKey").jsonObject.toMutableMap()
                    key["zoom"] = JsonPrimitive(0)
                    frame["cameraKey"] = JsonObject(key)
                }
            },
            "fps 10" to { p -> p["frameRate"] = JsonPrimitive(10) },
            "aspect ratio 4:5" to { p -> p["aspectRatio"] = JsonPrimitive("4:5") },
            "gradient background" to { p ->
                p["background"] = JsonObject(
                    mapOf("type" to JsonPrimitive("gradient"), "fileExtension" to JsonPrimitive("jpg")),
                )
            },
            "onion opacity 101" to { p -> p.editObject("onionSkin") { it["opacityPercent"] = JsonPrimitive(101) } },
            "framesBefore -1" to { p -> p.editObject("onionSkin") { it["framesBefore"] = JsonPrimitive(-1) } },
            "durationMillis -1" to { p -> p.editArrayItem("sounds", 0) { it["durationMillis"] = JsonPrimitive(-1) } },
            "startFrameId not a frame" to { p ->
                p.editArrayItem("sounds", 1) { it["startFrameId"] = JsonPrimitive(UUID.randomUUID().toString()) }
            },
            "lesson step -1" to { p -> p.editObject("kind") { it["step"] = JsonPrimitive(-1) } },
            "reference frameCount 0" to { p -> p.editObject("reference") { it["frameCount"] = JsonPrimitive(0) } },
            "fileExtension ../x" to { p -> p.editObject("background") { it["fileExtension"] = JsonPrimitive("../x") } },
            "frame id ../x" to { p -> p.editArrayItem("frames", 3) { it["id"] = JsonPrimitive("../x") } },
            "uppercase layer id" to { p ->
                p.editArrayItem("layers", 0) {
                    it["id"] = JsonPrimitive("6F1C2A3E-0000-4000-8000-0000000000A1")
                }
            },
            "project id 1-1-1-1-1" to { p -> p["id"] = JsonPrimitive("1-1-1-1-1") },
        )

        cases.forEach { (label, edit) ->
            val result = ProjectFormat.decode(editSample(edit = edit))
            assertTrue("$label: $result", result is ProjectReadResult.Corrupt)
            assertEquals(label, CorruptReason.INVALID_CONTENT, (result as ProjectReadResult.Corrupt).reason)
        }
    }

    @Test
    fun decode_olderVersionWithoutMigration_returnsCorrupt() {
        val result = ProjectFormat.decode(editSample(version = JsonPrimitive(0)) {})
        assertTrue("$result", result is ProjectReadResult.Corrupt)
        assertEquals(CorruptReason.UNKNOWN_VERSION, (result as ProjectReadResult.Corrupt).reason)
    }

    @Test
    fun encode_invalidProject_throws() {
        val full = TestProjects.full()
        val elevenLayers = full.copy(
            layers = (1..11).map { i -> Layer(LayerId.random(), "Layer $i", visible = true) },
        )
        val nanCamera = full.copy(
            frames = full.frames.mapIndexed { index, frame ->
                if (index == 0) frame.copy(cameraKey = CameraKey(Float.NaN, 0f, 1f, 0f)) else frame
            },
        )
        // Control: the unchanged project encodes.
        ProjectFormat.encode(full)
        listOf("11 layers" to elevenLayers, "NaN camera" to nanCamera).forEach { (label, project) ->
            try {
                ProjectFormat.encode(project)
                fail("$label was encoded")
            } catch (expected: IllegalArgumentException) {
                // Rejected as required.
            }
        }
    }

    /**
     * The sample with `formatVersion` set to [version] (removed when null) and the `project` object changed by [edit].
     */
    private fun editSample(
        version: JsonElement? = JsonPrimitive(1),
        edit: (MutableMap<String, JsonElement>) -> Unit,
    ): String {
        val root = Json.parseToJsonElement(sample).jsonObject.toMutableMap()
        if (version == null) root.remove("formatVersion") else root["formatVersion"] = version
        val project = root.getValue("project").jsonObject.toMutableMap()
        edit(project)
        root["project"] = JsonObject(project)
        return JsonObject(root).toString()
    }

    private fun MutableMap<String, JsonElement>.arrayItem(key: String, index: Int): JsonObject =
        getValue(key).jsonArray[index].jsonObject

    private fun MutableMap<String, JsonElement>.editArrayItem(
        key: String,
        index: Int,
        edit: (MutableMap<String, JsonElement>) -> Unit,
    ) {
        val items = getValue(key).jsonArray.toMutableList()
        val item = items[index].jsonObject.toMutableMap()
        edit(item)
        items[index] = JsonObject(item)
        this[key] = JsonArray(items)
    }

    private fun MutableMap<String, JsonElement>.editObject(
        key: String,
        edit: (MutableMap<String, JsonElement>) -> Unit,
    ) {
        val item = getValue(key).jsonObject.toMutableMap()
        edit(item)
        this[key] = JsonObject(item)
    }
}
