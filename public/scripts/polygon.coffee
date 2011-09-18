window.Polygon = class Polygon
  @points = []
  
  constructor: (first, rest...) ->
    @first = first
    @rest = rest
    
  toPath: ->
    path = @first.moveTo()
    for point in @rest
      path += point.lineTo()
    "#{path}z"


  pointInside: (other) ->
    outside = false
    ax = a.getX()
    ay = a.getY()
    
    min = new Point
    max = new Point

    check_points = @points.copy[0...]
    start_point = check_points.pop()

    for point in check_points
      b = point

      if a.x < b.x
        min.x = a.x
        min.y = a.y
        max.x = b.x
        max.y = b.y
      else
        min.x = b.x
        min.y = b.y
        max.x = a.x
        max.y = a.y

      # Does it straddle?
      if (min.x <= other.x) and (other.x < max.x)
        # Is the intersection point north?
        if (other.x - min.x) * (max.y - min.y) > (other.y - min.y) * (max.x - min.x)
          outside = !outside

      a = b
      a.x = b.x
      a.y = b.y

    return outside

