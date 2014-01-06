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
 * ConnectFigure.java
 *
 * A ConnectFigure is a special kind of ScaleFigure.  It knows how to
 * compute just the right scale factor so that scaled out edges will join
 * up to create a fancier figure.  This is how we turn Rosettes into
 * Extended Rosettes.  To make sure that the resulting figure still lines
 * up with the feature that will eventually contain it, we need to do
 * some fancy reshuffling of the basic unit to move the apex to (1,0).
 */

package csk.taprats.app;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

import csk.taprats.geometry.*;
import csk.taprats.general.Loose;

public class ConnectFigure
	extends ScaleFigure
	implements Cloneable
{
	public ConnectFigure( RadialFigure child )
	{
		super( child, computeConnectScale( child ) );
	}

	public Object clone()
	{
		return new ConnectFigure( (RadialFigure)( child.clone() ) );
	}

	public RadialFigure getChild()
	{
		return child;
	}

	public void childChanged()
	{
		setS( computeConnectScale( child ) );
	}

	private static double computeConnectScale( RadialFigure child )
	{
		// Find the vertex at (1,0), extend its incoming edges, intersect
		// with rotations of same, and use the location of the intersection
		// to compute a scale factor.

		Map cunit = child.buildUnit();

		Point tip_pos = new Point( 1.0, 0.0 );
		Transform Tr = Transform.rotate( 2.0 * Math.PI / (double)child.getN() );

		// Find the tip, i.e. the vertex at (1,0)
		for( Enumeration e = cunit.getVertices(); e.hasMoreElements(); ) {
			Vertex vert = (Vertex)( e.nextElement() );
			Point pos = vert.getPosition();
			if( Loose.equals( pos, tip_pos ) ) {
				for( Enumeration en = vert.neighbours(); 
						en.hasMoreElements(); ) {
					Edge edge = (Edge)( en.nextElement() );
					Vertex ov = edge.getOther( vert );
					if( ov.getPosition().getY() < 0.0 ) {
						Point bpos = ov.getPosition();
						Point seg_end = tip_pos.add( tip_pos.subtract( 
							bpos ).normalize().scale( 100.0 ) );
						Point neg_seg = new Point(
							seg_end.getX(), -seg_end.getY() );

						Point ra = Tr.apply( tip_pos );
						Point rb = Tr.apply( neg_seg );

						Point isect = Intersect.getIntersection(
							tip_pos, seg_end, ra, rb );
						if( isect == null ) {
							return 1.0;
						} else {
							double alpha = Math.cos( 
								Math.PI / (double)child.getN() ) / isect.mag();
							return alpha;
						}
					}
				}
			}
		}

		return 1.0;
	}

	// Assume that the result from scaling is a figure with no apex at
	// the boundary of the enclosing n-gon, but rather edges that leave for
	// different n-gon edges.  Chop the basic unit in half and reassemble
	// the bottom half underneath the top half to solve this problem.

	private void rotateHalf( Map cunit )
	{
		Hashtable movers = new Hashtable();

		Transform Tp = Transform.rotate( -2.0 * Math.PI * don );

		for( Enumeration e = cunit.getVertices(); e.hasMoreElements(); ) {
			Vertex vert = (Vertex)( e.nextElement() );
			if( (vert.getPosition().getY() + Loose.TOL) > 0.0 ) {
				movers.put( vert, vert );
			}
		}

		for( Enumeration e = movers.keys(); e.hasMoreElements(); ) {
			Vertex vert = (Vertex)( e.nextElement() );
			Vertex nvert = cunit.insertVertex( 
				Tp.apply( vert.getPosition() ) );
			movers.put( vert, nvert );
		}

		Vector eadds = new Vector();

		for( Enumeration e = cunit.getEdges(); e.hasMoreElements(); ) {
			Edge edge = (Edge)( e.nextElement() );
			if( movers.containsKey( edge.getV1() ) &&
					movers.containsKey( edge.getV2() ) ) {
				eadds.addElement( edge );
			}
		}

		for( Enumeration e = eadds.elements(); e.hasMoreElements(); ) {
			Edge edge = (Edge)( e.nextElement() );
			cunit.insertEdge(
				(Vertex)( movers.get( edge.getV1() ) ),
				(Vertex)( movers.get( edge.getV2() ) ) );
		}

		for( Enumeration e = movers.keys(); e.hasMoreElements(); ) {
			Vertex vert = (Vertex)( e.nextElement() );
			if( vert.getPosition().getY() > Loose.TOL ) {
				cunit.removeVertex( vert );
			}
		}
	}

	private void scaleToUnit( Map cunit )
	{
		Vertex vmax = null;
		double xmax = 0.0;

		for( Enumeration e = cunit.getVertices(); e.hasMoreElements(); ) {
			Vertex vert = (Vertex)( e.nextElement() );
			if( vmax == null ) {
				vmax = vert;
				xmax = vert.getPosition().getX();
			} else {
				double x = vert.getPosition().getX();
				if( x > xmax ) {
					xmax = x;
					vmax = vert;
				}
			}
		}
		
		if( vmax != null ) {
			cunit.scale( 1.0 / xmax );
		}
	}

	public Map buildUnit()
	{
		Map cunit = super.buildUnit();

		// We want the tip of the new figure to still be at (1,0).
		// To accomplish this, move the top half of the figure around
		// to lie under the bottom half.  This rebuilds the tip
		// at the correct location.
		rotateHalf( cunit );

		if( !Loose.equals( s, 1.0 ) ) {
			cunit.transformMap( Transform.rotate( Math.PI * don ) );
		}

		scaleToUnit( cunit );

		return cunit;
	}

	public static void main( String[] args )
	{
		int n = Integer.parseInt( args[0] );
		double q = (new Double(args[1])).doubleValue();
		int s = Integer.parseInt( args[2] );
		int times = Integer.parseInt( args[3] );

		Rosette rosette = new Rosette( n, q, s );
		RadialFigure rf = rosette;
		for( int idx = 0; idx < times; ++idx ) {
			rf = new ConnectFigure( rf );
		}
		Map m = rf.getMap();
		m.verify();

		csk.taprats.ui.MapViewer mv = 
			new csk.taprats.ui.MapViewer( -1.5, 1.5, 3.0, m, true );
		mv.setSize( 500, 500 );
		csk.taprats.toolkit.Util.openTestFrame( mv );
	}
}
