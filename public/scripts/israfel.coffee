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
    polygon_drawing.attr({fill: "red"})

    for count in [1..500]
      x = Math.random() * 800
      y = Math.random() * 600
      if polygon.pointInside(new Point x, y)
        window.paper.circle(x, y, 5)
          .attr({fill: "green"})
          .attr({color: "green"})
          .attr({stroke: "none"})
      else
        window.paper.circle(x, y, 2)
          .attr({fill: "gray"})
          .attr({color: "gray"})
          .attr({stroke: "none"})

  
  update: ->
    $('#sides').text("" + @sides)
    $('#direction').text("" + @direction)
    @draw()
    @direction = (- @direction) unless MIN_SIDES <= @sides < MAX_SIDES
    @sides += @direction

$(document).ready ->
  window.paper = Raphael("paper", 800, 600)
  israfel = new Israfel()
  israfel.update()
  setInterval((-> israfel.update()), 100)
  
