package emse.smells.db;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.Test;

public class ClassInfoTest {

	@Test
	public void newSmell() {
		GregorianCalendar jan = new GregorianCalendar(2010, Calendar.JANUARY, 30);
		ClassInfo ci = new ClassInfo("a.java", jan, "hash1", 1);

		GregorianCalendar fev = new GregorianCalendar(2010, Calendar.FEBRUARY, 10);
		ci.current(fev, "hash2", 2, "pmd", "GodClass");
		
		Assert.assertEquals(1, ci.getAllSmells("pmd").size());
		
		LiveSmell smell = ci.getAllSmells("pmd").get(0);
		Assert.assertEquals("GodClass", smell.getName());
		Assert.assertEquals(fev, smell.getDayStarted());
		Assert.assertEquals("hash2", smell.getFirstSeenHash());
		Assert.assertEquals(fev, smell.getLastDaySeen());
		Assert.assertEquals("hash2", smell.getLastHashSeen());
		Assert.assertTrue(smell.isAlive());

		Assert.assertEquals(2, smell.getFirstSeenNumber());
		Assert.assertEquals(2, smell.getLastSeenNumber());
	}

	@Test
	public void updateSmell() {
		GregorianCalendar jan = new GregorianCalendar(2010, Calendar.JANUARY, 30);
		ClassInfo ci = new ClassInfo("a.java", jan, "hash1", 1);
		
		GregorianCalendar fev = new GregorianCalendar(2010, Calendar.FEBRUARY, 10);
		ci.current(fev, "hash2", 2, "pmd", "GodClass");

		GregorianCalendar mar = new GregorianCalendar(2010, Calendar.MARCH, 10);
		ci.current(mar, "hash3", 3, "pmd", "GodClass");
		
		Assert.assertEquals(1, ci.getAllSmells("pmd").size());
		
		LiveSmell smell = ci.getAllSmells("pmd").get(0);
		Assert.assertEquals("GodClass", smell.getName());
		Assert.assertEquals(fev, smell.getDayStarted());
		Assert.assertEquals("hash2", smell.getFirstSeenHash());
		Assert.assertEquals(mar, smell.getLastDaySeen());
		Assert.assertEquals("hash3", smell.getLastHashSeen());
		Assert.assertTrue(smell.isAlive());
		
		Assert.assertEquals(2, smell.getFirstSeenNumber());
		Assert.assertEquals(3, smell.getLastSeenNumber());
	}

	@Test
	public void smellRemoved() {
		GregorianCalendar jan = new GregorianCalendar(2010, Calendar.JANUARY, 30);
		ClassInfo ci = new ClassInfo("a.java", jan, "hash", 1);
		
		GregorianCalendar fev = new GregorianCalendar(2010, Calendar.FEBRUARY, 10);
		ci.current(fev, "hash", 2, "pmd", "GodClass");
		
		GregorianCalendar mar = new GregorianCalendar(2010, Calendar.MARCH, 10);
		ci.current(mar, "hash", 3, "pmd", "GodClass");

		LiveSmell smell = ci.getAllSmells("pmd").get(0);
		Assert.assertEquals("GodClass", smell.getName());
		Assert.assertEquals(fev, smell.getDayStarted());
		Assert.assertEquals(mar, smell.getLastDaySeen());
		Assert.assertTrue(smell.isAlive());
		
		GregorianCalendar abr = new GregorianCalendar(2010, Calendar.APRIL, 10);
		ci.clean(abr, "hash5", 4, "pmd");
		
		Assert.assertEquals(1, ci.getAllSmells("pmd").size());
		smell = ci.getAllSmells("pmd").get(0);
		Assert.assertEquals("GodClass", smell.getName());
		Assert.assertEquals(fev, smell.getDayStarted());
		Assert.assertEquals(abr, smell.getLastDaySeen());
		Assert.assertFalse(smell.isAlive());
		Assert.assertEquals("hash5", smell.getLastHashSeen());
		
		Assert.assertEquals(2, smell.getFirstSeenNumber());
		Assert.assertEquals(4, smell.getLastSeenNumber());
	}

