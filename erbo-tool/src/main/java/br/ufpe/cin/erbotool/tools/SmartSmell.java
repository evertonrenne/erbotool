package br.ufpe.cin.erbotool.tools;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.ufpe.cin.erbotool.entity.ProjectEntity;
import br.ufpe.cin.erbotool.exception.SmellException;
import br.ufpe.cin.erbotool.util.ProcessUtil;
import br.ufpe.cin.erbotool.util.PropertiesUtil;

public class SmartSmell implements SmellTools {

	private final static Logger LOGGER = LogManager.getLogger();
	
	@Override
	public String execute(ProjectEntity proj) throws IOException, SmellException {
		String consoleOutput = "";
		String mainDIR = PropertiesUtil.getMainDir();
		String smartSmellJar = PropertiesUtil.getSmartSmellJar();
		String smartSmellParameters = PropertiesUtil.getSmartSmellParameters();
		String parameters[] = smartSmellParameters.split(" ");		
		parameters[1] = mainDIR + File.separator + parameters[1];
		parameters[3] = proj.getPath().toAbsolutePath().toString();
				
		String[] cmdArray = new String[7];
		cmdArray[0] = PropertiesUtil.getJavaApp();
		cmdArray[1] = "-jar";
		cmdArray[2] = mainDIR + File.separator + smartSmellJar;
		cmdArray[3] = parameters[0];
		cmdArray[4] = parameters[1];
		cmdArray[5] = parameters[2];
		cmdArray[6] = parameters[3];
		
		LOGGER.trace("Running: " + Arrays.toString(cmdArray));
		
/*		ProcessBuilder pb = new ProcessBuilder(cmdArray);
		pb.redirectErrorStream(true);
		Process p = pb.start();
		try {
			consoleOutput = StreamUtil.readLine(p.getInputStream());		
			p.waitFor();	
			int exitValue = p.exitValue();
			if (exitValue != 0) {
				List<String> erroList = StreamUtil.readLines(p.getErrorStream());
				LOGGER.debug(Arrays.toString(erroList.toArray()));
				
				throw new SmellException("something goes wrong with execution: " + Arrays.toString(cmdArray));
			} 
			
			LOGGER.trace(Arrays.toString(cmdArray) + " finished with exitcode " + exitValue);
		} catch (InterruptedException e) {
			LOGGER.error(e.getMessage(), e);
		}*/
		
		consoleOutput = ProcessUtil.execute(cmdArray);
		
		return consoleOutput;
	}

	@Override
	public String execute(ProjectEntity proj, String parameters) throws IOException, SmellException {
		// TODO Auto-generated method stub
		return null;
	}

}
