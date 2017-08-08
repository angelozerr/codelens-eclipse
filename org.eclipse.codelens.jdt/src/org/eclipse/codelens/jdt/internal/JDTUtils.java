package org.eclipse.codelens.jdt.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.core.util.ClassFileBytesDisassembler;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.provisional.codelens.Range;
import org.eclipse.ui.texteditor.ITextEditor;

public class JDTUtils {

	private static Set<String> SILENCED_CODEGENS = Collections.singleton("lombok");
	public static final String MISSING_SOURCES_HEADER = " // Failed to get sources. Instead, stub sources have been generated.\n"
			+ " // Implementation of methods is unavailable.\n";
	private static final String LF = "\n";
	
	public static ICompilationUnit resolveCompilationUnit(ITextEditor textEditor) {
		return (ICompilationUnit) EditorUtility.getEditorInputJavaElement(textEditor, true);
	}

	public static boolean isHiddenGeneratedElement(IJavaElement element) {
		// generated elements are tagged with javax.annotation.Generated and
		// they need to be filtered out
		if (element instanceof IAnnotatable) {
			try {
				IAnnotation[] annotations = ((IAnnotatable) element).getAnnotations();
				if (annotations.length != 0) {
					for (IAnnotation annotation : annotations) {
						if (isSilencedGeneratedAnnotation(annotation)) {
							return true;
						}
					}
				}
			} catch (JavaModelException e) {
				// ignore
			}
		}
		return false;
	}

