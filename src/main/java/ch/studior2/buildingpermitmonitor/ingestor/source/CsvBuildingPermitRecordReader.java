package ch.studior2.buildingpermitmonitor.ingestor.source;

import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

@Component
public class CsvBuildingPermitRecordReader {

  public List<Map<String, String>> read(Reader reader) throws Exception {
    try (CSVParser parser =
        CSVParser.parse(
            reader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build())) {
      return parser.stream().map(record -> toPayload(parser, record)).toList();
    }
  }

  private Map<String, String> toPayload(CSVParser parser, CSVRecord record) {
    Map<String, String> payload = new HashMap<>();

    for (String header : parser.getHeaderMap().keySet()) {
      payload.put(header, record.get(header));
    }

    return payload;
  }
}
