package org.eclipse.text.viewzone.codelens;

public class Range {

	/**
	 * Line number on which the range starts (starts at 1).
	 */
	public final int startLineNumber;

	public Range(int startLineNumber) {
		this.startLineNumber = startLineNumber;
	}
	/**
	 * Column on which the range starts in line `startLineNumber` (starts at 1).
	 */
	// readonly startColumn: number;
	/**
	 * Line number on which the range ends.
	 */
	// readonly endLineNumber: number;
	/**
	 * Column on which the range ends in line `endLineNumber`.
	 */
	// readonly endColumn: number;
}
