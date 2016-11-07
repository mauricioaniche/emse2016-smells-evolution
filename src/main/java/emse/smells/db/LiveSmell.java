package emse.smells.db;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LiveSmell {

	private String name;
	private Calendar dayStarted;
	private String firstSeenHash;

	private Calendar lastDaySeen;
	private String lastHashSeen;
	private boolean alive;
	
	public LiveSmell(String name, Calendar firstSeenDate, String firstSeenHash) {
		super();
		this.name = name;
		dayStarted = firstSeenDate;
		this.firstSeenHash = firstSeenHash;
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

	public boolean isAlive() {
		return alive;
	}

	public void removed(Calendar date, String hash) {
		this.alive = false;
		update(date, hash);
	}

	public String getFirstSeenHash() {
		return firstSeenHash;
	}
	@Override
	public String toString() {
		return "LiveSmell [name=" + name + ", dayStarted=" + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(dayStarted.getTime()) + ", lastDaySeen=" + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(lastDaySeen.getTime()) + ", alive="
				+ alive + "]";
	}

	public void update(Calendar date, String hash) {
		this.lastDaySeen = date;
		this.lastHashSeen = hash;
	}

	
}
