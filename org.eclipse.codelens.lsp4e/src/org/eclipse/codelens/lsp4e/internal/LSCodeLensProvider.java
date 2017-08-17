package org.eclipse.codelens.lsp4e.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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

public class LSCodeLensProvider implements ICodeLensProvider {

	@Override
	public ICodeLens[] provideCodeLenses(ICodeLensContext context, IProgressMonitor monitor) {
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

			CodeLensParams param = new CodeLensParams(new TextDocumentIdentifier(info.getFileUri().toString()));;
			final CompletableFuture<List<? extends CodeLens>> codeLens = info.getLanguageClient().getTextDocumentService().codeLens(param);
			try {
				List<ICodeLens> lenses = new ArrayList<>();
				List<? extends CodeLens> lens = codeLens.get(5000, TimeUnit.MILLISECONDS);
				for (CodeLens cl : lens) {
					lenses.add(new LSCodeLens(cl));
				}
				return lenses.toArray(new ICodeLens[lenses.size()]);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return null;
	}

	@Override
	public ICodeLens resolveCodeLens(ICodeLensContext context, ICodeLens codeLens, IProgressMonitor monitor) {
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
			LSCodeLens lscl = ((LSCodeLens) codeLens);
			CodeLens unresolved = lscl.getCl(); 
			try {
				CodeLens resolved = info.getLanguageClient().getTextDocumentService().resolveCodeLens(unresolved).get(5000, TimeUnit.MILLISECONDS);
				lscl.update(resolved);
				return lscl;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

}
