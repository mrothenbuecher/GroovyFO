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
 * @author Michael Rothenb�cher, Finetech GmbH & Co. KG
 * 
 *         JFOP erwartet als ersten Parameter das Groovyscript welches
 *         ausgef�hrt werden soll und �bergibt alle Parameter an dieses weiter
 * 
 */
public class ScriptExecutor implements ContextRunnable {

	
	public static int executeScript(FOPSessionContext arg0, String[] arg1){
		
		GroovyShell shell = null;
		boolean error = false;
		FO.eingabe("DATEI.F");
		// Genug Parameter �bergben?
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
						binding.setVariable("args", arg1);

						// Imports festlegen damit diese nicht selbst
						// hinzugef�gt werden m�ssen
						CompilerConfiguration cc = new CompilerConfiguration();
						ImportCustomizer ic = new ImportCustomizer();
						// abas Standard
						ic.addStarImports("de.abas.eks.jfop.remote");
						//ic.addStaticStars("de.abas.eks.jfop.remote.EKS");
						// Helferklassen f�r GroovyFO
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

						// Script ausf�hren
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
						return 0;
					} catch (CommandException e) {
						// FIXME Sprach unabh�ngigkeit
						FO.box("Fehler", e.getMessage());
						FO.box("Unbehandelte Ausnahme in " + arg1[1],
								getStacktrace(e));
						error = true;
					} catch (AbortedException e) {
						// FIXME Sprach unabh�ngigkeit
						FO.box("FOP abgebrochen",
								"FOP wurde durch Anwender abgebrochen");
						error = true;
					} catch (CompilationFailedException e) {
						// FIXME Sprach unabh�ngigkeit
						FO.box("�bersetzung fehlgeschlagen",
								"GroovyFO konnte das Script nicht �bersetzen: "
										+ e.getMessage() + "\n" + getStacktrace(e));
						error = true;
					} catch (Exception e) {
						// FIXME Sprach unabh�ngigkeit
						FO.box("Unbehandelte Ausnahme in " + arg1[1],
								getStacktrace(e));
						error = true;
					} finally {
						clearGroovyClassesCache();
						if(error){
							//FIXME m�glichkeit f�r b�sen Fehler
							return -2;
						}
					}
				} else {
					// FIXME Sprach unabh�ngigkeit
					FO.box("Unzureichende Argumente",
							"Groovy Script ist keine Datei!");
				}
			} else {
				// FIXME Sprach unabh�ngigkeit
				FO.box("Unzureichende Argumente",
						"Groovy Script existiert nicht!");
			}
		} else {
			// FIXME Sprach unabh�ngigkeit
			FO.box("Unzureichende Argumente", "keine Groovy Script angegeben!");
		}
		return -1;
	}
	
	@Override
	public int runFop(FOPSessionContext arg0, String[] arg1)
			throws FOPException {
		return ScriptExecutor.executeScript(arg0, arg1);
	}
	
	private static String getStacktrace(Exception e){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}
	
	protected static boolean clearGroovyClassesCache() {
		try {
			Class<MetaClassRegistryImpl> mcriClass = MetaClassRegistryImpl.class;

			Field constantMetaClassesField = mcriClass
					.getDeclaredField("constantMetaClasses");
			constantMetaClassesField.setAccessible(true);

			Field constantMetaClassCountField = mcriClass
					.getDeclaredField("constantMetaClassCount");
			constantMetaClassCountField.setAccessible(true);

			Class<GroovySystem> gsClass = GroovySystem.class;
			Field metaClassRegistryField = gsClass
					.getDeclaredField("META_CLASS_REGISTRY");
			metaClassRegistryField.setAccessible(true);
			MetaClassRegistryImpl mcri = (MetaClassRegistryImpl) metaClassRegistryField
					.get(null);
			ConcurrentReaderHashMap constantMetaClasses = (ConcurrentReaderHashMap) constantMetaClassesField
					.get(mcri);

			constantMetaClasses.clear();
			constantMetaClassCountField.set(mcri, 0);

			return true;
		} catch (Exception e) {
			return false;
		}
	}
}