package br.ufpe.cin.erbotool.checker;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.ufpe.cin.erbotool.util.PropertiesUtil;

public class SystemChecker {

	private final static Logger LOGGER = LogManager.getLogger();
	
	public static boolean checkGit() throws IOException {		
		String[] cmdArray = new String[2];
		cmdArray[0] = PropertiesUtil.getGitApp();
		cmdArray[1] = "help";
		
		Process p = Runtime.getRuntime().exec(cmdArray);
		try {
			p.waitFor();
		} catch (InterruptedException e) {
			LOGGER.error(e.getMessage(), e);
		}
		int exitValue = p.exitValue();
		
		LOGGER.trace(PropertiesUtil.getGitApp() + " exit with code " + exitValue);
		
		return exitValue==0;
	}
	
	public static boolean checkJar() throws IOException {
		String MAIN_DIR = PropertiesUtil.getMainDir();
		String JAR_EXTENSION = PropertiesUtil.getJarExtesion();
		String SMART_SMELL_JAR = PropertiesUtil.getSmartSmellJar();
		String ADOCTOR_JAR = PropertiesUtil.getADoctorJar();
		
		List<Path> fileList = Files.list(Paths.get(MAIN_DIR))
			    .filter(s -> s.toString().endsWith(JAR_EXTENSION))
				.collect(Collectors.toList());
		boolean jar1 = fileList.stream().filter(p -> p.getFileName().toString().contains(SMART_SMELL_JAR)).findFirst().isPresent();
		boolean jar2 = fileList.stream().filter(p -> p.getFileName().toString().contains(ADOCTOR_JAR)).findFirst().isPresent();
		
		LOGGER.trace(SMART_SMELL_JAR + " was found? " + jar1);
		LOGGER.trace(ADOCTOR_JAR + " was found? " + jar2);
		
		return jar1&&jar2;
	}
	
	public static boolean checkRepoDir() {
		String REPO_DIR = PropertiesUtil.getRepositoryDir();
		Path path = Paths.get(REPO_DIR);
		if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
			return true;
		}
		return false;
	}
	
	public static boolean checkFreeSpaceInDisk() throws IOException {
		String REPO_DIR = PropertiesUtil.getRepositoryDir();
		Path path = Paths.get(REPO_DIR);
		FileStore store = Files.getFileStore(path);
		LOGGER.trace("space in disk available " + (store.getUsableSpace()/1024/1024/1024) + "GB, total " + (store.getTotalSpace()/1024/1024/1024) + "GB");
		return false;
	}
}
