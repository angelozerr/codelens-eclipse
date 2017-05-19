package org.eclipse.text.viewzone.internal;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.StyledTextContent;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.text.viewzone.ViewZoneChangeAccessor;

import javassist.util.proxy.ProxyFactory;

public class StyledTextRendererHelper {

	private static Field RENDERER_FIELD;
	private static Method SET_CONTENT_METHOD;
	private static Method SET_FONT_METHOD;

	public static void setViewZoneStyledTextRenderer(ViewZoneChangeAccessor accessor) throws Exception {

		// 1) As it's not possible to override StyledTextRenderer#getTextLayout,
		// StyledTextRenderer#drawLine methods,
		// recreate an instance of StyledTextRenderer with Javassist
		Object viewZoneRenderer = createViewZoneStyledTextRenderer(accessor);

		StyledText text = accessor.getText();
		// 2) Reinitialize the renderer like StyledText constructor does.
		// renderer = new StyledTextRenderer(getDisplay(), this);
		// renderer.setContent(content);
		// renderer.setFont(getFont(), tabLength);
		initialize(viewZoneRenderer, text);

		// 3) Set the the new renderer
		getRendererField(text).set(text, viewZoneRenderer);

	}

	private static void initialize(Object viewZoneRenderer, StyledText text)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		// renderer.setContent(content);
		Method m1 = getSetContentMethod(viewZoneRenderer);
		m1.invoke(viewZoneRenderer, text.getContent());
		// renderer.setFont(getFont(), tabLength);
		Method m2 = getSetFontMethod(viewZoneRenderer);
		m2.invoke(viewZoneRenderer, text.getFont(), 4);
	}

	private static /* org.eclipse.swt.custom.StyledTextRenderer */ Object createViewZoneStyledTextRenderer(
			ViewZoneChangeAccessor accessor) throws Exception {
		StyledText text = accessor.getText();
		// get the org.eclipse.swt.custom.StyledTextRenderer instance of
		// StyledText
		/* org.eclipse.swt.custom.StyledTextRenderer */ Object originalRenderer = getRendererField(text).get(text);

		// Create a Javassist proxy
		ProxyFactory factory = new ProxyFactory();
		factory.setSuperclass(originalRenderer.getClass());
		factory.setHandler(new ViewZoneStyledTextRenderer(accessor));
		return factory.create(new Class[] { Device.class, StyledText.class }, new Object[] { text.getDisplay(), text });
	}

	private static Field getRendererField(StyledText text) throws Exception {
		if (RENDERER_FIELD == null) {
			RENDERER_FIELD = text.getClass().getDeclaredField("renderer");
			RENDERER_FIELD.setAccessible(true);
		}
		return RENDERER_FIELD;
	}

	private static Method getSetContentMethod(Object viewZoneRenderer) throws NoSuchMethodException {
		if (SET_CONTENT_METHOD == null) {
			SET_CONTENT_METHOD = viewZoneRenderer.getClass().getDeclaredMethod("setContent",
					new Class[] { StyledTextContent.class });
			SET_CONTENT_METHOD.setAccessible(true);
		}
		return SET_CONTENT_METHOD;
	}

	private static Method getSetFontMethod(Object viewZoneRenderer) throws NoSuchMethodException {
		if (SET_FONT_METHOD == null) {
			SET_FONT_METHOD = viewZoneRenderer.getClass().getDeclaredMethod("setFont",
					new Class[] { Font.class, int.class });
			SET_FONT_METHOD.setAccessible(true);
		}
		return SET_FONT_METHOD;
	}
}
