package org.eclipse.codelens.samples;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.text.viewzone.codelens.Command;
import org.eclipse.text.viewzone.codelens.ICodeLens;
import org.eclipse.text.viewzone.codelens.ICodeLensProvider;

public class ClassReferencesCodeLensProvider implements ICodeLensProvider {

	@Override
	public ICodeLens[] provideCodeLenses(ITextViewer textViewer) {
		StyledText styledText = textViewer.getTextWidget();
		List<ICodeLens> lenses = new ArrayList<>();
		int lineCount = styledText.getLineCount();
		for (int i = 0; i < lineCount; i++) {
			String line = styledText.getLine(i);
			int index = line.indexOf("class ");
			if (index != -1) {
				String className = line.substring(index + "class ".length(), line.length());
				index = className.indexOf(" ");
				if (index != -1) {
					className = className.substring(index, className.length());
				}
				if (className.length() > 0) {
					lenses.add(new ClassCodeLens(className, i));
				}
			}
		}
		return lenses.toArray(new ICodeLens[0]);
	}

	@Override
	public ICodeLens resolveCodeLens(ITextViewer textViewer, ICodeLens codeLens) {
		StyledText styledText = textViewer.getTextWidget();
		String className = ((ClassCodeLens) codeLens).getClassName();
		int refCount = 0;
		int lineCount = styledText.getLineCount();
		for (int i = 0; i < lineCount; i++) {
			String line = styledText.getLine(i);
			refCount += line.contains("new " + className) ? 1 : 0;
		}
		((ClassCodeLens) codeLens).setCommand(new Command( refCount + " references", ""));
		return codeLens;
	}

}
