package org.eclipse.text.viewzone.internal;

import java.lang.reflect.Method;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.TextLayout;

import javassist.util.proxy.MethodHandler;

public class StyledTextRendererEmulator implements MethodHandler {

	private final StyledText text;

	public StyledTextRendererEmulator(StyledText text) {
		this.text = text;
	}

	@Override
	public Object invoke(Object obj, Method thisMethod, Method proceed, Object[] args) throws Throwable {
		if ("getTextLayout".equals(thisMethod.getName()) && args.length > 1) {
			int lineIndex = (int) args[0];
			int orientation = (int) args[1];
			int width = (int) args[2];
			int lineSpacing = (int) args[3];
			return getTextLayout(lineIndex, orientation, width, lineSpacing, obj, proceed, args);
		} else if ("drawLine".equals(thisMethod.getName())) {
			int lineIndex = (int) args[0];
			int paintX = (int) args[1];
			int paintY = (int) args[2];
			GC gc = (GC) args[3];
			Color widgetBackground = (Color) args[4];
			Color widgetForeground = (Color) args[5];
			return drawLine(lineIndex, paintX, paintY, gc, widgetBackground, widgetForeground, obj, proceed, args);
		}
		return proceed.invoke(obj, args);
	}

	protected TextLayout getTextLayout(int lineIndex, int orientation, int width, int lineSpacing, Object obj,
			Method proceed, Object[] args) throws Exception {
		return (TextLayout) proceed.invoke(obj, args);
	}

	protected int drawLine(int lineIndex, int paintX, int paintY, GC gc, Color widgetBackground, Color widgetForeground,
			Object obj, Method proceed, Object[] args) throws Exception {
		return (int) proceed.invoke(obj, args);
	}

	public StyledText getText() {
		return text;
	}
}