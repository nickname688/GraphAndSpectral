package org.graph.spectral.models

class GraphGenerator {
    fun getGraph(name: String): Graph {
        return when (name) {
            "C3/K3" -> getC3()
            "C4" -> getC4()
            "K4" -> getK4()
            "C5" -> getC5()
            "M5" -> getM5()
            else -> Graph()
        }
    }

    private fun getC3(): Graph {
        val graph = Graph()
        graph.addEdge("1", "2")
        graph.addEdge("2", "3")
        graph.addEdge("1", "3")
        return graph
    }

    private fun getC4(): Graph {
        val graph = Graph()
        graph.addEdge("1", "2")
        graph.addEdge("2", "3")
        graph.addEdge("3", "4")
        graph.addEdge("1", "4")
        return graph
    }

    private fun getK4(): Graph {
        val graph = Graph()
        for (i in 1..4) {
            for (j in i + 1..4) {
                graph.addEdge(i.toString(), j.toString())
            }
        }
        return graph
    }

    private fun getC5(): Graph {
        val graph = Graph()
        graph.addEdge("1", "2")
        graph.addEdge("2", "3")
        graph.addEdge("3", "4")
        graph.addEdge("4", "5")
        graph.addEdge("5", "1")
        return graph
    }

    private fun getM5(): Graph {
        val graph = getC4()
        for (i in 1..4) {
            graph.addEdge("5", i.toString())
        }
        return graph
    }

    fun getGraphByCommand(gr: Graph, command: String): Graph? {
        val graph = gr
        var aim = 0
        var flag = false
        var flagChanged = false
        val a = arrayOf(mutableListOf<Char>(), mutableListOf<Char>())

        for (char in command) {
            when (char) {
                '(', '（' -> {
                    flag = true
                }
                ')', '）' -> {
                    flag = false
                    if (aim == 0) {
                        aim = 1
                    } else {
                        aim = 0
                        val result = link(graph, a[0].joinToString(""), a[1].joinToString(""))
                        if (result == null) return null
                        flagChanged = true
                        a[0].clear()
                        a[1].clear()
                    }
                }
                else -> {
                    if (checkNumOrChar(char) || char == '-') {
                        if (flag) {
                            a[aim].add(char)
                        } else {
                            a[aim].add(char)
                            if (aim == 0) {
                                aim = 1
                            } else {
                                aim = 0
                                val result = link(graph, a[0].joinToString(""), a[1].joinToString(""))
                                if (result == null) return null
                                flagChanged = true
                                a[0].clear()
                                a[1].clear()
                            }
                        }
                    } else {
                        return null
                    }
                }
            }
        }

        return if (flagChanged) graph else null
    }

    private fun checkNumOrChar(c: Char): Boolean {
        return c.isDigit() || c.isLetter()
    }

    private fun checkAndTrans(s: String): List<String>? {
        if (s.count { it == '-' } > 1 || s.startsWith('-') || s.endsWith('-')) {
            return null
        }

        if (!s.contains('-')) {
            return listOf(s)
        }

        val parts = s.split('-')
        if (parts.size != 2) return null

        val s1 = parts[0].toIntOrNull() ?: return null
        val s2 = parts[1].toIntOrNull() ?: return null

        if (s2 - s1 < 0 || s2 - s1 >= 50) {
            return null
        }

        return (s1..s2).map { it.toString() }
    }

    private fun link(g: Graph, a: String, b: String): Graph? {
        val nodesA = checkAndTrans(a)
        val nodesB = checkAndTrans(b)

        if (nodesA == null || nodesB == null) {
            return null
        }

        for (i in nodesA) {
            for (j in nodesB) {
                if (i != j) {
                    g.addEdge(i, j)
                }
            }
        }

        return g
    }
}
