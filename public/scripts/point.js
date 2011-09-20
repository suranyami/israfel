(function() {
  var Point;
  window.Point = Point = (function() {
    function Point(x, y) {
      this.x = x != null ? x : 0;
      this.y = y != null ? y : 0;
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
