window.Edge = class Edge
  constructor: (@vertex1, @vertex2) ->
    @vertex1.addEdge self
    @vertex2.addEdge self

  getOther: (vertex) ->
    if vertex == @vertex1
      @vertex2
    else
      @vertex1

  getMinX: ->
    Math.min @vertex1.position.x, @vertex2.position.x

  getMinY: ->
    Math.min @vertex1.position.y, @vertex2.position.y

  toPath: ->
    path = @vertex1.moveTo()
    path += @vertex2.lineTo()
    "#{path}z"
