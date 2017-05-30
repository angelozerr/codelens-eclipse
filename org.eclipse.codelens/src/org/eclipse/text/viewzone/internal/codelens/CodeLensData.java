package org.eclipse.text.viewzone.internal.codelens;

import org.eclipse.text.viewzone.codelens.ICodeLens;
import org.eclipse.text.viewzone.codelens.ICodeLensProvider;

public class CodeLensData {

	private final ICodeLens symbol;
	private final ICodeLensProvider provider;

	public CodeLensData(ICodeLens symbol, ICodeLensProvider provider) {
		this.symbol = symbol;
		this.provider = provider;
	}

	public ICodeLens getSymbol() {
		return symbol;
	}

	public ICodeLensProvider getProvider() {
		return provider;
	}
}
