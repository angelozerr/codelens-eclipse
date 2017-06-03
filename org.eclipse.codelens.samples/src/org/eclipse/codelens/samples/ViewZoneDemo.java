package org.eclipse.codelens.samples;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.provisional.viewzones.DefaultViewZone;
import org.eclipse.jface.text.provisional.viewzones.ViewZoneChangeAccessor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ViewZoneDemo {

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
		StyledText styledText = textViewer.getTextWidget();
		styledText.setLayoutData(new GridData(GridData.FILL_BOTH));

		ViewZoneChangeAccessor viewZones = new ViewZoneChangeAccessor(textViewer);
		
		Button add = new Button(parent, SWT.NONE);
		add.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 0, 0));
		add.setText("Add Zone");
		add.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(), "", "Enter view zone content",
						"Zone " + viewZones.getSize(), null);
				if (dlg.open() == Window.OK) {
					int line = styledText.getLineAtOffset(styledText.getCaretOffset());
					viewZones.addZone(new DefaultViewZone(line, 20, dlg.getValue()));
				}
			}
		});

		shell.open();
		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();
	}
}
