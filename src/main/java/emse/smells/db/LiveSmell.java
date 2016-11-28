package emse.smells.db;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LiveSmell implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private Calendar dayStarted;
	private String firstSeenHash;
	private int firstSeenNumber;

	private Calendar lastDaySeen;
	private String lastHashSeen;
	private boolean alive;
	private int lastSeenNumber;
	
	public LiveSmell(String name, Calendar firstSeenDate, String firstSeenHash, int count) {
		super();
		this.name = name;
		dayStarted = firstSeenDate;
		this.firstSeenHash = firstSeenHash;
		firstSeenNumber = count;
		
		this.alive = true;
	}

	public Calendar getDayStarted() {
		return dayStarted;
	}

	public Calendar getLastDaySeen() {
		return lastDaySeen;
	}

	public void setLastDaySeen(Calendar lastDaySeen) {
		this.lastDaySeen = lastDaySeen;
	}
	
	public String getLastHashSeen() {
		return lastHashSeen;
	}

	public void setLastHashSeen(String lastHashSeen) {
		this.lastHashSeen = lastHashSeen;
	}

	public String getName() {
		return name;
	}

	public int getLastSeenNumber() {
		return lastSeenNumber;
	}
	
	public boolean isAlive() {
		return alive;
	}

	public void removed(Calendar date, String hash, int count) {
		this.alive = false;
		update(date, hash, count);
	}

	public String getFirstSeenHash() {
		return firstSeenHash;
	}
	
	public int getFirstSeenNumber() {
		return firstSeenNumber;
	}
	
	@Override
	public String toString() {
		return "LiveSmell [name=" + name + ", dayStarted=" + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(dayStarted.getTime()) + ", lastDaySeen=" + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(lastDaySeen.getTime()) + ", alive="
				+ alive + "]";
	}

	public void update(Calendar date, String hash, int count) {
		this.lastDaySeen = date;
		this.lastHashSeen = hash;
		this.lastSeenNumber = count;
	}

	
}
