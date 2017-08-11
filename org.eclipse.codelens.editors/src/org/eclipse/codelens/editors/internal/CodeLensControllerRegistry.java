package org.eclipse.codelens.editors.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.codelens.editors.ICodeLensController;
import org.eclipse.codelens.editors.ICodeLensControllerFactory;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionDelta;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IRegistryChangeEvent;
import org.eclipse.core.runtime.IRegistryChangeListener;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.texteditor.ITextEditor;

public class CodeLensControllerRegistry implements IRegistryChangeListener {

	private static final CodeLensControllerRegistry INSTANCE = new CodeLensControllerRegistry();
	private static final String EXTENSION_CODELENS_CONTROLLER_FACTORIES = "codeLensControllerFactories";

	public static CodeLensControllerRegistry getInstance() {
		return INSTANCE;
	}

	private boolean loaded;
	private final List<ICodeLensControllerFactory> factories;

	public CodeLensControllerRegistry() {
		this.factories = new ArrayList<>();
		this.loaded = false;
	}

	public ICodeLensController create(ITextEditor textEditor) {
		ICodeLensControllerFactory factory = getFactory(textEditor);
		if (factory != null) {
			return factory.create(textEditor);
		}
		return null;
	}

	public ICodeLensControllerFactory getFactory(ITextEditor textEditor) {
		loadFactoriesIfNeeded();
		for (ICodeLensControllerFactory factory : factories) {
			if (factory.isRelevant(textEditor)) {
				return factory;
			}
		}
		return null;
	}

	@Override
	public void registryChanged(IRegistryChangeEvent event) {
		IExtensionDelta[] deltas = event.getExtensionDeltas(CodeLensEditorPlugin.PLUGIN_ID,
				EXTENSION_CODELENS_CONTROLLER_FACTORIES);
		if (deltas != null) {
			for (IExtensionDelta delta : deltas)
				handleCodeLensProvidersDelta(delta);
		}
	}

	private void loadFactoriesIfNeeded() {
		if (loaded) {
			return;
		}
		loadFactories();
	}

	/**
	 * Load the SourceMap language supports.
	 */
	private synchronized void loadFactories() {
		if (loaded) {
			return;
		}

		try {
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			if (registry == null) {
				return;
			}
			IConfigurationElement[] cf = registry.getConfigurationElementsFor(CodeLensEditorPlugin.PLUGIN_ID,
					EXTENSION_CODELENS_CONTROLLER_FACTORIES);
			loadCodeLensProvidersFromExtension(cf);
		} finally {
			loaded = true;
		}
	}

	/**
	 * Add the SourceMap language supports.
	 */
	private synchronized void loadCodeLensProvidersFromExtension(IConfigurationElement[] cf) {
		for (IConfigurationElement ce : cf) {
			try {
				ICodeLensControllerFactory factory = (ICodeLensControllerFactory) ce.createExecutableExtension("class");
				factories.add(factory);
			} catch (Throwable e) {
				CodeLensEditorPlugin.log(e);
			}
		}
	}

	protected void handleCodeLensProvidersDelta(IExtensionDelta delta) {
		if (!loaded) // not loaded yet
			return;

		IConfigurationElement[] cf = delta.getExtension().getConfigurationElements();

		// List<CodeLensProviderType> list = new
		// ArrayList<CodeLensProviderType>(
		// codeLensProviders);
		// if (delta.getKind() == IExtensionDelta.ADDED) {
		// loadCodeLensProvidersFromExtension(cf, list);
		// } else {
		// int size = list.size();
		// CodeLensProviderType[] st = new CodeLensProviderType[size];
		// list.toArray(st);
		// int size2 = cf.length;
		//
		// for (int i = 0; i < size; i++) {
		// for (int j = 0; j < size2; j++) {
		// if (st[i].getId().equals(cf[j].getAttribute("id"))) {
		// list.remove(st[i]);
		// }
		// }
		// }
		// }
		// codeLensProviders = list;
	}

	public void initialize() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		registry.addRegistryChangeListener(this, CodeLensEditorPlugin.PLUGIN_ID);
	}

	public void destroy() {
		Platform.getExtensionRegistry().removeRegistryChangeListener(this);
	}
}
