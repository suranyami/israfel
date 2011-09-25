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
    return describe("with a point outside", function() {
      return it("has pointInside() false", function() {
        var x, _i, _len, _ref, _results;
        _ref = [0.9, 2.1, 3, 10];
        _results = [];
        for (_i = 0, _len = _ref.length; _i < _len; _i++) {
          x = _ref[_i];
          _results.push((expect(this.polygon.pointInside(new Point(x, x)))).toEqual(false));
        }
        return _results;
      });
    });
  });
}).call(this);
