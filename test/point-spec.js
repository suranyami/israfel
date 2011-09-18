(function() {
  var SPACE_DIGITS;
  SPACE_DIGITS = /^[0-9]+ [0-9]+/;
  describe("Point", function() {
    beforeEach(function() {
      return this.point = new Point(1, 5);
    });
    describe("toPath", function() {
      return it("should have 2 numbers separated by a space", function() {
        return (expect(this.point.toPath())).toMatch(SPACE_DIGITS);
      });
    });
    describe("moveTo", function() {
      return it("should have an M followed by 2 numbers separated by a space", function() {
        return (expect(this.point.moveTo())).toMatch(/^M[0-9]+ [0-9]+/);
      });
    });
    return describe("lineTo", function() {
      return it("should have an L followed by 2 numbers separated by a space", function() {
        return (expect(this.point.lineTo())).toMatch(/^L[0-9]+ [0-9]+/);
      });
    });
  });
}).call(this);
