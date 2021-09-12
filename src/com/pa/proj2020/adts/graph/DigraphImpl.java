package com.pa.proj2020.adts.graph;

import java.util.*;

public class DigraphImpl<V, E> implements Digraph<V, E>  {

    private final Map<V, Vertex<V>> vertices;

    private volatile boolean running;

    public DigraphImpl() {
        this.vertices = new HashMap<>();
    }

    /**
     * Returns the number of vertices inside the digraph
     *
     * @return number of vertices
     */
    @Override
    public int numVertices() {
        return this.vertices.size();
    }

    /**
     * Returns the number of edges inside digraph
     *
     * @return number of edges
     */
    @Override
    public int numEdges() {
        return edges().size();
    }

    /**
     * Return a collection of all vertices inside the digraph
     *
     * @return collection of vertices
     */
    @Override
    public Collection<Vertex<V>> vertices() {
        return this.vertices.values();
    }

    /**
     * Return a collection of all edges inside the digraph
     *
     * @return collection of edges
     */
    @Override
    public Collection<Edge<E, V>> edges() {

        List<Edge<E, V>> edges = new ArrayList<>();

        for (Vertex<V> v : vertices.values()) {
            MyVertex v2 = checkVertex(v);
            for (Edge<E, V> e : v2.edges
            ) {
                if (edges.contains(e) == false) edges.add(e);
            }
        }
        return edges;
    }

    /**
     * Returns a collection with all edges that are incident to the vertex given in parameters
     *
     * @param inbound Inbound Vertex
     *
     * @return list of edges
     *
     * @exception InvalidVertexException if the vertex is not present in the digraph
     */
    @Override
    public Collection<Edge<E, V>> incidentEdges(Vertex<V> inbound) throws InvalidVertexException {
        List<Edge<E, V>> edges = new ArrayList<>();
        MyVertex v = checkVertex(inbound);
        for (Edge<E, V> e : v.edges
        ) {
            MyEdge me = checkEdge(e);
            if (me.vertexInbound == v) edges.add(e);
        }
        return edges;
    }

    /**
     * Returns the vertex that is opposite of the edge or vertex given in parameters
     *
     * @param v Vertex
     * @param e Edge
     *
     * @return vertex
     *
     * @exception InvalidVertexException if the vertex is not present in the digraph
     * @exception InvalidEdgeException if the edge is not prsent in the digraph
     */
    @Override
    public Vertex<V> opposite(Vertex<V> v, Edge<E, V> e) throws InvalidVertexException, InvalidEdgeException {
        MyVertex mv = checkVertex(v);
        Vertex rV = null;
        for (Edge<E, V> e2 : mv.edges
        ) {
            if (e2 == e) {
                MyEdge me = checkEdge(e2);
                if (me.vertexInbound == v)
                    rV = me.vertexOutbound;
                else
                    rV = me.vertexInbound;
            }
        }
        return rV;
    }

    /**
     * Returns the outbound edges of the vertex given in parameter
     *
     * @param outbound Outbound Vertex
     *
     * @return List of Edges
     *
     * @exception InvalidVertexException if the vertex is not present in the digraph
     */
    @Override
    public Collection<Edge<E, V>> outboundEdges(Vertex<V> outbound) throws InvalidVertexException {
        List<Edge<E, V>> edges = new ArrayList<>();
        MyVertex v = checkVertex(outbound);
        for (Edge<E, V> e : v.edges
        ) {
            MyEdge me = checkEdge(e);
            if (me.vertexOutbound == v) edges.add(e);
        }
        return edges;
    }

    /**
     * Returns if the Outbound and Inbound vertex are adjacent
     *
     * @param outbound Outbound Vertex
     * @param inbound Inbound Vertex
     *
     * @return boolean
     *
     * @exception InvalidVertexException if the vertex is not present in the digraph
     */
    @Override
    public boolean areAdjacent(Vertex<V> outbound, Vertex<V> inbound) throws InvalidVertexException {
        MyVertex v = checkVertex(outbound);

        for (Edge<E, V> e : v.edges
        ) {
            MyEdge me = checkEdge(e);
            if (me.vertexInbound == inbound && me.vertexOutbound == outbound) return true;
        }
        return false;
    }

