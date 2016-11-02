package emse.smells;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Report {

	private List<Smell> smells;

	public Report() {
		this.smells = new ArrayList<Smell>();
	}
	
	public void add(Smell smell) {
		this.smells.add(smell);
	}
	
	public List<Smell> smellsFor(String canonicalPath) {
		return smells.stream().filter(x -> x.getFile().equals(canonicalPath))
				.collect(Collectors.toList());
	}

	public boolean contains(String canonicalPath) {
		return smells.stream().anyMatch(x -> x.getFile().equals(canonicalPath));
	}
	
}
