SPACE_DIGITS = /^[0-9]+ [0-9]+/

describe "Point", ->
  beforeEach ->
    @point = new Point 1, 5
  
  describe "toPath", ->
    it "should have 2 numbers separated by a space", ->
      (expect @point.toPath()).toMatch SPACE_DIGITS

  describe "moveTo", ->
    it "should have an M followed by 2 numbers separated by a space", ->
      (expect @point.moveTo()).toMatch /^M[0-9]+ [0-9]+/

  describe "lineTo", ->
    it "should have an L followed by 2 numbers separated by a space", ->
      (expect @point.lineTo()).toMatch /^L[0-9]+ [0-9]+/

