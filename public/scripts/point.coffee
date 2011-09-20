window.Point = class Point
  constructor: (@x = 0, @y = 0) ->
    
  toString: -> "(#{@x}, #{@y})"
  toPath: -> "#{@x} #{@y}"
  moveTo: -> "M#{@toPath()}"
  lineTo: -> "L#{@toPath()}"
