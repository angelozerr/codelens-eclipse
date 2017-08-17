package org.eclipse.codelens.lsp4e.internal;

import org.eclipse.jface.text.provisional.codelens.CodeLens;
import org.eclipse.jface.text.provisional.codelens.Range;
import org.eclipse.lsp4j.Command;

public class LSCodeLens extends CodeLens {

	private org.eclipse.lsp4j.CodeLens cl;

	public LSCodeLens(org.eclipse.lsp4j.CodeLens cl) {
		super(toRange(cl));
		update(cl);
		this.cl = cl;
	}

	private static Range toRange(org.eclipse.lsp4j.CodeLens cl) {
		org.eclipse.lsp4j.Range range = cl.getRange();
		return new Range(range.getStart().getLine() + 1, range.getStart().getCharacter());
	}

	@Override
	public void open() {
		// TODO Auto-generated method stub

	}

	public org.eclipse.lsp4j.CodeLens getCl() {
		return cl;
	}

	public void update(org.eclipse.lsp4j.CodeLens cl) {
		Command command = cl.getCommand();
		if (command != null) {
			org.eclipse.jface.text.provisional.codelens.Command c = new org.eclipse.jface.text.provisional.codelens.Command(
					command.getTitle(), command.getCommand());
			super.setCommand(c);
		}
	}

}
