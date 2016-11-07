package emse.smells.db;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ClassRepository {

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
		db.keySet().stream().filter(x -> x.endsWith(oldPath)).forEach(fullOldPath -> {
			ClassInfo classInfo = db.get(fullOldPath);
			db.remove(fullOldPath);
			classInfo.setFile(fullOldPath.replace(oldPath, newPath));
			db.put(fullOldPath.replace(oldPath, newPath), classInfo);
		});
	}

	public void delete(String oldPath, Calendar date, String hash) {
		db.keySet().stream().filter(x -> x.endsWith(oldPath)).forEach(fullOldPath -> {
			ClassInfo classInfo = db.get(fullOldPath);
			classInfo.deleted(date, hash);
		});
	}

}
