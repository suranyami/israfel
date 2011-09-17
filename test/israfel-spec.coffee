describe "Polygon", ->
  beforeEach ->
    @polygon = new Polygon 'Sirloin Steak $18.99 mains'
  
  it "extract title", ->
    (expect @dish.title).toEqual 'Sirloin Steak'

  it "extract price", ->
    (expect @dish.price).toEqual '$18.99'

