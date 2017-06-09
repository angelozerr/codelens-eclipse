package org.eclipse.codelens.samples;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.provisional.codelens.CodeLens;
import org.eclipse.swt.widgets.Display;

public class ClassCodeLens extends CodeLens {

	private String className;
	
	public ClassCodeLens(String className, int startLineNumber) {
		super(startLineNumber);
		this.className = className;
	}

	public String getClassName() {
		return className;
	}

	@Override
	public void open() {
		Display.getDefault().asyncExec(new Runnable() {
			
			@Override
			public void run() {				
				MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Open class CodeLens", ClassCodeLens.this.className);
	
			}
		});		
	}
}
