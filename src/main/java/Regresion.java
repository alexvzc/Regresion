/* Regresion.java
 * by Alejandro Vázquez C.
 */

import java.io.FilenameFilter;
import java.util.Iterator;
import java.util.Set;
import netscape.application.Alert;
import netscape.application.Application;
import netscape.application.ExternalWindow;
import netscape.application.FileChooser;
import netscape.application.InternalWindow;
import netscape.application.Menu;
import netscape.application.MenuView;
import netscape.application.Size;
import netscape.application.Target;
import netscape.application.Window;
import netscape.application.WindowOwner;
import netscape.util.Vector;
import org.reflections.Reflections;
import org.reflections.scanners.Scanner;

public class Regresion extends Application
  implements WindowOwner, Target
{
  public final static String C_NEW = "~~Nuevo~~";
  public final static String C_OPEN = "~~Abrir~~";
  public final static String C_ABOUT = "~~Acerca~de~~";
  public final static String C_EXIT = "~~Salir~~";

  public final static FilenameFilter vitali_ext = new RegFilter("vitali");

  public Menu createMenu()
  {
    Menu menu = new Menu();
    Menu file = menu.addItemWithSubmenu("Archivo").submenu();
      file.addItem("Nuevo", C_NEW, this);
      file.addItem("Abrir...", C_OPEN, this);
      file.addSeparator();
      file.addItem("Acerca de...", C_ABOUT, this);
      file.addSeparator();
      file.addItem("Salir", C_EXIT, this);

    return menu;
  }

  public void init()
  {
    MenuView mv = new MenuView(createMenu());
    mv.sizeToMinSize();
    mainRootView().externalWindow().setMenuView(mv);
    performCommand(C_ABOUT, null);
  }

  public void performCommand(String cmd, Object data)
  {
    if (C_NEW.equals(cmd))
      new VitaliWindow();
    else if (C_OPEN.equals(cmd))
    {
      FileChooser fileopen = new FileChooser(mainRootView(), "Abrir...", FileChooser.LOAD_TYPE);
      fileopen.setFilenameFilter(vitali_ext);
      fileopen.showModally();
      if (fileopen.file() != null)
        new VitaliWindow(fileopen.file());
    }
    else if (C_ABOUT.equals(cmd))
      Alert.runAlertInternally(Alert.notificationImage(),
        "Acerca de...", "Regresión 1.0 - Prohibida su venta\n\n"+
        "663201 María Guadalupe Torres Salas\n"+
        "663387 Alejandro Vázquez Cantú\n"+
        "663633 Jorge Antonio Ruiz Zepeda\n",
        "Ok", null, null);
    else if (cmd==C_EXIT)
      mainRootView().externalWindow().hide();
  }

  public static void main(String args[])
  {
    Reflections r = new Reflections("", new Scanner[]{});
    Set s = r.getSubTypesOf(VitaliFunction.class);
    for(Iterator i = s.iterator(); i.hasNext();) {
      try {
        ((Class)i.next()).newInstance();
      } catch(InstantiationException ex) {
      } catch(IllegalAccessException ex) {
      }
    }

    Regresion regresion = new Regresion();
    ExternalWindow mainWindow = new ExternalWindow();
    Size size = mainWindow.windowSizeForContentSize(520, 440);
    mainWindow.sizeTo(size.width, size.height);

    regresion.setMainRootView(mainWindow.rootView());

    mainWindow.setTitle("Regresion");
    mainWindow.setOwner(regresion);
    mainWindow.show();

    regresion.run();

    System.exit(0);
  }

  public void windowDidHide(Window w)
  { stopRunning(); }

  public boolean windowWillHide(Window w)
  {
    Vector iws = (Vector)mainRootView().internalWindows().clone();
    for (int i=0; i<iws.size(); i++)
    {
      InternalWindow iw = (InternalWindow)iws.elementAt(i);
      iw.hide();
      if (iw.isVisible())
        return false;
    }
    return true;
  }

  public boolean windowWillShow(Window w)
  { return true; }

  public void windowDidBecomeMain(Window w) {}
  public void windowDidResignMain(Window w) {}
  public void windowDidShow(Window w) {}
  public void windowWillSizeBy(Window w, Size s) {}
}
