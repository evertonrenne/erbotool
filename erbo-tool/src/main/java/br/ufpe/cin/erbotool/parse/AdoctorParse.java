package br.ufpe.cin.erbotool.parse;

import java.io.IOException;
import java.util.List;

import br.ufpe.cin.erbotool.entity.CodeSmellEntity;
import br.ufpe.cin.erbotool.entity.ProjectEntity;

public interface AdoctorParse extends SmellParse {

	List<CodeSmellEntity> parse(String csvFilepath, ProjectEntity projectEntity, String smellName) throws IOException;
	
	String writeResult(List<CodeSmellEntity> smellList, ProjectEntity projectEntity);
}
