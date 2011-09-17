class face_info
  # States
  UNDONE = 0
  PROCESSING = 1
  DONE_BLACK = 2
  DONE_WHITE = 3

  constructor: (@face, @state) ->


# This class contains the algorithm to extract a collection of
# faces from a planar map.  It actually does a bit more than that.
# It assume the map represents a checkerboard diagram (i.e., every
# vertex has even degree except possibly for some at the borders
# of the map) and returns two arrays of faces, representing a 
# two-colouring of the map.  The algorithm returns polygons because
# the only code that cares about faces doesn't need graph information
# about the faces, just the coordinates of their corners.
class Faces
  
  # Walk from a vertex along an edge, always taking the
  # current edge's neighbour at every vertex.
  # The set of vertices encountered determines a face.
  extractFace: (fromVertex, edge) ->
    result = new Vector
    current = fromVertex
    current_edge = edge

    while true
      result.addElement current
      n = current_edge.getOther current

      if n.numNeighbours() < 2
        return null

      before_and_after = n.getBeforeAndAfter current_edge

      current = n
      current_edge = before_and_after[0]

      if current.equals fromVertex
        return result

   # Find the face_info that lies across the given edge.  Used to
   # propagate the search to adjacent faces, giving them opposite colours.

  getTwin: (face_info, index, v1, v2 ) ->
    fromVertex = face_info.face.elementAt index
    toVertex = face_info.face.elementAt((idx + 1) % face_info.face.size())
    connection = fromVertex.getNeighbour toVertex

    if connection.getV1().equals(fromVertex)
      return v2.get connection
    else
      return  v1.get connection

  # Is a polygon (given here as a vector of Vertex instances) clockwise?
  # We can answer that question by finding a spot on the polygon where
  # we can distinguish inside from outside and looking at the edges
  # at that spot.
  # In this case, we look at the vertex with the maximum X value.
  # Whether the polygon is clockwise depends on whether
  # the edge leaving that vertex is to the left or right of the edge
  # entering the vertex.
  # Left-or-right is computed using the sign of the cross product.

  isClockwise: (points) ->
    int sz = points.size()

    # First, find the vertex with the greatest X coordinate.
    imax = 0
    xmax = points.elementAt(0).getPosition().getX()

    for int idx = 1 idx < sz ++idx
      x = points.elementAt(idx).getPosition().getX()
      if (x > xmax)
        imax = idx
        xmax = x

    pmax =  points.elementAt(imax).getPosition()
    pnext = points.elementAt((imax + 1) % sz).getPosition()
    pprev = points.elementAt((imax + sz - 1) % sz).getPosition()
    
    dprev = pmax.subtract pprev
    dnext = pnext.subtract pmax

    return (dprev.cross dnext) < 0.0

  handleVertex: (vert, edge, v1, v2, faces) ->
    fc = extractFace vert, edge
    return if fc == null
    face_info = new face_info( fc, face_info.UNDONE )

    # This algorithm doesn't distinguish between clockwise and
    # counterclockwise.  So every map will produce one extraneous
    # face, namely the countour that surrounds the whole map.
    # By the nature of extractFace, the surrounding polygon will
    # be the only clockwise polygon in the set.  So check for
    # clockwise and throw it away.
    if( !isClockwise( fc ) ) {
      faces.addElement( face_info )
    }

    for( int v = 0 v < fc.size() ++v ) {
      Vertex fromVertex = (Vertex)( fc.elementAt( v ) )
      Vertex to = (Vertex)( fc.elementAt( (v+1)%fc.size() ) )
      Edge conn = fromVertex.getNeighbour( to )

      if( conn.getV1().equals( fromVertex ) ) {
        v1.put( conn, face_info )
      } else {
        v2.put( conn, face_info )
      }
    }
  }

  /*
   * The main interface to this file.  Given a map and two 
   * empty vectors, this function fills the vectors with arrays
   * of points corresponding to a two-colouring of the faces of
   * the map.
   */
  public static final void extractFaces( Map map, Vector blk, Vector wht ) {
    Hashtable v1 = new Hashtable()
    Hashtable v2 = new Hashtable()
    Vector faces = new Vector()

    // First, build all the faces.
    
    for( Enumeration e = map.getEdges() e.hasMoreElements() ) {
      Edge edge = (Edge)( e.nextElement() )

      if( !v1.containsKey( edge ) ) {
        handleVertex( edge.getV1(), edge, v1, v2, faces )
      }
      if( !v2.containsKey( edge ) ) {
        handleVertex( edge.getV2(), edge, v1, v2, faces )
      }
    }

    
    // Now propagate colours using a DFS.

    int num_faces = faces.size()
    Stack st = new Stack()

    face_info face_info = (face_info)( faces.elementAt( 0 ) )
    st.push( face_info )
    face_info.state = face_info.PROCESSING

    while( !st.empty() ) {
      face_info = (face_info)( st.pop() )

      int sz = face_info.face.size()
      int col = -1

      for( int idx = 0 idx < sz ++idx ) {
        face_info nface_info = getTwin( face_info, idx, v1, v2 )

        if( nface_info == null ) {
          continue
        }

        switch( nface_info.state ) {
        case face_info.UNDONE:
          nface_info.state = face_info.PROCESSING
          st.push( nface_info )
          break
        case face_info.PROCESSING:
          break
        case face_info.DONE_BLACK:
          if( col == 0 ) {
            throw new InternalError( "Filling problem 1" )
          } 
          col = 1
          break
        case face_info.DONE_WHITE:
          if( col == 1 ) {
            throw new InternalError( "Filling problem 2" )
          } 
          col = 0
          break
        }
      }

      if( col == 1 ) {
        face_info.state = face_info.DONE_WHITE
      } else {
        face_info.state = face_info.DONE_BLACK
      }
    }

    // Finally, turn all the face_info objects into arrays of points.

    for( int idx = 0 idx < faces.size() ++idx ) {
      face_info = (face_info)( faces.elementAt( idx ) )

      Point[] points = new Point[ face_info.face.size() ]
      for( int v = 0 v < face_info.face.size() ++v ) {
        points[ v ] = ((Vertex)face_info.face.elementAt(v)).getPosition()
      }

      if( face_info.state == face_info.DONE_WHITE ) {
        wht.addElement( points )
      } else if( face_info.state == face_info.DONE_BLACK ) {
        blk.addElement( points )
      } else {
        // This shouldn't happen.
      }
    }
  }
}
