(function() {
  var Vertex;
  var __indexOf = Array.prototype.indexOf || function(item) {
    for (var i = 0, l = this.length; i < l; i++) {
      if (this[i] === item) return i;
    }
    return -1;
  };
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
}).call(this);
