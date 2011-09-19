window.Polygon = class Polygon
  @points = []
  
  constructor: (@points) ->
    
  toPath: ->
    points = @points.slice(0)
    path = points.pop().moveTo()
    path += " " + (point.lineTo() for point in points).join(" ")
    "#{path}z"

  pointInside: (other) ->
    outside = false

    min = new Point
    max = new Point

    check_points = @points[0...]
    a = check_points.pop()

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

window.RegularPolygon = class RegularPolygon extends Polygon
  constructor: (@x, @y, @sides = 3, @radius = 100) ->
    @angle_inc = Math.PI * 2.0 / @sides
    @points = (@getPoint(count) for count in [0..@sides])
    
  getPoint: (index) ->
    cur_angle = @angle_inc * index
    x = @x + @radius * Math.cos(cur_angle)
    y = @y + @radius * Math.sin(cur_angle)
    new Point x, y