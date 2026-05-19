package ch.studior2.buildingpermitmonitor.ingestor.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.stereotype.Component;

@Component
public class ExternalIdGenerator {

  public String generate(Map<String, String> payload) {
    String stableInput = new TreeMap<>(payload).toString();
    return sha256(stableInput);
  }

  private String sha256(String input) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] bytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(bytes);
    } catch (Exception exception) {
      throw new IllegalStateException("Could not create external ID hash.", exception);
    }
  }
}
