package br.ufpe.cin.erbotool.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.ufpe.cin.erbotool.util.enums.PropertiesEnum;

public class PropertiesUtil {
	
	private final static Logger LOGGER = LogManager.getLogger();
	
	public final static Properties prop = new Properties();
	
	public static Properties load(String path) throws IOException {
		InputStream input = new FileInputStream(path);
		prop.load(input);
		
		LOGGER.trace("Properties " + path + " loaded.");

		showProperties();
		
		return prop;
	}
	
	public static void showProperties() {
		LOGGER.trace("Showing properties...");
		
		Set<String> keys = prop.stringPropertyNames();
	    for (String key : keys) {
	    		LOGGER.trace(key + " : " + prop.getProperty(key));
	    }
	}
	
	public static String getProjectList() {
		return prop.getProperty(PropertiesEnum.GITHUB_PROJECT_LIST.toString());
	}
	
	public static String getRepositoryDir() {
		return prop.getProperty(PropertiesEnum.REPO_DIR.toString());
	}
	
	public static String getGitApp() {
		return prop.getProperty(PropertiesEnum.GIT_APP.toString());
	}
	
	public static String getJavaApp() {
		return prop.getProperty(PropertiesEnum.JAVA_APP.toString());
	}
	
	public static String getMainDir() {
		return prop.getProperty(PropertiesEnum.MAIN_DIR.toString());
	}
	
	public static String getRefactoringMinerBin() {
		return prop.getProperty(PropertiesEnum.REFACTORINGMINER_BIN.toString());
	}
	
	public static String getRefactoringMinerParameters() {
		return prop.getProperty(PropertiesEnum.REFACTORINGMINER_PARAMETERS.toString());
	}
	
	public static String getSmartSmellJar() {
		return prop.getProperty(PropertiesEnum.SMARTSMELL_JAR.toString());
	}
	
	public static String getSmartSmellParameters() {
		return prop.getProperty(PropertiesEnum.SMARTSMELL_PARAMETERS.toString());
	}
	
	public static String getADoctorJar() {
		return prop.getProperty(PropertiesEnum.ADOCTOR_JAR.toString());
	}
	
	public static String getADoctorParameters() {
		return prop.getProperty(PropertiesEnum.ADOCTOR_PARAMETERS.toString());
	}
	
	public static String getADoctorSmells() {
		return prop.getProperty(PropertiesEnum.ADOCTOR_SMELLS.toString());
	}
	
	public static String getADoctorOutput() {
		return prop.getProperty(PropertiesEnum.ADOCTOR_OUTPUT.toString());
	}
	
	public static String getJarExtesion() {
		return prop.getProperty(PropertiesEnum.JAR_EXTENSION.toString());
	}
	
	public static String getGitTagScript() {
		return prop.getProperty(PropertiesEnum.GIT_TAG_SCRIPT.toString());
	}
}
