package org.eclipse.jface.text.provisional.viewzones.internal;

import java.lang.reflect.Method;

import org.eclipse.jface.text.provisional.viewzones.IViewZone;
import org.eclipse.jface.text.provisional.viewzones.ViewZoneChangeAccessor;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.TextLayout;

public class ViewZoneStyledTextRenderer extends StyledTextRendererEmulator {

	private final ViewZoneChangeAccessor accessor;

	public ViewZoneStyledTextRenderer(ViewZoneChangeAccessor accessor) {
		super(accessor.getStyledText());
		this.accessor = accessor;
	}

	@Override
	protected TextLayout getTextLayout(int lineIndex, int orientation, int width, int lineSpacing, Object obj,
			Method proceed, Object[] args) throws Exception {
		TextLayout layout = super.getTextLayout(lineIndex, orientation, width, lineSpacing, obj, proceed, args);
		int lineNumber = lineIndex + 1;
		IViewZone viewZone = accessor.getViewZone(lineNumber);
		if (viewZone != null) {
			// There is view zone to render for the given line, update the line
			// spacing of the TextLayout linked to this line number.
			layout.setSpacing(viewZone.getHeightInPx());
			StyledTextRendererHelper.updateSpacing(getText());
		}
		return layout;
	}

	@Override
	protected int drawLine(int lineIndex, int paintX, int paintY, GC gc, Color widgetBackground, Color widgetForeground,
			Object obj, Method proceed, Object[] args) throws Exception {
		// Render the ine
		int lineHeight = super.drawLine(lineIndex, paintX, paintY, gc, widgetBackground, widgetForeground, obj, proceed,
				args);

		// Render the view zone
		StyledText text = super.getText();
		if (lineIndex == 0) {
			IViewZone viewZone = accessor.getViewZone(lineIndex);
			if (viewZone != null) {
				// Case for line number equals to 0:
				// - update the top margin of StyledText with height of the view
				// zone.
				// - renderer the view zone in the top margin of StyledText
				// area.
				int height = viewZone.getHeightInPx();
				if (getText().getTopMargin() != height) {
					getText().setTopMargin(height);
				}
				GC g = new GC(getText());
				g.setBackground(gc.getBackground());
				g.setForeground(gc.getForeground());
				int x = paintX;
				int y = paintY - height;
				int width = gc.getLineWidth() > 0 ? gc.getLineWidth() : 1;
				g.drawRectangle(x, y, width, height);
				drawViewZone(viewZone, x, y, g, getText());
				g.dispose();
			} else {
				int height = 0;
				if (getText().getTopMargin() != height) {
					getText().setTopMargin(height);
				}
			}
		}

		IViewZone viewZone = accessor.getViewZone(lineIndex + 1);
		if (viewZone != null) {
			// Case for line number > 0:
			// - renderer the view zone in this after the line (in the line
			// spacing updated in the TextLayout).
			drawViewZone(viewZone, paintX, paintY + viewZone.getHeightInPx(), gc, getText());
		}
		return lineHeight;
	}

	private void drawViewZone(IViewZone viewZone, int paintX, int paintY, GC gc, StyledText styledText) {
		viewZone.getRenderer().draw(viewZone, paintX, paintY, gc, styledText);
	}
}
