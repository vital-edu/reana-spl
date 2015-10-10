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

import fdtmc.FDTMC;

/**
 * Façade to a PARAM executable.
 *
 * @author Thiago
 *
 */
public class ParamWrapper implements ParametricModelChecker {
	private final String PARAM_PATH = "/opt/param-2-3-64";
	private final String PRISM_PATH = "/opt/prism-4.2.1-src/bin/prism";

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
			if (model.contains("param")) {
				formula = invokeParametricModelChecker(modelFile.getAbsolutePath(),
													   propertyFile.getAbsolutePath(),
													   resultsFile.getAbsolutePath());
			} else {
				formula = invokeModelChecker(modelFile.getAbsolutePath(),
											 propertyFile.getAbsolutePath(),
											 resultsFile.getAbsolutePath());
			}
			return formula.trim().replaceAll("\\s+", "");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	private String invokeParametricModelChecker(String modelPath,
												String propertyPath,
												String resultsPath) throws IOException {
		String commandLine = PARAM_PATH+" "
							 +modelPath+" "
							 +propertyPath+" "
							 +"--result-file "+resultsPath;
		return invokeAndGetResult(commandLine, resultsPath+".out");
	}

	private String invokeModelChecker(String modelPath,
									  String propertyPath,
									  String resultsPath) throws IOException {
		String commandLine = PRISM_PATH+" "
				 			 +modelPath+" "
				 			 +propertyPath+" "
				 			 +"-exportresults "+resultsPath;
		return invokeAndGetResult(commandLine, resultsPath);
	}

	private String invokeAndGetResult(String commandLine, String resultsPath) throws IOException {
		Process program = Runtime.getRuntime().exec(commandLine);
		try {
			int exitCode = program.waitFor();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> lines = Files.readAllLines(Paths.get(resultsPath), Charset.forName("UTF-8"));
		String formula = lines.get(lines.size()-1);
		return formula;
	}

}
