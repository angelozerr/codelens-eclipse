package org.eclipse.codelens.samples;

import org.eclipse.jface.text.provisional.codelens.CodeLens;

public class ClassCodeLens extends CodeLens {

	private String className;
	
	public ClassCodeLens(String className, int startLineNumber) {
		super(startLineNumber);
		this.className = className;
	}

	public String getClassName() {
		return className;
	}
}
