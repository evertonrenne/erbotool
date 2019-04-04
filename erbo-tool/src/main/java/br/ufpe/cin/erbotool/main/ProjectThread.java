package br.ufpe.cin.erbotool.main;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.ufpe.cin.erbotool.entity.ProjectEntity;
import br.ufpe.cin.erbotool.entity.RefactoringEntity;
import br.ufpe.cin.erbotool.entity.ResultEntity;
import br.ufpe.cin.erbotool.git.GitWorker;
import br.ufpe.cin.erbotool.run.RunDetectionTool;
import br.ufpe.cin.erbotool.run.RunRefactoringTool;
import br.ufpe.cin.erbotool.util.FileUtil;
import br.ufpe.cin.erbotool.util.Util;

public class ProjectThread extends Thread {
	
	private static Logger LOGGER = LogManager.getLogger();
	
	ProjectEntity proj;
	
	public ProjectThread(ProjectEntity proj) {
		this.proj = proj;
		this.setName(proj.getName());
	}
	
	@Override
	public void run() {
		Set<ResultEntity> resultMatchMessageList = new LinkedHashSet<ResultEntity>();
		
		LOGGER.info("Runnning tool for " + proj.getName() + " ("+proj.getUrl()+")");
		
		try {
			RunDetectionTool detectionTool = new RunDetectionTool(proj);
			detectionTool.run();
			RunRefactoringTool refactoringTool = new RunRefactoringTool(proj, detectionTool.getFullList());
			refactoringTool.run();
			
			// retrieve commit messages
			List<ResultEntity> resultList = refactoringTool.getResultList();
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
			}
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
}
