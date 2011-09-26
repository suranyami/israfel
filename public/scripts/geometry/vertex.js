(function() {
  var Vertex;
  var __indexOf = Array.prototype.indexOf || function(item) {
    for (var i = 0, l = this.length; i < l; i++) {
      if (this[i] === item) return i;
    }
    return -1;
  };
  Array.prototype.remove = function(e) {
    var t, _ref;
    if ((t = this.indexOf(e)) > -1) {
      return ([].splice.apply(this, [t, t - t + 1].concat(_ref = [])), _ref);
    }
  };
  window.Vertex = Vertex = (function() {
    function Vertex(point, edges) {
      this.point = point != null ? point : new Point(0, 0);
      this.edges = edges != null ? edges : [];
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
    Vertex.prototype.removeEdge = function(edge) {
      this.edges.remove(edge);
      return console.log(this.edges.length);
    };
    Vertex.prototype.toString = function() {
      return "(" + this.point.x + ", " + this.point.y + ")[" + this.edges.length + "]";
    };
    Vertex.prototype.neighbours = function() {};
    return Vertex;
  })();
}).call(this);
