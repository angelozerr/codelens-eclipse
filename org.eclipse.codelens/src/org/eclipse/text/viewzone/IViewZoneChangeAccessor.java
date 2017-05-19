package org.eclipse.text.viewzone;

public interface IViewZoneChangeAccessor {

	void addZone(int line, int height, String text);

	int getSize();
}
