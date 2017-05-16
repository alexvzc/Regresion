// VitaliFunction.java
// by Alejandro Vázquez C.

import java.io.Serializable;
import java.util.Hashtable;

public abstract class VitaliFunction implements Serializable
{
  protected static Hashtable functions = new Hashtable();

  protected static void register(VitaliFunction fn)
  { functions.put(fn.name(), fn); }
  
  public abstract String name();

  public matriz fill(double[] X0, int vars)
  {
    matriz X = new matriz(X0.length, vars);
    for(int i=0; i<X.width; i++)
      for(int j=0; j<X.height; j++)
        X.data[i][j] = dofn(X0[i], j);
    return X;
  }

  public double calculate(matriz beta, double x0)
  {
    double acc = 0;
    for(int i=0; i<beta.width; i++)
      acc += dofn(x0, i)*beta.data[i][0];
    return acc;
  }

  public abstract double dofn(double x0, int i0);
}
