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
 * RenderSketch.java
 *
 * One day, it occured to me that I might be able to get a sketchy
 * hand-drawn effect by drawing an edge as a set of line segments whose
 * endpoints are jittered relative to the original edge.  And it worked!
 * Also, since the map is fixed, we can just reset the random seed every
 * time we draw the map to get coherence.  Note that coherence might not
 * be a good thing -- some animations work well precisely because the 
 * random lines that make up some object change from frame to frame (c.f.
 * Bill Plympton).  It's just a design decision, and easy to reverse
 * (or provide a UI for).
 *
 * I haven't tried it yet, but I doubt this looks any good as postscript.
 * the resolution is too high and it would probably look like, well, 
 * a bunch of lines.
 */

package csk.taprats.ui;

import java.util.Enumeration;
import java.awt.Color;

import csk.taprats.general.Signal;
import csk.taprats.toolkit.GeoGraphics;
import csk.taprats.geometry.*;

public class RenderSketch
	extends RenderPlain
{
	protected java.util.Random rand;

	public RenderSketch()
	{
		super();

		rand = new java.util.Random();
	}

	public void draw( GeoGraphics gg )
	{
		gg.setColor( new Color( 0.1f, 0.1f, 0.1f ) );
		Transform T = gg.getTransform();
		java.awt.Graphics g = gg.getDirectGraphics();

		rand.setSeed( 279401L );

		if( pts != null ) {
			for( int idx = 0; idx < pts.length; idx += 4 ) {
				int x1;
				int y1;
				int x2;
				int y2;

				x1 = (int)( T.applyX( pts[ idx ], pts[ idx + 1 ] ) );
				y1 = (int)( T.applyY( pts[ idx ], pts[ idx + 1 ] ) );
				x2 = (int)( T.applyX( pts[ idx + 2 ], pts[ idx + 3 ] ) );
				y2 = (int)( T.applyY( pts[ idx + 2 ], pts[ idx + 3 ] ) );

				for( int c = 0; c < 8; ++c ) {
					g.drawLine(
						x1 + (int)(rand.nextDouble() * 9) - 4,
						y1 + (int)(rand.nextDouble() * 9) - 4,
						x2 + (int)(rand.nextDouble() * 9) - 4,
						y2 + (int)(rand.nextDouble() * 9) - 4 );
				}
			}
		}
	}
}
