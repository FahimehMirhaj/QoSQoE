/**
 * BenchLab: Internet Scale Benchmarking.
 * Copyright (C) 2010-2011 Emmanuel Cecchet.
 * Contact: cecchet@cs.umass.edu
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 *
 * Initial developer(s): Emmanuel Cecchet.
 * Contributor(s): Alan Richardson.
 */

package edu.umass.cs.benchlab.har;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class defines a ISO8601DateFormatter
 * 
 * @author <a href="mailto:cecchet@cs.umass.edu>Emmanuel Cecchet</a>
 * @version 1.0
 */
public class ISO8601DateFormatter
{
  private static final SimpleDateFormat SDF  = new SimpleDateFormat(
                                                 "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
  private static final SimpleDateFormat SDFZ = new SimpleDateFormat(
                                                 "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

  /**
   * Converts an ISO8601 date string like "2011-01-31T17:16:38.500-05:00" to a
   * Date object
   * 
   * @param iso8601Str the date string
   * @return a Date object
   * @throws ParseException if the date is not in a valid format
   */
  public synchronized static Date parseDate(String iso8601Str)
      throws ParseException
  {
    if ("null".equals(iso8601Str))
      return null;

    // Remove the : in the timezone
    int cidx = iso8601Str.lastIndexOf(':');
    if (cidx == -1)
      throw new ParseException(
          "Invalid date format, expected '2011-01-31T17:16:38.500-05:00' instead of "
              + iso8601Str, 0);
    if (iso8601Str.endsWith("Z")) // Time format generated by Chrome
      return SDFZ.parse(iso8601Str);
    else if (iso8601Str.endsWith("+0000")) // Time format by browsermob-proxy
      return SDF.parse(iso8601Str);
    else if (iso8601Str.length() > 32)
    {
      // Fiddler outputs milliseconds with 7 numerals, strip down to three
      // crude parsing routine below

      /*
       * I could use regex as follows find: (2012-06-20T..:..:..\.)(...)(....)\+
       * replace: $1$2\+
       */
      int milliSecondPeriod = iso8601Str.lastIndexOf(".");
      int plusSymbol = iso8601Str.lastIndexOf("+");
      String parseStringAs = iso8601Str.substring(0, milliSecondPeriod + 1);
      parseStringAs = parseStringAs
          + iso8601Str.substring(milliSecondPeriod + 1, milliSecondPeriod + 4);
      parseStringAs = parseStringAs + iso8601Str.substring(plusSymbol);

      // now remove the : in the timezone
      cidx = parseStringAs.lastIndexOf(':');
      return SDF.parse(parseStringAs.substring(0, cidx)
          + parseStringAs.substring(cidx + 1));
    }
    else
      return SDF.parse(iso8601Str.substring(0, cidx)
          + iso8601Str.substring(cidx + 1));
  }

  /**
   * Format a date object to ISO8601 format like "2011-01-31T17:16:38.500-05:00"
   * 
   * @param date the Date object
   * @return the ISO8601 formatted date string
   */
  public synchronized static String format(Date date)
  {
    if (date == null)
      return "null";
    String iso = SDF.format(date);
    return iso.substring(0, iso.length() - 2) + ":"
        + iso.substring(iso.length() - 2);
  }

}
