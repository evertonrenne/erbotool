package br.ufpe.cin.erbotool.parse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.ufpe.cin.erbotool.entity.CodeSmellEntity;
import br.ufpe.cin.erbotool.entity.ProjectEntity;
import br.ufpe.cin.erbotool.util.enums.SmellToolEnum;

public class SmartSmellConsoleOutputParse implements SmartSmellParse {

	private final static Logger LOGGER = LogManager.getLogger();

	private final String SMARTSMELL_FIELD_SEPARATOR = " - ";
	private final String SMARTSMELL_RESOURCE_FIELD_SEPARATOR = ":";
	private final String SMARTSMELL_RESOURCE_FIELD_PREFIX = "at ";

	@Override
	public List<CodeSmellEntity> parse(String consoleOutput, ProjectEntity projectEntity) throws IOException {
		List<CodeSmellEntity> smellList = new ArrayList<>();
				
		// split lines
		String[] outputSections = consoleOutput.split(System.getProperty("line.separator") + System.getProperty("line.separator"));
		
		if ( outputSections.length == 4 ) {
			String[] lines = outputSections[1].split(System.getProperty("line.separator"));
			for(int i=1; i<lines.length; i++) {
				String line = lines[i];
				String[] tokens = line.split(SMARTSMELL_FIELD_SEPARATOR);
				if ( tokens.length == 3 ) {
					CodeSmellEntity smell = new CodeSmellEntity();
					smell.setProject(projectEntity);
					String[] resource = parseResource(tokens[2]);
					smell.setResource(getResource(resource));
					smell.setLine(getLine(resource));
					smell.setColumn(getColumn(resource));
					smell.setSmell(tokens[0]);
					smell.setTool(SmellToolEnum.SMARTSMELLS);
					smell.setTag(projectEntity.getCurrentTag());
					
					// add
					smellList.add(smell);
				}
			}			
		} else if ( outputSections.length == 3 ) {
			return smellList;
		}
		
		LOGGER.trace("Found " + smellList.size() + " smells in " + projectEntity.getName() + "_" + projectEntity.getCurrentTag());
		
		return smellList;
	}
	
	private String[] parseResource(String token) {
		String[] tokens = token.split(SMARTSMELL_RESOURCE_FIELD_SEPARATOR);
		return tokens;		
	}
	
	private String getResource(String[] tokens) {
		if ( tokens.length == 3 )
			return tokens[0].replace("/", ".").replace(SMARTSMELL_RESOURCE_FIELD_PREFIX, "").replace(".java", "");
		return Arrays.toString(tokens);
	}
	
	private String getLine(String[] tokens) {
		if ( tokens.length == 3 )
			return tokens[1];
		return Arrays.toString(tokens);
	}
	
	private String getColumn(String[] tokens) {
		if ( tokens.length == 3 )
			return tokens[2];
		return Arrays.toString(tokens);
	}
}
