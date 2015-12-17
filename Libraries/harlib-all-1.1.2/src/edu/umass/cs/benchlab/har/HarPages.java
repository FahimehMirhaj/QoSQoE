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
 * Contributor(s): Fabien Mottet.
 */

package edu.umass.cs.benchlab.har;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

import edu.umass.cs.benchlab.har.tools.HarFileWriter;

/**
 * This class defines a HarPages
 * 
 * @author <a href="mailto:cecchet@cs.umass.edu>Emmanuel Cecchet</a>
 * @version 1.0
 */
public class HarPages
{
  private List<HarPage> pages;

  /**
   * Creates a new <code>HarPages</code> object
   */
  public HarPages()
  {
    pages = new ArrayList<HarPage>();
  }

  /**
   * Creates a new <code>HarEntries</code> objectfrom a JsonParser already
   * positioned at the beginning of the element content
   * 
   * @param jp a JsonParser already positioned at the beginning of the element
   *          content
   * @param warnings null if parser should fail on first error, pointer to
   *          warning list if warnings can be issued for missing fields
   * @throws JsonParseException
   * @throws IOException
   */
  public HarPages(JsonParser jp, List<HarWarning> warnings)
      throws JsonParseException, IOException
  {
    pages = new ArrayList<HarPage>();

    // Read the content of the pages element
    if (jp.nextToken() != JsonToken.START_ARRAY)
    {
      throw new JsonParseException("[ missing after \"pages\" element "
          + jp.getCurrentName(), jp.getCurrentLocation());
    }

    while (jp.nextToken() != JsonToken.END_ARRAY)
    {
      addPage(new HarPage(jp, warnings));
    }
  }

  /**
   * Creates a new <code>HarPages</code> object from a database. Retrieves the
   * HarPage objects that corresponds to the HarLog object with the specified
   * id.
   * 
   * @param config the database configuration to use
   * @param logId the HarLog id to read
   * @throws SQLException if a database error occurs
   */
  public HarPages(HarDatabaseConfig config, long logId) throws SQLException
  {
    pages = new ArrayList<HarPage>();
    Connection c = config.getConnection();
    String tableName = config.getTablePrefix() + "pages";
    PreparedStatement ps = null;
    ResultSet rs = null;
    try
    {
      ps = c.prepareStatement("SELECT page_id FROM " + tableName
          + " WHERE log_id=?");
      ps.setLong(1, logId);
      rs = ps.executeQuery();
      while (rs.next())
        addPage(new HarPage(config, rs.getLong(1)));
    }
    finally
    {
      try
      {
        if (rs != null)
          rs.close();
      }
      catch (Exception ignore)
      {
      }
      try
      {
        if (ps != null)
          ps.close();
      }
      catch (Exception ignore)
      {
      }
      config.closeConnection(c);
    }
  }

  /**
   * Write this object on a JsonGenerator stream
   * 
   * @param g a JsonGenerator
   * @throws IOException if an IO error occurs
   * @throws JsonGenerationException if the generator fails
   * @see HarFileWriter#writeHarFile(HarLog, java.io.File)
   */
  public void writeHar(JsonGenerator g) throws JsonGenerationException,
      IOException
  {
    g.writeArrayFieldStart("pages");
    for (HarPage page : pages)
      page.writeHar(g);
    g.writeEndArray();
  }

