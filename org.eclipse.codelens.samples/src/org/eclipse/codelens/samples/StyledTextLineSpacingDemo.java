package org.eclipse.codelens.samples;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.patch.StyledTextPatcher;
import org.eclipse.swt.custom.provisional.ILineSpacingProvider;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class StyledTextLineSpacingDemo {

	public static void main(String[] args) throws Exception {
		// create the widget's shell
		Shell shell = new Shell();
		shell.setLayout(new FillLayout());
		shell.setSize(500, 500);
		Display display = shell.getDisplay();

		Composite parent = new Composite(shell, SWT.NONE);
		parent.setLayout(new GridLayout());

		StyledText styledText = new StyledText(parent, SWT.BORDER | SWT.V_SCROLL);
		styledText.setLayoutData(new GridData(GridData.FILL_BOTH));

		styledText.setText("a\nb\nc\nd");
		
		StyledTextPatcher.setLineSpacingProvider(styledText, new ILineSpacingProvider() {
			
			@Override
			public Integer getLineSpacing(int lineIndex) {
				if (lineIndex == 0) {
					return 10;
				} else if (lineIndex == 1) {
					return 30;
				}
				return null;
			}
		});
		
		shell.open();
		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();
	}
}