    /**
     * Insert a vertex in the digraprh
     *
     * @param vElement Element to add
     *
     * @return The vertex that was added
     *
     * @exception InvalidVertexException if the vertex is not present in the digraph
     */
    @Override
    public Vertex<V> insertVertex(V vElement) throws InvalidVertexException {
        if (vertices.containsKey(vElement) == true) {
            throw new InvalidVertexException("There's already a vertex with this element.");
        }

        MyVertex newVertex = new MyVertex(vElement);

        vertices.put(vElement, newVertex);

        return newVertex;
    }

    /**
     * Inserts an edge between the outbound and inbound vertexes given in parameters
     *
     * @param outbound Element to add
     * @param inbound Element to add
     * @param edgeElement Element to add
     *
     * @return The edge that was added
     *
     * @exception InvalidVertexException if the vertex is not present in the digraph
     * @exception InvalidEdgeException if the vertex is not present in the digraph
     */
    @Override
    public Edge<E, V> insertEdge(Vertex<V> outbound, Vertex<V> inbound, E edgeElement) throws InvalidVertexException, InvalidEdgeException {

        if (areAdjacent(outbound, inbound)) {
            throw new InvalidEdgeException("There's already an edge with this vertixes.");
        }

        MyVertex outVertex = checkVertex(outbound);
        MyVertex inVertex = checkVertex(inbound);

        MyEdge newEdge1 = new MyEdge(edgeElement, outVertex, inVertex);
        outVertex.edges.add(newEdge1);
        inVertex.edges.add(newEdge1);
        return newEdge1;
    }

    /**
     * Inserts an edge between the outbound and inbound element given in parameters
     *
     * @param outboundElement Element to add
     * @param inboundElement Element to add
     * @param edgeElement Element to add
     *
     * @return The edge that was added
     *
     * @exception InvalidVertexException if the vertex is not present in the digraph
     * @exception InvalidEdgeException if the vertex is not present in the digraph
     */
    @Override
    public Edge<E, V> insertEdge(V outboundElement, V inboundElement, E edgeElement) throws InvalidVertexException, InvalidEdgeException {
        Vertex<V> outboundvertex = vertices.get(outboundElement);
        Vertex<V> inboundvertex = vertices.get(inboundElement);

        if (areAdjacent(outboundvertex, inboundvertex))
            throw new InvalidVertexException("There's already an edge with this elements.");
        if (!vertices.containsKey(outboundElement)) {
            throw new InvalidVertexException("No vertex contains " + outboundElement);
        }
        if (!vertices.containsKey(inboundElement)) {
            throw new InvalidVertexException("No vertex contains " + inboundElement);
        }


        MyVertex outVertex = checkVertex(outboundvertex);
        MyVertex inVertex = checkVertex(inboundvertex);

        MyEdge newEdge1 = new MyEdge(edgeElement, outboundvertex, inboundvertex);
        outVertex.edges.add(newEdge1);
        inVertex.edges.add(newEdge1);

        return newEdge1;
    }

    /**
     * Removes a vertex of the digraph
     *
     * @param v Element to remove
     *
     * @return The edge that was removed
     *
     * @exception InvalidVertexException if the vertex is not present in the digraph
     */
    @Override
    public V removeVertex(Vertex<V> v) throws InvalidVertexException {
        MyVertex vertex = checkVertex(v);
        for (Edge<E, V> e : vertex.edges
        ) {
            removeEdge(e);
        }
        vertices.remove(vertex.element);
        return vertex.element();
    }

    /**
     * Removes an edge from the digraph
     *
     * @param e Edge
     *
     * @return The edge that was removed
     *
     * @exception InvalidEdgeException if the Edge is not present in the digraph
     */
    @Override
    public E removeEdge(Edge<E, V> e) throws InvalidEdgeException {
        MyEdge edge = checkEdge(e);
        MyVertex outboundVertex = checkVertex(edge.vertexOutbound);
        MyVertex inboundVertex = checkVertex(edge.vertexInbound);
        outboundVertex.edges.remove(e);
        inboundVertex.edges.remove(e);
        return edge.element();
    }

