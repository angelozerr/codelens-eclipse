package org.eclipse.jface.text.provisional.codelens;

import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IProgressMonitor;

public abstract class AbstractSyncCodeLensProvider implements ICodeLensProvider {

	@Override
	public CompletableFuture<ICodeLens[]> provideCodeLenses(ICodeLensContext context, IProgressMonitor monitor) {
		return CompletableFuture.supplyAsync(() -> {
			return provideSyncCodeLenses(context, monitor);
		});
	}

	@Override
	public CompletableFuture<ICodeLens> resolveCodeLens(ICodeLensContext context, ICodeLens codeLens,
			IProgressMonitor monitor) {
		return CompletableFuture.supplyAsync(() -> {
			return resolveSyncCodeLens(context, codeLens, monitor);
		});
	}

	protected abstract ICodeLens[] provideSyncCodeLenses(ICodeLensContext context, IProgressMonitor monitor);

	protected abstract ICodeLens resolveSyncCodeLens(ICodeLensContext context, ICodeLens codeLens,
			IProgressMonitor monitor);
}
