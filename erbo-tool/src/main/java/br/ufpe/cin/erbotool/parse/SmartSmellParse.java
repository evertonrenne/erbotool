package br.ufpe.cin.erbotool.parse;

import java.io.IOException;
import java.util.List;

import br.ufpe.cin.erbotool.entity.CodeSmellEntity;
import br.ufpe.cin.erbotool.entity.ProjectEntity;

public interface SmartSmellParse extends SmellParse {

	List<CodeSmellEntity> parse(String consoleOutput, ProjectEntity projectEntity) throws IOException;
	
}
