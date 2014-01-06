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
 * Signal.java
 *
 * This file fills me with anxiety.
 *
 * Here's the idea.  Take a look at the signal system used by Gtk--, 
 * the C++ wrapper library for Gtk+ (www.gtk.org).  Beautiful stuff, 
 * definitely a good use of C++ templates.  Allows me to say something
 * like this:
 *
 *  class A {
 *    void foo() {
 *      cout << "event." << endl;
 *    }
 *    A() {
 *      Gtk::Button b( "Hello" );
 *      SigC::connect( b.clicked, SigC::bind( &A::foo, this ) );  // HERE
 *  }
 *
 * The thing to observe is that "b.clicked" is a public member variable
 * in the class Gtk::Button.  You attach a callback to it by naming
 * the variable explicitly.  The enclosing button fires the callbacks
 * by telling the signal object to iterate over its connections.  Works
 * great, because by containing rather than subclassing, you can have as
 * many different signals as you want in one object.  They have different
 * callback lists and are distinguished by identity.
 *
 * So I tried it in Java (see, for example, csk.taprats.toolkit.Slider).  
 * The upshot is that it works fine, but I would never put these sorts 
 * of signals in a Java library I expected others to build on.  The problem 
 * is that once I expose the member variable, there's nothing to stop 
 * another class from reassigning it and destroying my signal.  In C++, 
 * this isn't a problem, through a combination of stack-based allocation 
 * and operator overloading.  The language won't let me reassign the signal
 * that way.  So the variable is public, but it only affords connecting.
 *
 * Interesting line of thought.  Is there some way to get closer to
 * libsigc++ in Java?
 */

package csk.taprats.general;

import java.util.Observable;

public class Signal
	extends Observable
{
	public void notify( Object arg )
	{
		setChanged();
		notifyObservers( arg );
		clearChanged();
	}
}
