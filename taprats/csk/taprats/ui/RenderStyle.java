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
 * A RenderStyle is a panel that encapsulates a set of controls for 
 * drawing a map with some interesting style.  It also contains the
 * code for the actual drawing.  In the long run, I would almost certainly
 * break the actual draw code out from the UI code, the way 
 * csk.taprats.app.Star is different from csk.taprats.ui.StarEditor.
 * For now, I'll leave it this way.
 */

package csk.taprats.ui;

import java.awt.Panel;

import csk.taprats.general.Signal;
import csk.taprats.toolkit.GeoGraphics;
import csk.taprats.geometry.Map;

public class RenderStyle
	extends Panel
{
	/* 
	 * When a slider is twiddled, the subclass fires the changed signal
	 * so the viewing area can redraw.
	 */
	public Signal 	style_changed;

	protected RenderStyle()
	{
		super();
		this.style_changed = new Signal();
	}

	public void draw( GeoGraphics gg )
	{}

	public void setMap( Map map )
	{}
}
