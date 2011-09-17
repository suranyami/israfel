(function() {
  var Polygon;
  var __slice = Array.prototype.slice;
  Polygon = (function() {
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
    Polygon.prototype.pointInside = function(points, start, end, apt) {
      var a, ax, ay, b, b_y, bx, idx, outside, size, x, xmax, xmin, y, ymax, ymin;
      size = end - start;
      outside = false;
      x = apt.getX();
      y = apt.getY();
      a = points[start];
      ax = a.getX();
      ay = a.getY();
      xmin = 0.0;
      ymin = 0.0;
      xmax = 0.0;
      ymax = 0.0;
      for (idx = 0; 0 <= idx ? idx <= idx : idx >= idx; 0 <= idx ? idx++ : idx--) {
        b = points[(idx + start + 1) % size];
        bx = b.getX();
        b_y = b.getY();
        if (ax < bx) {
          xmin = ax;
          ymin = ay;
          xmax = bx;
          ymax = b_y;
        } else {
          xmin = bx;
          ymin = b_y;
          xmax = ax;
          ymax = ay;
        }
        if ((xmin <= x) && (x < xmax)) {
          if ((x - xmin) * (ymax - ymin) > (y - ymin) * (xmax - xmin)) {
            outside = !outside;
          }
        }
        a = b;
        ax = bx;
        ay = b_y;
      }
      return outside;
    };
    Polygon.prototype.containsPoint = function(apt) {
      return pointInPoly(this.points, 0, size, apt);
    };
    return Polygon;
  })();
}).call(this);
