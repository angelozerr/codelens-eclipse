package org.eclipse.codelens.samples;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.text.viewzone.ViewZoneChangeAccessor;
import org.eclipse.text.viewzone.codelens.CodeLensProviderRegistry;
import org.eclipse.text.viewzone.codelens.ICodeLens;
import org.eclipse.text.viewzone.codelens.ICodeLensProvider;

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

		StyledText widget = new StyledText(parent, SWT.BORDER);
		widget.setTopMargin(20);
		widget.setLayoutData(new GridData(GridData.FILL_BOTH));

		ViewZoneChangeAccessor viewZones = new ViewZoneChangeAccessor(widget);
		CodeLensProviderRegistry registry = new CodeLensProviderRegistry();

		widget.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent event) {
				renderCodeLens(viewZones, registry);
			}
		});

		registry.register(CONTENT_TYPE_ID, new ICodeLensProvider<StyledText>() {

			@Override
			public ICodeLens[] provideCodeLenses(StyledText document) {
				ICodeLens[] lens = new ICodeLens[1];
				return lens;
			}

			@Override
			public ICodeLens[] resolveCodeLens(ICodeLens codeLens) {
				// TODO Auto-generated method stub
				return null;
			}
		});

		shell.open();
		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();
	}

	private static void renderCodeLens(ViewZoneChangeAccessor viewZones, CodeLensProviderRegistry registry) {
		StyledText text = viewZones.getText();
		Collection<ICodeLensProvider> providers = registry.all(CONTENT_TYPE_ID);
		for (ICodeLensProvider provider : providers) {
			ICodeLens[] lens = provider.provideCodeLenses(text);
		}
	}
}
