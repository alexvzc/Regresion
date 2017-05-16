/* ResultWindow.java
 * by Alejandro Vázquez C.
 */

import netscape.application.*;

class FnGraphic extends View
{
  VitaliResult vr;
  double[] xs;
  double[] ys;

  public FnGraphic(int x, int y, int w, int h)
  {
    super(x, y, w, h);
    setBuffered(true);
  }

  public void setResult(VitaliResult vr0)
  {
    vr = vr0;
    xs = vr.minmaxX();
    ys = vr.minmaxY();
  }

  public void drawView(Graphics g)
  {
    if (vr==null)
      return;

    Rect r = new Rect(0, 0, width(), height());
    if (xs[0]==xs[1] || ys[0]==ys[1])
    {
      g.setColor(Color.darkGray);
      g.fillRect(r);
      return;
    }

    g.setColor(Color.white);
    g.fillRect(r);

    g.setColor(Color.black);

    double x0 = xs[0], x1 = (xs[1]-xs[0])/r.width;
    double y1 = -r.height/(ys[1]-ys[0]), y0 = r.height-ys[0]*y1;

    int lasty = r.width;
    for(int i=0; i<r.width; i++)
    {
      int y = (int)(y1*vr.dofn(x1*i+x0)+y0);
      if (i>0)
        g.drawLine(i-1, lasty, i, y);
      lasty = y;
    }

    g.setColor(Color.red);

    for(int i=0; i<vr.realx.length; i++)
    {
      int xi = (int)((vr.realx[i]-x0)/x1);
      int yi = (int)(y1*vr.Y.data[i][0]+y0);
      g.drawOval(xi-4, yi-4, 8, 8);
    }
  }
}

class HippoWindow extends InternalWindow
  implements Target
{
  public static final String C_UPDATE = "~~Actualiza~~";

  TextField tf0;
  TextField tf1;
  VitaliResult vr;
  int index;

  public HippoWindow(VitaliResult vr0, int indx0)
  {
    super();
    vr = vr0;
    index = indx0;
    Size size = windowSizeForContentSize(250, 55);
    sizeTo(size.width, size.height);
    moveTo(60,60);
    setCloseable(true);

    Label lbl = new Label("Beta:", null);
    lbl.moveTo(5,5);
    addSubview(lbl);

    tf0 = new TextField(40, 5, 205, 20);
    tf0.setStringValue(Double.toString(vr.betas()[index]));
    tf0.setCommand(C_UPDATE);
    tf0.setTarget(this);
    addSubview(tf0);

    lbl = new Label("t:", null);
    lbl.moveTo(5, 30);
    addSubview(lbl);

    tf1 = new TextField(40, 30, 205, 20);
    tf1.setEditable(false);
    addSubview(tf1);
  }

  public void performCommand(String cmd, Object data)
  {
    if (cmd==C_UPDATE)
      try
      {
        double bx = Double.valueOf(tf0.stringValue()).doubleValue();
        double t = vr.T(index, bx);
        tf1.setStringValue(Double.toString(t));
      }
      catch(NumberFormatException nfe)
      { tf0.setFocusedView(); }
    else
      super.performCommand(cmd, data);
  }
}

public class ResultWindow extends InternalWindow
  implements Target
{
  public static final String C_HIPPO = "~~Prueba~de~hipótesis~~";
 
  VitaliResult vr;
  ListView lv;

  public ResultWindow(VitaliResult vr0)
  {
    super();
    vr = vr0;
    Size size = windowSizeForContentSize(490, 280);
    sizeTo(size.width, size.height);
    moveTo(30,30);    
    setTitle("Resultados");
    setCloseable(true);

    ScrollGroup sg = new ScrollGroup(5, 5, 205, 270);
    lv = new ListView(0, 0, 205, 0);

    double[] b = vr.betas();
    for(int i=0; i<b.length; i++)
    {
      ListItem li = new ListItem();
      li.setTitle("B"+i+": "+b[i]);
      li.setData(new Integer(i));
      lv.addItem(li);
    }

    lv.sizeToMinSize();
    lv.setTarget(this);
    lv.setDoubleCommand(C_HIPPO);
    sg.setContentView(lv);
    sg.setHasVertScrollBar(true);
    addSubview(sg);

    FnGraphic fng = new FnGraphic(215, 5, 270, 270);
    fng.setResult(vr);
    addSubview(fng);
  }

  public void performCommand(String cmd, Object data)
  {
    if (cmd==null)
      return;
    if (cmd==C_HIPPO)
    {
      int indx = ((Integer)lv.selectedItem().data()).intValue();
      (new HippoWindow(vr, indx)).showModally();
    }
    else
      super.performCommand(cmd, data);
  }
}
