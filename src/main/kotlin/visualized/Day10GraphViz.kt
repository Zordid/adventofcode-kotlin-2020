package visualized

import Day10
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.graph
import guru.nidi.graphviz.invoke
import guru.nidi.graphviz.toGraphviz
import java.io.File

fun main() {
    val adapters = Day10().allRatings
    val g = graph(directed = true) {
    }
    g.invoke {
        for (adapter in adapters) {
            val canFit = adapters.filter { rating -> rating in (adapter + 1..adapter + 3) }
            canFit.forEach {
                when {
                    adapter > 0 && it < adapters.last() -> "$adapter" - "$it"
                    it == adapters.last() -> "$adapter" - "device"
                    else -> "Wall outlet" - "$it"
                }
            }
        }
    }
    g.toGraphviz().render(Format.PNG).toFile(File("Day10.png"))
}
