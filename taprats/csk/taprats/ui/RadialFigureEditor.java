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
 * RadialFigureEditor.java
 *
 * A figure editor that brings together the editors for the different 
 * kinds of known radial figures, and a drop-down chooser for what kind
 * to use.
 */

package csk.taprats.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;

import csk.taprats.app.*;

public class RadialFigureEditor
	extends FigureEditor
{
	private Choice 			choice;

	private StarEditor		star_edit;
	private RosetteEditor	rosette_edit;
	private RosetteEditor	ex_rosette_edit;

	private Panel 			card_panel;
	private CardLayout 		cards;

	public RadialFigureEditor( DesignEditor editor )
	{
		super( editor );

		this.choice = new Choice();
		choice.add( "Star" );
		choice.add( "Rosette" );
		choice.add( "Extended" );

		star_edit = new StarEditor( editor );
		rosette_edit = new RosetteEditor( editor );
		ex_rosette_edit = new RosetteEditor( editor );

		Observer observer = (new Observer() {
			public void update( Observable obs, Object arg )
			{
				changed();
			}
		} );

		star_edit.figure_changed.addObserver( observer );
		rosette_edit.figure_changed.addObserver( observer );
		ex_rosette_edit.figure_changed.addObserver( observer );

		card_panel = new Panel();
		this.cards = new CardLayout();
		card_panel.setLayout( cards );

		card_panel.add( star_edit, "Star" );
		card_panel.add( rosette_edit, "Rosette" );
		card_panel.add( ex_rosette_edit, "Extended" );

		setLayout( new BorderLayout() );
		add( "West", choice );
		add( "Center", card_panel );

		choice.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent ie ) 
			{
				cards.show( card_panel, choice.getSelectedItem() );
				changed();
			}
		} );
	}

	public Figure getFigure()
	{
		String active = choice.getSelectedItem();
		if( active.equals( "Star" ) ) {
			return star_edit.getFigure();
		} else if( active.equals( "Rosette" ) ) {
			return rosette_edit.getFigure();
		} else {
			return new ConnectFigure( 
				(RadialFigure)( ex_rosette_edit.getFigure() ) );
		}
	}

	public void resetWithFigure( Figure figure )
	{
		// Every time you reset, construct defaults for all of the
		// radial figure types.

		RadialFigure rf = (RadialFigure)( figure );
		int n = rf.getN();
		star_edit.setStar( new Star( n, 3.0, 2 ) );
		rosette_edit.setRosette( new Rosette( n, 0.0, 3 ) );
		ex_rosette_edit.setRosette( new Rosette( n, 0.0, 3 ) );

		// Now override the default for the type of the actual figure sent 
		// over.  Like MasterFigureEditor, this uses explicit typechecking.
		// Again, this is to promote encapsulation and UI independence in
		// the csk.taprats.app code.

		if( rf instanceof Star ) {
			Star star = (Star)( rf );
			star_edit.setStar( star );
			cards.show( card_panel, "Star" );
			choice.select( "Star" );
		} else if( rf instanceof Rosette ) {
			Rosette rosette = (Rosette)( rf );
			rosette_edit.setRosette( rosette );
			cards.show( card_panel, "Rosette" );
			choice.select( "Rosette" );
		} else if( rf instanceof ConnectFigure ) {
			// Assume that the only kind of ConnectFigure we'll be creating
			// is a Connected Rosette.
			Rosette rosette = (Rosette)( ((ConnectFigure)rf).getChild() );
			ex_rosette_edit.setRosette( rosette );
			cards.show( card_panel, "Extended" );
			choice.select( "Extended" );
		}
	}
}
