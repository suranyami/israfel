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
        var x, _results, _step;
        _results = [];
        for (x = 1.9, _step = 0.1; 1.9 <= 1.5 ? x <= 1.5 : x >= 1.5; x += _step) {
          _results.push((expect(this.polygon.pointInside(new Point(x, x)))).toEqual(true));
        }
        return _results;
      });
    });
    return describe("with a point outside", function() {
      return it("has pointInside() false", function() {
        var x, _i, _len, _ref, _results;
        _ref = [1, 3, 10];
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
