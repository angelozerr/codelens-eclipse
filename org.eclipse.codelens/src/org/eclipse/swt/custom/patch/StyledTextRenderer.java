/**
 *  Copyright (c) 2015-2017 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.swt.custom.patch;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.patch.internal.StyledTextRendererEmulator;
import org.eclipse.swt.custom.provisional.ILineSpacingProvider;
import org.eclipse.swt.graphics.TextLayout;

/**
 * Class which should replace the private class
 * {@link org.eclipse.swt.custom.StyledTextRenderer} to support line spacing for
 * a given line.
 *
 */
public class StyledTextRenderer extends StyledTextRendererEmulator {

	private ILineSpacingProvider lineSpacingProvider;

	private final StyledText styledText;

	private final Class<? extends Object> swtRendererClass;

	private boolean computing;

	private int topIndex;

	private TextLayout[] layouts;

	public StyledTextRenderer(StyledText styledText, Class<? extends Object> swtRendererClass) {
		this.styledText = styledText;
		this.swtRendererClass = swtRendererClass;
	}

	public void setLineSpacingProvider(ILineSpacingProvider lineSpacingProvider) {
		this.lineSpacingProvider = lineSpacingProvider;
	}

	@Override
	protected TextLayout getTextLayout(int lineIndex, int orientation, int width, int lineSpacing, Object obj,
			Method proceed, Object[] args) throws Exception {
		if (lineSpacingProvider == null) {
			return super.getTextLayout(lineIndex, orientation, width, lineSpacing, obj, proceed, args);
		}

		// Compute line spacing for the given line index.
		int newLineSpacing = lineSpacing;
		Integer spacing = lineSpacingProvider.getLineSpacing(lineIndex);
		if (spacing != null) {
			newLineSpacing = spacing;
		}

		Field f = swtRendererClass.getDeclaredField("topIndex");
		f.setAccessible(true);
		this.topIndex = (int) f.get(obj);
		f = swtRendererClass.getDeclaredField("layouts");
		f.setAccessible(true);
		this.layouts = (TextLayout[]) f.get(obj);

		if (isSameLineSpacing(lineIndex, newLineSpacing)) {
			return super.getTextLayout(lineIndex, orientation, width, newLineSpacing, obj, proceed, args);
		}

		TextLayout layout = super.getTextLayout(lineIndex, orientation, width, lineSpacing, obj, proceed, args);
		if (layout.getSpacing() != newLineSpacing) {
			layout.setSpacing(newLineSpacing);
			if (computing) {
				return layout;
			}
			try {
				computing = true;
				StyledTextPatcher.resetCache(styledText, lineIndex, styledText.getLineCount());
				StyledTextPatcher.setVariableLineHeight(styledText);
				StyledTextPatcher.setCaretLocation(styledText);
				styledText.redraw();
			} finally {
				computing = false;
			}
		}
		return layout;
	}
	
	boolean isSameLineSpacing(int lineIndex, int newLineSpacing) {
		if (layouts == null) {
			return false;
		}
		int layoutIndex = lineIndex - topIndex;
		if (0 <= layoutIndex && layoutIndex < layouts.length) {
			TextLayout layout = layouts[layoutIndex];
			return layout != null && !layout.isDisposed() && layout.getSpacing() == newLineSpacing;
		}
		return false;
	}

}
