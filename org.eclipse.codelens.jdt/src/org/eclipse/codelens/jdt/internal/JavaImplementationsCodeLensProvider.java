package org.eclipse.codelens.jdt.internal;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.provisional.codelens.AbstractSyncCodeLensProvider;
import org.eclipse.jface.text.provisional.codelens.ICodeLens;
import org.eclipse.jface.text.provisional.codelens.ICodeLensContext;

public class JavaImplementationsCodeLensProvider extends AbstractSyncCodeLensProvider {

	@Override
	protected ICodeLens[] provideSyncCodeLenses(ICodeLensContext context, IProgressMonitor monitor) {
		return null;
	}

	@Override
	protected ICodeLens resolveSyncCodeLens(ICodeLensContext context, ICodeLens codeLens, IProgressMonitor monitor) {
		return null;
	}

}