	private static boolean isSilencedGeneratedAnnotation(IAnnotation annotation) throws JavaModelException {
		if ("javax.annotation.Generated".equals(annotation.getElementName())) {
			IMemberValuePair[] memberValuePairs = annotation.getMemberValuePairs();
			for (IMemberValuePair m : memberValuePairs) {
				if ("value".equals(m.getMemberName()) && IMemberValuePair.K_STRING == m.getValueKind()) {
					if (m.getValue() instanceof String) {
						return SILENCED_CODEGENS.contains(m.getValue());
					} else if (m.getValue() instanceof Object[]) {
						for (Object val : (Object[]) m.getValue()) {
							if (SILENCED_CODEGENS.contains(val)) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	public static Range toRange(IOpenable openable, int offset, int length) throws JavaModelException {
		if (offset > 0 || length > 0) {
			int[] loc = null;
			int[] endLoc = null;
			IBuffer buffer = openable.getBuffer();
			// if (buffer != null) {
			// loc = JsonRpcHelpers.toLine(buffer, offset);
			// endLoc = JsonRpcHelpers.toLine(buffer, offset + length);
			// }
			// if (loc == null) {
			// loc = new int[2];
			// }
			// if (endLoc == null) {
			// endLoc = new int[2];
			// }
			// setPosition(range.getStart(), loc);
			// setPosition(range.getEnd(), endLoc);
			IDocument document = toDocument(buffer);
			try {
				int line = document.getLineOfOffset(offset);
				int column = offset - document.getLineOffset(line);
				return new Range(line + 1, column + 1);
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns an {@link IDocument} for the given buffer. The implementation tries
	 * to avoid copying the buffer unless required. The returned document may or may
	 * not be connected to the buffer.
	 *
	 * @param buffer
	 *            a buffer
	 * @return a document with the same contents as the buffer or <code>null</code>
	 *         is the buffer is <code>null</code>
	 */
	public static IDocument toDocument(IBuffer buffer) {
		if (buffer == null) {
			return null;
		}
		if (buffer instanceof IDocument) {
			return (IDocument) buffer;
		}
		/*
		 * else if (buffer instanceof org.eclipse.jdt.ls.core.internal.DocumentAdapter)
		 * { IDocument document = ((org.eclipse.jdt.ls.core.internal.DocumentAdapter)
		 * buffer).getDocument(); if (document != null) { return document; } }
		 */
		return new org.eclipse.jdt.internal.core.DocumentAdapter(buffer);
	}

	public static IJavaElement findElementAtSelection(ITypeRoot unit, Range range) throws JavaModelException, BadLocationException {
		return findElementAtSelection(unit, range.startLineNumber - 1, range.startColumn - 1);
	}
	
	public static IJavaElement findElementAtSelection(ITypeRoot unit, int line, int column) throws JavaModelException, BadLocationException {
		IJavaElement[] elements = findElementsAtSelection(unit, line, column);
		if (elements != null && elements.length == 1) {
			return elements[0];
		}
		return null;
	}

	public static IJavaElement[] findElementsAtSelection(ITypeRoot unit, int line, int column) throws JavaModelException, BadLocationException {
		if (unit == null) {
			return null;
		}
		int offset = toDocument(unit.getBuffer()).getLineOffset(line ) + column;
		if (offset > -1) {
			return unit.codeSelect(offset, 0);
		}
		if (unit instanceof IClassFile) {
			IClassFile classFile = (IClassFile) unit;
			String contents = disassemble(classFile);
			if (contents != null) {
				IDocument document = new Document(contents);
				try {
					offset = document.getLineOffset(line) + column;
					if (offset > -1) {
						String name = parse(contents, offset);
						if (name == null) {
							return null;
						}
						SearchPattern pattern = SearchPattern.createPattern(name, IJavaSearchConstants.TYPE,
								IJavaSearchConstants.DECLARATIONS, SearchPattern.R_FULL_MATCH);

						IJavaSearchScope scope = createSearchScope(unit.getJavaProject());

						List<IJavaElement> elements = new ArrayList<>();
						SearchRequestor requestor = new SearchRequestor() {
							@Override
							public void acceptSearchMatch(SearchMatch match) {
								if (match.getElement() instanceof IJavaElement) {
									elements.add((IJavaElement) match.getElement());
								}
							}
						};
						SearchEngine searchEngine = new SearchEngine();
						searchEngine.search(pattern,
								new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() }, scope,
								requestor, null);
						return elements.toArray(new IJavaElement[0]);
					}
				} catch (BadLocationException | CoreException e) {
					//JavaLanguageServerPlugin.logException(e.getMessage(), e);
				}
			}
		}
		return null;
	}
	
	public static IJavaSearchScope createSearchScope(IJavaProject project) {
		if (project == null) {
			return SearchEngine.createWorkspaceScope();
		}
		return SearchEngine.createJavaSearchScope(new IJavaProject[] { project },
				IJavaSearchScope.SOURCES | IJavaSearchScope.APPLICATION_LIBRARIES | IJavaSearchScope.SYSTEM_LIBRARIES);
	}
	
	private static String parse(String contents, int offset) {
		if (contents == null || offset < 0 || contents.length() < offset
				|| !isJavaIdentifierOrPeriod(contents.charAt(offset))) {
			return null;
		}
		int start = offset;
		while (start - 1 > -1 && isJavaIdentifierOrPeriod(contents.charAt(start - 1))) {
			start--;
		}
		int end = offset;
		while (end <= contents.length() && isJavaIdentifierOrPeriod(contents.charAt(end))) {
			end++;
		}
		if (end >= start) {
			return contents.substring(start, end);
		}
		return null;
	}
	
	private static boolean isJavaIdentifierOrPeriod(char ch) {
		return Character.isJavaIdentifierPart(ch) || ch == '.';
	}
	
	public static String disassemble(IClassFile classFile) {
		ClassFileBytesDisassembler disassembler = ToolFactory.createDefaultClassFileBytesDisassembler();
		String disassembledByteCode = null;
		try {
			disassembledByteCode = disassembler.disassemble(classFile.getBytes(), LF,
					ClassFileBytesDisassembler.WORKING_COPY);
			disassembledByteCode = MISSING_SOURCES_HEADER + LF + disassembledByteCode;
		} catch (Exception e) {
			//JavaLanguageServerPlugin.logError("Unable to disassemble " + classFile.getHandleIdentifier());
			e.printStackTrace();
		}
		return disassembledByteCode;
	}


}
