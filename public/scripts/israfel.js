(function() {
  var Israfel;
  window.Israfel = Israfel = (function() {
    var MAX_SIDES, MIN_SIDES;
    MAX_SIDES = 10;
    MIN_SIDES = 3;
    function Israfel() {
      this.sides = 3;
      this.direction = 1;
    }
    Israfel.prototype.draw = function() {
      var count, polygon, polygon_drawing, x, y, _results;
      window.paper.clear();
      polygon = new RegularPolygon(400, 300, this.sides, 200);
      polygon_drawing = window.paper.path(polygon.toPath());
      polygon_drawing.attr({
        color: "black"
      });
      polygon_drawing.attr({
        fill: "red"
      });
      _results = [];
      for (count = 1; count <= 500; count++) {
        x = Math.random() * 800;
        y = Math.random() * 600;
        _results.push(polygon.pointInside(new Point(x, y)) ? window.paper.circle(x, y, 5).attr({
          fill: "green"
        }).attr({
          color: "green"
        }).attr({
          stroke: "none"
        }) : window.paper.circle(x, y, 2).attr({
          fill: "gray"
        }).attr({
          color: "gray"
        }).attr({
          stroke: "none"
        }));
      }
      return _results;
    };
    Israfel.prototype.update = function() {
      var _ref;
      $('#sides').text("" + this.sides);
      $('#direction').text("" + this.direction);
      this.draw();
      if (!((MIN_SIDES <= (_ref = this.sides) && _ref < MAX_SIDES))) {
        this.direction = -this.direction;
      }
      return this.sides += this.direction;
    };
    return Israfel;
  })();
  $(document).ready(function() {
    var israfel;
    window.paper = Raphael("paper", 800, 600);
    israfel = new Israfel();
    israfel.update();
    return setInterval((function() {
      return israfel.update();
    }), 1000);
  });
}).call(this);
