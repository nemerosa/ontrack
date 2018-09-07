package net.nemerosa.ontrack.migration.postgresql

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MigrationTest {

    @Test
    fun `Column spec with target only`() {
        val (source, target, type) = Migration.ColumnMigration.parse("NAME")
        assertEquals("NAME", source)
        assertEquals("NAME", target)
        assertNull(type)
    }

    @Test
    fun `Column spec with source and target`() {
        val (source, target, type) = Migration.ColumnMigration.parse("OLD->NAME")
        assertEquals("OLD", source)
        assertEquals("NAME", target)
        assertNull(type)
    }

    @Test
    fun `Column spec with target and type`() {
        val (source, target, type) = Migration.ColumnMigration.parse("NAME::JSONB")
        assertEquals("NAME", source)
        assertEquals("NAME", target)
        assertEquals("JSONB", type)
    }

    @Test
    fun `Column spec with source, target and type`() {
        val (source, target, type) = Migration.ColumnMigration.parse("OLD->NAME::JSONB")
        assertEquals("OLD", source)
        assertEquals("NAME", target)
        assertEquals("JSONB", type)
    }

}