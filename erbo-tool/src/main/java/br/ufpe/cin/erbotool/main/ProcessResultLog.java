package br.ufpe.cin.erbotool.main;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.apache.http.util.Asserts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.ufpe.cin.erbotool.util.Util;

public class ProcessResultLog {

	private static Logger LOGGER = LogManager.getLogger();
	// Key = String = ProjectName
	// String[] = {releaseName, smellsDetectedInRelease, smellsThatDisappearedInRelease, smellsThatAppearedInRelease, refactoringsInRelease}
	private static Map<String, List<String[]>> mapSmellsCount = new HashMap<>();
	
	
	public static void main(String[] args) {
		LOGGER.info("Execution started!");
		String LOG_FILE = "all.log";
		try {
			int numberLines = countLinesNew(LOG_FILE);
			LOGGER.info(LOG_FILE + " has " + numberLines + " lines.");			
			File file = new File("all.log");
			long fileSizeInMB = (file.length() / 1024) / 1024;
			LOGGER.info(LOG_FILE + " has " + fileSizeInMB + "MB.");
			loadDataFromLog(LOG_FILE);
			exportToCsv();
		} catch (IOException e) {
			LOGGER.error("Error: " + e.getMessage(), e);
		}
		
		
		LOGGER.info("Execution finished!");
	}

	public static void exportToCsv() {
		StringBuilder sb = new StringBuilder();
		// HEADER
		sb.append("projectName" + ";" + "releaseName" + ";" + "smellsDetectedInRelease" + ";" + "smellsThatDisappearedInRelease" + ";" + "smellsThatAppearedInRelease" + ";" + "refactoringsInRelease" + System.getProperty("line.separator"));
		for(Entry<String, List<String[]>> entry : mapSmellsCount.entrySet()) {
			List<String[]> list = entry.getValue();
			for(String[] s : list) {
				sb.append(entry.getKey() + ";" + s[0] + ";" + s[1] + ";" + s[2] + ";" + s[3] + ";" + s[4] + System.getProperty("line.separator"));
			}
			// FOOTER
			sb.append(System.getProperty("line.separator"));
			sb.append("projectName" + ";" + "releaseName" + ";" + "smellsDetectedInRelease" + ";" + "smellsThatDisappearedInRelease" + ";" + "smellsThatAppearedInRelease" + ";" + "refactoringsInRelease" + System.getProperty("line.separator"));
		}
		Path path = Paths.get("results/" + Util.getFormatedDateYYYYMMDDHHmmss(new Date()) + "_data.csv");
		try (BufferedWriter writer = Files.newBufferedWriter(path)) {
			writer.write(sb.toString());
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			LOGGER.info("Exported to " + path.toAbsolutePath());
		}
	}
	
	public static void loadDataFromLog(String filename) throws IOException {
		try (Scanner sc = new Scanner(new File(filename), "UTF-8")) {
	        while (sc.hasNextLine()) {
	            String line = sc.nextLine();
	            boolean foundLine = countSmellsByProject(line);
	            if ( foundLine ) 
	            		continue;
	            foundLine = countSmellsThatDisappearedInRelease(line);
	            if ( foundLine ) 
	            		continue;
	            foundLine = countSmellsThatAppearedInRelease(line);
	            if ( foundLine ) 
	            		continue;
	            foundLine = countRefactoringsInRelease(line);
	            if ( foundLine ) 
	            		continue;
	        }
	        if (sc.ioException() != null) {
	            throw sc.ioException();
	        }
	    } 
	}
	
	private static void updateSmellsThatDisappearedInRelease(List<String[]> smellCountList, String releaseName, String smellsThatDisappearedInRelease) {
		// String[] = {releaseName, smellsDetectedInRelease, smellsThatDisappearedInRelease, smellsThatAppearedInRelease, refactoringsInRelease}
		for(int i=0; i<smellCountList.size(); i++) {
			String[] s = smellCountList.get(i);
			if ( s[0].equalsIgnoreCase(releaseName) ) {
				s[2] = smellsThatDisappearedInRelease;
				break;
			}
		}
	}
	
	private static void updateSmellsThatAppearedInRelease(List<String[]> smellCountList, String releaseName, String smellsThatAppearedInRelease) {
		// String[] = {releaseName, smellsDetectedInRelease, smellsThatDisappearedInRelease, smellsThatAppearedInRelease, refactoringsInRelease}
		for(int i=0; i<smellCountList.size(); i++) {
			String[] s = smellCountList.get(i);
			if ( s[0].equalsIgnoreCase(releaseName) ) {
				s[3] = smellsThatAppearedInRelease;
				break;
			}
		}
	}
	
