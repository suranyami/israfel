# A point where a number of Edges meet.
window.Vertex = class Vertex
  constructor: (@point, @edges = []) ->

  addEdge: (edge) ->
    @edges.push(edge) unless edge in @edges

  moveTo: -> @point.moveTo()
  lineTo: -> @point.LineTo()
  
  removeEdge: (edge) ->
    @edges = @edges.splice(@edges.indexOf(edge), 1) if edge in @edges


  neighbours: () ->
    