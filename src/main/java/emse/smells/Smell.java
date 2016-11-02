package emse.smells;

public class Smell {

	private String file;
	private String smell;
	
	public Smell(String file, String smell) {
		this.file = file;
		this.smell = smell;
	}

	public String getFile() {
		return file;
	}

	public String getSmell() {
		return smell;
	}

	@Override
	public String toString() {
		return "Smell [file=" + file + ", smell=" + smell + "]";
	}

}
