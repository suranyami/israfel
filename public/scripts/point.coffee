window.Point = class Point
  constructor: (@x, @y) ->
    
  toString: -> "(#{@x}, #{@y})"
  toPath: -> "#{@x} #{@y}"
  moveTo: -> "M#{@toPath()}"
  lineTo: -> "L#{@toPath()}"
