# The UI that provides an entry point to the entire system.
# a UI that allows the user to navigate between all the
# tilings defined
# in the system and choose one to work with.

class TilingSelector
    @current
    @openButton
    @prevButton
    @nextButton

    constructor: (@tilings = Tiling.getTilings()) ->
      @card = @tilings.first
      @current @tilings.first

      $('ul#tilings li a').click(->
        index = $(this).data('tiling_index')
        @current = @tilings[index]
        @current.render($('#current_tiling'))
      )

    display_tilings: ->
      for tiling in @tilings
        alert()


     #      public void setNext()

     #        ++current
     #        if( current >= tilings.length )
     #          current = 0

     #        card.setTiling( tilings[ current ] )


     #      public void openCurrent()

     #        DesignEditor editor = new DesignEditor( tilings[ current ] )
     #        Frame f = new Frame( "Design editor: " + tilings[ current ].getName() )
     #        editor.setParentFrame( f )
     #        f.setLayout( new BorderLayout() )
     #        f.add( "Center", editor )
     #        f.addWindowListener( new WindowCloser( f, false ) )
     #        f.pack()
     #        f.show()


     #      public void addNotify()

     #        super.addNotify()

     #        Font current = getFont()
     #        Font f = new Font( "SansSerif", Font.BOLD, current.getSize() + 2 )
     #        prev.setFont( f )
     #        next.setFont( f )


     #      public static final void main( String[] args )

     #        csk.taprats.toolkit.Util.openTestFrame( new TilingSelector() )
     #
