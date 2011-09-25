(function() {
  describe("PlanarMap", function() {
    beforeEach(function() {
      var e1, e2, e3, e4;
      this.points = [new Point(0, 0), new Point(0, 1), new Point(1, 1)];
      this.vertices = [new Vertex(this.points[0]), new Vertex(this.points[1]), new Vertex(this.points[2])];
      e1 = new Edge(this.vertices[0], this.vertices[1]);
      e2 = new Edge(this.vertices[1], this.vertices[2]);
      e3 = new Edge(this.vertices[2], this.vertices[0]);
      e4 = new Edge(this.vertices[0], this.vertices[2]);
      this.edges = [new Edge(this.vertices[0], this.vertices[1]), new Edge(this.vertices[1], this.vertices[2]), new Edge(this.vertices[2], this.vertices[0])];
      console.log(this.edges);
      this.map = new PlanarMap;
      this.map.addEdge(this.edges[0]);
      this.map.addEdge(this.edges[1]);
      this.map.addEdge(e3);
      return this.map.addEdge(e4);
    });
    return describe("removeEdge", function() {
      return it("deletes the edge", function() {
        console.log(this.edges);
        console.log(this.map.edges);
        this.map.removeEdge(this.edges[0]);
        (expect(this.map.edges.length)).toEqual(2);
        console.log(this.map.edges);
        return (expect(this.map.edges)).not.toContain(this.edges[0]);
      });
    });
  });
}).call(this);
