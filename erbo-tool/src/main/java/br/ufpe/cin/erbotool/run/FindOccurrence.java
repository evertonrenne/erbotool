package br.ufpe.cin.erbotool.run;

import java.util.List;

import br.ufpe.cin.erbotool.entity.CodeSmellEntity;
import br.ufpe.cin.erbotool.entity.ProjectEntity;

public interface FindOccurrence<T> {

	List<CodeSmellEntity> find(List<CodeSmellEntity> smells, ProjectEntity proj);
}
