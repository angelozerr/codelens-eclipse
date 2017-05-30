package org.eclipse.text.viewzone.internal.codelens;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.text.viewzone.ViewZone;
import org.eclipse.text.viewzone.internal.StyledTextRendererHelper;

public class CodeLensViewZone extends ViewZone {

	private String text;

	public CodeLensViewZone(int afterLineNumber, int height) {
		super(afterLineNumber, height, CodeLensViewZoneRenderer.getInstance());
	}

	public void setText(String text) {
		this.text = text;
		redraw();
	}

	public String getText() {
		return text;
	}
}