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
}