  /**
   * Write this object in the given database referencing the specified logId.
   * 
   * @param logId the logId this object refers to
   * @param config the database configuration
   * @throws SQLException if a database access error occurs
   */
  public void writeJDBC(long logId, HarDatabaseConfig config)
      throws SQLException
  {
    Connection c = config.getConnection();
    String pageTableName = config.getTablePrefix() + HarPage.TABLE_NAME;
    if (!config.isCreatedTable(pageTableName))
    {
      try
      {
        Statement s = c.createStatement();
        s.executeUpdate("CREATE TABLE " + pageTableName + " (page_id "
            + config.getDbAutoGeneratedId() + ",start_date "
            + config.getTimestampDbType() + ",id " + config.getStringDbType()
            + ",title " + config.getStringDbType() + ",comment "
            + config.getStringDbType() + ",log_id " + config.getLongDbType()
            + ")");
        s.close();
        config.addCreatedTable(pageTableName);
      }
      catch (Exception ignore)
      { // Database table probably already exists
      }
    }
    String timingsTableName = config.getTablePrefix()
        + HarPageTimings.TABLE_NAME;
    if (!config.isCreatedTable(timingsTableName))
    {
      try
      {
        Statement s = c.createStatement();
        s.executeUpdate("CREATE TABLE " + config.getTablePrefix()
            + "page_timings " + " ( page_timings_id "
            + config.getDbAutoGeneratedId() + ", on_content_load "
            + config.getLongDbType() + ", on_load " + config.getLongDbType()
            + ", comment " + config.getStringDbType() + ", page_id "
            + config.getLongDbType() + ")");
        s.close();
        config.addCreatedTable(timingsTableName);
      }
      catch (Exception ignore)
      { // Database table probably already exists
      }
    }
    PreparedStatement pagePs = c.prepareStatement("INSERT INTO "
        + pageTableName
        + " (start_date,id,title,comment,log_id) VALUES (?,?,?,?,?)",
        Statement.RETURN_GENERATED_KEYS);
    PreparedStatement timingsPs = c.prepareStatement("INSERT INTO "
        + timingsTableName
        + " (on_content_load,on_load,comment,page_id) VALUES (?,?,?,?)",
        Statement.RETURN_GENERATED_KEYS);
    try
    {
      for (HarPage page : pages)
        page.writeJDBC(config, logId, pagePs, timingsPs);
    }
    finally
    {
      try
      {
        if (pagePs != null)
          pagePs.close();
      }
      catch (Exception ignore)
      {
      }
      try
      {
        if (timingsPs != null)
          timingsPs.close();
      }
      catch (Exception ignore)
      {
      }
      config.closeConnection(c);
    }
  }

  /**
   * Delete page and page timings objects in the database referencing the
   * specified logId.
   * 
   * @param logId the logId this object refers to
   * @param config the database configuration
   * @throws SQLException if a database access error occurs
   */
  public void deleteFromJDBC(HarDatabaseConfig config, long logId)
      throws SQLException
  {
    Connection c = config.getConnection();
    String timingsTableName = config.getTablePrefix()
        + HarPageTimings.TABLE_NAME;

    PreparedStatement timingsPs = null;
    try
    {
      // Delete timings first
      timingsPs = c.prepareStatement("DELETE FROM " + timingsTableName
          + " WHERE page_id IN (SELECT page_id FROM " + config.getTablePrefix()
          + "pages WHERE log_id=?)");
      timingsPs.setLong(1, logId);
      timingsPs.executeUpdate();
      config.dropTableIfEmpty(c, timingsTableName, config);

      // Delete pages second
      config.deleteFromTable(logId, config, "pages");
    }
    finally
    {
      try
      {
        if (timingsPs != null)
          timingsPs.close();
      }
      catch (Exception ignore)
      {
      }
      config.closeConnection(c);
    }
  }

  /**
   * Add a new page to the list
   * 
   * @param page the page to add
   */
  public void addPage(HarPage page)
  {
    pages.add(page);
  }

  /**
   * Remove a page from the list
   * 
   * @param page the page to remove
   */
  public void removePage(HarPage page)
  {
    pages.remove(page);
  }

  /**
   * Returns the pages value.
   * 
   * @return Returns the pages.
   */
  public List<HarPage> getPages()
  {
    return pages;
  }

  /**
   * Sets the pages value.
   * 
   * @param pages The pages to set.
   */
  public void setPages(List<HarPage> pages)
  {
    this.pages = pages;
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuffer sb = new StringBuffer("  \"pages\": [");
    if (pages != null)
    {
      boolean first = true;
      for (HarPage page : pages)
      {
        if (first)
          first = false;
        else
          sb.append(", ");
        sb.append(page);
      }
    }
    sb.append("]\n");
    return sb.toString();
  }

}
