package us.sportsanalytics.backend.services.persistence.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CsvParser {

    private static final String DELIMITER = ",";

    private final int maxRows;

    public CsvParser(@Value("${app.processing.csv.parse.limit}") int maxRows) {
        this.maxRows = maxRows;
    }

    public Map<String, ColumnInference> mapColumnTypes(Reader reader) throws IOException {
        Map<String, ColumnInference> res = new HashMap<String, ColumnInference>();
        CSVParser parser = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).setTrim(true).build()
                .parse(reader);

        for (String colName : parser.getHeaderMap().keySet()) {
            res.put(colName, new ColumnInference());
        }

        int count = 0;
        for (CSVRecord record : parser) {
            if (count++ >= maxRows)
                break;
            for (String colName : res.keySet()) {
                String rawValue = record.get(colName);
                res.get(colName).observe(rawValue);
            }
        }
        return res;
    }

    public Map<String, ColumnInference> mapColumnTypes(Path csvPath) throws IOException {

        Map<String, ColumnInference> res = new HashMap<String, ColumnInference>();

        // try (BufferedReader br = Files.newBufferedReader(csvPath)) {
        // // Read first line as the csv header
        // // .split default behavior is skip empty trailing strings "a,b,c," => ["a",
        // "b",
        // // "c"] instead of
        // // ["a", "b", "c", ""] => Set to -1 to indicate no limites, keep all empty
        // // trailing strings
        // String headerLine = br.readLine();

        // if (headerLine == null) {
        // throw new IllegalArgumentException("CSV File is Empty");
        // }

        // String[] header = headerLine.split(DELIMITER, -1);
        // int nCol = header.length;

        // for (String colName : header) {
        // res.put(colName, new ColumnInference());
        // }

        // String line;
        // // int countRow = 0;
        // while ((line = br.readLine()) != null) {

        // String[] row = line.split(DELIMITER, -1);

        // for (int i = 0; i < nCol; i++) {

        // String raw = i < row.length ? row[i] : "";

        // // if (raw == "" || raw == null) {
        // // System.out.println(countRow);
        // // }

        // res.get(header[i]).observe(raw);
        // }
        // }

        // return res;
        // }

        try (BufferedReader br = Files.newBufferedReader(csvPath)) {
            CSVParser parser = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).setTrim(true).build()
                    .parse(br);

            for (String colName : parser.getHeaderMap().keySet()) {
                res.put(colName, new ColumnInference());
            }

            int count = 0;
            for (CSVRecord record : parser) {
                if (count++ >= maxRows)
                    break;
                for (String colName : res.keySet()) {
                    String rawValue = record.get(colName);
                    res.get(colName).observe(rawValue);
                }
            }
        }

        return res;

    }
}
