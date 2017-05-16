// VitaliWindow.java
// by Alejandro Vázquez C.

import netscape.application.*;
import java.io.*;
import java.util.*;

public class VitaliWindow extends InternalWindow implements WindowOwner, Target
{
  public final static String C_SAVE = "~~Guardar~~";
  public final static String C_SAVEAS = "~~Guardar~como~~";
  public final static String C_CLOSE = "~~Cerrar~~";
  public final static String C_ADD = "~~Añadir~~";
  public final static String C_MODIFY = "~~Modificar~~";
  public final static String C_DELETE = "~~Eliminar~~";
  public final static String C_COMPUTE = "~~Calcular~~";
  public final static String C_LISTCHANGED = "~~Cambio~en~la~lista~~";
  public final static String C_MISCCHANGED = "~~Cambio~~miscelaneo~~";

  protected String filename;
  protected boolean saved = true;

  protected TextField xinput;
  protected TextField yinput;
  protected TextField vinput;
  protected Button add_butt;
  protected Button modify_butt;
  protected Button delete_butt;
  protected Button compute_butt;
  protected ListView xy_list;
  protected Popup fn_popup;

  public VitaliWindow()
  {
    super(20, 20, 275, 350);
    init();
  }

  public VitaliWindow(String filename0)
  {
    super(20, 20, 275, 350);
    filename = filename0;
    VitaliData vd;
    try 
    { vd = loadfile(); }
    catch (IOException ioe)
    {
      Alert.runAlertInternally(Alert.warningImage(), "Error de Archivo",
        "Hubo un error al tratar de leer el archivo "+filename,
        "Ok", null, null);
      return;
    }
    init();
    decodedata(vd);
  }

  public Menu createMenu()
  {
    Menu menu = new Menu();
    Menu file = menu.addItemWithSubmenu("Archivo").submenu();
      file.addItem("Guardar", C_SAVE, this);
      file.addItem("Guardar como...", C_SAVEAS, this);
      file.addSeparator();
      file.addItem("Cerrar", C_CLOSE, this);

    return menu;
  }

  public void init()
  {
    setOwner(this);
    setCloseable(true);
    if (filename!=null)
      setTitle(filename);
    else
      setTitle("[ sin nombre ]");
    MenuView mv = new MenuView(createMenu());
    mv.sizeToMinSize();
    setMenuView(mv);

    View view = new Label("X:", null);
    view.moveTo(5,8);
    addSubview(view);
    xinput = new TextField(20, 5, 60, 20);
    xinput.setStringValue("0");
    addSubview(xinput);

    view = new Label("Y:", null);
    view.moveTo(85,8);
    addSubview(view);
    yinput = new TextField(100, 5, 60, 20);
    yinput.setStringValue("0");
    addSubview(yinput);

    ScrollGroup sg = new ScrollGroup(5, 30, 155, 270);
    xy_list = new ListView(0,0,155,0);
    xy_list.setCommand(C_LISTCHANGED);
    xy_list.setTarget(this);
    sg.setContentView(xy_list);
    sg.setHasVertScrollBar(true);
    sg.setBorder(new BezelBorder(BezelBorder.LOWERED, Color.lightGray));
    addSubview(sg);

    add_butt = new Button(170, 5, 90, 20);
    add_butt.setCommand(C_ADD);
    add_butt.setTarget(this);
    add_butt.setTitle("Añadir");
    addSubview(add_butt);

    modify_butt = new Button(170, 30, 90, 20);
    modify_butt.setCommand(C_MODIFY);
    modify_butt.setTarget(this);
    modify_butt.setTitle("Modificar");
    addSubview(modify_butt);

    delete_butt = new Button(170, 55, 90, 20);
    delete_butt.setCommand(C_DELETE);
    delete_butt.setTarget(this);
    delete_butt.setTitle("Eliminar");
    addSubview(delete_butt);

    view = new Label("Vars:", null);
    view.moveTo(170,80);
    addSubview(view);
    vinput = new TextField(170, 100, 60, 20);
    vinput.setStringValue("1");
    vinput.setCommand(C_MISCCHANGED);
    vinput.setTarget(this);
    addSubview(vinput);

    fn_popup = new Popup(170, 130, 90, 25);

    Enumeration e = VitaliFunction.functions.keys();
    while(e.hasMoreElements())
      fn_popup.addItem((String)e.nextElement(), C_MISCCHANGED);

    fn_popup.setCommand(C_MISCCHANGED);
    fn_popup.setTarget(this);
    addSubview(fn_popup);

    compute_butt = new Button(170, 170, 90, 20);
    compute_butt.setCommand(C_COMPUTE);
    compute_butt.setTarget(this);
    compute_butt.setTitle("Calcular");
    addSubview(compute_butt);

    show();
  }

  public VitaliElem parsedata()
  {
    try
    {
      return new VitaliElem(
        Double.valueOf(xinput.stringValue()).doubleValue(),
        Double.valueOf(yinput.stringValue()).doubleValue());
    }
    catch (NumberFormatException nfe)
    { return null; }
  }

  public void unparsedata(VitaliElem ve)
  {
    xinput.setStringValue(Double.toString(ve.x));
    yinput.setStringValue(Double.toString(ve.y));
  }

  public int getvarno()
  {
    try
    { return Integer.valueOf(vinput.stringValue()).intValue(); }
    catch (NumberFormatException nfe)
    { return 0; }
  }

  public void add_elem()
  {
    VitaliElem ve = parsedata();
    if (ve!=null)
    {
      ListItem li = new ListItem();
      li.setTitle(ve.toString());
      li.setData(ve);
      xy_list.addItem(li);
      xy_list.sizeToMinSize();
      xy_list.draw();
      saved=false;
    }
    else
      Alert.runAlertInternally(Alert.warningImage(), "Datos inválidos",
        "Alguno de los campos contiene información invalida. Recurede que "+
        "sólo debe ingresar números.", "Ok", null, null);
  }

