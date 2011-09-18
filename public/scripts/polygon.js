(function() {
  var Polygon;
  var __slice = Array.prototype.slice;
  window.Polygon = Polygon = (function() {
    Polygon.points = [];
    function Polygon() {
      var first, rest;
      first = arguments[0], rest = 2 <= arguments.length ? __slice.call(arguments, 1) : [];
      this.first = first;
      this.rest = rest;
    }
    Polygon.prototype.toPath = function() {
      var path, point, _i, _len, _ref;
      path = this.first.moveTo();
      _ref = this.rest;
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        point = _ref[_i];
        path += point.lineTo();
      }
      return "" + path + "z";
    };
    Polygon.prototype.pointInside = function(other) {
      var a, ax, ay, b, check_points, max, min, outside, point, start_point, _i, _len;
      outside = false;
      ax = a.getX();
      ay = a.getY();
      min = new Point;
      max = new Point;
      check_points = this.points.copy.slice(0);
      start_point = check_points.pop();
      for (_i = 0, _len = check_points.length; _i < _len; _i++) {
        point = check_points[_i];
        b = point;
        if (a.x < b.x) {
          min.x = a.x;
          min.y = a.y;
          max.x = b.x;
          max.y = b.y;
        } else {
          min.x = b.x;
          min.y = b.y;
          max.x = a.x;
          max.y = a.y;
        }
        if ((min.x <= other.x) && (other.x < max.x)) {
          if ((other.x - min.x) * (max.y - min.y) > (other.y - min.y) * (max.x - min.x)) {
            outside = !outside;
          }
        }
        a = b;
        a.x = b.x;
        a.y = b.y;
      }
      return outside;
    };
    return Polygon;
  })();
}).call(this);
