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
 * RenderPanel.java
 *
 * A panel for displaying a map with various rendering styles.  Shows the
 * rendered map and includes an editing area for choosing the rendering
 * style and providing controls for that style.
 *
 * FIXME --
 * this class is fairly poorly implemented right now.  Overhaul it if possible.
 */

package csk.taprats.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.Observer;
import java.util.Observable;
import java.util.Hashtable;
import java.util.Enumeration;

import csk.taprats.geometry.*;
import csk.taprats.geometry.Point;

import csk.taprats.toolkit.ClosePanel;

public class RenderPanel
	extends ClosePanel
{
	private Map				map;

	private RenderView 		view;

	private Choice			style;
	private CardLayout		card_layout;
	private Panel			style_panel;

	private Hashtable		styles;
	private Hashtable		maps_set;
	private RenderStyle		current;

	public RenderPanel( Map map )
	{
		this.map = map;
		this.view = new RenderView( -5.0, 5.0, 10.0, this );
		this.view.setSize( 500, 500 );
		this.view.setSink( true );

		Observer observer = (new Observer() {
			public void update( Observable obj, Object arg )
			{
				RenderPanel.this.view.forceRedraw();
			}
		} );

		RenderStyle plain = new RenderPlain();
		RenderStyle outline = new RenderOutline();
		RenderStyle emboss = new RenderEmboss();
		RenderStyle interlace = new RenderInterlace();
		RenderStyle sketch = new RenderSketch();
		RenderStyle filled = new RenderFilled();

		String[] style_names = {
			"Plain", "Outline", "Filled", "Interlace", "Emboss", "Sketch" };
		RenderStyle[] style_instances = {
			plain, outline, filled, interlace, emboss, sketch };

		this.styles = new Hashtable();
		this.maps_set = new Hashtable();
		this.style = new Choice();

		this.style_panel = new Panel();
		this.card_layout = new CardLayout();
		this.style_panel.setLayout( this.card_layout );

		for( int idx = 0; idx < style_names.length; ++idx ) {
			this.style.add( style_names[ idx ] );
			this.style_panel.add( style_instances[ idx ], style_names[ idx ] );
			style_instances[ idx ].style_changed.addObserver( observer );
			styles.put( style_names[ idx ], style_instances[ idx ] );
		}

		this.current = plain;
		this.style.select( "Plain" );
		plain.setMap( map );

		this.style.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent ie ) 
			{
				card_layout.show( style_panel, style.getSelectedItem() );
				current = (RenderStyle)( styles.get( style.getSelectedItem()));

				if( !maps_set.containsKey( current ) ) {
					current.setMap( RenderPanel.this.map );
					maps_set.put( current, current );
				}

				view.forceRedraw();
			}
		} );

		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout( layout );

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = 10;
		gbc.weighty = 10;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets( 6, 6, 6, 6 );

		layout.setConstraints( view, gbc );
		add( view );

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.NONE;

		layout.setConstraints( style, gbc );
		add( style );

		gbc.gridy = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		layout.setConstraints( close, gbc );
		add( close );

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.gridheight = 2;
		gbc.weightx = 10;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;

		layout.setConstraints( style_panel, gbc );
		add( style_panel );
	}

	public RenderStyle getStyle()
	{
		return current;
	}

	public RenderView getView()
	{
		return view;
	}
}
