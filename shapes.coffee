toRadians: (degrees) -> degrees * Math.PI / 180

class Point
  constructor: (x, y) -> @x: x; @y: y
  toString: -> "($@x,$@y)"
  toPath: -> "$@x $@y"
  moveTo: -> "M${@toPath()}"
  lineTo: -> "L${@toPath()}"

class Polygon
  constructor: (first, rest...) ->
    @first: first
    @rest: rest
    
  toPath: ->
    path: "${@first.moveTo()}"
    for point in @rest
      path: "$path${point.lineTo()}"
    "${path}z\n"

class Vertex
	constructor: (point) ->
		@point: point
		@edges: []
	
	addEdge: (edge) ->
		@edges.push edge

class Edge
	constructor: (vertex1, vertex2) ->
		@vertex1: vertex1
		@vertex2: vertex2
		vertex1.addEdge self
		vertex2.addEdge self
	
	getOther: (vertex) -> vertex = @vertex1 ? @vertex2 : @vertex1
	getMinX: () -> Math.min(@vertex1.position.x, @vertex2.position.x


class PlanarMap
	constructor: ->
		@edges: []
		@vertices: []
		
	removeEdge: (edge) ->
		edge.vertex1.removeEdge(edge)
		edge.vertex2.removeEdge(edge)
		@edges.removeElement(edge)

	removeVertex: (vertex) ->
		vertex.neighbours.each(neighbour)
			edge: neighbour.nextElement
			edge.getOther(vertex).removeEdge(edge)
			@edges.removeElement(edge)
		@vertices.removeElement(vertex)

class EquilateralTriangle
  constructor: (p, length) ->
    height: length * Math.cos(toRadians(30))
    p2: new Point(p.x + (length/2), height)
    p3: new Point(p.x - (length/2), height)
    @polygon: new Polygon(p, p2, p3)

  toPath: -> @polygon.toPath()
  
p1: new Point(2,3)
p2: new Point(3,3)
p3: new Point(4,3)

canvas: Raphael("canvas", 200, 200);
canvas.path(new EquilateralTriangle(new Point(100, 100), 50).toPath())