	private static void updateRefactoringsInRelease(List<String[]> smellCountList, String releaseName, String refactoringsInRelease) {
		// String[] = {releaseName, smellsDetectedInRelease, smellsThatDisappearedInRelease, smellsThatAppearedInRelease, refactoringsInRelease}
		for(int i=0; i<smellCountList.size(); i++) {
			String[] s = smellCountList.get(i);
			if ( s[0].equalsIgnoreCase(releaseName) ) {
				int qty = Integer.parseInt(s[4]);
				int qtyRefact = Integer.parseInt(refactoringsInRelease);
				if ( qty > 0 && qtyRefact > 0 ) {
					int total = qty + qtyRefact;
					// put a minus to mark tag with two occourences of refactorings (e.g: 1.0.0-1.0.1: 20 refact and 1.0.1-1.0.0: 4 refact) 
					s[4] = "-"+total;
				} else {
					s[4] = refactoringsInRelease;
				}
				break;
			}
		}
	}
	
	public static boolean countRefactoringsInRelease(String line) throws IOException {
		String STRING_1 = " TRACE br.ufpe.cin.erbotool.parse.RefactoringMinerCsvParse - Found ";
		String STRING_2 = " possible refactorings in project ";
		String STRING_3 = " between tags ";
		String STRING_4 = " and ";

		if ( line.contains(STRING_1) && line.contains(STRING_2) ) {
//			LOGGER.trace(line);
			int projectNameIndexStart = line.indexOf("[");
			int projectNameIndexEnd = line.indexOf("]");
			String projectName = line.substring(projectNameIndexStart+1, projectNameIndexEnd);
			int refactCountIndexStart = line.indexOf(STRING_1)+STRING_1.length();
			int refactCountIndexEnd = line.indexOf(STRING_2);
			String refactoringsInInterval = line.substring(refactCountIndexStart, refactCountIndexEnd);
			int releaseName1IndexStart = line.indexOf(STRING_3, refactCountIndexEnd)+STRING_3.length();
			int releaseName1IndexEnd = line.indexOf(STRING_4, releaseName1IndexStart);			
			String releaseName1 = line.substring(releaseName1IndexStart, releaseName1IndexEnd);
			int releaseName2IndexEnd = line.indexOf(STRING_4, releaseName1IndexEnd);
			String releaseName2 = line.substring(releaseName2IndexEnd+STRING_4.length());
			
			if ( mapSmellsCount.containsKey(projectName) ) {
				List<String[]> smellsTagList = mapSmellsCount.get(projectName);
				updateRefactoringsInRelease(smellsTagList, releaseName2, refactoringsInInterval);
			} else {
				// String[] = {releaseName, smellsDetectedInRelease, smellsThatDisappearedInRelease, smellsThatAppearedInRelease, refactoringsInRelease}
				List<String[]> smellsTagList = new ArrayList<>();
				String[] smellsInTag = {releaseName2, "0", "0", "0", refactoringsInInterval};
				smellsTagList.add(smellsInTag);
				mapSmellsCount.put(projectName, smellsTagList);
			}
			
			return true;
        }
		return false;
	}
	
	public static boolean countSmellsThatDisappearedInRelease(String line) throws IOException {
		String STRING_1 = " TRACE br.ufpe.cin.erbotool.run.RunRefactoringTool - ";
		String STRING_2 = ": Smells that appear in some tag and disapear in this-one: ";

		if ( line.contains(STRING_1) && line.contains(STRING_2) ) {
//			LOGGER.trace(line);
			int projectNameIndexStart = line.indexOf("[");
			int projectNameIndexEnd = line.indexOf("]");
			String projectName = line.substring(projectNameIndexStart+1, projectNameIndexEnd);
			int releaseNameIndexStart = line.indexOf(STRING_1)+STRING_1.length();
			int releaseNameIndexEnd = line.indexOf(":", releaseNameIndexStart);
			String releaseName = line.substring(releaseNameIndexStart, releaseNameIndexEnd);
			int smellCountIndexStart = line.indexOf(STRING_2)+STRING_2.length();
			String smellsThatDisappearedInRelease = line.substring(smellCountIndexStart, line.length());
			
			if ( mapSmellsCount.containsKey(projectName) ) {
				List<String[]> smellsTagList = mapSmellsCount.get(projectName);
				updateSmellsThatDisappearedInRelease(smellsTagList, releaseName, smellsThatDisappearedInRelease);
			} else {
				// String[] = {releaseName, smellsDetectedInRelease, smellsThatDisappearedInRelease, smellsThatAppearedInRelease, refactoringsInRelease}
				List<String[]> smellsTagList = new ArrayList<>();
				String[] smellsInTag = {releaseName, "0", smellsThatDisappearedInRelease, "0", "0"};
				smellsTagList.add(smellsInTag);
				mapSmellsCount.put(projectName, smellsTagList);
			}
			
			return true;
        }
		return false;
	}
	
