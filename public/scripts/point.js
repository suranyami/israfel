(function() {
  var Point;
  Point = (function() {
    function Point(x, y) {
      this.x = x;
      this.y = y;
    }
    Point.prototype.toString = function() {
      return "(" + this.x + ", " + this.y + ")";
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
    return Point;
  })();
}).call(this);
