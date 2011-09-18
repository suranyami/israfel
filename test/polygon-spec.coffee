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
  
  describe "with a point inside", ->
    it "has pointInside() true", ->
      (expect @polygon.pointInside(new Point 1.9, 1.9)).toEqual true
  
  describe "with a point outside", ->
    it "has pointInside() false", ->
      (expect @polygon.pointInside(new Point 3, 3)).toEqual false
      (expect @polygon.pointInside(new Point 1, 1)).toEqual false