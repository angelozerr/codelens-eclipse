package org.eclipse.jface.text.provisional.viewzones;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.provisional.viewzones.internal.StyledTextRendererHelper;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

public class ViewZoneChangeAccessor implements IViewZoneChangeAccessor {

	private final StyledText styledText;
	private List<IViewZone> viewZones;
	
	public ViewZoneChangeAccessor(StyledText styledText) throws Exception {
		this.styledText = styledText;
		this.viewZones = new ArrayList<>();
		StyledTextRendererHelper.setViewZoneStyledTextRenderer(this);
		synch(styledText);
	}

	@Override
	public void addZone(IViewZone zone) {
		viewZones.add(zone);
		zone.setStyledText(styledText);
		int line = zone.getAfterLineNumber();
		if (line == 0) {
			styledText.setTopMargin(zone.getHeightInPx());
		} else {
			line--;
			int start = getStyledText().getOffsetAtLine(line);
			int length = getStyledText().getText().length() - start;
			styledText.redrawRange(start, length, true);
		}
	}

	@Override
	public void removeZone(IViewZone zone) {
		viewZones.remove(zone);
		int line = zone.getAfterLineNumber();
		if (line == 0) {
			styledText.setTopMargin(0);
		} else {
			line--;
			int start = getStyledText().getOffsetAtLine(line);
			int length = getStyledText().getText().length() - start;
			styledText.redrawRange(start, length, true);
		}
	}
	
//	@Override
//	public void layoutZone(int id) {
//		// TODO Auto-generated method stub
//		
//	}
	
	public StyledText getStyledText() {
		return styledText;
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
