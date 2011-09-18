(function() {
  var FaceInfo, Faces;
  FaceInfo = (function() {
    var DONE_BLACK, DONE_WHITE, PROCESSING, UNDONE;
    UNDONE = 0;
    PROCESSING = 1;
    DONE_BLACK = 2;
    DONE_WHITE = 3;
    function FaceInfo(face, state) {
      this.face = face;
      this.state = state;
    }
    return FaceInfo;
  })();
  window.Faces = Faces = (function() {
    function Faces() {}
    Faces.prototype.extractFace = function(fromVertex, edge) {
      var before_and_after, current, current_edge, n, result, _results;
      result = new Vector;
      current = fromVertex;
      current_edge = edge;
      _results = [];
      while (true) {
        result.addElement(current);
        n = current_edge.getOther(current);
        if (n.numNeighbours() < 2) {
          return null;
        }
        before_and_after = n.getBeforeAndAfter(current_edge);
        current = n;
        current_edge = before_and_after[0];
        if (current.equals(fromVertex)) {
          return result;
        }
      }
      return _results;
    };
    Faces.prototype.getTwin = function(face_info, index, v1, v2) {
      var connection, fromVertex, toVertex;
      fromVertex = face_info.face[index];
      toVertex = face_info.face[(index + 1) % face_info.face.size()];
      connection = fromVertex.getNeighbour(toVertex);
      if (connection.getV1().equals(fromVertex)) {
        return v2.get(connection);
      } else {
        return v1.get(connection);
      }
    };
    Faces.prototype.isClockwise = function(points) {
      var dnext, dprev, imax, pmax, pnext, point, pprev, sz, x, xmax, _i, _len;
      int(sz = points.size());
      imax = 0;
      xmax = points.elementAt(0.getPosition().getX());
      for (_i = 0, _len = points.length; _i < _len; _i++) {
        point = points[_i];
        x = point.getPosition().getX();
        if (x > xmax) {
          imax = idx;
          xmax = x;
        }
      }
      pmax = points[imax].getPosition();
      pnext = points[(imax + 1) % sz].getPosition();
      pprev = points[(imax + sz - 1) % sz].getPosition();
      dprev = pmax.subtract(pprev);
      dnext = pnext.subtract(pmax);
      return (dprev.cross(dnext)) < 0.0;
    };
    Faces.prototype.handleVertex = function(vert, edge, v1, v2, faces) {
      var conn, face_info, fc, fromVertex, toVertex, v, _len, _results;
      fc = extractFace(vert, edge);
      if (fc === null) {
        return;
      }
      face_info = new FaceInfo(fc, FaceInfo.prototype.UNDONE);
      if (!isClockwise(fc)) {
        faces << face_info;
      }
      _results = [];
      for (v = 0, _len = fc.length; v < _len; v++) {
        fromVertex = fc[v];
        toVertex = fc[(v + 1) % fc.size()];
        Edge(conn = fromVertex.getNeighbour(toVertex));
        _results.push(conn.getV1().equals(fromVertex) ? v1.put(conn, face_info) : v2.put(conn, face_info));
      }
      return _results;
    };
    Faces.prototype.extractFaces = function(map, blk, wht) {
      var col, edge, face, face_info, faces, idx, nface_info, num_faces, points, stack, sz, v1, v2, _i, _j, _k, _len, _len2, _len3, _ref, _ref2, _results;
      v1 = {};
      v2 = {};
      faces = [];
      _ref = map.getEdges();
      for (_i = 0, _len = _ref.length; _i < _len; _i++) {
        edge = _ref[_i];
        if (!v1.containsKey(edge)) {
          handleVertex(edge.getV1(), edge, v1, v2, faces);
        }
        if (!v2.containsKey(edge)) {
          handleVertex(edge.getV2(), edge, v1, v2, faces);
        }
      }
      num_faces = faces.size();
      stack = new Stack();
      face_info = faces[0];
      stack.push(face_info);
      face_info.state = face_info.PROCESSING;
      while (!stack.empty()) {
        face_info = stack.pop();
        sz = face_info.face.size();
        col = -1;
        for (idx = 0; 0 <= sz ? idx <= sz : idx >= sz; 0 <= sz ? idx++ : idx--) {
          nface_info = getTwin(face_info, idx, v1, v2);
          if (nface_info === null) {
            continue;
          }
          switch (nface_info.state) {
            case face_info.UNDONE:
              nface_info.state = face_info.PROCESSING;
              stack.push(nface_info);
              break;
            case face_info.PROCESSING:
              break;
            case face_info.DONE_BLACK:
              if (col === 0) {
                throw new InternalError("Filling problem 1");
              }
              col = 1;
              break;
            case face_info.DONE_WHITE:
              if (col === 1) {
                throw new InternalError("Filling problem 2");
              }
              col = 0;
          }
        }
        if (col === 1) {
          face_info.state = face_info.DONE_WHITE;
        } else {
          face_info.state = face_info.DONE_BLACK;
        }
      }
      _results = [];
      for (_j = 0, _len2 = faces.length; _j < _len2; _j++) {
        face_info = faces[_j];
        _ref2 = face_info.face;
        for (_k = 0, _len3 = _ref2.length; _k < _len3; _k++) {
          face = _ref2[_k];
          points = face.getPosition();
        }
        _results.push(face_info.state === face_info.DONE_WHITE ? wht.addElement(points) : face_info.state === face_info.DONE_BLACK ? blk.addElement(points) : void 0);
      }
      return _results;
    };
    return Faces;
  })();
}).call(this);
