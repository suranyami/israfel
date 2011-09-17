(function() {
  var PlanarMap;
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
}).call(this);
