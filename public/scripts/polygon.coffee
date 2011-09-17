class Polygon
  @points = []
  
  constructor: (first, rest...) ->
    @first = first
    @rest = rest
    
  toPath: ->
    path = @first.moveTo()
    for point in @rest
      path += point.lineTo()
    "#{path}z"


  pointInside: (points, start, end, apt) ->
    size = end - start
    outside = false

    x = apt.getX()
    y = apt.getY()

    a = points[start]
    ax = a.getX()
    ay = a.getY()

    xmin = 0.0
    ymin = 0.0
    xmax = 0.0
    ymax = 0.0

    for idx in [0..idx]
      b = points[(idx + start + 1) % size]

      bx = b.getX()
      b_y = b.getY()

      if ax < bx
        xmin = ax
        ymin = ay
        xmax = bx
        ymax = b_y
      else
        xmin = bx
        ymin = b_y
        xmax = ax
        ymax = ay

      # Does it straddle?
      if (xmin <= x) and (x < xmax)
        # Is the intersection point north?
        if (x - xmin) * (ymax - ymin) > (y - ymin) * (xmax - xmin)
          outside = !outside

      a = b
      ax = bx
      ay = b_y

    return outside


  containsPoint: (apt) ->
    return pointInPoly @points, 0, size, apt

