package de.finetech.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import de.abas.eks.jfop.FOPException;
import de.abas.eks.jfop.remote.FO;
import de.abas.eks.jfop.remote.FOPRunnable;

/**
 * @author Michael KÃ¼rbis, Finetech GmbH & Co. KG
 * 
 *         JFOP erwartet als zweiten Parameter das Groovyscript welches
 *         ausgefÃ¼hrt werden soll und Ã¼bergibt alle parameter an dieses weiter
 * 
 */
public class ScriptExcecuter implements FOPRunnable {

	public int runFop(String[] arg0) throws FOPException {
		// Genug Parameter Ã¼bergben?
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
						// hinzugefÃ¼gt werden mÃ¼ssen
						CompilerConfiguration cc = new CompilerConfiguration();
						ImportCustomizer ic = new ImportCustomizer();
						// abas Standard
						ic.addStarImports("de.abas.eks.jfop.remote");
						ic.addStarImports("de.finetech.groovy");
						// 
						ic.addImports("java.awt.Color", "java.util.Calendar",
								"java.text.SimpleDateFormat",
								"java.text.DateFormat", "java.util.Date");
						ic.addImports("de.finetech.utils.Infosystemcall",
								"de.finetech.utils.InfosystemcallResult");
						// ic.addStaticImport("de.finetech.groovy.SelectionBuilder",
						// "SelectionBuilder");
						cc.addCompilationCustomizers(ic);
						// Basisklasse festlegen
						cc.setScriptBaseClass("de.finetech.groovy.AbasBaseScript");

						// Script ausfÃ¼hren
						GroovyShell shell = new GroovyShell(this.getClass()
								.getClassLoader(), binding, cc);
						shell.evaluate(groovyScript);

						return 0;
					} catch (Exception e) {
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
