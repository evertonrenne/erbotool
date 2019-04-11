package br.ufpe.cin.erbotool.main;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.ufpe.cin.erbotool.entity.CodeSmellEntity;
import br.ufpe.cin.erbotool.entity.ProjectEntity;
import br.ufpe.cin.erbotool.entity.RefactoringEntity;
import br.ufpe.cin.erbotool.entity.ResultEntity;
import br.ufpe.cin.erbotool.entity.TagIntervalEntity;
import br.ufpe.cin.erbotool.exception.SmellException;
import br.ufpe.cin.erbotool.git.GitWorker;
import br.ufpe.cin.erbotool.run.RunDetectionTool;
import br.ufpe.cin.erbotool.run.RunRefactoringTool;
import br.ufpe.cin.erbotool.util.FileUtil;
import br.ufpe.cin.erbotool.util.Util;

public class ProjectThread extends Thread {

	private static Logger LOGGER = LogManager.getLogger();

	ProjectEntity proj;
	List<CodeSmellEntity> resultMatchMessageList = new ArrayList<>();

	public ProjectThread(ProjectEntity proj) {
		this.proj = proj;
		this.setName(proj.getName());
	}

	@Override
	public void run() {
		List<ResultEntity> resultList = new ArrayList<>();
		
		LOGGER.info("Runnning tool for " + proj.getName() + " ("+proj.getUrl()+")");
		
		try {
			RunDetectionTool detectionTool = new RunDetectionTool(proj);
			detectionTool.run();
			RunRefactoringTool refactoringTool = new RunRefactoringTool(proj, detectionTool.getFullList());
			refactoringTool.run();
			
			// cross refactorings detected by refactoringminer with code smells detected by smartsmell
			List<CodeSmellEntity> occurrenceResultList = refactoringTool.getOccurrenceResultList(); // contains all smells that appear in some point (tag) and disappear in next tags
			List<TagIntervalEntity> intervalList = refactoringTool.getIntervalList(); // contains all refactoring detected in all tag's interval that is in occurrenceResultList
			
			// step 1: cross smell resource (in occurrence) with refactoringlist to check if there is some refactorinng and if something was changed in that specific resource
			for(CodeSmellEntity smell : occurrenceResultList) {
				List<TagIntervalEntity> smellIntervalList = intervalList.stream().filter(i -> i.getSmellList().contains(smell)).collect(Collectors.toList());
				for(TagIntervalEntity interval : smellIntervalList) {					
					if (interval.getRefactoringList().stream().anyMatch(r -> r.getRefactoringDetail().toLowerCase().contains(smell.getResource().toLowerCase()))) {
						List<RefactoringEntity> refactoringList = interval.getRefactoringList().stream().filter(r -> r.getRefactoringDetail().toLowerCase().contains(smell.getResource().toLowerCase())).collect(Collectors.toList());
						LOGGER.info("Found " + refactoringList.size() + " refactorings that MATCH with resource " + smell.getResource() + " in tag's interval " + interval.getTagBeforeFix() + "-" + interval.getTagFixed());
						smell.setRefactoringList(refactoringList);
						searchRefactoringInCommitLog(smell);
						//
						refactoringList = refactoringList.stream().filter(r -> r.isCommitMessageMatch()).collect(Collectors.toList());
						smell.setRefactoringList(refactoringList);
					}
				}
			}
			
			// step 2: if step 1 found something, we'll retrieve commit messages (log) and search for keywords that refer to something related with refactoring or code smell
			
			// step 3: persist all finds
			
			/*
			// retrieve commit messages
			resultList = refactoringTool.getResultList();
			String[] refactoringWords = {"refactor", "smell", "improve", "performance", "evolve", "evolution", 
					"dataclass", "data class", 
					"featureenvy", "feature envy", 
					"largeclass", "large class", 
					"longmethod", "long method", 
					"longparameterlist", "long parameter", 
					"messagechain", "message chain", 
					"middleman", "middle man", 
					"refusedparentbequest", "refused parent bequest", 
					"shotgunsurgery", "shotgun surgery"
			};
			GitWorker git = new GitWorker();
			
			for(ResultEntity result : resultList) {
				for(RefactoringEntity refactoring : result.refactoringList) {
					String message = git.log(proj, refactoring.getCommit());
					refactoring.setCommitMessage(message);
					// filter by keywords
					for(String word : refactoringWords) {
						if ( message.contains(word) ) {
							refactoring.setCommitMessageMatch(true);
							resultMatchMessageList.add(result);
							
							LOGGER.info("FOUND A MATCH!!! "+ proj.getName() + " ("+proj.getUrl()+")"
									+ " commit: " + refactoring.getCommit() 
									+ " word: " + word 
									+ " refactoring: " + refactoring.getRefactoringType() 
									+ " detail: " + refactoring.getRefactoringDetail() 
									+ " smell: " + result.smell);
							break;
						}
					}					
				}				
			}*/
		} catch (Exception e) {
			LOGGER.error("ERROR: " + e.getMessage(), e);
		} finally {
			if ( resultMatchMessageList.size() > 0 ) {
				// write csv or json
				String resultFilename = "results/" + Util.getFormatedDateYYYYMMDDHHmmss(new Date()) + "_" + proj.getName() + "_" + "result.json";
				FileUtil.persistJson(resultMatchMessageList, resultFilename);
				LOGGER.info("Execution finished for " + proj.getName() + " ("+proj.getUrl()+") results in " + resultFilename);
			} else {
				LOGGER.info("Execution finished for " + proj.getName() + " ("+proj.getUrl()+")");
			}
		}		
		Main.delProj(proj, resultMatchMessageList);
	}
	
	private void searchRefactoringInCommitLog(CodeSmellEntity smell) throws SmellException {
		String[] refactoringWords = {"refactor", "smell", "improve", "performance", "evolve", "evolution", 
				"dataclass", "data class", 
				"featureenvy", "feature envy", 
				"largeclass", "large class", 
				"longmethod", "long method", 
				"longparameterlist", "long parameter", 
				"messagechain", "message chain", 
				"middleman", "middle man", 
				"refusedparentbequest", "refused parent bequest", 
				"shotgunsurgery", "shotgun surgery"
		};
		GitWorker git = new GitWorker();
		
		for(RefactoringEntity refactoring : smell.getRefactoringList()) {
			String message = git.log(proj, refactoring.getCommit());
			refactoring.setCommitMessage(message);
			// filter by keywords
			for(String word : refactoringWords) {
				if ( message.contains(word) ) {
					refactoring.setCommitMessageMatch(true);
					smell.setProject(proj);
					refactoring.setProject(proj);
					resultMatchMessageList.add(smell);
					
					LOGGER.info("FOUND A MATCH!!! "+ proj.getName() + " ("+proj.getUrl()+")"
							+ " commit: " + refactoring.getCommit() 
							+ " word: " + word 
							+ " refactoring: " + refactoring.getRefactoringType() 
							+ " detail: " + refactoring.getRefactoringDetail() 
							+ " smell: " + smell.getSmell());
					break;
				}
			}					
		}				
	}
}
