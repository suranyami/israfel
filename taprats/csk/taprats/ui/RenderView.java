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
 * RenderView.java
 *
 * A RenderView is a GeoView that delegates its drawing function to
 * a RenderStyle.  The RenderStyle is determined by the currently active
 * style in a RenderPanel.
 */

package csk.taprats.ui;

import csk.taprats.toolkit.*;

public class RenderView
	extends GeoView
{
	private RenderPanel render_panel;

	public RenderView( 
		double left, double top, double width, RenderPanel render_panel )
	{
		super( left, top, width );
		this.render_panel = render_panel;
	}

	public void redraw( GeoGraphics gg )
	{
		render_panel.getStyle().draw( gg );
	}
}
