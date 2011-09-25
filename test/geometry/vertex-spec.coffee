describe "Vertex", ->
  beforeEach ->
    @vertex = new Vertex()
  it "has a string representation that looks like (0, 0)[0]", ->
    (expect @vertex.toString()).toEqual("(0, 0)[0]")
