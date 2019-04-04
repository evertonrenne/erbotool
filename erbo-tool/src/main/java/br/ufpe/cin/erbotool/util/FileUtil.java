package br.ufpe.cin.erbotool.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.ufpe.cin.erbotool.entity.CodeSmellEntity;
import br.ufpe.cin.erbotool.entity.ProjectEntity;

public class FileUtil {

	private final static Logger LOGGER = LogManager.getLogger();
	
	public static void persistJson(Object result, String resultFilename) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.writerWithDefaultPrettyPrinter().writeValue(new File(resultFilename), result);
			LOGGER.info("persisting json in disk -> " + resultFilename);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
//			throw e;
		}
	}
	
	public static List<String> readLines(String filepath) throws IOException {
		List<String> list = new ArrayList<String>();
		
		try (Stream<String> stream = Files.lines(Paths.get(filepath))) {
			list = stream.collect(Collectors.toList());
		}
		
		return list;
	}
	
	public static Path createDirRandom(String basedir) throws IOException {
		String uuid = java.util.UUID.randomUUID().toString();
		Path path = Paths.get(basedir + File.separator + uuid);
		while (Files.exists(path)) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				LOGGER.error(e.getMessage(), e);
			} finally {
				uuid = java.util.UUID.randomUUID().toString();
				path = Paths.get(basedir + File.pathSeparator + uuid);
			}
		}
		path = Files.createDirectories(path);
		return path;
	}
	
	public static String createOccurrenceJsonFileName(ProjectEntity proj) {
		String filename = PropertiesUtil.getMainDir() + File.separator + proj.getName() + "_occurrences" + ".json";
		return filename;
	}
	
	public static String createResultJsonFileName(ProjectEntity proj) {
		String filename = PropertiesUtil.getMainDir() + File.separator + proj.getName() + "_smells" + ".json";
		return filename;
	}
	
	public static String createResultJsonFileName(ProjectEntity proj, CodeSmellEntity smell) {
		String filename = PropertiesUtil.getMainDir() + File.separator + proj.getName() + "_" + smell.getResource() + "_" + smell.getSmell() + "_" + smell.getTag() + "_" + smell.getTagFixed() + ".json";
		return filename;
	}
	
	public static String createAdoctorProjectFileName(ProjectEntity proj) {
		String filename = proj.getPath().toAbsolutePath().toString() + File.separator + proj.getCurrentTag() + "_" + PropertiesUtil.getADoctorOutput();
		return filename;
	}
	
	public static String createAdoctorProjectFileName(ProjectEntity proj, String prefix) {
		String filename = proj.getPath().toAbsolutePath().toString() + File.separator + prefix + "_" + proj.getCurrentTag() + "_" + PropertiesUtil.getADoctorOutput();
		return filename;
	}
	
	public static void writeFile(String filename, String content) throws IOException { 
		Files.write(Paths.get(filename), content.getBytes());
	}
	
}
