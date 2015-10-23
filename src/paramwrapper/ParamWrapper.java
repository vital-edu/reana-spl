/**
 *
 */
package paramwrapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import fdtmc.FDTMC;

/**
 * Façade to a PARAM executable.
 *
 * @author Thiago
 *
 */
public class ParamWrapper implements ParametricModelChecker {
    private static final Logger LOGGER = Logger.getLogger(ParamWrapper.class.getName());

	private final String paramPath = "/opt/param-2-3-64";
	private final String prismPath = "/opt/prism-4.2.1-src/bin/prism";
	private boolean usePrism = false;

	public String fdtmcToParam(FDTMC fdtmc) {
		ParamModel model = new ParamModel(fdtmc);
		return model.toString();
	}

	@Override
	public String getReliability(FDTMC fdtmc) {
		String model = fdtmcToParam(fdtmc);
		String reliabilityProperty = "P=? [ F \"success\" ]";

		return evaluate(model, reliabilityProperty);
	}

	private String evaluate(String model, String property) {
		try {
			File modelFile = File.createTempFile("model", "param");
			FileWriter modelWriter = new FileWriter(modelFile);
			modelWriter.write(model);
			modelWriter.flush();
			modelWriter.close();

			File propertyFile = File.createTempFile("property", "prop");
			FileWriter propertyWriter = new FileWriter(propertyFile);
			propertyWriter.write(property);
			propertyWriter.flush();
			propertyWriter.close();

			File resultsFile = File.createTempFile("result", null);

			String formula;
			if (usePrism && !model.contains("param")) {
			    formula = invokeModelChecker(modelFile.getAbsolutePath(),
			                                 propertyFile.getAbsolutePath(),
			                                 resultsFile.getAbsolutePath());
			} else {
			    formula = invokeParametricModelChecker(modelFile.getAbsolutePath(),
			                                           propertyFile.getAbsolutePath(),
			                                           resultsFile.getAbsolutePath());
			}
			return formula.trim().replaceAll("\\s+", "");
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
		return "";
	}

	private String invokeParametricModelChecker(String modelPath,
												String propertyPath,
												String resultsPath) throws IOException {
		String commandLine = paramPath+" "
							 +modelPath+" "
							 +propertyPath+" "
							 +"--result-file "+resultsPath;
		return invokeAndGetResult(commandLine, resultsPath+".out");
	}

	private String invokeModelChecker(String modelPath,
									  String propertyPath,
									  String resultsPath) throws IOException {
		String commandLine = prismPath+" "
				 			 +modelPath+" "
				 			 +propertyPath+" "
				 			 +"-exportresults "+resultsPath;
		return invokeAndGetResult(commandLine, resultsPath);
	}

	private String invokeAndGetResult(String commandLine, String resultsPath) throws IOException {
	    LOGGER.fine(commandLine);
		Process program = Runtime.getRuntime().exec(commandLine);
		int exitCode = 0;
		try {
			exitCode = program.waitFor();
		} catch (InterruptedException e) {
			LOGGER.severe("Exit code: " + exitCode);
			LOGGER.log(Level.SEVERE, e.toString(), e);
		}
		List<String> lines = Files.readAllLines(Paths.get(resultsPath), Charset.forName("UTF-8"));
		// Formula
		return lines.get(lines.size()-1);
	}

}
