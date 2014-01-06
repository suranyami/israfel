Array::remove = (e) ->
  @[t..t] = [] if (t = @indexOf(e)) > -1

# A point where a number of Edges meet.
window.Vertex = class Vertex
  constructor: (@point = new Point(0, 0), edges) ->
    @edges = edges ? []
    
  addEdge: (edge) ->
    @edges.push(edge) unless edge in @edges

  moveTo: -> @point.moveTo()
  lineTo: -> @point.LineTo()
  
  removeEdge: (edge) ->
    @edges.remove(edge)
    console.log @edges.length

  toString: ->
    "(#{@point.x}, #{@point.y})[#{@edges.length}]"

  neighbours: () ->
    