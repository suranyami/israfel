(function() {
  require('polygon');
  $(document).ready(function() {
    var canvas, triangle, triangle_path;
    canvas = Raphael("paper", 800, 600);
    triangle_path = new EquilateralTriangle(new Point(100, 100), 50).toPath();
    triangle = canvas.path(triangle_path);
    return triangle.animate({
      rotation: 180
    }, 2000);
  });
}).call(this);
