package org.eclipse.text.viewzone;

import org.eclipse.swt.custom.StyledText;

public class ViewZone implements IViewZone {

	private final StyledText styledText;
	private int offsetAtLine;
	private int afterLineNumber;
	private int height;
	private String text;

	public ViewZone(int afterLineNumber, int height, String text, StyledText styledText) {
		this.styledText = styledText;
		this.height = height;
		this.text = text;
		setAfterLineNumber(afterLineNumber);
	}

	public void setAfterLineNumber(int afterLineNumber) {
		this.afterLineNumber = afterLineNumber;
		this.offsetAtLine = -1;
	}

	@Override
	public int getAfterLineNumber() {
		if (afterLineNumber == -1) {
			afterLineNumber = styledText.getLineAtOffset(offsetAtLine);
		}
		return afterLineNumber;
	}

	public int getOffsetAtLine() {
		offsetAtLine = styledText.getOffsetAtLine(afterLineNumber);
		return offsetAtLine;
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
	public String getText() {
		return text;
	}
}
