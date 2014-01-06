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
 * FeatureButton.java
 *
 * A feature button is an (optionally clickable) GeoView that displays
 * a DesignElement (perhaps DesignElementButton would have been a better
 * name).  It draws the underlying feature with the figure overlaid.
 * It includes some optimization for drawing radially symmetric figures
 * without building the complete map.
 *
 * These buttons are meant to function like radio buttons -- they live
 * in groups, and one is active at any given time.  If a button is active,
 * it gets drawn with a red border.
 *
 * This class is also used to show the large DesignElement being edited
 * in the main editing window.  Nice!
 */

package csk.taprats.ui;

import java.util.Enumeration;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.event.*;

import csk.taprats.app.*;
import csk.taprats.geometry.*;
import csk.taprats.toolkit.*;
import csk.taprats.tile.*;

import csk.taprats.general.Signal;

public class FeatureButton
	extends GeoView
{
	private DesignElement 	de;
	private boolean 		selected;

	public Signal			action;

	private boolean			viewport_dirty;

	private static final Color feature_interior = new Color( 240, 240, 255 );
	private static final Color feature_border = new Color( 140, 140, 160 );

	public FeatureButton()
	{
		this( null );
	}

	public FeatureButton( DesignElement de )
	{
		super( -1.25, 1.25, 2.5 );
		this.de = de;
		this.selected = false;
		this.viewport_dirty = true;

		this.action = new Signal();

		addMouseListener( new MouseAdapter() {
			public void mousePressed( MouseEvent me ) 
			{
				if( !isShift( me ) && isButton( me, 1 ) ) {
					action.notify( null );
				}
			}
		} );

		setSink( true );
	}

	public DesignElement getDesignElement()
	{
		return de;
	}
	
	public void setDesignElement( DesignElement de )
	{
		this.de = de;
		designElementChanged();
	}

	public void designElementChanged()
	{
		this.viewport_dirty = true;
		forceRedraw();
	}

	public void resetViewport()
	{
		// Reset the viewport to look at the design element.
		// We can't do this until the Component is mapped and 
		// the design element is set.  So we do it really lazily --
		// set a flag to reset the viewport whenever the current
		// DesignElement changes and recompute the viewport from
		// the paint function when the flag is set.

/*
		if( de == null ) {
			System.err.println( "de == null" );
			return;
		}
		if( fig == null ) {
			System.err.println( "fig == null" );
			return;
		}
*/
		Figure fig = de.getFigure();

		// Get the bounding box of all the figure's vertices and all the
		// feature's vertices.  Then show that region in the viewport.  
		// In other words, scale the view to show the DesignElement.  

		Map map = fig.getMap();
		Point[] pts = de.getFeature().getPoints();

		boolean have_one = false;
		double xmin = 0.0;
		double xmax = 0.0;
		double ymin = 0.0;
		double ymax = 0.0;

		Enumeration e = map.getVertices();
		int pidx = 0;

		while( true ) {
			Point p = null;

			// This is a cheesy way to string together two streams of points.
			if( e.hasMoreElements() ) {
				Vertex vert = (Vertex)( e.nextElement() );
				p = vert.getPosition();
			} else if( pidx < pts.length ) {
				p = pts[ pidx ];
				++pidx;
			} else {
				break;
			}

			double x = p.getX();
			double y = p.getY();

			if( have_one ) {
				xmin = Math.min( xmin, x );
				xmax = Math.max( xmax, x );

				ymin = Math.min( ymin, y );
				ymax = Math.max( ymax, y );
			} else {
				xmin = x;
				xmax = xmin;

				ymin = y;
				ymax = ymin;

				have_one = true;
			}
		}

		if( have_one ) {
			lookAt( new Rect( xmin, ymax, xmax-xmin, ymax-ymin ) );
		}
	}

	public void setSelected( boolean selected )
	{
		if( this.selected != selected ) {
			this.selected = selected;
			forceRedraw();
		}
	}

	/*
	 * We have to override GeoView's zero-arg redraw method to draw
	 * as normal and then add the red border if this button is selected.
	 */

	public boolean redraw()
	{
		if( viewport_dirty ) {
			viewport_dirty = false;
			resetViewport();
		}

		if( !super.redraw() ) {
			return false;
		}

		if( selected ) {
			Graphics g = getBackGraphics();
			
			Dimension d = getSize();
			g.setColor( Color.red );
			g.fillRect( 0, 0, d.width, 2 );
			g.fillRect( 0, 0, 2, d.height );
			g.fillRect( 0, d.height - 2, d.width, 2 );
			g.fillRect( d.width - 2, 0, 2, d.height );
		}

		return true;
	}

	public void redraw( GeoGraphics gg )
	{
		// FIXME -- 
		//
		// We could keep the figure's map cached until the figure changes.
		// On the other hand, it's really fast enough already. 

		// Draw the feature.

		if( de != null ) {
			Point[] pts = de.getFeature().getPoints();

			gg.setColor( feature_interior );
			gg.drawPolygon( pts, true );
			gg.setColor( feature_border );
			gg.drawPolygon( pts, false );

			// Draw the figure
			Figure fig = de.getFigure();
			gg.setColor( Color.black );

			if( fig instanceof RadialFigure ) {
				// Optimize for the case of a RadialFigure.
				RadialFigure figure = (RadialFigure)( fig );
				Map m = figure.buildUnit();
				Transform R = Transform.rotate( 
					Math.PI * 2.0 / (double)figure.getN() );
				Transform T = (Transform)R.clone();

				for( int idx = 0; idx < figure.getN(); ++idx ) {
					for( Enumeration e = m.getEdges(); e.hasMoreElements(); ) {
						Edge edge = (Edge)( e.nextElement() );
						Point v1 = T.apply( edge.getV1().getPosition() );
						Point v2 = T.apply( edge.getV2().getPosition() );
						gg.drawLine( v1, v2 );
					}
					T.composeD( R );
				}
			} else {
				Map map = fig.getMap();

				for( Enumeration e = map.getEdges(); e.hasMoreElements(); ) {
					Edge edge = (Edge)( e.nextElement() );
					Point p1 = edge.getV1().getPosition();
					Point p2 = edge.getV2().getPosition();

					gg.drawLine( p1, p2 );
				}
			}
		}
	}
}
