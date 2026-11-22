package com.vgl.draw.animation.core.project.model

import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class IdsTest {
    private val invalid = listOf(
        "../x",
        "",
        "1-1-1-1-1",
        UUID.randomUUID().toString().uppercase(),
    )

    private val constructors: List<Pair<String, (String) -> Any>> = listOf(
        "ProjectId" to { s -> ProjectId(s) },
        "FrameId" to { s -> FrameId(s) },
        "LayerId" to { s -> LayerId(s) },
        "SoundClipId" to { s -> SoundClipId(s) },
    )

    @Test
    fun acceptsCanonicalUuidOnly() {
        // Control: a canonical UUID is accepted by every id class and by parseOrNull.
        val canonical = UUID.randomUUID().toString()
        constructors.forEach { (_, make) -> make(canonical) }
        assertEquals(ProjectId(canonical), ProjectId.parseOrNull(canonical))
        assertTrue(ProjectId.random().value.let { UUID.fromString(it).toString() == it })

        constructors.forEach { (className, make) ->
            invalid.forEach { value ->
                try {
                    make(value)
                    fail("$className accepted \"$value\"")
                } catch (expected: IllegalArgumentException) {
                    // Rejected as required.
                }
            }
        }
        invalid.forEach { value -> assertNull("parseOrNull(\"$value\")", ProjectId.parseOrNull(value)) }
    }
}
