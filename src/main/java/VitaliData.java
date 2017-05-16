// VitaliData.java
// by Alejandro Vázquez C.

import java.io.*;
import java.util.*;

class VitaliElem implements Serializable
{
  public double x;
  public double y;

  public VitaliElem(double x0, double y0)
  { x = x0; y = y0; }

  public String toString()
  { return "X: "+x+" Y: "+y; }
}

class VitaliResult implements Serializable
{
  public double[] realx;
  public matriz X;
  public matriz Y;
  public matriz C;
  public matriz beta;
  public double SEC;
  public double S2;
  public VitaliFunction function;

  public VitaliResult(double[] X0, double[] Y0, int vars, VitaliFunction fn0)
  {
    function = fn0;
    X = function.fill(X0, vars);
    realx = X0;

    Y = new matriz(Y0.length, 1);
    for(int i=0; i<Y0.length; i++)
      Y.data[i][0] = Y0[i];

    matriz Xtrans = X.trans();
    C = Xtrans.mult(X).inv();
    beta = C.mult(Xtrans).mult(Y);
    SEC = Y.trans().mult(Y).sub(beta.trans().mult(Xtrans).mult(Y)).data[0][0];
    S2 = SEC/(X.width-(X.height+1));
  }

  public double[] betas()
  {
    double[] result = new double[beta.width];
    for (int i=0; i<beta.width; i++)
      result[i] = beta.data[i][0];
    return result;
  }

  public double dofn(double x0)
  { return function.calculate(beta, x0); }

  public double varbeta(int i0)
  { return C.data[i0][i0]*SEC; }

  public double T(int i0, double realbeta)
  { return (beta.data[i0][0]-realbeta)/(Math.sqrt(Math.abs(S2*C.data[i0][i0]))); }

  public double[] minmaxX()
  {
    if (X.height>1)
    {
      double xminmax[] = { Double.MAX_VALUE, Double.MIN_VALUE };
      for(int i=0; i<X.width; i++)
      {
        if (X.data[i][1] < xminmax[0])
          xminmax[0] = X.data[i][1];
        if (X.data[i][1] > xminmax[1])
          xminmax[1] = X.data[i][1];
      }
      double xdelta = (xminmax[1] - xminmax[0])/5;
      xminmax[0] -= xdelta;
      xminmax[1] += xdelta;

      return xminmax;
    }
    else
    {
      double[] xmm = {0.0, 0.0};
      return xmm;
    }
  }

  public double[] minmaxY()
  {
    if(Y.width<=1)
    {
      double[] ymm = {0.0, 0.0};
      return ymm;
    }

    double yminmax[] = { Double.MAX_VALUE, Double.MIN_VALUE };
    for(int i=0; i<Y.width; i++)
    {
      if (Y.data[i][0] < yminmax[0])
        yminmax[0] = Y.data[i][0];
      if (Y.data[i][0] > yminmax[1])
        yminmax[1] = Y.data[i][0];
    }
    double ydelta = (yminmax[1] - yminmax[0])/5;
    yminmax[0] -= ydelta;
    yminmax[1] += ydelta;

    return yminmax;
  }
}

public class VitaliData implements Serializable
{
  public Vector data = new Vector();

  public int vars;
  public VitaliFunction fn;

  public VitaliData(int v0, VitaliFunction f0)
  { vars = v0; fn = f0; }

  public void add(VitaliElem v0)
  { data.addElement(v0); }

  public void eraseAll()
  { data.removeAllElements(); }

  public VitaliResult compute()
  {
    double[] X = new double[data.size()];
    double[] Y = new double[data.size()];
    for(int i=0; i<data.size(); i++)
    {
      VitaliElem vi = (VitaliElem)data.elementAt(i);
      X[i] = vi.x;
      Y[i] = vi.y;
    }
    return new VitaliResult(X, Y, vars, fn);
  }
}
