package de.finetech.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import de.abas.eks.jfop.AbortedException;
import de.abas.eks.jfop.CommandException;
import de.abas.eks.jfop.FOPException;
import de.abas.eks.jfop.remote.FO;
import de.abas.eks.jfop.remote.FOPRunnable;

/**
 * @author Michael Kürbis, Finetech GmbH & Co. KG
 * 
 *         JFOP erwartet als zweiten Parameter das Groovyscript welches
 *         ausgeführt werden soll und übergibt alle parameter an dieses weiter
 * 
 */
public class ScriptExcecuter implements FOPRunnable {

	public int runFop(String[] arg0) throws FOPException {
		// Genug Parameter übergben?
		if (arg0.length > 1) {
			File groovyScript = new File(arg0[1]);
			// existiert die Datei ?
			if (groovyScript.exists()) {
				// ist es eine Datei ?
				if (groovyScript.isFile()) {
					try {
						Binding binding = new Binding();
						// Parameter weitergeben
						binding.setVariable("arg", arg0);

						// Imports festlegen damit diese nicht selbst
						// hinzugefügt werden müssen
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
						ic.addImports("de.finetech.utils.SelectionBuilder","de.finetech.utils.Infosystemcall",
								"de.finetech.utils.InfosystemcallResult");
						// ic.addStaticImport("de.finetech.groovy.SelectionBuilder",
						// "SelectionBuilder");
						cc.addCompilationCustomizers(ic);
						// Basisklasse festlegen
						cc.setScriptBaseClass("de.finetech.groovy.AbasBaseScript");

						// Script ausführen
						GroovyShell shell = new GroovyShell(this.getClass()
								.getClassLoader(), binding, cc);
						shell.evaluate(groovyScript);

						return 0;
					} catch(CommandException e){
						//FIXME Sprach unabh�ngigkeit
						e.printStackTrace();
						FO.box("Fehler", e.getMessage());
						StringWriter sw = new StringWriter();
						PrintWriter pw = new PrintWriter(sw);
						e.printStackTrace(pw);
						FO.box("Unbehandelte Ausnahme in " + arg0[1],
								sw.toString());
					} catch(AbortedException e){
						//FIXME Sprach unabh�ngigkeit
						FO.box("FOP abgebrochen", "FOP wurde durch Anwender abgebrochen");
					} catch (Exception e) {
						//FIXME Sprach unabh�ngigkeit
						StringWriter sw = new StringWriter();
						PrintWriter pw = new PrintWriter(sw);
						e.printStackTrace(pw);
						FO.box("Unbehandelte Ausnahme in " + arg0[1],
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