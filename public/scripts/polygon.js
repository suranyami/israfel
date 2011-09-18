(function() {
  var Polygon;
  window.Polygon = Polygon = (function() {
    Polygon.points = [];
    function Polygon(points) {
      this.points = points;
    }
    Polygon.prototype.toPath = function() {
      var path, point, points;
      points = this.points.slice(0);
      path = points.pop().moveTo();
      path += " " + ((function() {
        var _i, _len, _results;
        _results = [];
        for (_i = 0, _len = points.length; _i < _len; _i++) {
          point = points[_i];
          _results.push(point.lineTo());
        }
        return _results;
      })()).join(" ");
      return "" + path + "z";
    };
    Polygon.prototype.pointInside = function(other) {
      var a, b, check_points, max, min, outside, point, _i, _len;
      outside = false;
      min = new Point;
      max = new Point;
      check_points = this.points.slice(0);
      a = check_points.pop();
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
