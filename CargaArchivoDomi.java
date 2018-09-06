package com.afbanamex.GestionCorrespondencia;

import com.jonima.utils.guiclient.*;
import javax.swing.JRadioButtonMenuItem;

/**
 * <p>Title: Llamar a forma de carga de archivos</p>
 * <p>Description: Esta clase llama a la forma de Carga de Archvios Domiciliación</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Afore banamex</p>
 * @author: DOC
 * @version 1.0
 */

public class CargaArchivoDomi
    implements IJonStartableFrame {
  public CargaArchivoDomi() {
  }

  public void start(int int0, int int1) {
	  frmCargaArchivoDomi dlg = new frmCargaArchivoDomi();
    dlg.startInternal("Carga de Archivos Domiciliación");
  }

  /**
   * closeJonWindow
   */
  public void closeJonWindow() {
  }

  /**
   * getMenuItem
   *
   * @return JRadioButtonMenuItem
   */
  public JRadioButtonMenuItem getMenuItem() {
    return null;
  }

  /**
   * getName
   *
   * @return String
   */
  public String getName() {
    return "";
  }
}
