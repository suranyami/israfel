window.Point = class Point
  constructor: (@x = 0, @y = 0) ->
    
  toString: -> "(#{@x}, #{@y})"
  toPath: -> "#{@x} #{@y}"
  moveTo: -> "M#{@toPath()}"
  lineTo: -> "L#{@toPath()}"

  randomPoint: (range_x = 800, range_y = 600)->
    @x = Math.random() * range_x
    @y = Math.random() * range_y