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

    check_points = @points.slice(0)
    
    a = check_points.pop()
    size = @points.length
    for index in [0...size]
      b = @points[(index + 1) % size]

      if a.x < b.x
        {x: min.x, y: min.y} = a
        {x: max.x, y: max.y} = b
      else
        {x: min.x, y: min.y} = b
        {x: max.x, y: max.y} = a

      # Does it straddle?
      if min.x <= other.x < max.x
        # Is the intersection point north?
        if (other.x - min.x) * (max.y - min.y) > (other.y - min.y) * (max.x - min.x)
          outside = !outside

      a = b

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