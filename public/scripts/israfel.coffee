window.Israfel = class Israfel
  MAX_SIDES = 10
  MIN_SIDES = 3
  
  constructor: () ->
    @sides = 3
    @direction = 1
    
  draw: ->
    window.paper.clear()
    polygon = new RegularPolygon(400, 300, @sides, 200)

    polygon_drawing = window.paper.path polygon.toPath()
    polygon_drawing.attr({color: "black"})
    polygon_drawing.attr({fill: "lightgray"})

    for count in [1..500]
      x = Math.random() * 800
      y = Math.random() * 600
      if polygon.pointInside(new Point x, y)
        window.paper.circle(x, y, 2)
          .attr({fill: "red"})
          .attr({color: "red"})
          .attr({stroke: "none"})
      else
        window.paper.circle(x, y, 3)
          .attr({fill: "lightgray"})
          .attr({color: "lightgray"})
          .attr({stroke: "none"})

  
  update: ->
    $('#sides').text("" + @sides)
    $('#direction').text("" + @direction)
    @draw()
    @sides += @direction
    @direction = (- @direction) unless MIN_SIDES + 1 <= @sides < MAX_SIDES

$(document).ready ->
  window.paper = Raphael("paper", 800, 600)
  israfel = new Israfel()
  israfel.update()
  setInterval((-> israfel.update()), 100)
  
