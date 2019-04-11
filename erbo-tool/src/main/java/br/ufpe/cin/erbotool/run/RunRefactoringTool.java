package br.ufpe.cin.erbotool.run;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.ufpe.cin.erbotool.entity.CodeSmellEntity;
import br.ufpe.cin.erbotool.entity.ProjectEntity;
import br.ufpe.cin.erbotool.entity.RefactoringEntity;
import br.ufpe.cin.erbotool.entity.ResultEntity;
import br.ufpe.cin.erbotool.entity.TagIntervalEntity;
import br.ufpe.cin.erbotool.parse.RefactoringMinerCsvParse;
import br.ufpe.cin.erbotool.parse.RefactoringParse;
import br.ufpe.cin.erbotool.tools.RefactoringMiner;
import br.ufpe.cin.erbotool.tools.RefactoringTool;
import lombok.Getter;

public class RunRefactoringTool implements Runnable {

	private static Logger LOGGER = LogManager.getLogger();
	
	private ProjectEntity project;
	private List<CodeSmellEntity> fullList;
	@Getter
	private List<TagIntervalEntity> intervalList;
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
		
		// all smells of all tags		
		LOGGER.trace("Smells detected in all tags: "+fullList.size());
		// all smells of all tags		
		LOGGER.trace("Smells that appear in some tag and disapear in another: "+occurrenceResultList.size());
		// get all tag's interval where smells were introduce and fixed
		Set<TagIntervalEntity> tagIntervalList = new HashSet<>();
		
		// collecting data about smells and tags
		for(String tag : project.getTags()) {
			// get all smells introduced in this tag 
			List<CodeSmellEntity> tagSmellIntroducedList = fullList.stream().filter(s -> s.getTag().equalsIgnoreCase(tag)).collect(Collectors.toList());
			LOGGER.trace(tag + ": Smells detected in this tag: "+tagSmellIntroducedList.size());
			List<CodeSmellEntity> tagSmellIntroducedList2 = occurrenceResultList.stream().filter(s -> s.getTag().equalsIgnoreCase(tag)).collect(Collectors.toList());
			LOGGER.trace(tag + ": Smells that appear in this tag and disapear in another: "+tagSmellIntroducedList2.size());
			// get all smells fixed (disappeared) in this tag
			List<CodeSmellEntity> tagSmellFixedList = occurrenceResultList.stream().filter(s -> s.getTagFixed().equalsIgnoreCase(tag)).collect(Collectors.toList());
			LOGGER.trace(tag + ": Smells that appear in some tag and disapear in this-one: "+tagSmellFixedList.size());
			// get all tag's interval where smells were introduce and fixed
			for(CodeSmellEntity smell : tagSmellIntroducedList2) {
				TagIntervalEntity tagInterval = new TagIntervalEntity();
				tagInterval.setTagBeforeFix(smell.getTagBeforeFix());
				tagInterval.setTagFixed(smell.getTagFixed());
				if (tagIntervalList.contains(tagInterval)) {
					tagIntervalList.stream().filter(t -> t.equals(tagInterval)).findFirst().get().getSmellList().add(smell);
				} else {
					tagInterval.getSmellList().add(smell);
					tagIntervalList.add(tagInterval);
				}
			}
			LOGGER.trace(tag + ": tagIntervalList.size()="+tagIntervalList.size());			
		}
		
		// print all interval
		intervalList = new ArrayList<TagIntervalEntity>(tagIntervalList);
		intervalList.sort((TagIntervalEntity t1, TagIntervalEntity t2)-> {
			int compare = t1.getTagBeforeFix().compareTo(t2.getTagBeforeFix());
			if ( compare == 0 ) {
				compare = t1.getTagFixed().compareTo(t2.getTagFixed());
			}			
			return compare;
		});
		for(TagIntervalEntity interval : intervalList) {
			LOGGER.trace(interval.getTagBeforeFix()+"..."+interval.getTagFixed());
			// invoking refactoringminer to check all refactorings in tag's interval
			List<RefactoringEntity> refactoringList = runRefactorMiner(interval);
			interval.setRefactoringList(refactoringList);
		}
		
		
		
		/*for(int i=0; i<occurrenceResultList.size(); i++) {
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
		}*/
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
	/**
	 * Execute refactoringminer to identify all refactorings between tags 
	 * @param interval
	 * @return A list of all refactoring that were detected by RefactoringMiner
	 */
	private List<RefactoringEntity> runRefactorMiner(TagIntervalEntity interval) {
		// RefactoringMiner
		RefactoringTool refactoringMiner = new RefactoringMiner();
		RefactoringParse refactoringMinerParser = new RefactoringMinerCsvParse();
		List<RefactoringEntity> refactoringFullList = new ArrayList<>();
		try {
			String csvFilename  = refactoringMiner.execute(interval, project, false);
			String csvFilename2 = refactoringMiner.execute(interval, project, true);
			refactoringFullList = refactoringMinerParser.parse(csvFilename, project, interval);
			refactoringFullList.addAll(refactoringMinerParser.parse(csvFilename2, project, interval));
		} catch (Exception e) {
			LOGGER.error("ERROR: " + e.getMessage(), e);			
		} 
		return refactoringFullList;		
	}

}
