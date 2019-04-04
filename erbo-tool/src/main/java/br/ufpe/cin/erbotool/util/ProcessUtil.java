package br.ufpe.cin.erbotool.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.ufpe.cin.erbotool.exception.SmellException;

public class ProcessUtil {
	
	private final static Logger LOGGER = LogManager.getLogger();

	public static boolean executeBoolean(String[] cmdArray) throws SmellException {
		boolean result = false;
		String consoleOutput = "";
		
		try {
			ProcessBuilder pb = new ProcessBuilder(cmdArray);
			pb.redirectErrorStream(true);
			Process p = pb.start();
			consoleOutput = StreamUtil.readLine(p.getInputStream());		
			p.waitFor();	
			int exitValue = p.exitValue();
			if (exitValue != 0) {
				List<String> erroList = StreamUtil.readLines(p.getErrorStream());
				LOGGER.debug(Arrays.toString(erroList.toArray()));
				
				throw new SmellException("something goes wrong with execution: " + Arrays.toString(cmdArray));
			} 
			result=true;
			LOGGER.trace(Arrays.toString(cmdArray) + " finished with exitcode " + exitValue);
			p.destroy();
		} catch (InterruptedException|IOException e) {
			LOGGER.error(e.getMessage(), e);
			
		}
		return result;
	}
	
	public static String execute(String[] cmdArray) throws SmellException {
		String consoleOutput = "";
		
		try {
			ProcessBuilder pb = new ProcessBuilder(cmdArray);
			pb.redirectErrorStream(false);
			Process p = pb.start();
			consoleOutput = StreamUtil.readLine(p.getInputStream());		
			p.waitFor();	
			int exitValue = p.exitValue();
			if (exitValue != 0) {
				List<String> erroList = StreamUtil.readLines(p.getErrorStream());
				LOGGER.debug(Arrays.toString(erroList.toArray()));
				
				throw new SmellException("something goes wrong with execution: " + Arrays.toString(cmdArray));
			} 
			
			LOGGER.trace(Arrays.toString(cmdArray) + " finished with exitcode " + exitValue);
			p.destroy();
		} catch (InterruptedException|IOException e) {
			LOGGER.error(e.getMessage(), e);
			
		}
		return consoleOutput;
	}
	
	public static List<String> executeList(String[] cmdArray) throws SmellException {
		List<String> consoleOutput = new ArrayList<String>();
		
		try {
			ProcessBuilder pb = new ProcessBuilder(cmdArray);
			pb.redirectErrorStream(false);
			Process p = pb.start();
			consoleOutput = StreamUtil.readLines(p.getInputStream());		
			p.waitFor();	
			int exitValue = p.exitValue();
			if (exitValue != 0) {
				List<String> erroList = StreamUtil.readLines(p.getErrorStream());
				LOGGER.debug(Arrays.toString(erroList.toArray()));
				
				throw new SmellException("something goes wrong with execution: " + Arrays.toString(cmdArray));
			} 
			
			LOGGER.trace(Arrays.toString(cmdArray) + " finished with exitcode " + exitValue);
			p.destroy();
		} catch (InterruptedException|IOException e) {
			LOGGER.error(e.getMessage(), e);
			
		}
		return consoleOutput;
	}
	
}
