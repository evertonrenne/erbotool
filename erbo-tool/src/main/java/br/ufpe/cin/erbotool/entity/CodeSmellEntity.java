package br.ufpe.cin.erbotool.entity;

import java.util.List;

import br.ufpe.cin.erbotool.util.enums.SmellToolEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @EqualsAndHashCode(callSuper=true)
public class CodeSmellEntity extends AbstractCodeEntity {

	private String smell;
	private SmellToolEnum tool;	
	private String tag;
	private String tagBeforeFix;
	private String tagFixed;
	private String line="0";
	private String column="0";
	
	//
	private List<RefactoringEntity> refactoringList;
	
}
