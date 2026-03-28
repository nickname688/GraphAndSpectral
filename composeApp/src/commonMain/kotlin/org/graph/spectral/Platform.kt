package org.graph.spectral

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform