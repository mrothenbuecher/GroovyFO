package de.finetech.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import groovy.lang.GroovySystem;
import groovy.lang.Script;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;

import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.runtime.metaclass.ConcurrentReaderHashMap;
import org.codehaus.groovy.runtime.metaclass.MetaClassRegistryImpl;

import de.abas.eks.jfop.AbortedException;
import de.abas.eks.jfop.CommandException;
import de.abas.eks.jfop.FOPException;
import de.abas.eks.jfop.remote.ContextRunnable;
import de.abas.eks.jfop.remote.FO;
import de.abas.eks.jfop.remote.FOPSessionContext;

/**
 * @author Michael KÃ¼rbis, Finetech GmbH & Co. KG
 * 
 *         JFOP erwartet als zweiten Parameter das Groovyscript welches
 *         ausgefÃ¼hrt werden soll und Ã¼bergibt alle parameter an dieses weiter
 * 
 */
public class ScriptExcecuter implements ContextRunnable {

	@Override
	public int runFop(FOPSessionContext arg0, String[] arg1)
			throws FOPException {
		GroovyShell shell = null;
		Script script = null;
		boolean error = false;
		// Genug Parameter übergben?
		if (arg1.length > 1) {
			File groovyScript = new File(arg1[1]);
			// existiert die Datei ?
			if (groovyScript.exists()) {
				// ist es eine Datei ?
				if (groovyScript.isFile()) {
					try {
						Binding binding = new Binding();
						// Parameter weitergeben
						binding.setVariable("arg0", arg0);
						binding.setVariable("arg1", arg1);

						// Imports festlegen damit diese nicht selbst
						// hinzugefügt werden müssen
						CompilerConfiguration cc = new CompilerConfiguration();
						ImportCustomizer ic = new ImportCustomizer();
						// abas Standard
						ic.addStarImports("de.abas.eks.jfop.remote");
						// Helferklassen für GroovyFO
						ic.addStarImports("de.finetech.groovy");
						ic.addStarImports("de.finetech.groovy.utils");
						ic.addStarImports("de.finetech.groovy.utils.datatypes");
						ic.addStarImports("de.finetech.utils","de.finetech.utils.charts");
						//
						ic.addImports("java.awt.Color", "java.util.Calendar",
								"java.text.SimpleDateFormat",
								"java.text.DateFormat", "java.util.Date");

						cc.addCompilationCustomizers(ic);
						// Basisklasse festlegen
						cc.setScriptBaseClass("de.finetech.groovy.AbasBaseScript");

						// Script ausführen
						GroovyClassLoader loader = new GroovyClassLoader();
						shell = new GroovyShell(loader, binding, cc);
						Script gscript = shell.parse(groovyScript);
						//gscript.run();
						Object o = gscript.run();
						if(o != null){
							GroovySystem.getMetaClassRegistry().removeMetaClass(o.getClass());
							GroovySystem.getMetaClassRegistry().removeMetaClass(gscript.getClass());
						}
						loader.clearCache();
						shell = null;
						error = false;
					} catch (CommandException e) {
						// FIXME Sprach unabhängigkeit
						e.printStackTrace();
						FO.box("Fehler", e.getMessage());
						StringWriter sw = new StringWriter();
						PrintWriter pw = new PrintWriter(sw);
						e.printStackTrace(pw);
						FO.box("Unbehandelte Ausnahme in " + arg1[1],
								sw.toString());
						error = true;
					} catch (AbortedException e) {
						// FIXME Sprach unabhängigkeit
						FO.box("FOP abgebrochen",
								"FOP wurde durch Anwender abgebrochen");
						error = true;
					} catch (CompilationFailedException e) {
						StringWriter sw = new StringWriter();
						PrintWriter pw = new PrintWriter(sw);
						e.printStackTrace(pw);
						FO.box("Übersetzung fehlgeschlagen",
								"GroovyFO konnte das Script nicht übersetzen: "
										+ e.getMessage() + "\n" + sw.toString());
						error = true;
					} catch (Exception e) {
						// FIXME Sprach unabhängigkeit
						StringWriter sw = new StringWriter();
						PrintWriter pw = new PrintWriter(sw);
						e.printStackTrace(pw);
						FO.box("Unbehandelte Ausnahme in " + arg1[1],
								sw.toString());
						error = true;
					} finally {
						clearGroovyClassesCache();
						if(error){
							return -2;
						}
						return 0;
					}
				} else {
					FO.box("Unzureichende Argumente",
							"Groovy Script ist keine Datei!");
				}
			} else {
				FO.box("Unzureichende Argumente",
						"Groovy Script existiert nicht!");
			}
		} else {
			FO.box("Unzureichende Argumente", "keine Groovy Script angegeben!");
		}
		return -1;
	}
	
	protected boolean clearGroovyClassesCache() {
		try {
			// Retrieve the fields we will manipulate
			Class<MetaClassRegistryImpl> mcriClass = MetaClassRegistryImpl.class;

			Field constantMetaClassesField = mcriClass
					.getDeclaredField("constantMetaClasses");
			constantMetaClassesField.setAccessible(true);

			Field constantMetaClassCountField = mcriClass
					.getDeclaredField("constantMetaClassCount");
			constantMetaClassCountField.setAccessible(true);

			// Retrieve the object to be manipulate
			Class<GroovySystem> gsClass = GroovySystem.class;
			Field metaClassRegistryField = gsClass
					.getDeclaredField("META_CLASS_REGISTRY");
			metaClassRegistryField.setAccessible(true);
			MetaClassRegistryImpl mcri = (MetaClassRegistryImpl) metaClassRegistryField
					.get(null);
			ConcurrentReaderHashMap constantMetaClasses = (ConcurrentReaderHashMap) constantMetaClassesField
					.get(mcri);

			// Clears the map
			constantMetaClasses.clear();
			constantMetaClassCountField.set(mcri, 0);

			return true;
		} catch (Exception e) {
			// Log this exception. It indicates that the code changed.
			return false;
		}
	}
}