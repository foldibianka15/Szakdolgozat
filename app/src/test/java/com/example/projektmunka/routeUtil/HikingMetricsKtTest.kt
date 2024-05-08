package com.example.projektmunka.routeUtil

import com.example.projektmunka.data.Node
import com.example.projektmunka.data.Route
import com.google.common.truth.Truth.assertThat
import org.jgrapht.Graph
import org.jgrapht.graph.DefaultUndirectedWeightedGraph
import org.jgrapht.graph.DefaultWeightedEdge
import org.junit.Before
import org.junit.Test


class HikingMetricsKtTest {

    private lateinit var graph1: Graph<Node, DefaultWeightedEdge>

    @Before
    fun setup() {
        // itt inicializálod a gráf egyet
       graph1 =
            DefaultUndirectedWeightedGraph<Node, DefaultWeightedEdge>(DefaultWeightedEdge::class.java)

        val node1 = Node(0, 47.532039,19.0381565 )
        graph1.addVertex(node1)
        graph1.addVertex(Node(0, 47.5320482,19.038076, 0.0))
        graph1.addEdge(Node(0, 47.532039,19.0381565 ), Node(0, 47.5320482,19.038076, 0.0))
    }

    @Test
    fun `calculate route ascent with flat route returns 0 ascent`() {
        val nodes = mutableListOf<Node>()

        nodes.add(Node(0, 47.532039,19.0381565, 0.0))
        nodes.add(Node(0, 47.5320482,19.038076, 0.0))
        nodes.add(Node(0, 47.532054, 19.038008, 0.0))
        val route = Route(nodes)
        val result = calculateRouteAscent(route)

        assertThat(result).isZero()
    }

    @Test
    fun `calculate route ascent with ascending route returns positive ascent`() {
        val nodes = mutableListOf<Node>()

        nodes.add(Node(0, 47.532039,19.0381565, 0.0))
        nodes.add(Node(0, 47.5320482,19.038076, 5.0))
        nodes.add(Node(0, 47.532054, 19.038008, 10.0))
        val route = Route(nodes)
        val result = calculateRouteAscent(route)

        assertThat(result).isGreaterThan(0.0)
    }

    @Test
    fun `calculate route ascent with descending route returns 0`() {
        val nodes = mutableListOf<Node>()

        nodes.add(Node(0, 47.532039,19.0381565, 10.0))
        nodes.add(Node(0, 47.5320482,19.038076, 5.0))
        nodes.add(Node(0, 47.532054, 19.038008, 0.0))
        val route = Route(nodes)
        val result = calculateRouteAscent(route)

        assertThat(result).isZero()
    }

    @Test
    fun `calculate route ascent with returns expected value`() {
        val nodes = mutableListOf<Node>()

        nodes.add(Node(0, 47.532039,19.0381565, 5.0))
        nodes.add(Node(0, 47.5320482,19.038076, 9.0))
        nodes.add(Node(0, 47.532054, 19.038008, 3.0))
        val route = Route(nodes)
        val result = calculateRouteAscent(route)

        assertThat(result).isEqualTo(4.0)
    }
}