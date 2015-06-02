/**
 *
 */
package paramwrapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import FeatureFamilyBasedAnalysisTool.FDTMC;
import FeatureFamilyBasedAnalysisTool.ParametricModelChecker;

/**
 * Façade to a PARAM executable.
 *
 * @author Thiago
 *
 */
public class ParamWrapper implements ParametricModelChecker {
	private final String PARAM_PATH = "/opt/param-2-3-64";

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

			File propertyFile = File.createTempFile("property", "prop");
			FileWriter propertyWriter = new FileWriter(propertyFile);
			propertyWriter.write(property);
			propertyWriter.flush();

			String formula = invokeParam(modelFile, propertyFile);
			return formula.trim().replaceAll("\\s+", "");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	private String invokeParam(File modelFile, File propertyFile) throws IOException {
		String modelPath = modelFile.getAbsolutePath();
		String propertyPath = propertyFile.getAbsolutePath();

		File result = File.createTempFile("result", null);

		String commandLine = PARAM_PATH+" "
							 +modelPath+" "
							 +propertyPath+" "
							 +"--result-file "+result.getAbsolutePath();
		Process param = Runtime.getRuntime().exec(commandLine);
		try {
			int exitCode = param.waitFor();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> lines = Files.readAllLines(Paths.get(result.getAbsolutePath()+".out"), Charset.forName("UTF-8"));
		String formula = lines.get(lines.size()-1);
		return formula;
	}
}
