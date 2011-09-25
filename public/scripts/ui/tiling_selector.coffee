# The UI that provides an entry point to the entire system.  TilingSelector
# it a UI that allows the user to navigate between all the tilings defined
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
        

      @open = new Button( "Open") 
      @open.addActionListener( new ActionListener()  
        public void actionPerformed( ActionEvent ae ) 
         
          openCurrent() 
         
          )


            layout.setConstraints( card, gbc ) 
            add( card ) 

            gbc.gridy = 1 
            gbc.gridwidth = 1 
            gbc.fill = GridBagConstraints.BOTH 
            gbc.weighty = 1 
            gbc.insets = new Insets( 3, 3, 3, 3 ) 

            gbc.gridx = 0 
            gbc.weightx = 1 
            layout.setConstraints( prev, gbc ) 
            add( prev ) 

            gbc.gridx = 1 
            gbc.weightx = 2 
            layout.setConstraints( open, gbc ) 
            add( open ) 

            gbc.gridx = 2 
            gbc.weightx = 1 
            layout.setConstraints( next, gbc ) 
            add( next ) 
           

          public void setPrevious()
           
            --current 
            if( current < 0 )  
              current = tilings.length - 1 
             
            card.setTiling( tilings[ current ] ) 
           

          public void setNext()
           
            ++current 
            if( current >= tilings.length )  
              current = 0 
             
            card.setTiling( tilings[ current ] ) 
           

          public void openCurrent()
           
            DesignEditor editor = new DesignEditor( tilings[ current ] ) 
            Frame f = new Frame( "Design editor: " + tilings[ current ].getName() ) 
            editor.setParentFrame( f ) 
            f.setLayout( new BorderLayout() ) 
            f.add( "Center", editor ) 
            f.addWindowListener( new WindowCloser( f, false ) ) 
            f.pack() 
            f.show() 
           

          public void addNotify()
           
            super.addNotify() 

            Font current = getFont() 
            Font f = new Font( "SansSerif", Font.BOLD, current.getSize() + 2 ) 
            prev.setFont( f ) 
            next.setFont( f ) 
           

          public static final void main( String[] args )
           
            csk.taprats.toolkit.Util.openTestFrame( new TilingSelector() ) 
           

     