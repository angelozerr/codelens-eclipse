package org.eclipse.text.viewzone.internal;

import java.lang.reflect.Method;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.text.viewzone.IViewZone;
import org.eclipse.text.viewzone.ViewZoneChangeAccessor;

public class ViewZoneStyledTextRenderer extends StyledTextRendererEmulator {

	private final ViewZoneChangeAccessor accessor;

	public ViewZoneStyledTextRenderer(ViewZoneChangeAccessor accessor) {
		super(accessor.getText());
		this.accessor = accessor;
	}

	@Override
	protected TextLayout getTextLayout(int lineIndex, int orientation, int width, int lineSpacing, Object obj,
			Method proceed, Object[] args) throws Exception {
		TextLayout layout = super.getTextLayout(lineIndex, orientation, width, lineSpacing, obj, proceed, args);
		IViewZone viewZone = accessor.getViewZone(lineIndex);
		if (viewZone != null && layout.getSpacing() != viewZone.getHeightInPx()) {
			layout.setSpacing(viewZone.getHeightInPx());
			updateSpacing(getText());
		}
		return layout;
	}

	private void updateSpacing(StyledText text) {
		try {
			// text.setVariableLineHeight();
			Method m1 = text.getClass().getDeclaredMethod("setVariableLineHeight");
			m1.setAccessible(true);
			m1.invoke(text);

			// text.resetCache(0, text.getContent().getLineCount());
			// Method m2 = text.getClass().getDeclaredMethod("resetCache",
			// new Class[] { int.class, int.class });
			// m2.setAccessible(true);
			// m2.invoke(text, 0, text.getContent().getLineCount());
			//
			// // text.setCaretLocation();
			// Method m3 =
			// text.getClass().getDeclaredMethod("setCaretLocation");
			// m3.setAccessible(true);
			// m3.invoke(text);

			// text.redraw();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected int drawLine(int lineIndex, int paintX, int paintY, GC gc, Color widgetBackground, Color widgetForeground,
			Object obj, Method proceed, Object[] args) throws Exception {
		int result = super.drawLine(lineIndex, paintX, paintY, gc, widgetBackground, widgetForeground, obj, proceed,
				args);
		IViewZone viewZone = accessor.getViewZone(lineIndex);
		if (viewZone != null) {
			gc.setForeground(getText().getDisplay().getSystemColor(SWT.COLOR_GRAY));
			gc.drawText(viewZone.getText(), paintX, paintY + viewZone.getHeightInPx());
		}
		return result;
	}
}
