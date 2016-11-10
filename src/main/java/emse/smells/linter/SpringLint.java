package emse.smells.linter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;

import emse.smells.Report;
import emse.smells.Smell;
import emse.smells.util.Cmd;

public class SpringLint {

	private String linterPath;
	private static Logger log = Logger.getLogger(SpringLint.class);

	public SpringLint(String linterPath) {
		this.linterPath = linterPath;
	}

	public Report run(String path) {
		try {
			Cmd cmd = new Cmd(linterPath);
			log.info("Running Springlint");
			
			cmd.execute("rm smells.csv");
			cmd.execute("java -jar springlint.jar -p " + path + " -otype csv -o .");
			String response = cmd.execute("cat smells.csv");
			
			CSVParser parsedCsv = CSVParser.parse(response, CSVFormat.DEFAULT);
			
			Report report = new Report();
			
			int count = 0;
			for (CSVRecord csvRecord : parsedCsv) {
				if(count++ == 0) continue;
				String file = csvRecord.get(0);
				String smell = csvRecord.get(3);
				
				report.add(new Smell(file, smell));
			}
			
			log.info("Springlint found " + report.size() + " smells");			
			return report;
		} catch (Exception e) {
			log.error("error in SpringLint", e);
			return new Report();
		}
	}

}
