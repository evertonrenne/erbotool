package br.ufpe.cin.erbotool.parse;

import java.io.IOException;
import java.util.List;

import br.ufpe.cin.erbotool.entity.CodeSmellEntity;
import br.ufpe.cin.erbotool.entity.ProjectEntity;
import br.ufpe.cin.erbotool.entity.RefactoringEntity;

public interface RefactoringParse {

	List<RefactoringEntity> parse(String csvFilename, ProjectEntity projectEntity, CodeSmellEntity smell) throws IOException;
}