    /**
     * Replace the element of the vertex that is given in parameter
     *
     * @param v vertex that you want to change the element
     * @param newElement the element that is going to be changed
     *
     * @return old element
     *
     * @exception InvalidVertexException if the vertex is already with the element given in parameter
     */
    @Override
    public V replace(Vertex<V> v, V newElement) throws InvalidVertexException {
        if (!vertices.containsKey(newElement)) {
            throw new InvalidVertexException("There's already a vertex with this element.");
        }

        MyVertex newVertex = checkVertex(v);
        V oldElement = newVertex.element;
        newVertex.element = newElement;

        return oldElement;
    }

    /**
     * Replace the element of the edge that is given in parameter
     *
     * @param e edge that you want to change the element
     * @param newElement the element that is going to be changed
     *
     * @return old element
     *
     * @exception InvalidEdgeException if the edge is already with the element given in parameter
     */
    @Override
    public E replace(Edge<E, V> e, E newElement) throws InvalidEdgeException {
        for (Edge<E, V> edge : edges()
        ) {
            if (edge.element() == newElement) {
                throw new InvalidEdgeException("There's already an edge with this element.");
            }
        }
        MyEdge changeEdge = checkEdge(e);

        E oldElement = changeEdge.element;
        changeEdge.element = newElement;

        return oldElement;
    }

    class MyVertex implements Vertex<V> {

        V element;
        protected List<Edge<E, V>> edges;

        public MyVertex(V element) {
            this.element = element;
            edges = new LinkedList<>();
        }

        @Override
        public V element() {
            return this.element;
        }

        @Override
        public String toString() {
            return "Vertex{" + element + '}';
        }

        public V getElement() {
            return element;
        }

        public List<Edge<E, V>> getEdges() {
            return edges;
        }
    }

    class MyEdge implements Edge<E, V> {

        E element;
        Vertex<V> vertexOutbound;
        Vertex<V> vertexInbound;

        public MyEdge(E element, Vertex<V> vertexOutbound, Vertex<V> vertexInbound) {
            this.element = element;
            this.vertexOutbound = vertexOutbound;
            this.vertexInbound = vertexInbound;
        }

        @Override
        public E element() {
            return this.element;
        }

        public boolean contains(Vertex<V> v) {
            return (vertexOutbound == v || vertexInbound == v);
        }

        @Override
        public Vertex<V>[] vertices() {
            Vertex[] vertices = new Vertex[2];
            vertices[0] = vertexOutbound;
            vertices[1] = vertexInbound;

            return vertices;
        }

        @Override
        public String toString() {
            return "Edge{{" + element + "}, vertexOutbound=" + vertexOutbound.toString()
                    + ", vertexInbound=" + vertexInbound.toString() + '}';
        }
    }

    /**
     * Checks whether a given vertex is valid and belongs to this graph
     *
     * @param v
     * @return Vertex
     *
     * @throws InvalidVertexException
     */
    public DigraphImpl.MyVertex checkVertex(Vertex<V> v) throws InvalidVertexException {
        if (v == null) throw new InvalidVertexException("Null vertex.");

        DigraphImpl.MyVertex vertex;
        try {
            vertex = (DigraphImpl.MyVertex) v;
        } catch (ClassCastException e) {
            throw new InvalidVertexException("Not a vertex.");
        }

        if (!vertices.containsKey(vertex.element)) {
            throw new InvalidVertexException("Vertex does not belong to this graph.");
        }

        return vertex;
    }

    /**
     * Checks whether a given vertex is valid and belongs to this graph
     *
     * @param e
     * @return edge
     *
     *
     * @throws InvalidVertexException
     */
    public DigraphImpl.MyEdge checkEdge(Edge<E, V> e) throws InvalidEdgeException {
        if (e == null) throw new InvalidEdgeException("Null edge.");

        DigraphImpl.MyEdge edge;
        try {
            edge = (DigraphImpl.MyEdge) e;
        } catch (ClassCastException ex) {
            throw new InvalidVertexException("Not an edge.");
        }
        Collection<Edge<E, V>> edges = edges();
        if (!edges.contains(edge)) {
            throw new InvalidEdgeException("Edge does not belong to this graph.");
        }


        return edge;
    }
}