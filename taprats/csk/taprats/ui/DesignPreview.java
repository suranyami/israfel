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
 * DesignPreview.java
 *
 * Boy, am I glad I thought of this class.  The design preview uses the
 * same FillRegion algorithm as TilingViewer (used by TilingCard, for 
 * instance), to draw instead the _figures_ as they would appear in the 
 * final design.  Because we're not constructing the map, just drawing
 * lines, previewing the design is really fast.  Plus, you can use the
 * viewport of the preview window to indicate the region to fill for the
 * final design -- I was searching for a way to let the user express 
 * this piece of information.
 *
 * This class just turns the translational unit into a collection of line
 * segments and then draws them repeatedly to fill the window.
 */

package csk.taprats.ui;

import java.util.Vector;
import java.util.Enumeration;

import csk.taprats.toolkit.*;
import csk.taprats.geometry.*;
import csk.taprats.app.*;
import csk.taprats.tile.*;

class line_segment
{
	double x1;
	double y1;
	double x2;
	double y2;

	line_segment( double x1, double y1, double x2, double y2 )
	{
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
}

public class DesignPreview
	extends GeoView
{
	Point			t1;
	Point 			t2;

	Vector			lines;
	DrawIt			callback;
	FillRegion		fill;

	public DesignPreview( Prototype proto )
	{
		super( -4.9, 4.9, 9.8 );

		this.t1 = proto.getTiling().getTrans1();
		this.t2 = proto.getTiling().getTrans2();

		this.fill = new FillRegion();
		this.callback = new DrawIt();

		this.lines = new Vector();

		Tiling tiling = proto.getTiling();
		for( int idx = 0; idx < tiling.numFeatures(); ++idx ) {
			PlacedFeature pf = tiling.getFeature( idx );
			Feature feature = pf.getFeature();
			Transform T = pf.getTransform();
			Figure fig = proto.getFigure( feature );
			Map map = fig.getMap();

			for( Enumeration e = map.getEdges(); e.hasMoreElements(); ) {
				Edge edge = (Edge)( e.nextElement() );

				Point v1 = T.apply( edge.getV1().getPosition() );
				Point v2 = T.apply( edge.getV2().getPosition() );

				lines.addElement( new line_segment(
					v1.getX(), v1.getY(), v2.getX(), v2.getY() ) );
			}
		}
	}

	class DrawIt
		implements UnitCallback
	{
		GeoGraphics gg;

		DrawIt()
		{
			gg = null;
		}

		public void receive( int a1, int a2 )
		{
			Transform T = Transform.translate( 
				t1.scale( (double)a1 ).add( t2.scale( (double)a2 ) ) );

			gg.pushAndCompose( T );

			for( int idx = 0; idx < lines.size(); ++idx ) {
				line_segment ls = (line_segment)( lines.elementAt( idx ) );
				gg.drawLine( ls.x1, ls.y1, ls.x2, ls.y2 );
			}

			gg.pop();
		}
	}

	public void redraw( GeoGraphics gg )
	{
		callback.gg = gg;
		Polygon bds = getBoundary();
		Polygon nbds = new Polygon( 4 );
		Point c = bds.getVertex( 0 ).convexSum( bds.getVertex( 2 ), 0.5 );

		// Again, inflate the draw region a bit to help make it
		// cover the window.
		nbds.addVertex( c.convexSum( bds.getVertex( 0 ), 1.5 ) );
		nbds.addVertex( c.convexSum( bds.getVertex( 1 ), 1.5 ) );
		nbds.addVertex( c.convexSum( bds.getVertex( 2 ), 1.5 ) );
		nbds.addVertex( c.convexSum( bds.getVertex( 3 ), 1.5 ) );

		fill.fill( nbds, t1, t2, callback );
	}
}
