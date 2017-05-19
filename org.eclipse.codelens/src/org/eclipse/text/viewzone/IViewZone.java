package org.eclipse.text.viewzone;

public interface IViewZone {

	/**
	 * The line number after which this zone should appear.
	 * Use 0 to place a view zone before the first line number.
	 */
	int getAfterLineNumber();
	
	int getOffsetAtLine();
	
	void setOffsetAtLine(int offsetAtLine);
	
	int getHeightInPx();

	String getText();
}
