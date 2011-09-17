(function() {
  var Edge;
  Edge = (function() {
    function Edge(vertex1, vertex2) {
      this.vertex1 = vertex1;
      this.vertex2 = vertex2;
      this.vertex1.addEdge(self);
      this.vertex2.addEdge(self);
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
}).call(this);
