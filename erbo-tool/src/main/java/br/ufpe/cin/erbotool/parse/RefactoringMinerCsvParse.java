package br.ufpe.cin.erbotool.parse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.ufpe.cin.erbotool.entity.CodeSmellEntity;
import br.ufpe.cin.erbotool.entity.ProjectEntity;
import br.ufpe.cin.erbotool.entity.RefactoringEntity;
import br.ufpe.cin.erbotool.util.FileUtil;
import br.ufpe.cin.erbotool.util.enums.SmellToolEnum;

public class RefactoringMinerCsvParse implements RefactoringParse {

	private final static Logger LOGGER = LogManager.getLogger();
	
	private final String REFACTORINGMINER_FIELD_SEPARATOR = ";";
	
	@Override
	public List<RefactoringEntity> parse(String csvFilename, ProjectEntity projectEntity, CodeSmellEntity smell) throws IOException {
		List<RefactoringEntity> refactoringList = new ArrayList<>();
		List<String> csvList = FileUtil.readLines(csvFilename);
		// tokenizer refactorings
		for(int i=1; i<csvList.size(); i++) {
			String line = csvList.get(i);
			String[] tokens = line.split(REFACTORINGMINER_FIELD_SEPARATOR);
			if ( tokens.length == 3 && tokens[2].contains(smell.getResource())) {
				RefactoringEntity refactoring = new RefactoringEntity();
				refactoring.setCommit(tokens[0]);
				refactoring.setRefactoringType(tokens[1]);
				refactoring.setRefactoringDetail(tokens[2]);
				//refactoring.setProject(projectEntity);
				refactoring.setResource(smell.getResource());

				// add
				refactoringList.add(refactoring);
			}
		}
		
		LOGGER.trace("Found " + refactoringList.size() + " possible refactorings for smell " + smell.getSmell() + " in resource " + smell.getResource() + " of project " + projectEntity.getName() + " between tags " + smell.getTag() + " and " + smell.getTagFixed());
		
		return refactoringList;
	}

	
}
