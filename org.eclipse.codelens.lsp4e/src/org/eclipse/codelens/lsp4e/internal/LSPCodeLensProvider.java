package org.eclipse.codelens.lsp4e.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.codelens.editors.IEditorCodeLensContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.provisional.codelens.ICodeLens;
import org.eclipse.jface.text.provisional.codelens.ICodeLensContext;
import org.eclipse.jface.text.provisional.codelens.ICodeLensProvider;
import org.eclipse.lsp4e.LSPEclipseUtils;
import org.eclipse.lsp4e.LanguageServiceAccessor;
import org.eclipse.lsp4e.LanguageServiceAccessor.LSPDocumentInfo;
import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.CodeLensParams;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.ui.texteditor.ITextEditor;

public class LSPCodeLensProvider implements ICodeLensProvider {

	@Override
	public CompletableFuture<ICodeLens[]> provideCodeLenses(ICodeLensContext context, IProgressMonitor monitor) {
		ITextEditor textEditor = ((IEditorCodeLensContext) context).getTextEditor();

		LSPDocumentInfo info = null;
		Collection<LSPDocumentInfo> infos = LanguageServiceAccessor.getLSPDocumentInfosFor(
				LSPEclipseUtils.getDocument((ITextEditor) textEditor),
				capabilities -> capabilities.getCodeLensProvider() != null);
		if (!infos.isEmpty()) {
			info = infos.iterator().next();
		} else {
			info = null;
		}
		if (info != null) {

			CodeLensParams param = new CodeLensParams(new TextDocumentIdentifier(info.getFileUri().toString()));
			final CompletableFuture<List<? extends CodeLens>> codeLens = info.getLanguageClient()
					.getTextDocumentService().codeLens(param);
			return codeLens.thenApply(lens -> {
				List<ICodeLens> lenses = new ArrayList<>();
				for (CodeLens cl : lens) {
					lenses.add(new LSPCodeLens(cl));
				}
				return lenses.toArray(new ICodeLens[lenses.size()]);
			});
			// try {
			//
			//
			//
			// List<ICodeLens> lenses = new ArrayList<>();
			// List<? extends CodeLens> lens = codeLens.get(5000, TimeUnit.MILLISECONDS);
			// for (CodeLens cl : lens) {
			// lenses.add(new LSPCodeLens(cl));
			// }
			// return lenses.toArray(new ICodeLens[lenses.size()]);
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

		}
		return null;
	}

	@Override
	public CompletableFuture<ICodeLens> resolveCodeLens(ICodeLensContext context, ICodeLens codeLens,
			IProgressMonitor monitor) {
		ITextEditor textEditor = ((IEditorCodeLensContext) context).getTextEditor();

		LSPDocumentInfo info = null;
		Collection<LSPDocumentInfo> infos = LanguageServiceAccessor.getLSPDocumentInfosFor(
				LSPEclipseUtils.getDocument((ITextEditor) textEditor),
				capabilities -> capabilities.getCodeLensProvider() != null
						&& capabilities.getCodeLensProvider().isResolveProvider());
		if (!infos.isEmpty()) {
			info = infos.iterator().next();
		} else {
			info = null;
		}
		if (info != null) {
			LSPCodeLens lscl = ((LSPCodeLens) codeLens);
			CodeLens unresolved = lscl.getCl();
			return info.getLanguageClient().getTextDocumentService().resolveCodeLens(unresolved).thenApply(resolved -> {
				lscl.update(resolved);
				return lscl;
			});
		}
		return null;
	}

}
