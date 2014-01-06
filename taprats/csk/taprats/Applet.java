package csk.taprats;

import csk.taprats.ui.TilingSelector;
import csk.taprats.toolkit.WindowCloser;

import java.awt.*;
import java.applet.*;

public class Applet
	extends java.applet.Applet
{
	public void init()
	{
		TilingSelector ts = new TilingSelector();
		setLayout( new BorderLayout() );
		add( "Center", ts );
	}
}
