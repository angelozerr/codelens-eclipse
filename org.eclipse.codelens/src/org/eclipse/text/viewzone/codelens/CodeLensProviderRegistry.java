package org.eclipse.text.viewzone.codelens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CodeLensProviderRegistry {

	private final Map<String, Collection<ICodeLensProvider>> providersMap;

	public CodeLensProviderRegistry() {
		this.providersMap = new HashMap<>();
	}

	public void register(String contentTypeId, ICodeLensProvider provider) {
		Collection<ICodeLensProvider> providers = providersMap.get(contentTypeId);
		if (providers == null) {
			providers = new ArrayList<>();
			providersMap.put(contentTypeId, providers);
		}
		providers.add(provider);
	}

	public Collection<ICodeLensProvider> all(String contentTypeId) {
		return providersMap.get(contentTypeId);
	}
}
