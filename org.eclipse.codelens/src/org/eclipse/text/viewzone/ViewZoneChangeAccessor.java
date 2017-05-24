package org.eclipse.text.viewzone;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.text.viewzone.internal.StyledTextRendererHelper;

public class ViewZoneChangeAccessor implements IViewZoneChangeAccessor {

	private final StyledText text;
	private List<IViewZone> viewZones;

	public ViewZoneChangeAccessor(StyledText text) throws Exception {
		this.text = text;
		this.viewZones = new ArrayList<>();
		StyledTextRendererHelper.setViewZoneStyledTextRenderer(this);
		synch(text);
	}

	@Override
	public void addZone(int line, int height, String l) {
		addZone(new ViewZone(line, height, l, text));
	}

	private void addZone(IViewZone zone) {
		viewZones.add(zone);
		int line = zone.getAfterLineNumber();
		if (line == 0) {
			text.setTopMargin(zone.getHeightInPx());
			StyledTextRendererHelper.updateSpacing(getText());
		} else {
			line--;
			int start = getText().getOffsetAtLine(line);
			int length = getText().getText().length() - start;
			text.redrawRange(start, length, true);
		}
	}

	public void removeZone(IViewZone zone) {
		viewZones.remove(zone);
		int line = zone.getAfterLineNumber();
		if (line == 0) {
			text.setTopMargin(0);
		} else {
			line--;
			int start = getText().getOffsetAtLine(line);
			int length = getText().getText().length() - start;
			text.redrawRange(start, length, true);
		}
	}
	
	public StyledText getText() {
		return text;
	}

	public IViewZone getViewZone(int lineIndex) {
		for (IViewZone viewZone : viewZones) {
			if (lineIndex == viewZone.getAfterLineNumber()) {
				return viewZone;
			}
		}
		return null;
	}

	private void synch(StyledText text) {
		// use a verify listener to keep the offsets up to date
		text.addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent e) {
				int start = e.start;
				int replaceCharCount = e.end - e.start;
				int newCharCount = e.text.length();
				for (IViewZone viewZone : new ArrayList<>(viewZones)) {
					int offset = viewZone.getOffsetAtLine();
					if (start <= offset && offset < start + replaceCharCount) {
						// this zone is being deleted from the text
						removeZone(viewZone);
						offset = -1;
					}
					if (offset != -1 && offset >= start)
						offset += newCharCount - replaceCharCount;

					// int lastLine = viewZone.getAfterLineNumber();
					viewZone.setOffsetAtLine(offset);
					// int newLine = viewZone.getAfterLineNumber();
					// if (lastLine == 0 && newLine != lastLine) {
					// getText().setTopMargin(getText().getTopMargin() -
					// viewZone.getHeightInPx());
					// }
				}
			}
		});
	}

	@Override
	public int getSize() {
		return viewZones.size();
	}

}
