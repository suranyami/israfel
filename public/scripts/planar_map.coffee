# Contains the vertices and edges of a repeating element
# or the final rendered region with the repeating elements in it. (?)
class PlanarMap
  constructor: ->
    @edges = []
    @vertices = []

  removeEdge: (edge) ->
    edge.vertex1.removeEdge(edge)
    edge.vertex2.removeEdge(edge)
    @edges.removeElement(edge)

  removeVertex: (vertex) ->
    for neighbour in vertex.neighbours
      edge = neighbour.nextElement
      edge.getOther(vertex).removeEdge(edge)
      @edges.removeElement(edge)
    @vertices.removeElement(vertex)

