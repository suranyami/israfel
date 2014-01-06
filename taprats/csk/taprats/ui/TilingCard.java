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
 * TilingCard.java
 *
 * I'm pretty happy with this UI.  A TilingCard is sort of like a baseball
 * card, but with a tiling in place of an overpaid baseball player.  A
 * typical depiction of the tiling is shown along with its name and a brief
 * description.  All that's missing is the tiling's ERA.  Collect them all!
 * Trade with your friends!
 *
 * This class is used as the entry to the whole system; it's the UI that
 * lets the user select a Tiling to work with.
 */

package csk.taprats.ui;

import csk.taprats.tile.*;
import csk.taprats.toolkit.*;
import csk.taprats.geometry.*;

import java.awt.*;

public class TilingCard
	extends Panel
{
	private Tiling			tiling;

	private TilingViewer	viewer;

	private Label			name_label;
	private Label			name;

	private Label			desc_label;
	private TextArea		desc;

	public TilingCard( Tiling tiling )
	{
		super();

		this.tiling = tiling;
		this.viewer = new TilingViewer( tiling );
		this.viewer.setSink( true );
		this.viewer.setSize( 140, 140 );

		this.name_label = new Label( "Name:", Label.RIGHT );
		this.name = new Label( tiling.getName(), Label.LEFT );

		this.desc_label = new Label( "Description", Label.CENTER );
		String tdesc = tiling.getDescription();
		if( tdesc == null ) {
			tdesc = "No description available.";
		}
		this.desc = new TextArea( tdesc, 8, 26, TextArea.SCROLLBARS_NONE );
		this.desc.setEditable( false );

		GridBagConstraints gbc = new GridBagConstraints();
		GridBagLayout layout = new GridBagLayout();
		setLayout( layout );

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.gridheight = 3;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 2;
		gbc.insets = new Insets( 3, 3, 3, 3 );

		layout.setConstraints( viewer, gbc );
		add( viewer );

		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weighty = 1;
		gbc.weightx = 1;
		gbc.insets = new Insets( 0, 0, 0, 0 );
		
		layout.setConstraints( name_label, gbc );
		add( name_label );

		gbc.gridx = 3;

		layout.setConstraints( this.name, gbc );
		add( this.name );

		gbc.gridy = 1;
		gbc.gridx = 2;
		gbc.weighty = 1;
		gbc.gridwidth = 2;
		
		layout.setConstraints( desc_label, gbc );
		add( desc_label );

		gbc.gridy = 2;
		gbc.weighty = 5;
		gbc.insets = new Insets( 3, 3, 3, 3 );

		layout.setConstraints( this.desc, gbc );
		add( this.desc );
	}

	/*
	 * I want to make the words "name" and "descriptions" be bold but
	 * have the same font as the other labels.  Since the other labels 
	 * don't have fonts until their peers are created, I wait until
	 * addNotify time to take care of setting the fonts.
	 */
	public void addNotify()
	{
		super.addNotify();

		Font current_font = getFont();

		Font bf = new Font( 
			current_font.getName(), Font.BOLD, current_font.getSize() );

		name_label.setFont( bf );
		desc_label.setFont( bf );
	}

	public void setTiling( Tiling tiling )
	{
		this.tiling = tiling;
		this.name.setText( tiling.getName() );

		String tdesc = tiling.getDescription();
		if( tdesc != null ) {
			this.desc.setText( tdesc );
		} else {
			this.desc.setText( "No description available." );
		}

		viewer.setTiling( tiling );
	}

	public Tiling getTiling()
	{
		return tiling;
	}

	public static final void main( String[] args )
	{
		String tname = args[0];
		Tiling t = Tiling.find( tname );

		if( t == null ) {
			System.err.println( "no such tiling." );
			System.exit( -1 );
		}

		try {
			TilingCard tc = new TilingCard( t );
			csk.taprats.toolkit.Util.openTestFrame( tc );
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}
}
