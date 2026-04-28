package org.graph.spectral.models.hypergraph

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class HypergraphCommandParserTest {
    private val parser = HypergraphCommandParser()

    @Test
    fun parsesParenthesizedUniformHyperedges() {
        val result = parser.parseHyperedges("(1,2,3); （1，3，4）")

        assertEquals(3, result.uniformity)
        assertEquals(
            listOf(
                Hyperedge(listOf("1", "2", "3")),
                Hyperedge(listOf("1", "3", "4"))
            ),
            result.hyperedges
        )
    }

    @Test
    fun parsesCompleteUniformHypergraphShortcut() {
        val result = parser.parseHyperedges("K4^3")

        assertEquals(3, result.uniformity)
        assertEquals(
            listOf(
                Hyperedge(listOf("1", "2", "3")),
                Hyperedge(listOf("1", "2", "4")),
                Hyperedge(listOf("1", "3", "4")),
                Hyperedge(listOf("2", "3", "4"))
            ),
            result.hyperedges
        )
    }

    @Test
    fun parsesParenthesizedShortcutAndMixedExplicitHyperedge() {
        val result = parser.parseHyperedges("K(4,3); (2,3,5)")

        assertEquals(3, result.uniformity)
        assertEquals(5, result.hyperedges.size)
        assertEquals(Hyperedge(listOf("2", "3", "5")), result.hyperedges.last())
    }

    @Test
    fun parsesStarShortcut() {
        val result = parser.parseHyperedges("S5^3")

        assertEquals(
            listOf(
                Hyperedge(listOf("1", "2", "3")),
                Hyperedge(listOf("1", "2", "4")),
                Hyperedge(listOf("1", "2", "5")),
                Hyperedge(listOf("1", "3", "4")),
                Hyperedge(listOf("1", "3", "5")),
                Hyperedge(listOf("1", "4", "5"))
            ),
            result.hyperedges
        )
    }

    @Test
    fun parsesLoosePathShortcut() {
        val result = parser.parseHyperedges("P4^3")

        assertEquals(
            listOf(
                Hyperedge(listOf("1", "2", "3")),
                Hyperedge(listOf("3", "4", "5")),
                Hyperedge(listOf("5", "6", "7")),
                Hyperedge(listOf("7", "8", "9"))
            ),
            result.hyperedges
        )
    }

    @Test
    fun rejectsNonUniformInput() {
        assertFailsWith<IllegalArgumentException> {
            parser.parseHyperedges("(1,2,3); (1,2)")
        }
    }

    @Test
    fun rejectsRepeatedNodes() {
        assertFailsWith<IllegalArgumentException> {
            parser.parseHyperedges("(1,1,2)")
        }
    }

    @Test
    fun rejectsInvalidLabels() {
        assertFailsWith<IllegalArgumentException> {
            parser.parseHyperedges("(1,2,*)")
        }
    }

    @Test
    fun rejectsUnknownShortcut() {
        assertFailsWith<IllegalArgumentException> {
            parser.parseHyperedges("Q5^3")
        }
    }
}
