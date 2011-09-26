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
    Point.prototype.randomPoint = function(range_x, range_y) {
      if (range_x == null) {
        range_x = 800;
      }
      if (range_y == null) {
        range_y = 600;
      }
      this.x = Math.random() * range_x;
      this.y = Math.random() * range_y;
      return this;
    };
    Point.prototype.clone = function() {
      return new Point(this.x, this.y);
    };
    return Point;
  })();
}).call(this);
