package com.vgl.draw.animation.core.project.backup

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class BackupFileNamesTest {
    @Test
    fun forProject_plainName_addsExtension() {
        assertEquals("Cat.frameloop", BackupFileNames.forProject("Cat"))
    }

    @Test
    fun forProject_forbiddenCharacters_replaced() {
        assertEquals("a_b_c__.frameloop", BackupFileNames.forProject("a/b:c*?"))
        assertEquals("q_l_g_p_b_t_.frameloop", BackupFileNames.forProject("q\"l<g>p|b\\t\u0007"))
    }

    @Test
    fun forProject_blank_usesAppName() {
        assertEquals("Frameloop.frameloop", BackupFileNames.forProject("   "))
    }

    @Test
    fun forAll_datedName() {
        assertEquals("Frameloop-backup-2026-11-21.frameloop", BackupFileNames.forAll(LocalDate.of(2026, 11, 21)))
    }
}
