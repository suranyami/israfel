(function() {
  describe("Polygon", function() {
    beforeEach(function() {
      return this.polygon = new Polygon([new Point(1, 2), new Point(2, 2), new Point(2, 1)]);
    });
    describe("toPath", function() {
      return it("should be of the form M1 2 L2 2 L2 1Z", function() {
        return (expect(this.polygon.toPath())).toMatch(/^M[0-9]+ [0-9]+ L[0-9]+ [0-9]+ L[0-9]+ [0-9]+z/);
      });
    });
    describe("with a point inside", function() {
      return it("has pointInside() true", function() {
        return (expect(this.polygon.pointInside(new Point(1.9, 1.9)))).toEqual(true);
      });
    });
    return describe("with a point outside", function() {
      return it("has pointInside() false", function() {
        (expect(this.polygon.pointInside(new Point(3, 3)))).toEqual(false);
        return (expect(this.polygon.pointInside(new Point(1, 1)))).toEqual(false);
      });
    });
  });
}).call(this);