  public void modify_elem()
  {
    if (xy_list.count()>0)
    {
      VitaliElem ve = parsedata();
      if (ve!=null)
      {
        ListItem li = xy_list.selectedItem();
        li.setTitle(ve.toString());
        li.setData(ve);
        xy_list.sizeToMinSize();
        xy_list.draw();
        saved=false;
      }
      else
        Alert.runAlertInternally(Alert.warningImage(), "Datos inválidos",
          "Alguno de los campos contiene información invalida. Recurede "+
          "que sólo debe ingresar números.", "Ok", null, null);
    }
  }

  public void delete_elem()
  {
    if (xy_list.count()>0)
    {
      xy_list.removeItemAt(xy_list.selectedIndex());
      xy_list.sizeToMinSize();
      xy_list.draw();
      saved = xy_list.count()==0;
    }
  }

  public void select_elem()
  {
    ListItem li = xy_list.selectedItem();
    if (li!=null)
      unparsedata((VitaliElem)li.data());
  }

  public void build_vitali()
  {
    VitaliData vd = encodedata();

    if (vd!=null)
      (new ResultWindow(vd.compute())).showModally();
  }

  public void decodedata(VitaliData vd)
  {
    vinput.setStringValue(Integer.toString(vd.vars));

    for(int i=0; i<vd.data.size(); i++)
    {
      VitaliElem ve = (VitaliElem)vd.data.elementAt(i);
      ListItem li = new ListItem();
      li.setTitle(ve.toString());
      li.setData(ve);
      xy_list.addItem(li);
    }

    String fn_name = vd.fn.name();
    ListItem li;

    for(int j=0; j<fn_popup.count(); j++)
      if ((li=fn_popup.itemAt(j)).title().equals(vd.fn.name()))
      {
        fn_popup.selectItem(li);
        break;
      }

    xy_list.sizeToMinSize();
    xy_list.draw();
  }

  public VitaliData encodedata()
  {
    int vars = getvarno();
    if (vars<=0)
    {
      Alert.runAlertInternally(Alert.warningImage(), "Datos inválidos",
        "El número de variables es incorrecto.", "Ok", null, null);
      return null;
    }

    VitaliFunction fntn = (VitaliFunction)VitaliFunction.functions.get(
      fn_popup.selectedItem().title());

    VitaliData vd = new VitaliData(vars, fntn);
    for (int i=0; i<xy_list.count(); i++)
      vd.add((VitaliElem)xy_list.itemAt(i).data());
      
    return vd;
  }

  public VitaliData loadfile() throws IOException
  {
    VitaliData vd;
    FileInputStream is = new FileInputStream(filename);
    ObjectInputStream ois = new ObjectInputStream(is);
    try
    { vd = (VitaliData)ois.readObject(); }
    catch (ClassNotFoundException cnfe)
    { throw new IOException(cnfe.getMessage()); }
    is.close();
    return vd;      
  }

  public void savefile()
  {
    try
    {
      VitaliData vd = encodedata();
      FileOutputStream os = new FileOutputStream(filename);
      ObjectOutputStream oos = new ObjectOutputStream(os);
      oos.writeObject(vd);
      oos.flush();
      os.close();
    }
    catch (IOException ioe)
    {
      Alert.runAlertInternally(Alert.warningImage(), "Error de Archivo",
        "Hubo un error al tratar de escribir el archivo "+filename,
        "Ok", null, null);
      return;
    }
    saved = true;
  }

  public void saveas()
  {
    FileChooser filesave = new FileChooser(
      Application.application().mainRootView(),
      "Guardar como...", FileChooser.SAVE_TYPE);

      if (filename!=null)
        filesave.setFile(filename);
      filesave.setFilenameFilter(Regresion.vitali_ext);
      filesave.showModally();

    if (filesave.file()!=null)
    {
      setTitle(filename = filesave.file());
      savefile();    
    }
  }

  public void performCommand(String cmd, Object data)
  {
    if (cmd==C_ADD)
      add_elem();
    else if (cmd==C_MODIFY)
      modify_elem();
    else if (cmd==C_DELETE)
      delete_elem();
    else if (cmd==C_LISTCHANGED)
      select_elem();
    else if (cmd==C_COMPUTE)
      build_vitali();
    else if (cmd==C_MISCCHANGED)
      saved = xy_list.count()==0;
    else if (cmd==C_SAVE && !saved)
      if (filename!=null)
        savefile();
      else
        saveas();
    else if (cmd==C_SAVEAS)
      saveas();
    else if (cmd==C_CLOSE)
        hide();
    else
      super.performCommand(cmd, data);
  }

  public boolean windowWillHide(Window w)
  {
    if (saved)
      return true;

    int r = Alert.runAlertInternally(Alert.warningImage(), "¿Está usted seguro?",
        "No ha guardado la información del archivo "+filename+". ¿Quiere "+
        "guardar los cambios?", "Sí", "No", "Cancelar");

    switch(r)
    {
      case Alert.DEFAULT_OPTION:
        performCommand(C_SAVE, null);
        return saved;

      case Alert.SECOND_OPTION:
        return true;
    }
    return false;
  }

  public boolean windowWillShow(Window w)
  { return true; }

  public void windowDidHide(Window w) {}
  public void windowDidBecomeMain(Window w) {}
  public void windowDidResignMain(Window w) {}
  public void windowDidShow(Window w) {}
  public void windowWillSizeBy(Window w, Size s) {}

}
