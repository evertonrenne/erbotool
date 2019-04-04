package br.ufpe.cin.erbotool.tools;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.ufpe.cin.erbotool.entity.ProjectEntity;
import br.ufpe.cin.erbotool.exception.SmellException;
import br.ufpe.cin.erbotool.util.FileUtil;
import br.ufpe.cin.erbotool.util.PropertiesUtil;
import br.ufpe.cin.erbotool.util.StreamUtil;

public class Adoctor implements SmellTools {

	private final static Logger LOGGER = LogManager.getLogger();
	
	@Override
	public String execute(ProjectEntity proj) throws IOException, SmellException {
		String mainDIR = PropertiesUtil.getMainDir();
		String aDoctorJar = PropertiesUtil.getADoctorJar();
		String aDoctorParameters = PropertiesUtil.getADoctorParameters();
		String parameters[] = aDoctorParameters.split(" ");
		parameters[1] = proj.getPath().toAbsolutePath().toString();
		parameters[2] = proj.getPath().toAbsolutePath().toString() + File.separator + proj.getCurrentTag() + "_" + PropertiesUtil.getADoctorOutput();
		parameters[3] = PropertiesUtil.getADoctorSmells();
				
		String[] cmdArray = new String[7];
		cmdArray[0] = PropertiesUtil.getJavaApp();
		cmdArray[1] = "-cp";
		cmdArray[2] = mainDIR + File.separator + aDoctorJar;
		cmdArray[3] = parameters[0];
		cmdArray[4] = parameters[1];
		cmdArray[5] = parameters[2];
		cmdArray[6] = parameters[3];
		
		LOGGER.trace("Running: " + Arrays.toString(cmdArray));
		
		Process p = Runtime.getRuntime().exec(cmdArray);
		try {
			p.waitFor(SmellTools.CLONE_TIME_LIMITE, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			LOGGER.error(e.getMessage(), e);
		}
		int exitValue = p.exitValue();
		if (exitValue != 0) {
			List<String> erroList = StreamUtil.readLines(p.getErrorStream());
			LOGGER.error(Arrays.toString(erroList.toArray()));
			
			throw new SmellException("something goes wrong with execution: " + Arrays.toString(cmdArray));
		}
		
		LOGGER.trace(Arrays.toString(cmdArray) + " finished with exitcode " + exitValue);
		
		return parameters[2];
	}

	@Override
	public String execute(ProjectEntity proj, String parameters) throws IOException, SmellException {
		String mainDIR = PropertiesUtil.getMainDir();
		String aDoctorJar = PropertiesUtil.getADoctorJar();
		String aDoctorParameters = PropertiesUtil.getADoctorParameters();
		String parameter[] = aDoctorParameters.split(" ");
		parameter[1] = proj.getPath().toAbsolutePath().toString();
//		parameter[2] = proj.getPath().toAbsolutePath().toString() + File.separator + proj.getCurrentTag() + "_" + PropertiesUtil.getADoctorOutput();
		parameter[2] = FileUtil.createAdoctorProjectFileName(proj);
//		parameter[3] = PropertiesUtil.getADoctorSmells();
		parameter[3] = parameters;
				
		String[] cmdArray = new String[7];
		cmdArray[0] = PropertiesUtil.getJavaApp();
		cmdArray[1] = "-cp";
		cmdArray[2] = mainDIR + File.separator + aDoctorJar;
		cmdArray[3] = parameter[0];
		cmdArray[4] = parameter[1];
		cmdArray[5] = parameter[2];
		cmdArray[6] = parameter[3];
		
		LOGGER.trace("Running: " + Arrays.toString(cmdArray));
		
		Process p = Runtime.getRuntime().exec(cmdArray);
		try {
			p.waitFor();
		} catch (InterruptedException e) {
			LOGGER.error(e.getMessage(), e);
		}
		int exitValue = p.exitValue();
		if (exitValue != 0) {
			List<String> erroList = StreamUtil.readLines(p.getErrorStream());
			LOGGER.debug(Arrays.toString(erroList.toArray()));
			
			throw new SmellException("something goes wrong with execution: " + Arrays.toString(cmdArray));
		}
		
		LOGGER.trace(Arrays.toString(cmdArray) + " finished with exitcode " + exitValue);
		
		return parameter[2];
	}

}
