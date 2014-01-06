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
 * DesignEditor.java
 *
 * The main editor window for creating Islamic designs.  It contains a 
 * panel to select the feature to edit (launcher), a viewer to see 
 * the current figure (viewer), a region for the editing controls for the
 * current figure type (edit), and some control buttons (apply and preview).
 * One win here is that the launcher buttons use the same class as the
 * main viewer, just with smaller windows.
 *
 * As with a lot of user interface design, the easy part is creating all
 * the components and fitting them together.  The hard part is wiring up
 * the flow of information so that when controls are twiddled the appropriate
 * part of the model gets update.  This is particularly difficult when 
 * a given view has a varying set of controls depending on mode, as is the
 * case when switching between stars, rosettes and extended rosettes.
 * Fortunately, once the nasty work is done internally, the UI can hide all
 * the complexity and present a natural interface.
 */

package csk.taprats.ui;

import java.awt.*;
import java.awt.event.*;

import java.util.Observable;
import java.util.Observer;

import csk.taprats.tile.*;
import csk.taprats.app.*;
import csk.taprats.geometry.*;

import csk.taprats.toolkit.WindowCloser;
import csk.taprats.toolkit.ClosePanel;
import csk.taprats.geometry.Polygon;
import csk.taprats.geometry.Point;

public class DesignEditor
	extends ClosePanel
{
	private Tiling 				tiling;

	private FeatureLauncher 	launcher;

	private FeatureButton		viewer;

	private MasterFigureEditor	edit;

	private Button				apply;
	private Button				preview;

	public DesignEditor( Tiling tiling )
	{
		this.tiling = tiling;
		this.launcher = new FeatureLauncher( tiling );
		
		this.viewer = new FeatureButton();
		this.viewer.setSink( true );
		this.viewer.setSize( 400, 400 );
			
		Observer control_changed = (new Observer() {
			public void update( Observable obs, Object arg ) 
			{
				viewer.getDesignElement().setFigure( edit.getFigure() );
				viewer.designElementChanged();
			}
		} );

		edit = new MasterFigureEditor( this );
		edit.figure_changed.addObserver( control_changed );

		this.apply = new Button( "Apply" );
		this.apply.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ae ) 
			{
				FeatureButton fb = launcher.getCurrent();
				fb.getDesignElement().setFigure(
					(Figure)( edit.getFigure().clone() ) );
				fb.designElementChanged();
			}
		} );

		this.preview = new Button( "Proceed" );
		this.preview.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent ae ) 
			{
				Prototype proto = getPrototype();
				PreviewPanel pp = new PreviewPanel( proto );
				Frame frame = new Frame( "Preview for " + 
					DesignEditor.this.tiling.getName() );
				pp.setParentFrame( frame );
				frame.setLayout( new BorderLayout() );
				frame.add( "Center", pp );
				frame.addWindowListener( new WindowCloser( frame, false ) );
				frame.pack();
				frame.show();
			}
		} );

		/*
		 * In case I haven't mentioned it before, I'll mostly given up
		 * on Layouts other that GridBagLayout.  Yes, it's quite verbose.
		 * But it's really the only layout class that balances control
		 * and automation.
		 */

		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout( layout );

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 3;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.NONE;

		layout.setConstraints( launcher, gbc );
		add( launcher );

		gbc.gridx = 1;
		gbc.gridwidth = 3;
		gbc.gridheight = 1;
		gbc.weighty = 5;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets( 6, 6, 6, 6 );

		layout.setConstraints( viewer, gbc );
		add( viewer );

		gbc.gridy = 1;
		gbc.weighty = 1;

		layout.setConstraints( edit, gbc );
		add( edit );

		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.weightx = 1;

		layout.setConstraints( apply, gbc );
		add( apply );

		gbc.gridx = 2;

		layout.setConstraints( preview, gbc );
		add( preview );

		gbc.gridx = 3;

		layout.setConstraints( close, gbc );
		add( close );

		// this.launcher.selection_changed.addObserver( this );
		this.launcher.selection_changed.addObserver( 
			new Observer() {
				public void update( Observable obs, Object arg )
				{
					setActiveFeature( launcher.getCurrent() );
				}
			} );

		launcher.setCurrent( 0 );
		setActiveFeature( launcher.getFeatureButton( 0 ) );
	}

	public void setActiveFeature( FeatureButton fb )
	{
		DesignElement de = (DesignElement)( fb.getDesignElement().clone() );
		Figure figure = de.getFigure();

		viewer.setDesignElement( de );
		edit.resetWithFigure( figure );
	}

	/*
	 * Build a Prototype containing all the information about the 
	 * design in its current state.  This will then be used to construct
	 * the final map.  It's also used to infer maps for features.
	 *
	 * The prototype clones the design elements stored in the editor, 
	 * so multiple prototypes can be fired off for different purposes and
	 * those prototypes can be consumed if needed.  This follows nicely
	 * in my philosophy of "fire and forget" UIs -- the different 
	 * phases of a process should be independent.
	 */
	private Prototype getPrototype()
	{
		Prototype proto = new Prototype( tiling );
		for( int idx = 0; idx < launcher.numFeatureButtons(); ++idx ) {
			FeatureButton fb = launcher.getFeatureButton( idx );
			proto.addElement( 
				(DesignElement)( fb.getDesignElement().clone() ) );
		}

		return proto;
	}

	/*
	 * Infer a map for the currently selected feature, using 
	 * the csk.taprats.app.Infer algorithm.  Absolutely definitely
	 * guaranteed to not necessarily work or produce satisfactory
	 * results.
	 */
	Map getCurrentInferredMap() 
	{
		Prototype proto = getPrototype();

		try {
			Infer inf = new Infer( proto );
			return inf.infer( 
				launcher.getCurrent().getDesignElement().getFeature() );
		} catch( Exception e ) {
			return null;
		}
	}

	public static final void main( String[] args )
	{
		Tiling t = Tiling.find( args[0] );
		DesignEditor de = new DesignEditor( t );
		csk.taprats.toolkit.Util.openTestFrame( de );
	}
}
