package br.ufpe.cin.erbotool.parse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.ufpe.cin.erbotool.entity.CodeSmellEntity;
import br.ufpe.cin.erbotool.entity.ProjectEntity;
import br.ufpe.cin.erbotool.util.Constants;
import br.ufpe.cin.erbotool.util.FileUtil;
import br.ufpe.cin.erbotool.util.enums.SmellToolEnum;

public class AdoctorCsvParse implements AdoctorParse {

	private final static Logger LOGGER = LogManager.getLogger();
	
	private final String ADOCTOR_FIELD_SEPARATOR = ",";
	
	
	@Override
	public List<CodeSmellEntity> parse(String csvFilepath, ProjectEntity projectEntity, String smellName) throws IOException {		
		List<CodeSmellEntity> smellList = new ArrayList<>();
		List<String> csvList = FileUtil.readLines(csvFilepath);
		// tokenizer smells
		for(int i=1; i<csvList.size(); i++) {
			String line = csvList.get(i);
			String[] tokens = line.split(ADOCTOR_FIELD_SEPARATOR);
			if ( tokens.length == 2 && tokens[1].equals("1") ) {
				CodeSmellEntity smell = new CodeSmellEntity();
				smell.setProject(projectEntity);
				smell.setResource(tokens[0]);
				smell.setSmell(smellName);
				smell.setTool(SmellToolEnum.ADOCTOR);
				smell.setTag(projectEntity.getCurrentTag());
				
				// add
				smellList.add(smell);
			}
		}
		
		LOGGER.trace("Found " + smellList.size() + " smells in " + projectEntity.getName() + "_" + projectEntity.getCurrentTag());
		
		return smellList;
	}


	@Override
	public String writeResult(List<CodeSmellEntity> smellList, ProjectEntity proj) {
		String filename = FileUtil.createAdoctorProjectFileName(proj, Constants.RESULT_FILENAME_PREFIX);
		
		StringBuilder sb = new StringBuilder();
		// first line (header)
		sb.append(proj + ADOCTOR_FIELD_SEPARATOR);
		
		
		return null;
	}

}
