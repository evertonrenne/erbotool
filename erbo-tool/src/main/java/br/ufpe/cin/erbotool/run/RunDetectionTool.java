package br.ufpe.cin.erbotool.run;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.ufpe.cin.erbotool.entity.CodeSmellEntity;
import br.ufpe.cin.erbotool.entity.ProjectEntity;
import br.ufpe.cin.erbotool.git.GitWorker;
import br.ufpe.cin.erbotool.parse.AdoctorCsvParse;
import br.ufpe.cin.erbotool.parse.AdoctorParse;
import br.ufpe.cin.erbotool.parse.SmartSmellConsoleOutputParse;
import br.ufpe.cin.erbotool.parse.SmartSmellParse;
import br.ufpe.cin.erbotool.tools.Adoctor;
import br.ufpe.cin.erbotool.tools.SmartSmell;
import br.ufpe.cin.erbotool.tools.SmellTools;
import br.ufpe.cin.erbotool.util.enums.SmellToolEnum;
import lombok.Getter;
import lombok.Setter;

public class RunDetectionTool implements Runnable  {
	
	private static Logger LOGGER = LogManager.getLogger();
	
	@Getter @Setter
	private ProjectEntity project;
	@Getter @Setter
	private List<CodeSmellEntity> fullList;
	
	public RunDetectionTool(ProjectEntity entity) {
		this.project = entity;
	}

	@Override
	public void run() {
		GitWorker git = new GitWorker();
		SmellTools aDoctor = new Adoctor();
		SmellTools smartSmell = new SmartSmell();
		AdoctorParse aDoctorParser = new AdoctorCsvParse();
		SmartSmellParse smartSmellParse = new SmartSmellConsoleOutputParse();
		
		List<CodeSmellEntity> adoctorSmellList = new ArrayList<>();
		List<CodeSmellEntity> smartSmellList = new ArrayList<>();
		
		// checkout tag by tag
		for(int i=0; i<project.getTags().size(); i++) {
			String tag = project.getTags().get(i);
			project.setCurrentTag(tag);
			// checkout tag
			try {
				git.checkout(project, tag);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				
				String[] error = {null, null, tag};
				project.getTagsWithErrors().add(error);
				continue;
			} finally {
				project.setCurrentTag(tag);
			}
			
			// SmartSmell
			try {
				String smartSmellConsoleOutput = smartSmell.execute(project);
				LOGGER.trace("Parsing smartsmell output....");
				List<CodeSmellEntity> smellList = smartSmellParse.parse(smartSmellConsoleOutput, project); 
				smartSmellList.addAll(smellList);
				
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				String[] error = {SmellToolEnum.SMARTSMELLS.toString(), null, tag};
				project.getTagsWithErrors().add(error);
			}
			/*
			// aDoctor
			for(AdoctorEnum smell : AdoctorEnum.values()) {			
				try {
					String csvFile = aDoctor.execute(project, smell.getSmellCode());
					// parse csv
					LOGGER.trace("Merging csv file: " + csvFile);
					List<CodeSmellEntity> smellList = aDoctorParser.parse(csvFile, project, smell.toString());
					adoctorSmellList.addAll(smellList);
				} catch (Exception e) {
					LOGGER.debug(e.getMessage(), e);
					String[] error = {SmellToolEnum.ADOCTOR.toString(), smell.toString(), tag};
					project.getTagsWithErrors().add(error);
				}								
			}
			*/
		}
		
//		List<CodeSmellEntity> fullList = new ArrayList<>();
		fullList = new ArrayList<>();
		fullList.addAll(smartSmellList);
		fullList.addAll(adoctorSmellList);
		fullList.forEach(c -> c.setProject(null));
		
		// sort by RESOURCE
		fullList.sort((CodeSmellEntity c1, CodeSmellEntity c2)-> {
			int compare = c1.getResource().compareTo(c2.getResource());
			if ( compare == 0 ) {
				compare = c1.getTool().compareTo(c2.getTool());
			}
			if ( compare == 0 ) {
				compare = c1.getSmell().compareTo(c2.getSmell());
			}
			if ( compare == 0 ) {
				compare = c1.getLine().compareTo(c2.getLine());
			}
			return compare;
		});
				
		// summary
		LOGGER.trace(System.getProperty("line.separator") + System.getProperty("line.separator") + "Execution Summary" + System.getProperty("line.separator"));

		for(CodeSmellEntity codeSmell : fullList) {
			LOGGER.trace(project.getName() + " | " + codeSmell.getResource() + " | " + codeSmell.getSmell() + "("+codeSmell.getLine()+","+codeSmell.getColumn()+")" + " | " + codeSmell.getTag());
		}
		
/*		String resultFilename = FileUtil.createResultJsonFileName(project);		
		LOGGER.info("Persisting file in " + resultFilename);
		ResultEntity result = new ResultEntity();		
		result.project = project;		
		result.smellList = fullList;		
		result.occurrenceList = occurrenceList;
		result.refactoringList = refactoringFullList;
		persistJson(result, resultFilename);*/
		
		//LOGGER.info(System.getProperty("line.separator") + System.getProperty("line.separator") + "Execution finished!" + System.getProperty("line.separator"));
	}
	
	
	
}
