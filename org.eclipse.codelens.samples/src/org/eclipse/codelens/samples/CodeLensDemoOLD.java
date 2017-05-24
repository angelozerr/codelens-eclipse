package org.eclipse.codelens.samples;

import java.util.concurrent.CompletableFuture;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.Reconciler;
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
import org.eclipse.text.viewzone.IViewZoneChangeAccessor;
import org.eclipse.text.viewzone.ViewZoneChangeAccessor;
import org.eclipse.text.viewzone.codelens.CodeLensProviderRegistry;
import org.eclipse.text.viewzone.codelens.ICodeLens;
import org.eclipse.text.viewzone.codelens.ICodeLensProvider;

public class CodeLensDemoOLD {

	private static final String CONTENT_TYPE_ID = "ts";

	public static void main(String[] args) throws Exception {
		// create the widget's shell
		Shell shell = new Shell();
		shell.setLayout(new FillLayout());
		shell.setSize(500, 500);
		Display display = shell.getDisplay();

		Composite parent = new Composite(shell, SWT.NONE);
		parent.setLayout(new GridLayout());

		TextViewer viewer = new TextViewer(parent, SWT.BORDER);
		viewer.setDocument(new Document());
		viewer.getTextWidget().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		Reconciler reconciler = new Reconciler();
		reconciler.setReconcilingStrategy(new IReconcilingStrategy() {
			
			@Override
			public void setDocument(IDocument document) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void reconcile(IRegion partition) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
				System.err.println("reconcile");
				
			}
		}, "__dftl_partition_content_type");
		reconciler.install(viewer);
		
		
		IViewZoneChangeAccessor viewZones = new ViewZoneChangeAccessor(viewer.getTextWidget());

		CodeLensProviderRegistry registry = new CodeLensProviderRegistry();
		registry.register(CONTENT_TYPE_ID, new ICodeLensProvider() {

			@Override
			public CompletableFuture<ICodeLens[]> resolveCodeLens(ICodeLens codeLens) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public CompletableFuture<ICodeLens[]> provideCodeLenses() {
				// TODO Auto-generated method stub
				return null;
			}
		});

		shell.open();
		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();
	}
}
