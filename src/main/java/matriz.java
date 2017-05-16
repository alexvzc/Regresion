// matriz.java

public class matriz
{
  public double[][] data;
  public int width;
  public int height;

  public matriz(int r, int c)
  {
    data = new double[r][c];
    width = r;
    height = c;
    for (int i=0; i<width; i++)
      for (int j=0; j<height; j++)
        data[i][j] = 0;
  }

  public matriz(matriz a)
  {
    width = a.width;
    height = a.height;
    data = new double[width][height];
    for (int i=0; i<width; i++)
      for (int j=0; j<height; j++)
        data[i][j] = a.data[i][j];
  }

  public static matriz ident(int r)
  {
    matriz result = new matriz(r,r);
    for (int i=0; i<r; i++)
      result.data[i][i] = 1;
    return result;
  }

  public matriz add(matriz a)
  {
    if ((width!=a.width) || (height!=a.height))
      throw new ArrayIndexOutOfBoundsException();

    matriz result = new matriz(width, height);

    for(int i=0; i<width; i++)
      for(int j=0; j<height; j++)
        result.data[i][j] = data[i][j]+a.data[i][j];

    return result;
  }
  
  public matriz sub(matriz a)
  {
    if ((width!=a.width) || (height!=a.height))
      throw new ArrayIndexOutOfBoundsException();

    matriz result = new matriz(width, height);

    for(int i=0; i<width; i++)
      for(int j=0; j<height; j++)
        result.data[i][j] = data[i][j]-a.data[i][j];

    return result;
  }
  
  public matriz mult(matriz a)
  {
    if (height!=a.width)
      throw new ArrayIndexOutOfBoundsException();

    matriz result = new matriz(width, a.height);

    for(int i=0; i<width; i++)
      for(int j=0; j<a.height; j++)
      {
        double ax = 0;

        for (int k=0; k<height; k++)
          ax += data[i][k] * a.data[k][j];

        result.data[i][j] = ax;
      }

    return result;
  }

  public matriz mult(double f)
  {
    matriz result = new matriz(this);

    for (int i=0; i<width; i++)
      for (int j=0; j<height; j++)
        result.data[i][j] *= f;

    return result;
  }
  
  public matriz trans()
  {
    matriz result = new matriz(height, width);

    for(int i=0; i<width; i++)
      for(int j=0; j<height; j++)
        result.data[j][i] = data[i][j];

    return result;
  }

  public matriz inv()
  {
    if (width!=height)
      throw new ArrayIndexOutOfBoundsException();

    matriz t  = new matriz(this);
    matriz iv = matriz.ident(width);

    for(int i=0; i<width; i++)
    {
      for(int j=i; j<width; j++)
      {
        double f = t.data[j][i];
        if (f!=0)
          for(int k=0; k<height; k++)
          {
            t.data[j][k] /= f;
            iv.data[j][k] /= f;
          }
      }
      for(int j=i+1; j<width; j++)
      {
        if (t.data[j][i]!=0)
          for(int k=0; k<width; k++)
          {
            t.data[j][k] -= t.data[i][k];
            iv.data[j][k] -= iv.data[i][k];
          }
      }
    }

    for(int i=width-1; i>=0; i--)
      for(int j=i-1; j>=0; j--)
      {
        double f = t.data[j][i];
        for(int k=0; k<width; k++)
        {
          t.data[j][k] -= t.data[i][k]*f;
          iv.data[j][k] -= iv.data[i][k]*f;
        }
      }

    return iv;
  }
}
