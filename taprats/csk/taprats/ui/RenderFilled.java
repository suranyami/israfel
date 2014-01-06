/*
 * taprats -- an interactive design tool for computer-generated 
 *            Islamic patterns.
 *
 * Copyright (C) 2000 Craig S. Kaplan, all rights reserved
 *
 * email: csk@cs.washington.edu
 * www:   http://www.cs.washington.edu/homes/csk/taprats/
 *
 * You may not copy, redistribute or reuse this source code at
 * this time.  It is likely that in the future I will make the
 * source more freely available.  In the meantime, please be
 * patient, and contact me if you have questions about the use
 * of this source code or the compiled applet.
 *
 */

/*
 * RenderFilled.java
 *
 * A rendering style that converts the map to a collection of 
 * polygonal faces.  The faces are divided into two groups according to
 * a two-colouring of the map (which is always possible for the
 * kinds of Islamic designs we're building).
 *
 * The code to build the faces from the map is contained in 
 * csk.taprats.geometry.Faces.
 */

package csk.taprats.ui;

import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Stack;
import java.awt.*;
import java.awt.event.*;

import csk.taprats.general.Signal;
import csk.taprats.toolkit.GeoGraphics;
import csk.taprats.geometry.*;

import csk.taprats.geometry.Point;
import csk.taprats.geometry.Polygon;
import csk.taprats.geometry.Faces;

public class RenderFilled
	extends RenderStyle
{
	Vector		white;
	Vector		black;
	Map			map;

	public RenderFilled()
	{
		super();
		this.map = null;
	}

	public void setMap( Map map )
	{
		this.black = null;
		this.white = null;
		this.map = map;

		if( this.map != null ) {
			white = new Vector();
			black = new Vector();

			// Get the faces.
			Faces.extractFaces( this.map, this.black, this.white );
		}
	}

	private static Color light_interior = new Color( 240, 240, 255 );
	private static Color dark_interior = new Color( 90, 90, 120 );

	public void draw( GeoGraphics gg )
	{
		if( map != null ) {
			gg.setColor( dark_interior );
			for( Enumeration e = white.elements(); e.hasMoreElements(); ) {
				Point[] pts = (Point[])( e.nextElement() );
				gg.drawPolygon( pts, true );
			}

			gg.setColor( light_interior );
			for( Enumeration e = black.elements(); e.hasMoreElements(); ) {
				Point[] pts = (Point[])( e.nextElement() );
				gg.drawPolygon( pts, true );
			}
		}
	}
}
