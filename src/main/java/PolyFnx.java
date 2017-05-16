// PolyFnx.java
// by Alejandro Vázquez C.

public class PolyFnx extends VitaliFunction
{
  static
  { register(new PolyFnx()); }

  private PolyFnx()
  { }

  public String name()
  { return "x, x^2, x^3,..."; }

  public double dofn(double x0, int i0)
  {
    double result = 1;
    for(int i=0; i<i0; i++)
      result *= x0;
    return result;
  }
}
