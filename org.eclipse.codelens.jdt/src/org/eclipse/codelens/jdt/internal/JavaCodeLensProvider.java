package org.eclipse.codelens.jdt.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.tools.DocumentationTool.Location;

import org.eclipse.codelens.editors.IEditorCodeLensContext;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.internal.junit.util.CoreTestSearchEngine;
import org.eclipse.jdt.junit.JUnitCore;
import org.eclipse.jface.text.provisional.codelens.AbstractSyncCodeLensProvider;
import org.eclipse.jface.text.provisional.codelens.Command;
import org.eclipse.jface.text.provisional.codelens.ICodeLens;
import org.eclipse.jface.text.provisional.codelens.ICodeLensContext;
import org.eclipse.jface.text.provisional.codelens.Range;

/**
 * 
 * @see https://github.com/eclipse/eclipse.jdt.ls/blob/master/org.eclipse.jdt.ls.core/src/org/eclipse/jdt/ls/core/internal/handlers/CodeLensHandler.java
 *
 */
public class JavaCodeLensProvider extends AbstractSyncCodeLensProvider {

	private static final String IMPLEMENTATION_TYPE = "implementations";
	private static final String REFERENCES_TYPE = "references";

	@Override
	protected ICodeLens[] provideSyncCodeLenses(ICodeLensContext context, IProgressMonitor monitor) {
		// try {
		// Thread.sleep(1000);
		// } catch (InterruptedException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		ITypeRoot unit = JDTUtils.resolveCompilationUnit(((IEditorCodeLensContext) context).getTextEditor());
		if (unit == null || /* !unit.getResource().exists() || */ monitor.isCanceled()) {
			return null;
		}
		try {
			IJavaElement[] elements = unit.getChildren();
			List<ICodeLens> lenses = new ArrayList<>(elements.length);
			collectCodeLenses(unit, elements, lenses, monitor);
			if (monitor.isCanceled()) {
				lenses.clear();
			}

			try {
				if (CoreTestSearchEngine.isTestOrTestSuite(unit.findPrimaryType())) {
					IType[] tests = JUnitCore.findTestTypes(unit, monitor);
					System.err.println(tests);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return lenses.toArray(new ICodeLens[lenses.size()]);
		} catch (JavaModelException e) {
			// JavaLanguageServerPlugin.logException("Problem getting code lenses for" +
			// unit.getElementName(), e);
			e.printStackTrace();
		}
		return null;
	}

	private void collectCodeLenses(ITypeRoot unit, IJavaElement[] elements, List<ICodeLens> lenses,
			IProgressMonitor monitor) throws JavaModelException {
		for (IJavaElement element : elements) {
			if (monitor.isCanceled()) {
				return;
			}
			if (element.getElementType() == IJavaElement.TYPE) {
				collectCodeLenses(unit, ((IType) element).getChildren(), lenses, monitor);
			} else if (element.getElementType() != IJavaElement.METHOD || JDTUtils.isHiddenGeneratedElement(element)) {
				continue;
			}

			// if (preferenceManager.getPreferences().isReferencesCodeLensEnabled()) {
			ICodeLens lens = getCodeLens(REFERENCES_TYPE, element, unit);
			lenses.add(lens);
			// }
			// if (preferenceManager.getPreferences().isImplementationsCodeLensEnabled() &&
			// element instanceof IType) {
			if (element instanceof IType) {
				IType type = (IType) element;
				if (type.isInterface() || Flags.isAbstract(type.getFlags())) {
					lens = getCodeLens(IMPLEMENTATION_TYPE, element, unit);
					lenses.add(lens);
				}
			}
		}
	}

	private ICodeLens getCodeLens(String type, IJavaElement element, ITypeRoot unit) throws JavaModelException {
		ISourceRange r = ((ISourceReference) element).getNameRange();
		final Range range = JDTUtils.toRange(unit, r.getOffset(), r.getLength());

		ICodeLens lens = new JavaCodeLens(range, type);

		// String uri = ResourceUtils.toClientUri(JDTUtils.getFileURI(unit));
		// lens.setData(Arrays.asList(uri, range.getStart(), type));
		return lens;
	}

	@Override
	protected ICodeLens resolveSyncCodeLens(ICodeLensContext context, ICodeLens lens, IProgressMonitor monitor) {
		if (lens == null) {
			return null;
		}
		ITypeRoot unit = JDTUtils.resolveCompilationUnit(((IEditorCodeLensContext) context).getTextEditor());
		if (unit == null) {
			return lens;
		}
		Range range = lens.getRange();
		try {
			IJavaElement element = JDTUtils.findElementAtSelection(unit, range);
			JavaCodeLens javaLens = ((JavaCodeLens) lens);
			String type = javaLens.getType();
			if (REFERENCES_TYPE.equals(type)) {
				List<Location> references = findReferences(element, monitor);
				int refCount = references.size();
				javaLens.setCommand(new Command(refCount + " references", ""));
			} else if (IMPLEMENTATION_TYPE.equals(type)) {
				if (element instanceof IType) {
					List<Location> references = findImplementations((IType) element, monitor);
					int refCount = references.size();
					javaLens.setCommand(new Command(refCount + " implementations", ""));
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lens;
	}

	private List<Location> findReferences(IJavaElement element, IProgressMonitor monitor)
			throws JavaModelException, CoreException {
		if (element == null) {
			return Collections.emptyList();
		}
		SearchPattern pattern = SearchPattern.createPattern(element, IJavaSearchConstants.REFERENCES);
		final List<Location> result = new ArrayList<>();
		SearchEngine engine = new SearchEngine();
		engine.search(pattern, new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() },
				createSearchScope(), new SearchRequestor() {

					@Override
					public void acceptSearchMatch(SearchMatch match) throws CoreException {
						Object o = match.getElement();
						if (o instanceof IJavaElement) {
							IJavaElement element = (IJavaElement) o;
							ICompilationUnit compilationUnit = (ICompilationUnit) element
									.getAncestor(IJavaElement.COMPILATION_UNIT);
							if (compilationUnit == null) {
								return;
							}
							Location location = null; // JDTUtils.toLocation(compilationUnit, match.getOffset(),
														// match.getLength());
							result.add(location);
						}
					}
				}, monitor);

		return result;
	}

	private List<Location> findImplementations(IType type, IProgressMonitor monitor) throws JavaModelException {
		IType[] results = type.newTypeHierarchy(monitor).getAllSubtypes(type);
		final List<Location> result = new ArrayList<>();
		for (IType t : results) {
			ICompilationUnit compilationUnit = (ICompilationUnit) t.getAncestor(IJavaElement.COMPILATION_UNIT);
			if (compilationUnit == null) {
				continue;
			}
			Location location = null; // JDTUtils.toLocation(t);
			result.add(location);
		}
		return result;
	}

	private IJavaSearchScope createSearchScope() throws JavaModelException {
		IJavaProject[] projects = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()).getJavaProjects();
		return SearchEngine.createJavaSearchScope(projects, IJavaSearchScope.SOURCES);
	}

}
