// RegFilter.java
// by Alejandro Vázquez C.

import java.io.*;

public class RegFilter implements FilenameFilter
{
  protected String filter;

  public RegFilter(String f)
  { filter = f; }

  public boolean accept(File file, String name)
  { return name.endsWith("."+filter); }
}