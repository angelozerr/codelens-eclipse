package org.eclipse.codelens.editors;

import org.eclipse.core.runtime.IProgressMonitor;

public interface ICodeLensController {

	void install();
	
	void uninstall();
	
	void refresh();
	
	void setProgressMonitor(IProgressMonitor monitor);
}
