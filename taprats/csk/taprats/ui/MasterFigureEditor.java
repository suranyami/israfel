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
 * MasterFigureEditor.java
 *
 * The top-level figure editor that understands the complete range of
 * figure editors available in the applet and branches out to the right
 * kind of editor as the DesignElement being edited is changed.
 *
 * Right now, MasterFigureEditor stacks a RadialFigureEditor and an
 * ExplicitFigureEditor in a CardLayout.
 *
 */

package csk.taprats.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;

import csk.taprats.app.*;

public class MasterFigureEditor
	extends FigureEditor
{
	private RadialFigureEditor		radial;
	private ExplicitFigureEditor	expl;
	private FigureEditor			current;

	private Panel 			card_panel;
	private CardLayout 		cards;

	public MasterFigureEditor( DesignEditor editor )
	{
		super( editor );

		radial = new RadialFigureEditor( editor );
		expl = new ExplicitFigureEditor( editor );
		current = null;

		// Propagate the changed message up the hierarchy.
		Observer observer = (new Observer() {
			public void update( Observable obs, Object arg )
			{
				changed();
			}
		} );

		radial.figure_changed.addObserver( observer );
		expl.figure_changed.addObserver( observer );

		card_panel = new Panel();
		this.cards = new CardLayout();
		card_panel.setLayout( cards );

		card_panel.add( radial, "radial" );
		card_panel.add( expl, "explicit" );
		card_panel.add( new Panel(), "none" );

		cards.show( card_panel, "none" );

		setLayout( new BorderLayout() );
		add( "Center", card_panel );
	}

	public Figure getFigure()
	{
		if( current != null ) {
			return current.getFigure();
		} else {
			return null;
		}
	}

	public void resetWithFigure( Figure figure )
	{
		// Note that you'll encounter some explicit type checking here
		// and in RadialFigureEditor.  Yes, I know it's a taboo of
		// OO programming in a clean language like Java.
		//
		// The reason I do it this way is one of library encapsulation.
		// To get rid of the instanceof checks, I would need to embed UI 
		// knowledge into the app classes such as csk.taprats.app.Star --
		// something like
		//		class Figure {
		// 			...
		//			abstract public Panel getEditor();
		//			...
		//		}
		//
		// I'd rather keep those classes pure (i.e., free of any particular
		// UI) and use explicit switching here.  It makes sense -- the
		// UI of a program will always be the part that expresses most
		// clearly the limitations of the application.  I'm happy to 
		// apply the limits at the user side of the code.
		//
		// Now, if we had binary dispatch (multimethods), life would be
		// much better.  But whatever.

		if( figure instanceof RadialFigure ) {
			cards.show( card_panel, "radial" );
			radial.resetWithFigure( figure );
			current = radial;
		} else {
			cards.show( card_panel, "explicit" );
			expl.resetWithFigure( figure );
			current = expl;
		}
	}
}
