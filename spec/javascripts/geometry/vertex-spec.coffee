VERTEX_MATCH = /\([0-9\.]+, [0-9\.]+\)\[[0-9]+\]/

describe "Vertex", ->
  beforeEach ->
    @vertex = new Vertex()
  it "has a string representation that looks like (0, 0)[0]", ->
    (expect @vertex.toString()).toEqual("(0, 0)[0]")
    @vertex.point = (new Point()).randomPoint()
    (expect @vertex.toString()).toMatch VERTEX_MATCH
  
  describe "with edges", ->
    beforeEach ->
      @vertex2 = new Vertex()
      @edge = new Edge(@vertex, @vertex2)
      
    it "shows how many edges are connected", ->
      (expect @vertex.toString()).toMatch VERTEX_MATCH
      (expect @vertex.edges.length).toEqual 1
      (expect @vertex2.edges.length).toEqual 1
  
    it "removes an edge", ->
      @vertex2.removeEdge(@edge)
      (expect @vertex2.edges.length).toEqual 0
      