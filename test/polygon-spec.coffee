describe "Polygon", ->
  beforeEach ->
    @polygon = new Polygon [
      (new Point 1, 2),
      (new Point 2, 2),
      (new Point 2, 1)
    ]
  
  describe "toPath", ->
    it "should be of the form M1 2 L2 2 L2 1Z", ->
      (expect @polygon.toPath()).toMatch /^M[0-9]+ [0-9]+ L[0-9]+ [0-9]+ L[0-9]+ [0-9]+z/

  # 
  # describe "with a point inside", ->
  #   it "has pointInside() true", ->
  #     for x in [1.9..1.5] by 0.1
  #       (expect @polygon.pointInside(new Point x, x)).toEqual true
  # 
  describe "with a point outside", ->
    it "has pointInside() false", ->
      for x in [1, 3, 10]
        console.log x
        console.log @polygon.pointInside(new Point x, x)
        (expect @polygon.pointInside(new Point x, x)).toEqual false
