(function() {
  var PlanarMap;
  var __indexOf = Array.prototype.indexOf || function(item) {
    for (var i = 0, l = this.length; i < l; i++) {
      if (this[i] === item) return i;
    }
    return -1;
  };
  window.PlanarMap = PlanarMap = (function() {
    function PlanarMap(edges, vertices) {
      this.edges = edges != null ? edges : [];
      this.vertices = vertices != null ? vertices : [];
    }
    PlanarMap.prototype.numberVertices = function() {
      return this.vertices.length;
    };
    PlanarMap.prototype.numberEdges = function() {
      return this.edges.length;
    };
    PlanarMap.prototype.addEdge = function(edge) {
      if (__indexOf.call(this.edges, edge) < 0) {
        return this.edges.push(edge);
      }
    };
    PlanarMap.prototype.addVertex = function(vertex) {
      if (__indexOf.call(this.vertices, vertex) < 0) {
        return this.vertices.push(vertex);
      }
    };
    PlanarMap.prototype.removeEdge = function(edge) {
      edge.vertex1.removeEdge(edge);
      edge.vertex2.removeEdge(edge);
      if (__indexOf.call(this.edges, edge) >= 0) {
        return this.edges = this.edges.splice(this.edges.indexOf(edge), 1);
      }
    };
    PlanarMap.prototype.removeElement = function(array, item) {
      if (__indexOf.call(array, item) >= 0) {
        return array = array.splice(array.indexOf(item), 1);
      }
    };
    PlanarMap.prototype.removeVertex = function(vertex) {
      var edge, neighbour, _i, _len, _ref;
      _ref = vertex.neighbours();
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        neighbour = _ref[_i];
        edge = neighbour.nextElement;
        (edge.getOther(vertex)).removeEdge(edge);
        removeElement(this.edges, edge);
      }
      return removeElement(this.vertices, vertex);
    };
    return PlanarMap;
  })();
}).call(this);
