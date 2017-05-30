package org.eclipse.text.viewzone.codelens;

public class CodeLens implements ICodeLens {

	private Range range;
	private ICommand command;

	public CodeLens(int startLineNumber) {
		this.range = new Range(startLineNumber);
	}

	@Override
	public Range getRange() {
		return range;
	}

	@Override
	public boolean isResolved() {
		return getCommand() != null;
	}

	@Override
	public ICommand getCommand() {
		return command;
	}
	
	public void setCommand(ICommand command) {
		this.command = command;
	}

}
