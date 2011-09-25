(function() {
  var Vertex;
  var __indexOf = Array.prototype.indexOf || function(item) {
    for (var i = 0, l = this.length; i < l; i++) {
      if (this[i] === item) return i;
    }
    return -1;
  };
  window.Vertex = Vertex = (function() {
    function Vertex(point, edges) {
      this.point = point;
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
      if (__indexOf.call(this.edges, edge) >= 0) {
        return this.edges = this.edges.splice(this.edges.indexOf(edge), 1);
      }
    };
    Vertex.prototype.neighbours = function() {};
    return Vertex;
  })();
}).call(this);
