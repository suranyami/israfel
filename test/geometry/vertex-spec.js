(function() {
  var VERTEX_MATCH;
  VERTEX_MATCH = /\([0-9\.]+, [0-9\.]+\)\[[0-9]+\]/;
  describe("Vertex", function() {
    beforeEach(function() {
      return this.vertex = new Vertex();
    });
    it("has a string representation that looks like (0, 0)[0]", function() {
      (expect(this.vertex.toString())).toEqual("(0, 0)[0]");
      this.vertex.point = (new Point()).randomPoint();
      return (expect(this.vertex.toString())).toMatch(VERTEX_MATCH);
    });
    return describe("with edges", function() {
      beforeEach(function() {
        this.vertex2 = new Vertex();
        return this.edge = new Edge(this.vertex, this.vertex2);
      });
      it("shows how many edges are connected", function() {
        (expect(this.vertex.toString())).toMatch(VERTEX_MATCH);
        (expect(this.vertex.edges.length)).toEqual(1);
        return (expect(this.vertex2.edges.length)).toEqual(1);
      });
      return it("removes an edge", function() {
        this.vertex2.removeEdge(this.edge);
        return (expect(this.vertex2.edges.length)).toEqual(0);
      });
    });
  });
}).call(this);
