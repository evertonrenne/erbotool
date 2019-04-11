package br.ufpe.cin.erbotool.run;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.ufpe.cin.erbotool.entity.CodeSmellEntity;
import br.ufpe.cin.erbotool.entity.ProjectEntity;
import br.ufpe.cin.erbotool.util.Constants;

public class FindCorretionOccurrence implements FindOccurrence<CodeSmellEntity> {

	private static Logger LOGGER = LogManager.getLogger();
	
	private final String[] fowlerSmells = {"CommentSmell/Javadoc", "DataClass", "FeatureEnvy", "LargeClass", "LongMethod", "LongParameterList", "MessageChain", "MiddleMan", "RefusedParentBequest", "ShotgunSurgery"};
	
	private List<CodeSmellEntity> filterSmells(List<CodeSmellEntity> smells) {
		List<CodeSmellEntity> newList = new ArrayList<>();
		List<String> filterList = Arrays.asList("CommentSmell/Javadoc", "DataClass", "FeatureEnvy", "LargeClass", "LongMethod", "LongParameterList", "MessageChain", "MiddleMan", "RefusedParentBequest", "ShotgunSurgery");
		for(CodeSmellEntity smell : smells) {
			if ( filterList.contains(smell.getSmell()) ) {
				newList.add(smell);
			}
		}
		return newList;
	}
	
	@Override
	public List<CodeSmellEntity> find(List<CodeSmellEntity> smells, ProjectEntity proj) {
		List<CodeSmellEntity> retorno = new ArrayList<>();		
		List<CodeSmellEntity> filterSmells = filterSmells(smells);
		
		
		for(int i=0; i<filterSmells.size(); i++) {			
			CodeSmellEntity resource = filterSmells.get(i);
			// lista de todos os smells do resource
			List<CodeSmellEntity> resourceSmellList = filterSmells.stream().filter(c -> c.getResource().equalsIgnoreCase(resource.getResource())).collect(Collectors.toList());
			for(int x=0; x<resourceSmellList.size(); x++) {
//				CodeSmellEntity smell = filterSmells.get(i);
				CodeSmellEntity smell = resourceSmellList.get(x);
				boolean occurrenceIsPresent = retorno.stream().filter(o -> {
					if ( o.getSmell().equalsIgnoreCase(smell.getSmell()) && o.getResource().equalsIgnoreCase(smell.getResource()) ) {
						return true;
					}
					return false;
				}).findFirst().isPresent();
				
				if ( occurrenceIsPresent ) {
					// smell already identified
					continue;
				}
				// lista de com todas as ocorrencias do mesmo smell para este resource
				List<CodeSmellEntity> smellList = resourceSmellList.stream().filter(c -> c.getSmell().equalsIgnoreCase(smell.getSmell())).collect(Collectors.toList());
				int tagIndex = proj.getTags().indexOf(smell.getTag());
				for(int y=tagIndex+1; y<proj.getTags().size(); y++) {
//				for(int y=tagIndex+1; y<Constants.TEST_TAGS_SIZE_LIMITE; y++) {
					final String tag = proj.getTags().get(y);
					boolean smellStillPresent = smellList.stream().filter(t -> t.getTag().equalsIgnoreCase(tag)).findFirst().isPresent();
					if ( smellStillPresent == false ) {
						smell.setTagFixed(tag);
						smell.setTagBeforeFix(proj.getPreviously(tag));
						retorno.add(smell);
						
						LOGGER.trace("Maybe a occurrence of refactoring...");
						LOGGER.trace(proj.getName() + " | " + smell.getResource() + " | " + smell.getSmell() + "("+smell.getLine()+","+smell.getColumn()+")" + " | " + smell.getTag() + " | " + smell.getTagFixed() + " (smell has disappeared from the next tags)");
						
						
						// verificar se vale checar se o problema voltou em tags futuras.

						break;
					}
				}
				
			}
			
		}
		
		
		return retorno;
	}

}
