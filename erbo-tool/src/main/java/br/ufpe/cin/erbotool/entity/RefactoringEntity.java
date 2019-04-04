package br.ufpe.cin.erbotool.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @EqualsAndHashCode(callSuper=true)
public class RefactoringEntity extends AbstractCodeEntity {

	private String commit;
	private String refactoringType;
	private String refactoringDetail;
	private String commitMessage;
	private boolean commitMessageMatch;
}
