class Point
  constructor: (x, y) -> @x = x; @y = y
  toString: -> "(#{@x}, #{@y})"
  toPath: -> "#{@x} #{@y}"
  moveTo: -> "M#{@toPath()}"
  lineTo: -> "L#{@toPath()}"
