(function() {
  var Edge, EquilateralTriangle, PlanarMap, Point, Polygon, Vertex, canvas, p1, p2, p3, path;
  var __slice = Array.prototype.slice, __indexOf = Array.prototype.indexOf || function(item) {
    for (var i = 0, l = this.length; i < l; i++) {
      if (this[i] === item) return i;
    }
    return -1;
  };
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
  Polygon = (function() {
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
    return Polygon;
  })();
  Vertex = (function() {
    function Vertex(point) {
      this.point = point;
      this.edges = [];
    }
    Vertex.prototype.addEdge = function(edge) {
      if (__indexOf.call(this.edges, edge) < 0) {
        return this.edges.push(edge);
      }
    };
    Vertex.prototype.moveTo = function() {
      return this.point.moveTo();
    };
    Vertex.prototype.lineTo = function() {
      return this.point.LineTo();
    };
    return Vertex;
  })();
  Edge = (function() {
    function Edge(vertex1, vertex2) {
      this.vertex1 = vertex1;
      this.vertex2 = vertex2;
      vertex1.addEdge(self);
      vertex2.addEdge(self);
    }
    Edge.prototype.getOther = function(vertex) {
      if (vertex === this.vertex1) {
        return this.vertex2;
      } else {
        return this.vertex1;
      }
    };
    Edge.prototype.getMinX = function() {
      return Math.min(this.vertex1.position.x, this.vertex2.position.x);
    };
    Edge.prototype.toPath = function() {
      var path;
      path = this.vertex1.moveTo();
      path += this.vertex2.lineTo();
      return "" + path + "z";
    };
    return Edge;
  })();
  PlanarMap = (function() {
    function PlanarMap() {
      this.edges = [];
      this.vertices = [];
    }
    PlanarMap.prototype.removeEdge = function(edge) {
      edge.vertex1.removeEdge(edge);
      edge.vertex2.removeEdge(edge);
      return this.edges.removeElement(edge);
    };
    PlanarMap.prototype.removeVertex = function(vertex) {
      var edge, neighbour, _i, _len, _ref;
      _ref = vertex.neighbours;
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        neighbour = _ref[_i];
        edge = neighbour.nextElement;
        edge.getOther(vertex).removeEdge(edge);
        this.edges.removeElement(edge);
      }
      return this.vertices.removeElement(vertex);
    };
    return PlanarMap;
  })();
  EquilateralTriangle = (function() {
    function EquilateralTriangle(p, length) {
      var height, p2, p3;
      height = length * Math.cos(this.toRadians(30));
      p2 = new Point(p.x + (length / 2), height);
      p3 = new Point(p.x - (length / 2), height);
      this.polygon = new Polygon(p, p2, p3);
    }
    EquilateralTriangle.prototype.toPath = function() {
      return this.polygon.toPath();
    };
    EquilateralTriangle.prototype.toRadians = function(degrees) {
      return degrees * Math.PI / 180;
    };
    return EquilateralTriangle;
  })();
  p1 = new Point(2, 3);
  p2 = new Point(3, 3);
  p3 = new Point(4, 3);
  canvas = Raphael("canvas", 200, 200);
  path = new EquilateralTriangle(new Point(100, 100), 50).toPath();
  console.log(path);
  canvas.path(path);
}).call(this);
