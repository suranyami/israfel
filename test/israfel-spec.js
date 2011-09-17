(function() {
  describe("Polygon", function() {
    beforeEach(function() {
      return this.polygon = new Polygon('Sirloin Steak $18.99 mains');
    });
    it("extract title", function() {
      return (expect(this.dish.title)).toEqual('Sirloin Steak');
    });
    return it("extract price", function() {
      return (expect(this.dish.price)).toEqual('$18.99');
    });
  });
}).call(this);
