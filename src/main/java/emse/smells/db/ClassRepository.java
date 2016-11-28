package emse.smells.db;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClassRepository implements Serializable {

	private static final long serialVersionUID = 1L;
	private Map<String, ClassInfo> db;

	public ClassRepository() {
		this.db = new HashMap<>();
	}

	public boolean contains(String filePath) {
		return db.containsKey(filePath);
	}

	public void add(ClassInfo classInfo) {
		this.db.put(classInfo.getFile(), classInfo);
		
	}
	
	public Collection<ClassInfo> getAllClassInfo() {
		return db.values();
	}

	public ClassInfo get(String filePath) {
		return db.get(filePath);
	}

	public void rename(String oldPath, String newPath) {
		List<String> toRename = findKeysWithPath(oldPath);
		
		for(String fullOldPath : toRename) {
			ClassInfo classInfo = db.get(fullOldPath);
			db.remove(fullOldPath);
			classInfo.setFile(fullOldPath.replace(oldPath, newPath));
			db.put(fullOldPath.replace(oldPath, newPath), classInfo);
		}
	}

	public void delete(String oldPath, Calendar date, String hash, int count) {
		List<String> toDelete = findKeysWithPath(oldPath);
		
		for(String fullOldPath : toDelete) {
			ClassInfo classInfo = db.get(fullOldPath);
			classInfo.deleted(date, hash, count);
		}
	}

	private List<String> findKeysWithPath(String oldPath) {
		return db.keySet().stream()
		.filter(x -> x.endsWith(oldPath))
		.collect(Collectors.toList());
	}

	public void nothingChanged(String type, String hash, Calendar date, int count) {
		db.values().forEach(x -> {
			List<LiveSmell> smellyClasses = x.getAliveSmells(type);
			smellyClasses.stream().forEach(smell -> smell.update(date, hash, count));
		});
	}

}
