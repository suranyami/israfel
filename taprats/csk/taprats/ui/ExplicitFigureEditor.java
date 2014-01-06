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
 * ExplicitFigureEditor.java
 *
 * The editing controls for explicit figures.  A simple class, because
 * (right now) explicit figures don't have any editing controls.  All
 * you can do is ask the figure to be inferred from the rest of the 
 * Prototype.  So all we need is one button.
 * 
 * If I have time (I've got about 36 hours until the deadline), this 
 * is the place to expend lots of effort.  Add the ability to edit
 * the explicit map directly by hand, beginning with a vertex in the
 * centre of every edge of the feature.
 */

package csk.taprats.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;

import csk.taprats.app.*;
import csk.taprats.geometry.Map;

public class ExplicitFigureEditor
	extends FigureEditor
{
	ExplicitFigure 		figure;

	Button				infer;

	public ExplicitFigureEditor( DesignEditor editor )
	{
		super( editor );

		infer = new Button( "Infer" );
		setLayout( new FlowLayout() );
		add( infer );

		infer.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ae )
			{
				figure = new ExplicitFigure( 
					ExplicitFigureEditor.this.editor.getCurrentInferredMap() );
				if( figure == null ) {
					figure = new ExplicitFigure( new Map() );
				}

				changed();
			}
		} );
	}

	public Figure getFigure()
	{
		return figure;
	}

	public void resetWithFigure( Figure figure )
	{
		this.figure = (ExplicitFigure)( figure );
	}
}
