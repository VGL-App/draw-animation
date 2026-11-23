package com.vgl.draw.animation.core.project.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

/** Project names (plan T-002): trimmed, at most 40 code points, copies named "<name> copy", "<name> copy 2". */
class ProjectNamesTest {
    @Test
    fun normalize_trims() {
        assertEquals("Cat", ProjectNames.normalize(" Cat "))
    }

    @Test
    fun normalize_fortyChars_kept() {
        val name = "a".repeat(40)
        assertEquals(name, ProjectNames.normalize(name))
    }

    @Test
    fun normalize_fortyOneChars_cutToForty() {
        assertEquals("a".repeat(40), ProjectNames.normalize("a".repeat(41)))
    }

    @Test
    fun normalize_countsCodePoints_neverSplitsSurrogatePair() {
        // "😀" is 2 chars but 1 code point: 39 + 1 = 40 code points are kept, "b" is cut.
        assertEquals("a".repeat(39) + "😀", ProjectNames.normalize("a".repeat(39) + "😀b"))
    }

    @Test
    fun normalize_trailingSpaceAfterCut_removed() {
        assertEquals("a".repeat(39), ProjectNames.normalize("a".repeat(39) + " b"))
    }

    @Test
    fun normalize_blank_throws() {
        assertThrows(IllegalArgumentException::class.java) { ProjectNames.normalize("   ") }
        assertThrows(IllegalArgumentException::class.java) { ProjectNames.normalize("") }
    }

    @Test
    fun copyName_first_addsCopy() {
        assertEquals("Cat copy", ProjectNames.copyName("Cat", emptyList()))
    }

    @Test
    fun copyName_copyTaken_addsCopy2() {
        assertEquals("Cat copy 2", ProjectNames.copyName("Cat", listOf("Cat", "Cat copy")))
    }

    @Test
    fun copyName_copyAndCopy2Taken_addsCopy3() {
        assertEquals("Cat copy 3", ProjectNames.copyName("Cat", listOf("Cat copy", "Cat copy 2")))
    }

    @Test
    fun copyName_ofACopy_addsCopyAgain() {
        assertEquals("Cat copy copy", ProjectNames.copyName("Cat copy", listOf("Cat copy")))
    }

    @Test
    fun copyName_longSource_cutSoResultIsForty() {
        assertEquals("a".repeat(35) + " copy", ProjectNames.copyName("a".repeat(40), emptyList()))
    }

    @Test
    fun copyName_longSourceCopyTaken_cutMoreForCopy2() {
        val source = "a".repeat(40)
        assertEquals(
            "a".repeat(33) + " copy 2",
            ProjectNames.copyName(source, listOf(source, "a".repeat(35) + " copy")),
        )
    }

    @Test
    fun copyName_cutEndsWithSpace_spaceRemovedBeforeSuffix() {
        assertEquals("a".repeat(34) + " copy", ProjectNames.copyName("a".repeat(34) + " bcdef", emptyList()))
    }

    @Test
    fun copyName_emojiAtCut_neverSplitsSurrogatePair() {
        // Emoji is the 35th code point: kept whole (a cut by chars would keep only its first half).
        assertEquals(
            "a".repeat(34) + "😀 copy",
            ProjectNames.copyName("a".repeat(34) + "😀bcdef", emptyList()),
        )
        // Emoji is the 36th code point: dropped whole.
        assertEquals(
            "a".repeat(35) + " copy",
            ProjectNames.copyName("a".repeat(35) + "😀bcd", emptyList()),
        )
    }
}
