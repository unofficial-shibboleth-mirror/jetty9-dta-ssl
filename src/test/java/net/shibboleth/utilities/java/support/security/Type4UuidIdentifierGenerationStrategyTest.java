package net.shibboleth.utilities.java.support.security;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.Assert;
import org.testng.annotations.Test;

public class Type4UuidIdentifierGenerationStrategyTest {

    /**
     * Test generateIdentifier by generating a large number of identifiers
     * and seeing whether each one is a valid XML ID, and that they are
     * all different.
     */
    @Test
    public void testGenerateIdentifier() {
      final Pattern ncNamePattern = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_\\-\\.]+$");
      final IdentifierGenerationStrategy strat = new Type4UuidIdentifierGenerationStrategy();
      final int howMany = 1000;
      final Set<String> values = new HashSet<String>(1000);
      for (int iteration = 1; iteration<=howMany; iteration++) {
          final String value = strat.generateIdentifier();
          
          // we shouldn't see the same value twice
          if (values.contains(value)) {
              Assert.fail("duplicate value " + value + " on iteration " + iteration);
          }
          values.add(value);
          
          // values should be valid NCNames
          final Matcher match = ncNamePattern.matcher(value);
          if (!match.matches()) {
              Assert.fail("value " + value + " is not a valid NCName on iteration " + iteration);
          }
      }
  }
}
