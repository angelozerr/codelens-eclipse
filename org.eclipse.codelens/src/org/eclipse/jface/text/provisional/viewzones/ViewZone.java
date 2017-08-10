package org.eclipse.jface.text.provisional.viewzones;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;

public abstract class ViewZone implements IViewZone {

	private ITextViewer textViewer;
	private int offsetAtLine;
	private int afterLineNumber;
	private int height;

	private boolean disposed;

	public ViewZone(int afterLineNumber, int height) {
		this.height = height;
		setAfterLineNumber(afterLineNumber);
	}

	public void setTextViewer(ITextViewer textViewer) {
		this.textViewer = textViewer;
	}

	public void setAfterLineNumber(int afterLineNumber) {
		this.afterLineNumber = afterLineNumber;
		this.offsetAtLine = -1;
	}

	@Override
	public int getAfterLineNumber() {
		if (offsetAtLine != -1) {
			try {
				StyledText styledText = textViewer.getTextWidget();
				afterLineNumber = styledText.getLineAtOffset(offsetAtLine);//textViewer.getDocument().getLineOfOffset(offsetAtLine);
			} catch (Exception e) {
				// e.printStackTrace();
				return -1;
			}
		}
		return afterLineNumber;
	}

	public int getOffsetAtLine() {
		if (offsetAtLine == -1) {
			offsetAtLine = getOffsetAtLine(afterLineNumber);
		}
		return offsetAtLine;
	}

	protected int getOffsetAtLine(int lineIndex) {
//		try {
//			return textViewer.getDocument().getLineOffset(lineIndex);
//		} catch (BadLocationException e) {
//			return -1;
//		}
		StyledText styledText = textViewer.getTextWidget();
		return styledText.getOffsetAtLine(lineIndex);
	}

	public void setOffsetAtLine(int offsetAtLine) {
		this.afterLineNumber = -1;
		this.offsetAtLine = offsetAtLine;
	}

	@Override
	public int getHeightInPx() {
		return height;
	}

	@Override
	public void dispose() {
		this.disposed = true;
		this.setTextViewer(null);
	}

	@Override
	public boolean isDisposed() {
		return disposed;
	}

	@Override
	public void mouseHover(MouseEvent event) {
		// System.err.println("mouseHover");tt
	}

	@Override
	public void mouseEnter(MouseEvent event) {
		// System.err.println("mouseEnter");
	}

	@Override
	public void mouseExit(MouseEvent event) {
		// System.err.println("mouseExit");
	}

	@Override
	public void onMouseClick(MouseEvent event) {

	}

//	public StyledText getStyledText() {
//		return styledText;
//	}
	
	public ITextViewer getTextViewer() {
		return textViewer;
	}
}
