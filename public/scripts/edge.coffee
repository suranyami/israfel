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

  toPath: ->
    path = @vertex1.moveTo()
    path += @vertex2.lineTo()
    "#{path}z"
