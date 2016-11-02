package emse.smells.db;

import java.util.HashMap;
import java.util.Map;

public class ClassRepository {

	private Map<String, ClassInfo> db;

	public ClassRepository() {
		this.db = new HashMap<>();
	}

	public ClassInfo getSmellyClass(String fileName) {
		if(!db.containsKey(fileName))
			db.put(fileName, new ClassInfo(fileName));
		return db.get(fileName);
	}
		
}
