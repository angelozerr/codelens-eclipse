package org.eclipse.jface.text.provisional.viewzones;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.GC;

public interface IViewZone {

	/**
	 * The line number after which this zone should appear. Use 0 to place a
	 * view zone before the first line number.
	 */
	int getAfterLineNumber();

	int getOffsetAtLine();

	void setOffsetAtLine(int offsetAtLine);

	int getHeightInPx();

	void setTextViewer(ITextViewer textViewer);
	
	boolean isDisposed();

	void dispose();

	void mouseHover(MouseEvent event);

	void mouseExit(MouseEvent event);

	void mouseEnter(MouseEvent event);

	void onMouseClick(MouseEvent event);

	void draw(int paintX, int paintSpaceLeadingX, int paintY, GC gc);
}
