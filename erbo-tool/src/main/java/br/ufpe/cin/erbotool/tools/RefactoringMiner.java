package br.ufpe.cin.erbotool.tools;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.ufpe.cin.erbotool.entity.CodeSmellEntity;
import br.ufpe.cin.erbotool.entity.ProjectEntity;
import br.ufpe.cin.erbotool.exception.SmellException;
import br.ufpe.cin.erbotool.util.ProcessUtil;
import br.ufpe.cin.erbotool.util.PropertiesUtil;

public class RefactoringMiner implements RefactoringTool {

	private final static Logger LOGGER = LogManager.getLogger();
	
	@Override
	public String execute(CodeSmellEntity smell, ProjectEntity proj, boolean invertTags) throws IOException, SmellException {
		String consoleOutput = "";
		String mainDIR = PropertiesUtil.getMainDir();
		String refactoringMinerBin = PropertiesUtil.getRefactoringMinerBin();
		String refactoringMinerParameters = PropertiesUtil.getRefactoringMinerParameters();
		String parameters[] = refactoringMinerParameters.split(" ");		
		parameters[1] = proj.getPath().toAbsolutePath().toString();
//		parameters[2] = smell.getTag();
		if ( invertTags ) {
			parameters[3] = proj.getPreviously(smell.getTagFixed());
			parameters[2] = smell.getTagFixed();
		} else {
			parameters[2] = proj.getPreviously(smell.getTagFixed());
			parameters[3] = smell.getTagFixed();
		}
		String[] cmdArray = new String[5];
		cmdArray[0] = mainDIR + File.separator + refactoringMinerBin;
		cmdArray[1] = parameters[0];
		cmdArray[2] = parameters[1];
		cmdArray[3] = parameters[2];
		cmdArray[4] = parameters[3];
		
		LOGGER.trace("Running: " + Arrays.toString(cmdArray));
		
		/*ProcessBuilder pb = new ProcessBuilder(cmdArray);
		pb.redirectErrorStream(true);
		Process p = pb.start();
		try {
			p.waitFor();
			int exitValue = p.exitValue();
			if (exitValue != 0) {
				List<String> erroList = StreamUtil.readLines(p.getErrorStream());
				LOGGER.debug(Arrays.toString(erroList.toArray()));
				
				throw new SmellException("something goes wrong with execution: " + Arrays.toString(cmdArray));
			} 
			List<String> outputList = StreamUtil.readLines(p.getInputStream());
			if ( outputList.size() >= 2 ) {
				String penultimateLine = outputList.get(outputList.size()-2);
				consoleOutput = penultimateLine.replace("Finish mining, result is saved to file: ", "").trim();
			} else {
				throw new SmellException("something goes wrong with execution: " + Arrays.toString(cmdArray)); 
			}
			
			LOGGER.trace(Arrays.toString(cmdArray) + " finished with exitcode " + exitValue);
		} catch (InterruptedException e) {
			LOGGER.error(e.getMessage(), e);
		}*/
		
		List<String> outputList = ProcessUtil.executeList(cmdArray);
		if ( outputList.size() >= 2 ) {
			String penultimateLine = outputList.get(outputList.size()-2);
			consoleOutput = penultimateLine.replace("Finish mining, result is saved to file: ", "").trim();
		} else {
			throw new SmellException("something goes wrong with execution: " + Arrays.toString(cmdArray)); 
		}
		
		return consoleOutput;
	}

}
