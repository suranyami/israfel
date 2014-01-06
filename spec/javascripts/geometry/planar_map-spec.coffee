describe "PlanarMap", ->
  beforeEach ->
    @points = [
      (new Point 0, 0)
      (new Point 0, 1)
      (new Point 1, 1)
    ]

    @vertices = [
      new Vertex @points[0]
      new Vertex @points[1]
      new Vertex @points[2]
    ]

    e1 = new Edge(@vertices[0], @vertices[1])
    e2 = new Edge(@vertices[1], @vertices[2])
    e3 = new Edge(@vertices[2], @vertices[0])
    e4 = new Edge(@vertices[0], @vertices[2])

    @edges = [
      new Edge(@vertices[0], @vertices[1])
      new Edge(@vertices[1], @vertices[2])
      new Edge(@vertices[2], @vertices[0])
    ]
    
    console.log @edges
    
    @map = new PlanarMap
    @map.addEdge(@edges[0])
    @map.addEdge(@edges[1])
    @map.addEdge(e3)
    @map.addEdge(e4)

  describe "removeEdge", ->
    it "deletes the edge", ->
      console.log @edges
      console.log @map.edges
      @map.removeEdge(@edges[0])
      (expect @map.edges.length).toEqual 2
      console.log @map.edges
      (expect @map.edges).not.toContain(@edges[0])