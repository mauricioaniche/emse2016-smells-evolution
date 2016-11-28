package emse.smells.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClassInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	private String file;
	private Calendar firstSeen;
	private String firstSeenHash;
	private int firstSeenNumber;
	private Map<String, List<LiveSmell>> smells;
	private Calendar deletedDate;
	private String deletedHash;
	private int deletedNumber;

	public ClassInfo(String file, Calendar firstSeen, String firstSeenHash, int count) {
		this.file = file;
		this.firstSeenHash = firstSeenHash;
		this.firstSeen = firstSeen;
		this.firstSeenNumber = count;
		this.smells = new HashMap<>();
		
		smells.put("mvc", new ArrayList<>());
		smells.put("pmd", new ArrayList<>());
	}
	
	public void current(Calendar date, String hash, int count, String type, String smell) {
		Optional<LiveSmell> found = getAliveSmells(type, smell).findFirst();
		if(!found.isPresent()) {
			LiveSmell liveSmell = new LiveSmell(smell, date, hash, count);
			smells.get(type).add(liveSmell);
		}
			
		LiveSmell liveSmell = getAliveSmells(type, smell).findFirst().get();
		liveSmell.update(date, hash, count);
		
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

	public void clean(Calendar date, String hash, int count, String type) {
		List<LiveSmell> liveSmells = getAliveSmells(type);
		for(LiveSmell smell : liveSmells) {
			smell.removed(date, hash, count);
		}
	}

	public void remove(Calendar date, String hash, int count, String type, String smellName) {
		List<LiveSmell> liveSmells = getAliveSmells(type, smellName).collect(Collectors.toList());
		for(LiveSmell smell : liveSmells) {
			smell.removed(date, hash, count);
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

	public void deleted(Calendar deletedDate, String deletedHash, int count) {
		this.deletedDate = deletedDate;
		this.deletedHash = deletedHash;
		this.deletedNumber = count;
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
	
	public int getDeletedNumber() {
		return deletedNumber;
	}
	
	public int getFirstSeenNumber() {
		return firstSeenNumber;
	}

	@Override
	public String toString() {
		return "ClassInfo [file=" + file + ", firstSeen=" + firstSeen + ", firstSeenHash=" + firstSeenHash + ", smells="
				+ smells + ", deletedDate=" + deletedDate + ", deletedHash=" + deletedHash + "]";
	}
	
	
	
	
}
