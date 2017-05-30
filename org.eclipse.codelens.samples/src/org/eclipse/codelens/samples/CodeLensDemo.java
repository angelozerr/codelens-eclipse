package org.eclipse.codelens.samples;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.text.viewzone.codelens.CodeLensContribution;
import org.eclipse.text.viewzone.codelens.CodeLensProviderRegistry;

public class CodeLensDemo {

	private static final String CONTENT_TYPE_ID = "ts";

	public static void main(String[] args) throws Exception {
		// create the widget's shell
		Shell shell = new Shell();
		shell.setLayout(new FillLayout());
		shell.setSize(500, 500);
		Display display = shell.getDisplay();

		Composite parent = new Composite(shell, SWT.NONE);
		parent.setLayout(new GridLayout(2, false));

		ITextViewer textViewer = new TextViewer(parent, SWT.V_SCROLL | SWT.BORDER);
		textViewer.setDocument(new Document(""));
		StyledText widget = textViewer.getTextWidget();
		widget.setLayoutData(new GridData(GridData.FILL_BOTH));

		CodeLensProviderRegistry registry = CodeLensProviderRegistry.getInstance();
		registry.register(CONTENT_TYPE_ID, new ClassReferencesCodeLensProvider());

		new CodeLensContribution(textViewer, CONTENT_TYPE_ID);
		
		//ViewZoneChangeAccessor viewZones = new ViewZoneChangeAccessor(widget);

		shell.open();
		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();
	}

}
