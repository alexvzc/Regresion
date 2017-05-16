// OddFnx.java
// by Alejandro Vázquez C.

public class OddFnx extends VitaliFunction
{
  static
  { register(new OddFnx()); }

  private OddFnx()
  { }

  public String name()
  { return "x, x^3, x^5,..."; }

  public double dofn(double x0, int i0)
  {
    double result = 1;
    for(int i=0; i<i0; i++)
      result *= (i<1)?(x0):(x0*x0);
    return result;
  }
}
