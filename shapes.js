(function(){
  var EquilateralTriangle, Point, Polygon, p1, p2, p3, toRadians;
  var __slice = Array.prototype.slice;
  toRadians = function(degrees) {
    return degrees * Math.PI / 180;
  };
  Point = function(x, y) {
    this.x = x;
    this.y = y;
    return this;
  };
  Point.prototype.toString = function() {
    return "(" + this.x + "," + this.y + ")";
  };
  Point.prototype.toPath = function() {
    return "" + this.x + " " + this.y;
  };
  Point.prototype.moveTo = function() {
    return "M" + (this.toPath());
  };
  Point.prototype.lineTo = function() {
    return "L" + (this.toPath());
  };

  Polygon = function(first) {
    var rest;
    var _a = arguments.length, _b = _a >= 2;
    rest = __slice.call(arguments, 1, _a - 0);
    this.first = first;
    this.rest = rest;
    return this;
  };
  Polygon.prototype.toPath = function() {
    var _a, _b, _c, path, point;
    path = ("" + (this.first.moveTo()));
    _b = this.rest;
    for (_a = 0, _c = _b.length; _a < _c; _a++) {
      point = _b[_a];
      path = ("" + path + (point.lineTo()));
    }
    return "" + (path) + "z\n";
  };

  EquilateralTriangle = function(p, length) {
    var height, p2, p3;
    height = length * Math.cos(toRadians(30));
    p2 = new Point(p.x + (length / 2), height);
    p3 = new Point(p.x - (length / 2), height);
    this.polygon = new Polygon(p, p2, p3);
    return this;
  };
  EquilateralTriangle.prototype.toPath = function() {
    return this.polygon.toPath();
  };

  p1 = new Point(2, 3);
  p2 = new Point(3, 3);
  p3 = new Point(4, 3);
  print(new Polygon(p1).toPath());
  print(new Polygon(p1, p2).toPath());
  print(new Polygon(p1, p2, p3).toPath());
  print(new Polygon(p1, p2, p3).toPath());
  print(new EquilateralTriangle(new Point(0, 0), 10).toPath());
})();
