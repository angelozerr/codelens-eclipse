package org.eclipse.text.viewzone.codelens;

import org.eclipse.jface.text.Position;

public interface ICodeLens {

	/**
	 * The range in which this code lens is valid. Should only span a single
	 * line.
	 */
	Position getRange();
}
