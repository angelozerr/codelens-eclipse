/**
 *  Copyright (c) 2015-2016 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *  Lorenzo Dalla Vecchia <lorenzo.dallavecchia@webratio.com> - getter for ProblemManager
 */
package org.eclipse.codelens.editors.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class CodeLensEditorPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.eclipse.codelens.editors"; //$NON-NLS-1$

	// The shared instance.
	private static CodeLensEditorPlugin plugin;

	/**
	 * The constructor.
	 */
	public CodeLensEditorPlugin() {
		super();
		plugin = this;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		CodeLensControllerRegistry.getInstance().initialize();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		CodeLensControllerRegistry.getInstance().initialize();
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static CodeLensEditorPlugin getDefault() {
		return plugin;
	}

	public static void log(IStatus status) {
		CodeLensEditorPlugin plugin = getDefault();
		if (plugin != null) {
			plugin.getLog().log(status);
		} else {
			System.err.println(status.getPlugin() + ": " + status.getMessage()); //$NON-NLS-1$
		}
	}

	public static void log(Throwable e) {
		if (e instanceof CoreException) {
			log(new Status(IStatus.ERROR, PLUGIN_ID, ((CoreException) e).getStatus().getSeverity(), e.getMessage(),
					e.getCause()));
		} else {
			log(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e));
		}
	}
}
