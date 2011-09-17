# A point where a number of Edges meet.
class Vertex
  constructor: (point) ->
    @point = point
    @edges = []

  addEdge: (edge) ->
    @edges.push edge unless edge in @edges

  moveTo: -> @point.moveTo()
  lineTo: -> @point.LineTo()
