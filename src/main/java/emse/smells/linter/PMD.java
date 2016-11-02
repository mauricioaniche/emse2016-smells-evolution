package emse.smells.linter;

import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;

import emse.smells.Report;
import emse.smells.Smell;
import emse.smells.util.Cmd;

public class PMD {

	private static Logger log = Logger.getLogger(PMD.class);
	
	private String pmdPath;

	public PMD(String pmdPath) {
		this.pmdPath = pmdPath;
	}
	
	public Report run(String path) {
		try {
			Cmd cmd = new Cmd(pmdPath);
			String response = cmd.execute("./run.sh pmd -d " + path + " -f csv -R phd.xml -language java");
			CSVParser parsedCsv = CSVParser.parse(response, CSVFormat.DEFAULT);
			
			Report report = new Report();
			
			int count = 0;
			for (CSVRecord csvRecord : parsedCsv) {
				if(count++ == 0) continue;
				String file = csvRecord.get(2);
				String smell = csvRecord.get(7);
				
				report.add(new Smell(file, smell));
			}
			
			return report;
		} catch (IOException e) {
			log.error("error in pmd", e);
			return new Report();
		}
	}
}
