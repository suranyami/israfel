package csk.taprats;

import csk.taprats.ui.TilingSelector;
import csk.taprats.toolkit.WindowCloser;

import java.awt.*;

public class Program
{
	public static final void main( String[] args )
	{
		TilingSelector ts = new TilingSelector();
		Frame f = new Frame( "Taprats 0.3" );
		f.addWindowListener( new WindowCloser( f, true ) );
		f.setLayout( new BorderLayout() );
		f.add( "Center", ts );
		f.pack();
		f.show();
	}
}
