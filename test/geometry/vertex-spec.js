(function() {
  describe("Vertex", function() {
    beforeEach(function() {
      return this.vertex = new Vertex();
    });
    return it("has a string representation that looks like (0, 0)[0]", function() {
      return (expect(this.vertex.toString())).toEqual("(0, 0)[0]");
    });
  });
}).call(this);