	@Test
	public void keepRemovedSmellsThere() {
		GregorianCalendar jan = new GregorianCalendar(2010, Calendar.JANUARY, 30);
		ClassInfo ci = new ClassInfo("a.java", jan, "hash", 1);
		GregorianCalendar fev = new GregorianCalendar(2010, Calendar.FEBRUARY, 10);
		ci.current(fev, "hash", 2, "pmd", "GodClass");
		GregorianCalendar mar = new GregorianCalendar(2010, Calendar.MARCH, 10);
		ci.current(mar, "hash", 3, "pmd", "GodClass");
		GregorianCalendar abr = new GregorianCalendar(2010, Calendar.APRIL, 10);
		ci.clean(abr, "hash5", 4, "pmd");

		// 2nd time a bug exists
		GregorianCalendar fev2 = new GregorianCalendar(2011, Calendar.FEBRUARY, 10);
		ci.current(fev2, "hash", 5, "pmd", "GodClass");
		GregorianCalendar mar2 = new GregorianCalendar(2011, Calendar.MARCH, 10);
		ci.current(mar2, "hash", 6, "pmd", "GodClass");
		GregorianCalendar abr2 = new GregorianCalendar(2011, Calendar.APRIL, 10);
		ci.clean(abr2, "hash10", 7, "pmd");
		
		Assert.assertEquals(2, ci.getAllSmells("pmd").size());
		System.out.println(ci.getAllSmells("pmd"));
		LiveSmell smell1 = ci.getAllSmells("pmd").get(0);
		Assert.assertEquals("GodClass", smell1.getName());
		Assert.assertEquals(fev, smell1.getDayStarted());
		Assert.assertEquals(abr, smell1.getLastDaySeen());
		Assert.assertEquals("hash5", smell1.getLastHashSeen());
		Assert.assertFalse(smell1.isAlive());
		Assert.assertEquals(2, smell1.getFirstSeenNumber());
		Assert.assertEquals(4, smell1.getLastSeenNumber());

		LiveSmell smell2 = ci.getAllSmells("pmd").get(1);
		Assert.assertEquals("GodClass", smell2.getName());
		Assert.assertEquals(fev2, smell2.getDayStarted());
		Assert.assertEquals(abr2, smell2.getLastDaySeen());
		Assert.assertFalse(smell2.isAlive());
		Assert.assertEquals("hash10", smell2.getLastHashSeen());
		
		Assert.assertEquals(5, smell2.getFirstSeenNumber());
		Assert.assertEquals(7, smell2.getLastSeenNumber());
	}
	
	@Test
	public void twoSmells() {
		GregorianCalendar jan = new GregorianCalendar(2010, Calendar.JANUARY, 30);
		ClassInfo ci = new ClassInfo("a.java", jan, "hash1", 1);

		GregorianCalendar fev = new GregorianCalendar(2010, Calendar.FEBRUARY, 10);
		ci.current(fev, "hash2", 2, "pmd", "GodClass");
		ci.current(fev, "hash2", 2, "pmd", "LongClass");

		GregorianCalendar mar = new GregorianCalendar(2010, Calendar.MARCH, 10);
		ci.current(mar, "hash3", 3, "pmd", "GodClass");

		GregorianCalendar apr = new GregorianCalendar(2010, Calendar.APRIL, 10);
		ci.current(apr, "hash4", 4, "pmd", "LongClass");
		
		Assert.assertEquals(2, ci.getAllSmells("pmd").size());
		
		LiveSmell smell = ci.getAllSmells("pmd").get(0);
		Assert.assertEquals("GodClass", smell.getName());
		Assert.assertEquals("hash2", smell.getFirstSeenHash());
		Assert.assertEquals(mar, smell.getLastDaySeen());
		Assert.assertEquals("hash3", smell.getLastHashSeen());
		Assert.assertTrue(smell.isAlive());
		
		Assert.assertEquals(2, smell.getFirstSeenNumber());
		Assert.assertEquals(3, smell.getLastSeenNumber());

		LiveSmell smell2 = ci.getAllSmells("pmd").get(1);
		Assert.assertEquals("LongClass", smell2.getName());
		Assert.assertEquals("hash2", smell2.getFirstSeenHash());
		Assert.assertEquals(apr, smell2.getLastDaySeen());
		Assert.assertEquals("hash4", smell2.getLastHashSeen());
		Assert.assertTrue(smell2.isAlive());
		
		Assert.assertEquals(2, smell2.getFirstSeenNumber());
		Assert.assertEquals(4, smell2.getLastSeenNumber());
	}
}
