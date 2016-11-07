package emse.smells.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClassInfo {

	private String file;
	private Calendar firstSeen;
	private String firstSeenHash;
	private Map<String, List<LiveSmell>> smells;
	private Calendar deletedDate;
	private String deletedHash;

	public ClassInfo(String file, Calendar firstSeen, String firstSeenHash) {
		this.file = file;
		this.firstSeenHash = firstSeenHash;
		this.firstSeen = firstSeen;
		this.smells = new HashMap<>();
		
		smells.put("mvc", new ArrayList<>());
		smells.put("pmd", new ArrayList<>());
	}
	
	public void current(Calendar date, String hash, String type, String smell) {
		Optional<LiveSmell> found = getAliveSmells(type, smell).findFirst();
		if(!found.isPresent()) {
			LiveSmell liveSmell = new LiveSmell(smell, date, hash);
			smells.get(type).add(liveSmell);
		}
			
		LiveSmell liveSmell = getAliveSmells(type, smell).findFirst().get();
		liveSmell.update(date, hash);
		
	}

	public String getFile() {
		return file;
	}

	public Calendar getFirstSeen() {
		return firstSeen;
	}
	
	public List<LiveSmell> getAllSmells(String type) {
		return smells.get(type);
	}

	public List<LiveSmell> getAliveSmells(String type) {
		return smells.get(type).stream().filter(x -> x.isAlive()).collect(Collectors.toList());
	}

	public void clean(Calendar date, String hash, String type) {
		List<LiveSmell> liveSmells = getAliveSmells(type);
		for(LiveSmell smell : liveSmells) {
			smell.removed(date, hash);
		}
	}

	public void remove(Calendar date, String hash, String type, String smellName) {
		List<LiveSmell> liveSmells = getAliveSmells(type, smellName).collect(Collectors.toList());
		for(LiveSmell smell : liveSmells) {
			smell.removed(date, hash);
		}
	}

	private Stream<LiveSmell> getAliveSmells(String type, String smellName) {
		return getAliveSmells(type).stream().filter(x -> x.getName().equals(smellName));
	}
	
	public String getFirstSeenHash() {
		return firstSeenHash;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public void deleted(Calendar deletedDate, String deletedHash) {
		this.deletedDate = deletedDate;
		this.deletedHash = deletedHash;
	}

	public Calendar getDeletedDate() {
		return deletedDate;
	}

	public void setDeletedDate(Calendar deletedDate) {
		this.deletedDate = deletedDate;
	}

	public String getDeletedHash() {
		return deletedHash;
	}

	public void setDeletedHash(String deletedHash) {
		this.deletedHash = deletedHash;
	}
	
	
}
