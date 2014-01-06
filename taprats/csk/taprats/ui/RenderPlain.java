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
 * RenderPlain.java
 *
 * The trivial rendering style.  Render the map as a collection of
 * line segments.  Not very useful considering that DesignPreview does
 * this better.  But there needs to be a default style for the RenderView.
 * Who knows -- maybe some diagnostic information could be added later.
 */

package csk.taprats.ui;

import java.util.Enumeration;
import java.awt.Color;

import csk.taprats.general.Signal;
import csk.taprats.toolkit.GeoGraphics;
import csk.taprats.geometry.*;

public class RenderPlain
	extends RenderStyle
{
	protected double[] pts;

	public RenderPlain()
	{
		super();
		// setSize( 10, 10 );
		pts = null;
	}

	public void setMap( Map map )
	{
		pts = null;

		if( map != null ) {
			pts = new double[ 4 * map.numEdges() ];
			int index = 0;

			for( Enumeration e = map.getEdges(); e.hasMoreElements(); ) {
				Edge edge = (Edge)( e.nextElement() );
				Point v1 = edge.getV1().getPosition();
				Point v2 = edge.getV2().getPosition();

				pts[ index ] = v1.getX();
				pts[ index + 1 ] = v1.getY();
				pts[ index + 2 ] = v2.getX();
				pts[ index + 3 ] = v2.getY();

				index += 4;
			}
		}
	}

	public void draw( GeoGraphics gg )
	{
		gg.setColor( Color.black );
		if( pts != null ) {
			for( int idx = 0; idx < pts.length; idx += 4 ) {
				gg.drawLine( pts[idx], pts[idx+1], pts[idx+2], pts[idx+3] );
			}
		}
	}
}
