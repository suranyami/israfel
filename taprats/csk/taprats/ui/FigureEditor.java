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
 * FigureEditor.java
 *
 * An abstract class for containing the controls related to the editing
 * of one kind of figure.  A complex hierarchy of FigureEditors gets built
 * up to become the changeable controls for editing figures in DesignEditor.
 */

package csk.taprats.ui;

import java.awt.Panel;

import csk.taprats.general.Signal;
import csk.taprats.app.Figure;

public abstract class FigureEditor
	extends Panel
{
	protected DesignEditor	editor;

	public Signal figure_changed;

	public FigureEditor( DesignEditor editor )
	{
		super();
		this.editor = editor;
		figure_changed = new Signal();
	}

	/*
	 * Get the figure corresponding to the current settings.
	 */
	public abstract Figure getFigure();
	/*
	 * Reset the controls to correspond to the passed in figure.
	 */
	public abstract void resetWithFigure( Figure figure );

	protected void changed()
	{
		figure_changed.notify( null );
	}
}
