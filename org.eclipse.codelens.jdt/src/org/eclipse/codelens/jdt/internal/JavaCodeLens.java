package org.eclipse.codelens.jdt.internal;

import org.eclipse.jface.text.provisional.codelens.CodeLens;
import org.eclipse.jface.text.provisional.codelens.Range;

public class JavaCodeLens extends CodeLens {

	private final String type;

	public JavaCodeLens(Range range, String type) {
		super(range);
		this.type = type;
	}

	@Override
	public void open() {
		// TODO Auto-generated method stub

	}
	
	public String getType() {
		return type;
	}

}
