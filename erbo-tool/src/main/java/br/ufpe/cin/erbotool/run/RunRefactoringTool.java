package br.ufpe.cin.erbotool.run;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.ufpe.cin.erbotool.entity.CodeSmellEntity;
import br.ufpe.cin.erbotool.entity.ProjectEntity;
import br.ufpe.cin.erbotool.entity.RefactoringEntity;
import br.ufpe.cin.erbotool.entity.ResultEntity;
import br.ufpe.cin.erbotool.parse.RefactoringMinerCsvParse;
import br.ufpe.cin.erbotool.parse.RefactoringParse;
import br.ufpe.cin.erbotool.tools.RefactoringMiner;
import br.ufpe.cin.erbotool.tools.RefactoringTool;
import br.ufpe.cin.erbotool.util.FileUtil;
import lombok.Getter;

public class RunRefactoringTool implements Runnable {

	private static Logger LOGGER = LogManager.getLogger();
	
	private ProjectEntity project;
	private List<CodeSmellEntity> fullList;
	@Getter
	private List<CodeSmellEntity> occurrenceResultList;
	@Getter
	private List<ResultEntity> resultList = new ArrayList<>();
	
	public RunRefactoringTool(ProjectEntity project, List<CodeSmellEntity> list) {
		this.project = project;
		this.fullList = list;
	}
	
	@Override
	public void run() {
		occurrenceResultList = runFindOccurrences();
		
		for(int i=0; i<occurrenceResultList.size(); i++) {
			CodeSmellEntity smell = occurrenceResultList.get(i);
			List<RefactoringEntity> refactoringList = runRefactorMiner(smell);
			ResultEntity result = new ResultEntity();
			result.project = project;
			result.smell = smell;
			result.refactoringList = refactoringList;
			if ( refactoringList.size() > 0 ) {
				resultList.add(result);
				FileUtil.persistJson(result, FileUtil.createResultJsonFileName(project, smell));
			}
		}
	}
	
	private List<CodeSmellEntity> runFindOccurrences() {
		// Refactoring Occurrences
		FindOccurrence<CodeSmellEntity> find = new FindCorretionOccurrence();
		List<CodeSmellEntity> occurrenceList = find.find(fullList, project);
		LOGGER.info("found " + occurrenceList.size() + " possible occurrences of refactoring performed by developers.");
		// sort list
		occurrenceList.sort((CodeSmellEntity c1, CodeSmellEntity c2)-> {
			int compare = c1.getSmell().compareTo(c2.getSmell());
			if ( compare == 0 ) {
				compare = c1.getResource().compareTo(c2.getResource());
			}
			if ( compare == 0 ) {
				compare = c1.getTool().compareTo(c2.getTool());
			}			
			if ( compare == 0 ) {
				compare = c1.getLine().compareTo(c2.getLine());
			}
			return compare;
		});
		return occurrenceList;
	}
	
	private List<RefactoringEntity> runRefactorMiner(CodeSmellEntity smell) {
		// RefactoringMiner
		RefactoringTool refactoringMiner = new RefactoringMiner();
		RefactoringParse refactoringMinerParser = new RefactoringMinerCsvParse();
		List<RefactoringEntity> refactoringFullList = new ArrayList<>();
		try {
			String csvFilename  = refactoringMiner.execute(smell, project, false);
			String csvFilename2 = refactoringMiner.execute(smell, project, true);
			refactoringFullList = refactoringMinerParser.parse(csvFilename, project, smell);
			refactoringFullList.addAll(refactoringMinerParser.parse(csvFilename2, project, smell));
		} catch (Exception e) {
			LOGGER.error(project.getName() + " | " + smell.getResource() + " | " + smell.getSmell() + "("+smell.getLine()+","+smell.getColumn()+")" + " | " + smell.getTag() + " | " + smell.getTagFixed(), e);
			LOGGER.debug(e.getMessage(), e);
		} 
		return refactoringFullList;		
	}

}
