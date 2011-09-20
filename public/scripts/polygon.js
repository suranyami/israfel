(function() {
  var Polygon, RegularPolygon;
  var __hasProp = Object.prototype.hasOwnProperty, __extends = function(child, parent) {
    for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; }
    function ctor() { this.constructor = child; }
    ctor.prototype = parent.prototype;
    child.prototype = new ctor;
    child.__super__ = parent.prototype;
    return child;
  };
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
      var a, b, check_points, index, max, min, outside, size, _ref;
      outside = false;
      min = new Point;
      max = new Point;
      check_points = this.points.slice(0);
      a = check_points.pop();
      size = this.points.length;
      for (index = 0; 0 <= size ? index < size : index > size; 0 <= size ? index++ : index--) {
        b = this.points[(index + 1) % size];
        if (a.x < b.x) {
          min.x = a.x, min.y = a.y;
          max.x = b.x, max.y = b.y;
        } else {
          min.x = b.x, min.y = b.y;
          max.x = a.x, max.y = a.y;
        }
        if ((min.x <= (_ref = other.x) && _ref < max.x)) {
          if ((other.x - min.x) * (max.y - min.y) > (other.y - min.y) * (max.x - min.x)) {
            outside = !outside;
          }
        }
        a = b;
      }
      return outside;
    };
    return Polygon;
  })();
  window.RegularPolygon = RegularPolygon = (function() {
    __extends(RegularPolygon, Polygon);
    function RegularPolygon(x, y, sides, radius) {
      var count;
      this.x = x;
      this.y = y;
      this.sides = sides != null ? sides : 3;
      this.radius = radius != null ? radius : 100;
      this.angle_inc = Math.PI * 2.0 / this.sides;
      this.points = (function() {
        var _ref, _results;
        _results = [];
        for (count = 0, _ref = this.sides; 0 <= _ref ? count <= _ref : count >= _ref; 0 <= _ref ? count++ : count--) {
          _results.push(this.getPoint(count));
        }
        return _results;
      }).call(this);
    }
    RegularPolygon.prototype.getPoint = function(index) {
      var cur_angle, x, y;
      cur_angle = this.angle_inc * index;
      x = this.x + this.radius * Math.cos(cur_angle);
      y = this.y + this.radius * Math.sin(cur_angle);
      return new Point(x, y);
    };
    return RegularPolygon;
  })();
}).call(this);