	public static boolean countSmellsThatAppearedInRelease(String line) throws IOException {
		String STRING_1 = " TRACE br.ufpe.cin.erbotool.run.RunRefactoringTool - ";
		String STRING_2 = ": Smells that appear in this tag and disapear in another: ";

		if ( line.contains(STRING_1) && line.contains(STRING_2) ) {
//			LOGGER.trace(line);
			int projectNameIndexStart = line.indexOf("[");
			int projectNameIndexEnd = line.indexOf("]");
			String projectName = line.substring(projectNameIndexStart+1, projectNameIndexEnd);
			int releaseNameIndexStart = line.indexOf(STRING_1)+STRING_1.length();
			int releaseNameIndexEnd = line.indexOf(":", releaseNameIndexStart);
			String releaseName = line.substring(releaseNameIndexStart, releaseNameIndexEnd);
			int smellCountIndexStart = line.indexOf(STRING_2)+STRING_2.length();
			String smellsThatAppearedInRelease = line.substring(smellCountIndexStart, line.length());
			
			if ( mapSmellsCount.containsKey(projectName) ) {
				List<String[]> smellsTagList = mapSmellsCount.get(projectName);
				updateSmellsThatAppearedInRelease(smellsTagList, releaseName, smellsThatAppearedInRelease);
			} else {
				// String[] = {releaseName, smellsDetectedInRelease, smellsThatDisappearedInRelease, smellsThatAppearedInRelease, refactoringsInRelease}
				List<String[]> smellsTagList = new ArrayList<>();
				String[] smellsInTag = {releaseName, "0", "0", smellsThatAppearedInRelease, "0"};
				smellsTagList.add(smellsInTag);
				mapSmellsCount.put(projectName, smellsTagList);
			}
			
			return true;
        }
		return false;
	}
	
	public static boolean countSmellsByProject(String line) throws IOException {
		String STRING_1 = "SmartSmellConsoleOutputParse";
		String STRING_2 = "smells in ";

		if ( line.contains(STRING_1) && line.contains(STRING_2) ) {
			//LOGGER.trace(line);
			String[] tokens = line.split(" ");
			String projectName = tokens[2].substring(1, tokens[2].length()-1);
			String smells = tokens[7];
			
			int lastUnderscoreIndex = (projectName+"_").length();
			String projectName2 = tokens[10].substring(0, (projectName+"_").length()-1);
			String releaseName = tokens[10].substring(lastUnderscoreIndex, tokens[10].length());
			
			Asserts.check(projectName.equals(projectName2), "project name didn't match!");
			
			// String[] = {releaseName, smellsDetectedInRelease, smellsThatDisappearedInRelease, smellsThatAppearedInRelease, refactoringsInRelease}
			String[] smellsInTag = {releaseName, smells, "0", "0", "0"};
			if ( mapSmellsCount.containsKey(projectName2) ) {
				List<String[]> smellsTagList = mapSmellsCount.get(projectName2);
				smellsTagList.add(smellsInTag);
			} else {
				List<String[]> smellsTagList = new ArrayList<>();
				smellsTagList.add(smellsInTag);
				mapSmellsCount.put(projectName2, smellsTagList);
			}
			return true;
        }
		return false;
	}
	
	public static int countLinesNew(String filename) throws IOException {
	    InputStream is = new BufferedInputStream(new FileInputStream(filename));
	    try {
	        byte[] c = new byte[1024];

	        int readChars = is.read(c);
	        if (readChars == -1) {
	            // bail out if nothing to read
	            return 0;
	        }

	        // make it easy for the optimizer to tune this loop
	        int count = 0;
	        while (readChars == 1024) {
	            for (int i=0; i<1024;) {
	                if (c[i++] == '\n') {
	                    ++count;
	                }
	            }
	            readChars = is.read(c);
	        }

	        // count remaining characters
	        while (readChars != -1) {
	            System.out.println(readChars);
	            for (int i=0; i<readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	            readChars = is.read(c);
	        }

	        return count == 0 ? 1 : count;
	    } finally {
	        is.close();
	    }
	}
}
