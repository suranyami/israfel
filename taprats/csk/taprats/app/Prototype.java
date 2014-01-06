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
 * Prototype.java
 *
 * The complete information needed to build a pattern: the tiling and
 * a mapping from features to figures.  Prototype knows how to turn
 * this information into a finished design, returned as a Map.
 */

package csk.taprats.app;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;

import csk.taprats.tile.*;
import csk.taprats.geometry.*;

public class Prototype
{
	private Tiling 		tiling;
	private Hashtable	figures;

	public Prototype( Tiling tiling )
	{
		this.tiling = tiling;
		this.figures = new Hashtable();
	}

	public Tiling getTiling()
	{
		return tiling;
	}

	public void addElement( DesignElement element )
	{
		figures.put( element.getFeature(), element.getFigure() );
	}

	public Enumeration getFeatures()
	{
		return figures.keys();
	}

	public Figure getFigure( Feature feature )
	{
		Figure ret = (Figure)( figures.get( feature ) );
		return ret;
	}

	class ProtoCallback
		implements UnitCallback
	{
		Vector 		locations;

		ProtoCallback( Vector locations )
		{
			this.locations = locations;
		}

		public void receive( int t1, int t2 )
		{
			Transform T = Transform.translate( 
				tiling.getTrans1().scale( (double)t1 ).add( 
					tiling.getTrans2().scale( (double)t2 ) ) );
			locations.addElement( T );
		}
	}

	public Map construct( Polygon region )
	{
		Vector locations = new Vector();
		ProtoCallback pc = new ProtoCallback( locations );

		// Use FillRegion to get a list of translations for this tiling.
		FillRegion fr = new FillRegion();
		fr.fill( region, tiling.getTrans1(), tiling.getTrans2(), pc );

		Map ret = new Map();

		// Now, for each different feature, build a submap corresponding
		// to all translations of that feature.

		for( Enumeration e = figures.keys(); e.hasMoreElements(); ) {
			Feature feature = (Feature)( e.nextElement() );
			Figure figure = (Figure)( figures.get( feature ) );
			Map figmap = figure.getMap();
			Vector subT = new Vector();

			for( int idx = 0; idx < tiling.numFeatures(); ++idx ) {
				PlacedFeature pf = tiling.getFeature( idx );
				Feature f = pf.getFeature();
				if( f.equals( feature ) ) {
					subT.addElement( pf.getTransform() );
				}
			}

/*
			if( !figmap.verify() ) {
				System.err.println( "figmap didn't verify!" );
				figmap.dump( System.err );
				csk.taprats.ui.MapViewer mv = new csk.taprats.ui.MapViewer( 
					-5.0, 5.0, 10.0, figmap, false );
				csk.taprats.toolkit.Util.openTestFrame( mv, false );
			}
*/

			// Within a single translational unit, assemble the different
			// transformed figures corresponding to the given feature into
			// a map.
			Map transmap = new Map();
			transmap.mergeSimpleMany( figmap, subT );

			// Now put all the translations together into a single map for
			// this feature.
			Map featuremap = new Map();
			featuremap.mergeSimpleMany( transmap, locations );

			// And do a slow merge to add this map to the finished design.
			ret.mergeMap( featuremap );
		}

		return ret;
	}
}
