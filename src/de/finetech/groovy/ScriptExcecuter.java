package de.finetech.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import de.abas.eks.jfop.AbortedException;
import de.abas.eks.jfop.CommandException;
import de.abas.eks.jfop.FOPException;
import de.abas.eks.jfop.remote.ContextRunnable;
import de.abas.eks.jfop.remote.FO;
import de.abas.eks.jfop.remote.FOPSessionContext;

/**
 * @author Michael K√ºrbis, Finetech GmbH & Co. KG
 * 
 *         JFOP erwartet als zweiten Parameter das Groovyscript welches
 *         ausgef√ºhrt werden soll und √ºbergibt alle parameter an dieses weiter
 * 
 */
public class ScriptExcecuter implements ContextRunnable {

	@Override
	public int runFop(FOPSessionContext arg0, String[] arg1)
			throws FOPException {
		// Genug Parameter √ºbergben?
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
						// hinzugef√ºgt werden m√ºssen
						CompilerConfiguration cc = new CompilerConfiguration();
						ImportCustomizer ic = new ImportCustomizer();
						// abas Standard
						ic.addStarImports("de.abas.eks.jfop.remote");
						ic.addStarImports("de.finetech.groovy");
						ic.addStarImports("de.finetech.groovy.utils");
						ic.addStarImports("de.finetech.groovy.utils.datatypes");
						//
						ic.addImports("java.awt.Color", "java.util.Calendar",
								"java.text.SimpleDateFormat",
								"java.text.DateFormat", "java.util.Date");
						ic.addImports("de.finetech.utils.SelectionBuilder",
								"de.finetech.utils.Infosystemcall",
								"de.finetech.utils.InfosystemcallResult");
						// ic.addStaticImport("de.finetech.groovy.SelectionBuilder",
						// "SelectionBuilder");
						cc.addCompilationCustomizers(ic);
						// Basisklasse festlegen
						cc.setScriptBaseClass("de.finetech.groovy.AbasBaseScript");

						// Script ausf√ºhren
						GroovyShell shell = new GroovyShell(this.getClass()
								.getClassLoader(), binding, cc);
						shell.evaluate(groovyScript);

						return 0;
					} catch (CommandException e) {
						// FIXME Sprach unabh‰ngigkeit
						e.printStackTrace();
						FO.box("Fehler", e.getMessage());
						StringWriter sw = new StringWriter();
						PrintWriter pw = new PrintWriter(sw);
						e.printStackTrace(pw);
						FO.box("Unbehandelte Ausnahme in " + arg1[1],
								sw.toString());
					} catch (AbortedException e) {
						// FIXME Sprach unabh‰ngigkeit
						FO.box("FOP abgebrochen",
								"FOP wurde durch Anwender abgebrochen");
					} catch (CompilationFailedException e) {
						StringWriter sw = new StringWriter();
						PrintWriter pw = new PrintWriter(sw);
						e.printStackTrace(pw);
						FO.box("‹bersetzung fehlgeschlagen",
								"GroovyFO konnte das Script nicht ¸bersetzen: "
										+ e.getMessage() + "\n" + sw.toString());

					} catch (Exception e) {
						// FIXME Sprach unabh‰ngigkeit
						StringWriter sw = new StringWriter();
						PrintWriter pw = new PrintWriter(sw);
						e.printStackTrace(pw);
						FO.box("Unbehandelte Ausnahme in " + arg1[1],
								sw.toString());
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
}