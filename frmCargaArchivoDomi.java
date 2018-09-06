package com.afbanamex.GestionCorrespondencia;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Formatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.Iterator;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;

import com.afbanamex.general.clientshell.DataSvcAccess;
import com.afbanamex.utilerias.BDConnect;
import com.afbanamex.utilerias.clUtilerias;
import com.afbanamex.utilerias.calendario.RCalendarField;
import com.borland.jbcl.layout.XYConstraints;
import com.borland.jbcl.layout.XYLayout;
import com.jonima.utils.guiclient.ContextoAplicacion;
import com.jonima.utils.guiclient.IJonStartableFrame;

/**
 * <p>Title: Carga de Archivos Domiciliación</p>
 * <p>Description: Modulo para cargar archivos de Domiciliación</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Afore Banamex</p>
 * @author AG73734, HA72228, dg29505, mp14493
 * @version 1.0
 */

public class frmCargaArchivoDomi
        extends JInternalFrame
        implements IJonStartableFrame, ActionListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** Variable indicador para evitar que los combos efectúen la función de cambio de estado cuando se están llenando por primera vez */
    String wListsReady;
    ContextoAplicacion capp = ContextoAplicacion.instancia();
    JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem();
    //Para facultades
    DataSvcAccess svcShell = new DataSvcAccess();
    JInternalFrame parentWindow;
    /** Variables para acceso a clases importadas */
    BDConnect bdconn = new BDConnect();
    clUtilerias clUtil = new clUtilerias();
    clsFuncGrales Func = new clsFuncGrales();
    clsFuncCorreo FuncCorr = new clsFuncCorreo();
    /** Variable general para título de ventana */
    String wtitle;
    JPanel contentPane;
    /** Variables para guardar datos originales del registro */
    String OrigId;
    String OrigDescripcion;
    /** Controles de Calendario */
    Calendar date = Calendar.getInstance();
    RCalendarField CbFecIni = new RCalendarField();
    int liTipo; //Enviado o Capturado

    Object[] Carga = {"---- Seleccione Tipo de Carga ----", 
    		"Carga inicial de Registros",
    		"Datos de Solicitudes a Tramitar (Datos Generales)",
    		"Datos de Solicitudes a Tramitar (Domicilios)",
    		"Archivo Sici",
    		"Archivo dba",
    		//"Generación de archivo contactación",
    		"Respuesta contactación",
    		"Archivo dbc",
    		"Archivo DETAPOVOL",
    		"Domicilios y Correo Electónico para Notificaciones de Cargo y Abono"};
    XYConstraints restricciones1 = new XYConstraints();

    JPanel jPanel1 = new JPanel();
    XYLayout xYLayout1 = new XYLayout();
    JPanel jPanel2 = new JPanel();
    Border border1;
    JLabel lblCarga = new JLabel();
    XYLayout xYLayout2 = new XYLayout();
    JComboBox cboCarga = new JComboBox(Carga);
    JPanel jPanel3 = new JPanel();
    Border border2;
    JLabel lblArchivo = new JLabel();
    XYLayout xYLayout3 = new XYLayout();
    JTextField txtArchivo = new JTextField();
    JButton btnBuscar = new JButton();
    JButton btnProcesar = new JButton();
    JButton btnSalir = new JButton();

    public frmCargaArchivoDomi() {
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    }

    void jbInit() throws Exception {
        border1 = new EtchedBorder(EtchedBorder.RAISED, Color.white,
                                   new Color(148, 145, 140));
        border2 = BorderFactory.createEtchedBorder(Color.white,
                new Color(148, 145, 140));
        jPanel1.setDebugGraphicsOptions(0);
        jPanel1.setLayout(xYLayout1);
        jPanel2.setBorder(border1);
        jPanel2.setLayout(xYLayout2);
        lblCarga.setText("Tipo de Carga");
        jPanel3.setBorder(border2);
        jPanel3.setLayout(xYLayout3);
        lblArchivo.setText("Archivo:");
        txtArchivo.setText("");
        btnBuscar.setText("Buscar");
        btnProcesar.setText("Procesar");
        btnSalir.setText("Salir");
        this.setTitle("Cargar Archivos");
        this.setSize(new Dimension(492, 280));

        this.getContentPane().add(jPanel1, BorderLayout.CENTER);
        jPanel1.add(jPanel2, new XYConstraints(3, 8, 479, 37));
        jPanel2.add(lblCarga, new XYConstraints(5, 2, -1, -1));
        jPanel2.add(cboCarga, new XYConstraints(77, 4, 386, 20));
        jPanel1.add(jPanel3, new XYConstraints(3, 52, 482, 122));
        jPanel3.add(lblArchivo, new XYConstraints(3, 9, -1, -1));
        jPanel3.add(txtArchivo, new XYConstraints(52, 11, 273, 20));
        jPanel3.add(btnBuscar, new XYConstraints(382, 7, 93, 27));
        jPanel3.add(btnSalir, new XYConstraints(238, 79, 93, 27));
        jPanel3.add(btnProcesar, new XYConstraints(30, 79, 93, 27));

        Posiciona(btnProcesar, 70, 80, 93, 27, xYLayout3);
        Posiciona(btnSalir, 70, 238, 93, 27, xYLayout3);
        Posiciona(jPanel3, 52, 3, 482, 140, xYLayout1);
        //Eventos
        btnBuscar.addActionListener(new frmCargaArchivoDomi_btnBuscar_actionAdapter(this));
        btnProcesar.addActionListener(new frmCargaArchivoDomi_btnProcesar_actionAdapter(this));
        btnSalir.addActionListener(new frmCargaArchivoDomi_btnSalir_actionAdapter(this));
        cboCarga.addItemListener(new frmCargaArchivoDomi_cboCarga_itemAdapter(this));
    }

    public void start(int int0, int int1) {
    }

    public String getName() {
        return "Carga de Archivos";
    }

    public void startInternal(String title) {

        liTipo = cboCarga.getSelectedIndex();
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        try {
            JDesktopPane desk = (JDesktopPane) capp.getValue("desktop");

            wtitle = title;
            wListsReady = "N";
            jbInit();
            wListsReady = "Y";
            Dimension frmSize = this.getSize();

            /** Centrado de pantalla */
            this.setLocation( (desk.getWidth() - frmSize.width) / 2,
                             (desk.getHeight() - frmSize.height) / 2);
            this.setTitle(wtitle);
            desk.add(this);
            this.pack();
            this.show();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeJonWindow() {
        JDesktopPane desk = (JDesktopPane) capp.getValue("desktop");
        this.dispose();
        //Quitar la pantalla del padre
        desk.remove(this);
        JMenu menuVen = (JMenu) capp.getValue("windowmenu");
        menuVen.remove(menuItem);
    }

    /**
     * getMenuItem
     *
     * @return JRadioButtonMenuItem
     */
    public JRadioButtonMenuItem getMenuItem() {
        menuItem.setText("Carga de Archivos");
        return menuItem;
    }

    /**
     * actionPerformed
     *
     * @param e ActionEvent
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSalir) {
            cancel();
        }
    }

    void btnBuscar_actionPerformed(ActionEvent e) {
        JFileChooser fc = new JFileChooser();
		//se asignan filtros de usuario para evitar que puedan seleccionar archivos sin la extencion correspondiente
		
		FileFilter filterTxt = new FileNameExtensionFilter("Archivo .txt", "txt");
		FileFilter filterXls = new FileNameExtensionFilter("Archivo .xls", "xls");
		FileFilter filterDom = new FileNameExtensionFilter("Archivo .dom", "dom");
		FileFilter filter001 = new FileNameExtensionFilter("Archivo .001", "001");
		fc.setAcceptAllFileFilterUsed(false);
		
		// Limita la seleccion de el fileChoser
		if ((cboCarga.getSelectedIndex() == 1)||(cboCarga.getSelectedIndex() == 5)) {
			fc.setFileFilter(filterTxt);
		} else if((cboCarga.getSelectedIndex() == 7)){
			fc.setFileFilter(filterDom);
		} else if ((cboCarga.getSelectedIndex() == 8)) {
			fc.setFileFilter(filter001);
		} else { 
			fc.setFileFilter(filterXls);
		}
        //Validamos que no haya piezas pendientes a ajustar
        if (FuncCorr.fsValidaExistaAjuste(liTipo) == false) {
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int returnVal = fc.showOpenDialog(this);

            if (returnVal == JFileChooser.CANCEL_OPTION) {
                return;
            }

            File file = fc.getSelectedFile();

            if (file == null || file.getName().equals("")) {
                Func.fiMensajeError("Nombre de archivo incorrecto", "Error de Archivo");
            } else {
                txtArchivo.setText(file.getAbsoluteFile().toString());
            }
        }
    }

    void btnSalir_actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSalir) {
            cancel();
        }
    }

    //Overridden so we can exit when window is closed
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            cancel();
        }
        //super.processWindowEvent(e);
    }

    //Close the dialog
    void cancel() {
        dispose();
    }
	
 // Inicia Metodo CargaIncial
 	private String cargaInicial(String fileName) {
 		
 		String lineaArchivo = "";
 		StringBuilder resultado = new StringBuilder();
 		FileReader fr = null;
 		int registros = 0;
 		int errores = 0;
 		int total = 0;
 		try {
 			
 			String nombreArchivo = clUtil.TraeRegistro("NOMBREARCH", "TBITARCHIVOS"," WHERE NOMBREARCH = '"+clUtil.obtieneNombreArchivo(fileName)+"'");
 			//Verifica que el archivo seleccionado no se encuentre en la bitacora
 			if (clUtil.obtieneNombreArchivo(fileName).equals(String.valueOf(nombreArchivo))) {
 				return "El Archivo ya se ha cargado anteriormente, seleccione otro archivo";
 			}
 			
 			fr = new FileReader(fileName);
 			
 			BufferedReader entArch = new BufferedReader(fr);
 			while ((lineaArchivo = entArch.readLine()) != null) {
 				// Verifica el primer registro del txt sea 3 para entrar al if
 				String tipoRegistro = lineaArchivo.substring(0, 2).trim();
 				// Verifica que la clave del registro sea 03
 				if (tipoRegistro.equals("03")) {
 					tipoRegistro = lineaArchivo.substring(0, 2);
 					String numeronss = lineaArchivo.substring(2, 13).trim();
 					String afore = lineaArchivo.substring(13, 16);
 					String fechaTransaccion = lineaArchivo.substring(16, 24);
 					String curpAhorrador = lineaArchivo.substring(24, 42);
 					String nombreAhorrador = lineaArchivo.substring(42, 82);
 					String apePatAhorrador = LimpiaCampo(lineaArchivo.substring(82, 122));
 					String apeMatAhorrador = LimpiaCampo(lineaArchivo.substring(122, 162));
 					String montoAportacion = LimpiaCampo(lineaArchivo.substring(162, 177));
 					String folioSolicitud = LimpiaCampo(lineaArchivo.substring(179, 194));
 					String origenAportacion = LimpiaCampo(lineaArchivo.substring(194, 196));
 					String tipoCuenta = lineaArchivo.substring(196, 197);
 					String tipoEmisora = lineaArchivo.substring(197, 199);
 					String bancoEmisor = lineaArchivo.substring(199, 202);
 					String cuentaClabe = lineaArchivo.substring(202, 220).trim();
 					String numeroTarjeta = lineaArchivo.substring(220, 236).trim();
 					String tipoBanco = lineaArchivo.substring(236, 238);
 					String periodicidad = lineaArchivo.substring(238, 239);

 					int consecutivo = ObtenConsecutivo();
 					//obtiene valor de alta de el catalogo tipo transaccion
 		            String transaccion = clUtil.TraeRegistro("CLAVE","TCATTIPOTRANSACCION"," WHERE DESCRIPCION='Alta'");
 			
 		            
 					// inserta en tabla tsoldomiciliacion
 					String sql = "INSERT INTO TSOLDOMICILIACION VALUES "
 							+ "('"
 							+ consecutivo
 							+ "',"
 							+ "'"
 							+ numeronss
 							+ "',"
 							+ "'"
 							+ curpAhorrador
 							+ "',"
 							+ "'"
 							+ nombreAhorrador
 							+ "',"
 							+ "'"
 							+ apePatAhorrador
 							+ "',"
 							+ "'"
 							+ apeMatAhorrador
 							+ "',"
 							+ "'"
 							+ transaccion
 							+ "',"
 							+ "'"
 							+ getTipoCuenta(tipoCuenta)
 							+ "',"
 							+ "'"
 							+ getBanco(bancoEmisor)
 							+ "',"
 							+ "'"
 							+ (cuentaClabe.isEmpty()?numeroTarjeta:cuentaClabe)
 							+ "',"
 							+ "'0'"
 							+ ",to_number('"+Float.parseFloat(montoAportacion)+"','9999999999999.99'),"
 							+ "'"+getPeriodicidad(periodicidad)+"',"
 							+ "'ACEPTADO',"
 							+ "'0',"
 							+ "'5',"
 							+ "'"+folioSolicitud+"',"
 							+ "to_date('"+fechaTransaccion+"','YYYYMMDD'),"
 							+ "to_date(sysdate,'DD/MM/YYYY'),"
 							+ "'SI',"
 							+ "'ACTIVA',"
 							+ "'',"
 							+ "'')";					
 				
 					bdconn.Ejecuta(sql);
 					
 					registros++;

 				} else if(tipoRegistro.equals("01")||tipoRegistro.equals("09")) {
 					total++;
 				}else{
 					errores++;
 				}

 			}
 			
 			total = total +registros + errores;
 			resultado.append("\nResumen:" + "\nTotal Registros:" + total
 					+ "\nRegistros correctos:" + registros
 					+ "\nRegistros con errores:" + errores + "\n");
 			
 			//Inserta En La Bitacora
 			clUtil.insertarRegistroBitacoraArchivos(total, registros,0,errores,resultado,"1",fileName,"");
 		
 		} catch (FileNotFoundException e) {
 			e.printStackTrace();
 			resultado.append("\nERROR: Archivo no encontrado");
 		} catch (IOException e) {
 			e.printStackTrace();
 			resultado.append("\nERROR: No se puede leer del archivo");
 		} catch (SQLException e) {
 			e.printStackTrace();
 			resultado.append("\nERROR: Excepción sql");
 		} catch (Exception e) {
 			e.printStackTrace();
 			resultado.append("\nERROR: Excepción General");
 		}

 		return resultado.toString();
 	}
 	// Fin Metodo CargaInicial
	
 // Inicia Metodo CargaRespuestaContactación
 	private String cargaRespuestaContactacion(String fileName) {
 		StringBuilder resultado = new StringBuilder();
 		Integer contadorLineas = 2;// Saltamos encabezados
 		int idArchivo;
 		try {
 			
 			String nombreArchivo = clUtil.TraeRegistro("NOMBREARCH", "TBITARCHIVOS"," WHERE NOMBREARCH = '"+clUtil.obtieneNombreArchivo(fileName)+"'");
 			idArchivo = clUtil.obtenerConsecutivoSTBITARCHIVOS();
 			//Verifica que el archivo seleccionado no se encuentre en la bitacora
 			if (clUtil.obtieneNombreArchivo(fileName).equals(String.valueOf(nombreArchivo))) {
 				return "El Archivo ya se ha cargado anteriormente, seleccione otro archivo";
 			}
 			
 			InputStream myxls = new FileInputStream(fileName);
 			HSSFWorkbook wb = new HSSFWorkbook(myxls);
 			HSSFSheet sheet = wb.getSheetAt(0);
 			DataFormatter formatter = new DataFormatter();

 			Integer registros = 0;
 			Integer errores = 0;
 			HSSFDataFormat df = wb.createDataFormat();

 			while (true) {
 				HSSFRow row = sheet.getRow(contadorLineas);

 				if (row == null
 						|| formatter.formatCellValue(row.getCell(0)).equals("")) {
 					Integer total = registros + errores;
 					resultado.append("\nResumen:" + "\nTotal Registros:"
 							+ total.toString() + "\nRegistros correctos:"
 							+ registros.toString() + "\nRegistros con errores:"
 							+ errores.toString() + "\n");
 					break;
 				}

 				Integer consecutivo = ObtenConsecutivo();
 				String fechaRegPreSolicitud = formatter.formatCellValue(row
 						.getCell(1));
 				String folioPreSolicitud = formatter.formatCellValue(row
 						.getCell(2));
 				String nssImss = formatter.formatCellValue(row.getCell(3));
 				String curp = formatter.formatCellValue(row.getCell(4));
 				String nombre = formatter.formatCellValue(row.getCell(5));
 				String apePaterno = formatter.formatCellValue(row.getCell(6));
 				String apeMaterno = formatter.formatCellValue(row.getCell(7));
 				String correoElectronico = formatter.formatCellValue(row
 						.getCell(8));
 				String numCelular = LimpiaCampo(formatter.formatCellValue(row
 						.getCell(9)));
 				String numTelFijo = LimpiaCampo(formatter.formatCellValue(row
 						.getCell(10)));

 				String horarioContacto = formatter.formatCellValue(row
 						.getCell(11));
 				String tipoGuion = LimpiaCampo(formatter.formatCellValue(row
 						.getCell(12)));
 				String motivoRechazoAlta = LimpiaCampo(formatter
 						.formatCellValue(row.getCell(13)));
 				String montonDeAportacion = formatter.formatCellValue(row
 						.getCell(14));
 				String peridiocidadDeAportacion = LimpiaCampo(formatter
 						.formatCellValue(row.getCell(15)));
 				String bancoEmisor = formatter.formatCellValue(row.getCell(16));
 				String clabeInterbancaria = LimpiaCampo(formatter
 						.formatCellValue(row.getCell(17)));
 				String numTarjetaDebito = LimpiaCampo(formatter
 						.formatCellValue(row.getCell(18)));
 				String titularCuenta = formatter.formatCellValue(row
 						.getCell(19));
 				String resMarcacion = formatter
 						.formatCellValue(row.getCell(20));

 				String estatusContactacion = formatter.formatCellValue(row
 						.getCell(21));
 				String fechaContactoFinal = formatter.formatCellValue(row
 						.getCell(22));
 				String horaContactoFinal = removerAmPmHora(formatter
 						.formatCellValue(row.getCell(23)));
 				String nombreOperador = formatter.formatCellValue(row
 						.getCell(24));
 				String intentosContacto = LimpiaCampo(formatter
 						.formatCellValue(row.getCell(25)));
 				String tipoContacto = formatter
 						.formatCellValue(row.getCell(26));
 				String descDeContactacion = formatter.formatCellValue(row
 						.getCell(27));

 				String fechaContactacion1 = formatter.formatCellValue(row
 						.getCell(28));
 				String horaContactacion1 = removerAmPmHora(formatter
 						.formatCellValue(row.getCell(29)));
 				String operadorCatContactacion1 = formatter.formatCellValue(row
 						.getCell(30));
 				String estatusLlamada1 = formatter.formatCellValue(row
 						.getCell(31));
 				String idLlamada1 = LimpiaCampo(formatter.formatCellValue(row
 						.getCell(32)));

 				String fechaContactacion2 = formatter.formatCellValue(row
 						.getCell(33));
 				String horaContactacion2 = removerAmPmHora(formatter
 						.formatCellValue(row.getCell(34)));
 				String operadorCatContactacion2 = formatter.formatCellValue(row
 						.getCell(35));
 				String estatusLlamada2 = formatter.formatCellValue(row
 						.getCell(36));
 				String idLlamada2 = LimpiaCampo(formatter.formatCellValue(row
 						.getCell(37)));

 				String fechaContactacion3 = formatter.formatCellValue(row
 						.getCell(38));
 				String horaContactacion3 = removerAmPmHora(formatter
 						.formatCellValue(row.getCell(39)));
 				String operadorCatContactacion3 = formatter.formatCellValue(row
 						.getCell(40));
 				String estatusLlamada3 = formatter.formatCellValue(row
 						.getCell(41));
 				String idLlamada3 = LimpiaCampo(formatter.formatCellValue(row
 						.getCell(42)));

 				String fechaContactacion4 = formatter.formatCellValue(row
 						.getCell(43));
 				String horaContactacion4 = removerAmPmHora(formatter
 						.formatCellValue(row.getCell(44)));
 				String operadorCatContactacion4 = formatter.formatCellValue(row
 						.getCell(45));
 				String estatusLlamada4 = formatter.formatCellValue(row
 						.getCell(46));
 				String idLlamada4 = LimpiaCampo(formatter.formatCellValue(row
 						.getCell(47)));

 				String numeroMarcado1 = LimpiaCampo(formatter
 						.formatCellValue(row.getCell(48)));
 				String numeroMarcado2 = LimpiaCampo(formatter
 						.formatCellValue(row.getCell(49)));
 				String numeroMarcado3 = LimpiaCampo(formatter
 						.formatCellValue(row.getCell(50)));
 				String numeroMarcado4 = LimpiaCampo(formatter
 						.formatCellValue(row.getCell(51)));

 				// obtiene datos de la tabla TSDOMICONTACTO
 				String idsoldomicialiacion = clUtil.TraeRegistro(
 						"IDSOLDOMICILIACION", "TSDOMICONTACTO",
 						" WHERE FECHAREGPRESOL =TO_DATE('"
 								+ fechaRegPreSolicitud
 								+ "','YYYYMMDD') AND FOLIOPRESOL='"
 								+ folioPreSolicitud + "' AND NSS='" + nssImss
 								+ "' AND CURP ='" + curp + "'");

 				if ((idsoldomicialiacion != null)) {
 					
 					String sql = "UPDATE TSDOMICONTACTO SET " + "TIPOGUION = '"
 							+ tipoGuion
 							+ "',"
 							+ "MOTRECHAZOALTA = '"
 							+ motivoRechazoAlta
 							+ "',"
 							+ "RESMARCACION = '"
 							+ resMarcacion
 							+ "',"
 							+ "ESTATUSCONTACTO = '"
 							+ estatusContactacion
 							+ "',"
 							+ "FECCONTACFINAL = to_date('"
 							+ fechaContactoFinal
 							+ "','DD/MM/YYYY'),"
 							+ "HORACONTACFINAL = '"
 							+ horaContactoFinal
 							+ "',"
 							+ "NOMBREOPERADOR = '"
 							+ nombreOperador
 							+ "',"
 							+ "INTENTOSCONTAC = '"
 							+ intentosContacto
 							+ "',"
 							+ "TIPOCONTACTO = '"
 							+ tipoContacto
 							+ "',"
 							+ "DESCCONTACTO = '"
 							+ descDeContactacion
 							+ "',"
 							+ "FECCONTACTO1 = to_date('"
 							+ fechaContactacion1
 							+ "','DD/MM/YYYY'),"
 							+ "HORACONTACTO1 ='"
 							+ horaContactacion1
 							+ "',"
 							+ "OPERADORCAT1 = '"
 							+ operadorCatContactacion1
 							+ "',"
 							+ "ESTATUSLLAMADA1 = '"
 							+ estatusLlamada1
 							+ "',"
 							+ "IDLLAMADA1 = '"
 							+ idLlamada1
 							+ "',"
 							+ "FECCONTACTO2 = to_date('"
 							+ fechaContactacion2
 							+ "','DD/MM/YYYY'),"
 							+ "HORACONTACTO2 ='"
 							+ horaContactacion2
 							+ "',"
 							+ "OPERADORCAT2 = '"
 							+ operadorCatContactacion2
 							+ "',"
 							+ "ESTATUSLLAMADA2 = '"
 							+ estatusLlamada2
 							+ "',"
 							+ "IDLLAMADA2 = '"
 							+ idLlamada2
 							+ "',"
 							+ "FECCONTACTO3 = to_date('"
 							+ fechaContactacion3
 							+ "','DD/MM/YYYY'),"
 							+ "HORACONTACTO3 ='"
 							+ horaContactacion3
 							+ "',"
 							+ "OPERADORCAT3 = '"
 							+ operadorCatContactacion3
 							+ "',"
 							+ "ESTATUSLLAMADA3 = '"
 							+ estatusLlamada3
 							+ "',"
 							+ "IDLLAMADA3 = '"
 							+ idLlamada4
 							+ "',"
 							+ "FECCONTATCTO4 = to_date('"
 							+ fechaContactacion4
 							+ "','DD/MM/YYYY'),"
 							+ "HORACONTACTO4 ='"
 							+ horaContactacion4
 							+ "',"
 							+ "OPERADORCAT4 = '"
 							+ operadorCatContactacion4
 							+ "',"
 							+ "ESTATUSLLAMA = '"
 							+ estatusLlamada4
 							+ "',"
 							+ "IDLLAMADA4 = '"
 							+ idLlamada4
 							+ "',"
 							+ "NUMEROMARCADO1 = '"
 							+ numeroMarcado1
 							+ "',"
 							+ "NUMEROMARCADO2 = '"
 							+ numeroMarcado2
 							+ "',"
 							+ "NUMEROMARCADO3 = '"
 							+ numeroMarcado3
 							+ "',"
 							+ "NUMEROMARCADO4 = '"
 							+ numeroMarcado4
 							+ "',"
 							+ "FECCARGARESPUESTA = to_date(sysdate,'DD/MM/YYYY'),"
 							+ "IDARCHIVO =" + idArchivo
 							+ " WHERE IDSOLDOMICILIACION = '"
 							+ idsoldomicialiacion + "'";

 					
 					
 					String motivo = "";
 					String diagnostico ="";
 					
 					if(tipoGuion.equals("2")){
 						if(tipoContacto.toUpperCase().equals("CLIENTE CONTACTADO")){
 							motivo=clUtil.TraeRegistro("CLAVE", "TCATMOTRECHAZO"," WHERE CVEORIGEN ='"+motivoRechazoAlta+ "' AND CATTIPOARCHIVO =7");
 							diagnostico = "ACEPTADO";
 						}else{
 							diagnostico = "RECHAZADO";
 							motivo = "Lo intentamos contactar sin éxito para confirmar su trámite";
 						}
 						
 					}else{
 						
 						diagnostico=clUtil.TraeRegistro("DIAGNOSTICO", "TCATCONTACTACION"," WHERE UPPER(RESMARCACION) LIKE '"
 								+ resMarcacion.toUpperCase().trim() +"' AND upper(ESTATUSCONTACTO) LIKE '"+ estatusContactacion.toUpperCase().trim() +"' AND UPPER(TIPOCONTACTO) LIKE '"+tipoContacto.toUpperCase().trim()+
 	 							"' AND UPPER(DESCARCHCONTACT) LIKE '"+ descDeContactacion.toUpperCase().trim() +"'");
 						
 						motivo=clUtil.TraeRegistro("CVEMOTRECHAZO", "TCATCONTACTACION"," WHERE UPPER(RESMARCACION) LIKE '"
 	 							+ resMarcacion.toUpperCase().trim() +"' AND upper(ESTATUSCONTACTO) LIKE '"+ estatusContactacion.toUpperCase().trim() +"' AND UPPER(TIPOCONTACTO) LIKE '"+tipoContacto.toUpperCase().trim()+
 	 							"' AND UPPER(DESCARCHCONTACT) LIKE '"+ descDeContactacion.toUpperCase().trim() +"'");
 						if(diagnostico ==null){
 							errores++;
 							resultado.append("No existe registro en Catálogo Contactación para el folio: "+folioPreSolicitud);
 						}
 						else{
 						if(motivo==null)
 							motivo = " ";
 							
 	 					System.out.println("MOTIVO: "+motivo);
 	 					System.out.println("diagnostico: "+ diagnostico);
 	 					
 	 					
 	 					String sqlUpdate = "UPDATE TSOLDOMICILIACION SET DIAGNOSTICO = '"+diagnostico.toUpperCase()+"', MOTIVO = '"+motivo+"' WHERE IDSOLDOMICILIACION ='"
 								+ idsoldomicialiacion + "'";
 	 					System.out.println(sqlUpdate);
 	 					bdconn.Ejecuta(sql);
 	 					bdconn.Ejecuta(sqlUpdate);}
 					}
 					

 					
 					
 					registros++;

 				} else {
 					errores++;
 					resultado.append("El registro con folio: "+folioPreSolicitud+ "No se encuentra en la base de datos, o sus datos difieren.");
 				}
 				contadorLineas++;
 			}
 			//Inserta En La Bitacora
 			clUtil.insertarRegistroBitacoraArchivos(idArchivo,contadorLineas, registros,0,errores,resultado,"7",fileName,"");
 			
 		} catch (Exception e) {
 			e.printStackTrace();
 			resultado.append("\nError en la línea " + contadorLineas.toString()
 					+ ":\n" + e.getMessage());
 		}
 		return resultado.toString();
 	}
 	// Fin Metodo CargaRespuestaContactación
    
 // Inicia Metodo ProcesaDBA
 	private String ProcesaDBA(String fileName) {
 		String lineaArchivo = "";
 		StringBuilder resultado = new StringBuilder();
 		FileReader fr = null;
 		FileReader fr2 = null;
 		int registros = 0;
 		int errores = 0;
 		int total = 0;
 		int idArchivo =0;
 		
 		

 		try {
 			String alterFormat = "ALTER SESSION SET NLS_DATE_FORMAT = 'DD/MM/YYYY'";
 			bdconn.Ejecuta(alterFormat);
 			String nombreArchivo = clUtil.TraeRegistro("NOMBREARCH", "TBITARCHIVOS"," WHERE NOMBREARCH = '"+clUtil.obtieneNombreArchivo(fileName)+"'");
 			idArchivo = clUtil.obtenerConsecutivoSTBITARCHIVOS();
 			//Verifica que el archivo seleccionado no se encuentre en la bitacora
 			if (clUtil.obtieneNombreArchivo(fileName).equals(String.valueOf(nombreArchivo))) {
 				return "El Archivo ya se ha cargado anteriormente, seleccione otro archivo";
 			}
 			
 			fr = new FileReader(fileName);
 			BufferedReader entArch = new BufferedReader(fr);
 			
 			while((lineaArchivo = entArch.readLine())!=null){
 				String claveDeRegistro = lineaArchivo.substring(0, 1);
 				if (claveDeRegistro.equals("1")) {
 					
 					String contratoCuenta = Func.rellenarNSS(LimpiaCampo(lineaArchivo.substring(3, 43)));
 					boolean domicilios = clUtil.validarExistenciaDomicilios(contratoCuenta.trim(), "");
 					boolean nss= clUtil.validarNSSSExisteDatosGenerales(contratoCuenta.trim(), "");
 					System.out.println("domicilio- "+domicilios+" nss- " + contratoCuenta + "Encontrado: " +nss);
 					if (domicilios==false||nss==false) {
						resultado.append(contratoCuenta+",");
 						errores		++;
					}
				}
 			}
 			
 			if (errores>0) {
 				clUtil.insertarRegistroBitacoraArchivos(idArchivo,total, registros,0,errores,resultado,"5",fileName,"");
				return "Algunos NSS no se encontraron y/o algunos domicilios estan vacios";
			}
 			entArch.close();
 			fr.close();
 			fr2 = new FileReader(fileName);
 			BufferedReader entArch2 = new BufferedReader(fr2);
 			
 			while ((lineaArchivo = entArch2.readLine()) != null) {
 				// Verifica el primer registro del txt sea 1 para entrar al if
 				String claveDeRegistro = lineaArchivo.substring(0, 1);
 				// Verifica que la clave del registro sea 1
 				if (claveDeRegistro.equals("1")) {
 					String statusOperacion = lineaArchivo.substring(1, 3);
 					String contratoCuenta = lineaArchivo.substring(
 							3, 43).trim();
 					String cuentaCargo = lineaArchivo.substring(43, 63).trim();
 					String prefijoCuenta = lineaArchivo.substring(63, 67);
 					String numeroServicio = lineaArchivo.substring(67, 73);
 					String importe = lineaArchivo.substring(73, 83);
 					String diaCortePago = lineaArchivo.substring(83, 85);
 					String periodo = lineaArchivo.substring(85, 87);
 					String tipoPago = lineaArchivo.substring(87, 88);
 					String leyendaPersonalizacion = lineaArchivo.substring(88,
 							108);
 					String referencia1 = lineaArchivo.substring(108, 128);
 					String referencia2 = lineaArchivo.substring(128, 148);
 					String referencia3 = lineaArchivo.substring(148, 168);
 					String idDePago = lineaArchivo.substring(168, 169);
 					String tipoCuenta = lineaArchivo.substring(169, 171);
 					String diagnostico = lineaArchivo.substring(172, 174);
 					String claveMotRechazo = lineaArchivo.substring(174, 180);

 					int consecutivo = ObtenConsecutivo();
 					String nombreBanco = clUtil
 							.TraeRegistro(
 									"DESCRIPCION",
 									"TCATBANCOS",
 									" WHERE CLAVE='"
 											+ getBancoID(cuentaCargo,
 													tipoCuenta) + "'");
 					String clavePeriodicidad = clUtil.TraeRegistro("CLAVE",
 							"TCATPERIODICIDAD", " WHERE CVEDBA = '"
 									+ referencia1.trim() + "'");
 					String claveMotivoDB = clUtil.TraeRegistro(
 							"CLAVE",
 							"TCATMOTRECHAZO",
 							" WHERE CVEORIGEN = '"
 									+ Integer.parseInt(claveMotRechazo)
 									+ "' AND CATTIPOARCHIVO = 5");
 					
 					// inserta en tabla tsoldomiciliacion
 					String sql = "INSERT INTO TSOLDOMICILIACION(IDSOLDOMICILIACION,NSS,TRANSACCION,TIPOCUENTA,BANCO,CUENTA,SUCURSAL,MONTO,PERIODICIDAD,DIAGNOSTICO,MOTIVO,ORIGEN,FECCARGA,VOBO,ESTATUSDOMI) VALUES"
 							+ "('"
 							+ consecutivo
 							+ "',"
 							+ "'"
 							+ contratoCuenta.trim()
 							+ "',"
 							+ "'"
 							+ getStatusOperacion(statusOperacion)
 							+ "',"
 							+ "'"
 							+ tipoCuenta
 							+ "',"
 							+ "'"
 							+ nombreBanco
 							+ "',"
 							+ "'"
 							+ cuentaCargo.trim()
 							+ "',"
 							+ "'"
 							+ prefijoCuenta
 							+ "',"
 							+ "'"
 							+ importe
 							+ "',"
 							+ "'"
 							+ clavePeriodicidad
 							+ "',"
 							+ "'"
 							+ getDiagnostico(diagnostico)
 							+ "',"
 							+ "'"
 							+ claveMotivoDB
 							+ "'"
 							+ ",'2',"
 							+ "sysdate,"
 							+ "'NO',"
 							+ "'INACTIVO')";
 					bdconn.Ejecuta(sql);
 					

 					// inserta en tabla tsdomidetbancanet
 					String sql2 = "INSERT INTO TSDOMIDETBANCANET(IDSOLDOMICILIACION,CLAVEREGISTRO,NUMSERVICIO,DIACORTE,PERIODO,TIPOPAGO,LEYENDAPERSONAL,REFERENCIA2,REFERENCIA3,IDPAGO,IDARCHIVO) VALUES"
 							+ "('"
 							+ consecutivo
 							+ "',"
 							+ "'"
 							+ claveDeRegistro.trim()
 							+ "',"
 							+ "'"
 							+ numeroServicio.trim()
 							+ "',"
 							+ "'"
 							+ diaCortePago.trim()
 							+ "',"
 							+ "'"
 							+ periodo.trim()
 							+ "',"
 							+ "'"
 							+ tipoPago
 							+ "',"
 							+ "'"
 							+ leyendaPersonalizacion
 							+ "',"
 							+ "'"
 							+ referencia2
 							+ "',"
 							+ "'"
 							+ referencia3
 							+ "',"
 							+ "'"
 							+ idDePago
 							+ "'," 
 							+ idArchivo
 							+")";
 					bdconn.Ejecuta(sql2);
 					registros++;

 				} else if(claveDeRegistro.equals("0")||claveDeRegistro.equals("9")) {
 					total++;
 				}else{
 					errores++;
 				}

 			}

 			total = total + registros + errores;
 			resultado.append("\nResumen:" + "\nTotal Registros:" + total
 					+ "\nRegistros correctos:" + registros
 					+ "\nRegistros con errores:" + errores + "\n");
 			
 			//Inserta En La Bitacora
 			clUtil.insertarRegistroBitacoraArchivos(idArchivo,total, registros,0,errores,resultado,"5",fileName,"");
 			entArch2.close();
 			fr2.close();
 		} catch (FileNotFoundException e) {
 			e.printStackTrace();
 			resultado.append("\nERROR: Archivo no encontrado");
 		} catch (IOException e) {
 			e.printStackTrace();
 			resultado.append("\nERROR: No se puede leer del archivo");
 		} catch (SQLException e) {
 			e.printStackTrace();
 			resultado.append("\nERROR: Excepción sql");
 		} catch (Exception e) {
 			e.printStackTrace();
 			resultado.append("\nERROR: Excepción General");
 		}

 		return resultado.toString();
 	}
 	// Fin Metodo CargaDBA
 	
 	
 	//Método que carga el archivo de cargos DBC
	private String ProcesaDBC(String fileName){
		String lineaArchivo = "";
    	StringBuilder resumenCarga = new StringBuilder();
    	FileReader fr = null;
    	int idArchivo = 0;
        try {    	
			fr = new FileReader(fileName);
	        BufferedReader entArch = new BufferedReader(fr);
	       
	        String nomArc = ""; 
	        nomArc = clUtil.obtieneNombreArchivo(fileName);
	        System.out.print("nomArc:"+nomArc+ "\n" );
	        
	        String nombreArchivo = clUtil.TraeRegistro("NOMBREARCH", "TBITARCHIVOS"," WHERE NOMBREARCH = '"+clUtil.obtieneNombreArchivo(fileName)+"'");
            if (clUtil.obtieneNombreArchivo(fileName).equals(String.valueOf(nombreArchivo))) {
                    return "El Archivo ya se ha cargado anteriormente, seleccione otro archivo";
            }
            idArchivo = clUtil.insertarRegistroBitacoraArchivos(clUtil.ID_ARCHIVO_TCATARCHIVOS_DBC, fileName);
            
            Integer totalBloques = 1;
        	Integer registros = 0;
    		Integer errores = 0;
    		Integer numeroLinea = 0;
	        while(true){
		        lineaArchivo = entArch.readLine();
		        numeroLinea ++;
		        if(lineaArchivo==null){
		        	break;
		        }
	            //Validar el encabezado
		        String tipoRegistro = lineaArchivo.substring(0,2);
		        String numeroSecuencia = lineaArchivo.substring(2,9);
		        String codigoOperacion = lineaArchivo.substring(9,11);
		        String bancoParticipante = lineaArchivo.substring(11,14);
		        String sentido = lineaArchivo.substring(14,15);
		        String servicio = lineaArchivo.substring(15,16);
		        String numeroBloque = lineaArchivo.substring(16,23);
		        String fechaPresentacion = lineaArchivo.substring(23,31);
		        String codigoDivisas = lineaArchivo.substring(31,33);
		        String causaRechazoArchivo = lineaArchivo.substring(33,35);
		        String razonSocialEmisor = lineaArchivo.substring(60,100);
		        String RFCEmisor = lineaArchivo.substring(100,118);
		        String numeroCliente = lineaArchivo.substring(300,312);
		        String secuencialArchivo = lineaArchivo.substring(312,314);
	
		        boolean procesarBloque = true;
	            if(!tipoRegistro.equals("01") ){
            		resumenCarga.append("\nLa primera línea del archivo contiene un código de registro inválido. Línea: "
									+ numeroLinea.toString());
            		procesarBloque = false;
	            }

	            if(!numeroSecuencia.equals("0000001")){
            		resumenCarga.append("\nLa primera línea del archivo no tiene consecutivo 1. Línea: "
									+ numeroLinea.toString());
					procesarBloque = false;
	            }
	            if(!codigoOperacion.equals("30")){
            		resumenCarga.append("\nEl código de operación es inválido. Línea: "
									+ numeroLinea.toString());
            		procesarBloque = false;
	            }
	            
	            if(!bancoParticipante.equals("002")){
            		resumenCarga.append("\nEl código de banco participante no existe. Línea: "
									+ numeroLinea.toString());
            		procesarBloque = false;
	            }
	            if(!sentido.equals("S")){
            		resumenCarga.append("\nEl código de sentido es incorrecto. Línea: "
									+ numeroLinea.toString());
            		procesarBloque = false;
	            }
	            if(!servicio.equals("2")){
            		resumenCarga.append("\nEl código de servido es incorrecto. Línea: "
									+ numeroLinea.toString());
            		procesarBloque = false;
	            }
	            
	            // TODO: Verificar en dónde se almacena el código de bloque
				// (numeroBloque)
				// TODO: Verificar cuál es la fecha de proceso
				// (fechaPresentacion)
	            
	            if(!codigoDivisas.equals("01") && !codigoDivisas.equals("05")){
            		resumenCarga.append("\nEl código de divisa es incorrecto. Línea: "
									+ numeroLinea.toString());
            		procesarBloque = false;
	            }
	            if(!causaRechazoArchivo.equals("00")){
            		resumenCarga.append("\nEl código de rechazo es incorrecto. Línea: "
									+ numeroLinea.toString());
            		procesarBloque = false;
	            }
	            if(razonSocialEmisor.trim().length()==0){
            		resumenCarga.append("\nLa razón social está vacía. Línea: "
							+ numeroLinea.toString());
            		procesarBloque = false;
	            }
	            if(RFCEmisor.trim().length()==0){
            		resumenCarga.append("\nEl RFC está vacío. Línea: "
							+ numeroLinea.toString());
            		procesarBloque = false;
	            }
		        if(totalBloques==1 && procesarBloque == false){
		            resumenCarga.append("\nSe cancela la carga del archivo");
		            return resumenCarga.toString();
		        }
	            while(true){
		            lineaArchivo = entArch.readLine();
		            numeroLinea++;
		            if (lineaArchivo != null) {
		            	
		            	String claveRegistroDetalle= lineaArchivo.substring(0,2);
		            	if(claveRegistroDetalle.equals("09")){
		            		String claveRegistroResumen= lineaArchivo.substring(0,2);
		            		String numeroSecuenciaResumen= lineaArchivo.substring(2,9);
		            		String codigoOperacionResumen= lineaArchivo.substring(9,11);
		            		String numeroBloqueResumen= lineaArchivo.substring(11,18);
		            		String numeroOperaciones= lineaArchivo.substring(18,25);
		            		String importeTotalOperaciones= lineaArchivo.substring(25,43);
	            			Integer total = (registros + errores);
	            			resumenCarga.append("\nResumen:"+
	            					"\nTotal Registros totales:" + total.toString() + 
	            					"\nTotal Registros correctos:" + registros.toString() + 
	            					"\nTotal Registros con errores:" + errores.toString() + "\n");
	            			totalBloques++;
	            			break;
		            	}
		            	else if(procesarBloque==true) {
		            		String tipoRegistroDetalle = LimpiaCampo(lineaArchivo.substring(0, 02));
							String numeroSecuenciaDetalle = LimpiaCampo(lineaArchivo.substring(02, 9));
							String codigoOperacionDetalle = LimpiaCampo(lineaArchivo.substring(9, 11));
							String codigoDivisa = LimpiaCampo(lineaArchivo.substring(11, 13));
							String importeOperacion = LimpiaCampo(lineaArchivo.substring(13, 28));
							String fechaLiquidacion = lineaArchivo.substring(28, 36);
							String contadorReintento = LimpiaCampo(lineaArchivo.substring(36, 42));
							String tipoOperacion = LimpiaCampo(lineaArchivo.substring(60, 62));
							String fechaVencimiento = lineaArchivo.substring(62, 70);
							String bancoReceptorClienteUsuario = LimpiaCampo(lineaArchivo.substring(70, 73));
							String tipoCuentaClienteUsuario = LimpiaCampo(lineaArchivo.substring(73, 75));
							String numeroCuentaClienteUsuario = LimpiaCampo(lineaArchivo.substring(75, 95));
							String nombreClienteUsuario = lineaArchivo.substring(95, 135).trim();
							String referenciaServicioEmisor = lineaArchivo.substring(135, 175).trim();
							String nombreTitularServicio = lineaArchivo.substring(175, 215).trim();
							String importeIVAOperacion = LimpiaCampo(lineaArchivo.substring(215, 230));
							String referenciaNumericaEmisor = LimpiaCampo(lineaArchivo.substring(230, 237));
							String referenciaLeyendaEmisor = lineaArchivo.substring(237, 277).trim();
							String motivoDevolucion = LimpiaCampo(lineaArchivo.substring(277, 279));
							String digitoVerificador = LimpiaCampo(lineaArchivo.substring(300, 302));
							String numeroCorte = LimpiaCampo(lineaArchivo.substring(302,304));
							String indicadorPago = LimpiaCampo(lineaArchivo.substring(304,305));
							String autorizacionBanco = LimpiaCampo(lineaArchivo.substring(305, 311));
							String fechaAplicacion = lineaArchivo.substring(311, 319);
							String secuencialArchivoOriginal = lineaArchivo.substring(319, 321).trim();
							String referencia1 = lineaArchivo.substring(321, 341).trim();
							String referencia2 = lineaArchivo.substring(341, 361).trim();
							String referencia3 = lineaArchivo.substring(361, 381).trim();
			            	boolean insertar = true;
			            	String[] nombreSeparado = nombreClienteUsuario.split(" ");
			            	String nombrePropio = "";
			            	String apellidoPaterno = "";
			            	String apellidoMaterno = "";
			            	if(nombreSeparado!=null){
				            	nombrePropio = nombreSeparado[0];
				            	if(nombreSeparado.length>1){
				            		apellidoPaterno = nombreSeparado[1];
				            	}
				            	if(nombreSeparado.length > 2){
				            		apellidoMaterno  = nombreSeparado[2];
				            	}
			            	}
			            	if(insertar == true){
			            		Integer nuevoRegistro = ObtenConsecutivo();
			            		String sql = " INSERT INTO THISTORIALCARGOS (CONSECUTIVO, TIPOREGISTRO, NUMEROSECUENCIA, CODIGOOPER, CODIGODIVISA, IMPORTEOPERACION, FECLIQUIDACION, CONTADORREINTENT, TIPOOPERACION, FECVENCIMIENTO, BANCORECEPTOR, TIPOCUENTA, NUMCTACLIENTE, NOMBRECLIENTE, REFERENCIASERVICIO, NOMBRETITULAR, IMPORTEIVAOPER, REFNUMEMISOR, REFLEYENDAEMISOR, MOTIVODEVOLUCION, DIGITOVERIFICADOR, NUMEROCORTE, INDICADORPAGO, AUTORIZACIONBANCO, FECAPLICACION, SECARCHORIGINAL, REFERENCIA1, REFERENCIA2, REFERENCIA3, FECNOTIFICACION, IDARCHIVO) VALUES("
										+ "STHISTORIALCARGOS.NEXTVAL"
										+ ","
							    		+ tipoRegistroDetalle
							    		+ ","
										+ numeroSecuenciaDetalle
										+ ","
										+ codigoOperacionDetalle
										+ ","
										+ codigoDivisa
										+ ","
										+ importeOperacion
										+ ","
										+ "TO_DATE('"+ fechaLiquidacion+"','yyyymmdd')"
										+ ","
										+ contadorReintento
										+ ","
										+ tipoOperacion
										+ ","
										+ "TO_DATE('"+ fechaVencimiento+"','yyyymmdd')"
										+ ","
										+ bancoReceptorClienteUsuario
										+ ","
										+ tipoCuentaClienteUsuario
										+ ","
										+ numeroCuentaClienteUsuario
										+ ",'"
										+ nombreClienteUsuario
										+ "','"
										+ referenciaServicioEmisor
										+ "','"
										+ nombreTitularServicio
										+ "',"
										+ importeIVAOperacion
										+ ","
										+ referenciaNumericaEmisor
										+ ",'"
										+ referenciaLeyendaEmisor
										+ "',"
										+ motivoDevolucion
										+ ","
										+ digitoVerificador
										+ ","
										+ numeroCorte
										+ ","
										+ indicadorPago
										+ ","
										+ autorizacionBanco
										+ ","
										+ "TO_DATE('"+ fechaAplicacion+"','yyyymmdd')"
										+ ",'"
										+ secuencialArchivoOriginal
										+ "','"
										+ referencia1
										+ "','"
										+ referencia2
										+ "','"
										+ referencia3
										+ "',"
										+ "TO_DATE('','yyyymmdd')," + idArchivo + ")";
			            		System.out.println(sql);
								bdconn.Ejecuta(sql);
								registros++;
			            	} else{
			            		errores++;
			            	}
		            	}
		            } else{
		            	break;
		            }
	            }
	            totalBloques++;
	        }
            entArch.close();
			clUtil.actualizaRegistroBitacoraArchivos(idArchivo,registros+errores, registros , 0, errores, resumenCarga);
            
        } catch (FileNotFoundException e) {
        	resumenCarga.append("\nERROR: Archivo no encontrado");
			e.printStackTrace();
		} catch (IOException e) {
        	resumenCarga.append("\nERROR: No se puede leer del archivo");
			e.printStackTrace();
		} catch (Exception e) {
			resumenCarga.append("\nERROR: No se puede leer del archivo");
			e.printStackTrace();
		} 
        finally{
        	if(fr!=null){
        		try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }
        return resumenCarga.toString();    
	}
	
	/**Obtiene el consecutivo de STSOLDOMICILIACION*/
	private int ObtenConsecutivo(){
		int resultado = 0;
		try {
			resultado = Integer.parseInt(clUtil.TraeRegistro("STSOLDOMICILIACION.NEXTVAL", "DUAL", " "));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultado;
	}
	
	/**Obtiene el consecutivo de STHISTORIALABONOS*/
	private int obtenerConsecutivoSTHISTORIALABONOS() {
		int resultado = 0;
		try {
			resultado = Integer.parseInt(clUtil.TraeRegistro("STHISTORIALABONOS.NEXTVAL", "DUAL", " "));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultado;
	}
	
	/**El archivo DETAPOVOL, contiene todos los movimientos de los abonos de todos los origenes.*/
	private String ProcesaDetApoVol(String fileName) {
		StringBuilder resultado = new StringBuilder();
		Integer registros = 0;
		Integer errores = 0;
		Integer total = 0;
		List<ArchivoDetApoVol> nssAceptados = new ArrayList<ArchivoDetApoVol>();
		String claveArchivo = clUtil.traeClaveArchivo(clUtil.TIPO_ARCHIVO_TCATARCHIVOS_DETAPOVOL);
		if (clUtil.siExisteCargaArchivoBitacora(clUtil.obtieneNombreArchivo(fileName), claveArchivo)) {
			resultado.append("\nArchivo ya cargado " + clUtil.obtieneNombreArchivo(fileName) + ":\n");
			return resultado.toString();
		}
		try {
				String cadena;
		        String siefore = null;
		        String listaSiefore = "SAVBMX2 SBBMX1 SBBMX2 SBBMX3 SBBMX4";
		        String cadena2[];
		        String regex = "\\d+";
		        Pattern pattern = Pattern.compile(regex);
		       
		        FileReader f = new FileReader(fileName);
		        BufferedReader b = new BufferedReader(f);
		        while((cadena = b.readLine())!=null) { 
		             cadena2 = cadena.split(";");
		             if(cadena2.length > 10){
		                    int existeSiefore = listaSiefore.indexOf(cadena2[11].trim());
		                    if(existeSiefore > 0){
		                           siefore = cadena2[11].trim();
		                    }
		                    if(pattern.matcher(cadena2[0].trim()).matches()){
		                    	ArchivoDetApoVol archivo = new ArchivoDetApoVol();
		        				archivo.setNumero(cadena2[0].trim());
		        				archivo.setFechaOperacion(cadena2[1].trim());
		        				archivo.setNss(Func.rellenarNSS((cadena2[2].trim())));
		        				archivo.setNombreCompleto(cadena2[3].trim());
		        				archivo.setTb(cadena2[4].trim());
		        				archivo.setTipoDeposito(cadena2[5].trim());
		        				archivo.setNumeroAutorizacion(cadena2[6].trim());
		        				archivo.setSucursal(cadena2[7].trim());
		        				archivo.setNombreSucursal(cadena2[8].trim());
		        				archivo.setSubCuenta(cadena2[9].trim());
		        				archivo.setPrecioAccion(cadena2[10].trim());
		        				archivo.setAccionesVentanilla(cadena2[11].trim());
		        				archivo.setImporteVentanilla(cadena2[12].trim());
		        				archivo.setPrecioDolar(cadena2[13].trim());
		        				archivo.setImporteDolar(cadena2[14].trim());
		        				archivo.setLavadoDinero(cadena2[15].trim());
		        				archivo.setSiefore(siefore);
		        				nssAceptados.add(archivo);
		        				registros++;
		                    }       
		             }
		        }
		        b.close();
		        total = registros + errores;
				resultado.append("\nResumen:"+
						"\nTotal Registros:" + total.toString() + 
						"\nRegistros correctos:" + registros.toString() + 
						"\nRegistros con errores:" + errores.toString() + "\n");
		        
				Integer numeroBitacora = clUtil.insertarRegistroBitacoraArchivos(total, registros, 0, errores, resultado, claveArchivo, fileName, ""); 
				for (Iterator<ArchivoDetApoVol> iterator = nssAceptados.iterator(); iterator.hasNext();) {
					ArchivoDetApoVol objecto = (ArchivoDetApoVol) iterator.next();	
					insertarRegistroArchivoDetApoVol(objecto, numeroBitacora);
				}
		} catch (Exception e) {
			e.printStackTrace();
			resultado.append("\nError en la línea " + registros.toString() + ":\n" + e.getMessage());
		}
		return resultado.toString();
	}
	
	/**Procesa el archivos SICI*/
	private String ProcesaSICI(String fileName){
		StringBuilder resultado = new StringBuilder();
		Integer contadorLineas = 1;//Saltamos el encabezado
		String claveArchivo = clUtil.traeClaveArchivo(clUtil.TIPO_ARCHIVO_TCATARCHIVOS_SICI);
		
		try {
			InputStream myxls = new FileInputStream(fileName);
			HSSFWorkbook wb   = new HSSFWorkbook(myxls);
			HSSFSheet sheet   = wb.getSheetAt(0);
			DataFormatter formatter = new DataFormatter();
			
        	Integer registros = 0;
    		Integer errores = 0;
    		Integer total = 0;
    		List<ArchivoSICI> nssAceptados = new ArrayList<ArchivoSICI>();
    		boolean cargarArchivo = true;
    		
    		HSSFCellStyle estiloTarjeta = wb.createCellStyle();
    		HSSFDataFormat df = wb.createDataFormat();
			estiloTarjeta.setDataFormat(df.getFormat("0000000000000000"));
			while(true){
				HSSFRow row = sheet.getRow(contadorLineas);
				
				if(row==null || formatter.formatCellValue(row.getCell(0)).equals("")){
					total = registros + errores;
					resultado.append("\nResumen:"+
        					"\nTotal Registros:" + total.toString() + 
        					"\nRegistros correctos:" + registros.toString() + 
        					"\nRegistros con errores:" + errores.toString() + "\n");
					break;
				}
				ArchivoSICI archivoSICI = new ArchivoSICI();
				String cuentaCLABE = "";
				String numeroTarjetaDebito = "";
				String formatoNuevo = "";
						
				archivoSICI.setCurp(formatter.formatCellValue(row.getCell(0)));
				archivoSICI.setNss(formatter.formatCellValue(row.getCell(1)));
				archivoSICI.setNombre(formatter.formatCellValue(row.getCell(2)));
				archivoSICI.setApellidoPaterno(formatter.formatCellValue(row.getCell(3)));
				archivoSICI.setApellidoMaterno(formatter.formatCellValue(row.getCell(4)));
				archivoSICI.setFolioPreSolicitud(formatter.formatCellValue(row.getCell(5)));
				archivoSICI.setEstadoPreSolicitud(formatter.formatCellValue(row.getCell(6)));			
				archivoSICI.setMotivoRechazoProcesar(formatter.formatCellValue(row.getCell(7)));
				formatoNuevo = formatter.formatCellValue(row.getCell(8));
				//System.out.println("formatoNuevo: " + formatoNuevo);
				if (!Func.isVaciaCadena(formatoNuevo)){
					formatoNuevo = Func.cambioFormatoFechaDDMMYYY(formatoNuevo);
					//System.out.println("formatoNuevo2: " + formatoNuevo);
					archivoSICI.setFechaRegistroPreSolicitudProcesar(formatoNuevo);
				} else {
					archivoSICI.setFechaRegistroPreSolicitudProcesar("");
				}
				formatoNuevo = formatter.formatCellValue(row.getCell(9));
				//System.out.println("formatoNuevo: " + formatoNuevo);
				if (!Func.isVaciaCadena(formatoNuevo)){
					formatoNuevo = Func.cambioFormatoFechaDDMMYYY(formatoNuevo);
					//System.out.println("formatoNuevo 2: " + formatoNuevo);
					archivoSICI.setFechaRespuestaAdministradora(formatoNuevo);
				} else {
					archivoSICI.setFechaRespuestaAdministradora("");
				}
				//archivoSICI.setFechaRespuestaAdministradora(formatter.formatCellValue(row.getCell(9)));
				archivoSICI.setEstadoPreSolicitudAdmin(formatter.formatCellValue(row.getCell(10)));
				archivoSICI.setMotivoRechazo1(formatter.formatCellValue(row.getCell(11)));
				archivoSICI.setMotivoRechazo2(formatter.formatCellValue(row.getCell(12)));
				archivoSICI.setMotivoRechazo3(formatter.formatCellValue(row.getCell(13)));						
				cuentaCLABE = formatter.formatCellValue(row.getCell(14));
				numeroTarjetaDebito = formatter.formatCellValue(row.getCell(15));
				if (!cuentaCLABE.trim().equals("")) {
					archivoSICI.setCuenta(cuentaCLABE);
					archivoSICI.setTipoCuenta(clUtil.TIPO_CUENTA_INTERBANCARIA);
				} else {
					archivoSICI.setCuenta(numeroTarjetaDebito);
					archivoSICI.setTipoCuenta(clUtil.TIPO_CUENTA_DEBITO);
				}
				
				archivoSICI.setTitularCuenta(formatter.formatCellValue(row.getCell(16)));
				archivoSICI.setBancoEmisor(formatter.formatCellValue(row.getCell(17)));
				//archivoSICI.setCuentaDeposito(formatter.formatCellValue(row.getCell(18)));//TODO: 11/16/2017 DHDF DESCARTAR, YA NO SE MANEJA 
				archivoSICI.setAfore(formatter.formatCellValue(row.getCell(18)));
				archivoSICI.setMontoAportacion(formatter.formatCellValue(row.getCell(19)));
				//archivoSICI.setCambioMontoAportacion(formatter.formatCellValue(row.getCell(21))); //TODO: 11/16/2017 DHDF DESCARTAR, YA NO SE MANEJA
				archivoSICI.setDeducibilidadAportacion(formatter.formatCellValue(row.getCell(20)));
				//archivoSICI.setCambioDeducibilidad(formatter.formatCellValue(row.getCell(23)));//TODO: 11/16/2017 DHDF DESCARTAR, YA NO SE MANEJA
				archivoSICI.setPeriodicidadAportacion(formatter.formatCellValue(row.getCell(21))); 
				//archivoSICI.setCambioPeriodicidad(formatter.formatCellValue(row.getCell(25))); //TODO: 11/16/2017 DHDF DESCARTAR, YA NO SE MANEJA
				
				formatoNuevo = formatter.formatCellValue(row.getCell(22));
				//System.out.println("formatoNuevo: " + formatoNuevo);
				if (!Func.isVaciaCadena(formatoNuevo)){
					formatoNuevo = Func.cambioFormatoFechaDDMMYYY(formatoNuevo);
					//System.out.println("formatoNuevo 2: " + formatoNuevo);
					archivoSICI.setFechaDescuento(formatoNuevo);
				} else {
					archivoSICI.setFechaDescuento("");
				}
				archivoSICI.setDiaDescuento1(formatter.formatCellValue(row.getCell(23)));
				System.out.println("Archivo: "+ archivoSICI.getDiaDescuento1());
				if(archivoSICI.getDiaDescuento1().equals("N/A")){
					archivoSICI.setDiaDescuento1("NA");
				}
					
				
				archivoSICI.setDiaDescuento2(formatter.formatCellValue(row.getCell(24)));
				System.out.println("Archivo: "+ archivoSICI.getDiaDescuento2());
				if(archivoSICI.getDiaDescuento2().equals("N/A")){
					archivoSICI.setDiaDescuento2("NA");
					System.out.println("entro");
				}
					
				archivoSICI.setIncrementalidad(formatter.formatCellValue(row.getCell(25)));
				//archivoSICI.setCambioIncrementalidad(formatter.formatCellValue(row.getCell(30)));//TODO: 11/16/2017 DHDF DESCARTAR, YA NO SE MANEJA
				archivoSICI.setOpcionIncrementalidadMontoPorcentaje(formatter.formatCellValue(row.getCell(26)));
				archivoSICI.setPorcentajeIncrementar(formatter.formatCellValue(row.getCell(27)));
				archivoSICI.setMontoIncrementar(formatter.formatCellValue(row.getCell(28)));
				archivoSICI.setPeriodicidadIncrementalidad(formatter.formatCellValue(row.getCell(29)));
				archivoSICI.setNotificacionEstadoCtaEmail(formatter.formatCellValue(row.getCell(30)));
				archivoSICI.setAvisoCargoViaEmail(formatter.formatCellValue(row.getCell(31)));
				archivoSICI.setRecordatorioProximoCargoViaEmail(formatter.formatCellValue(row.getCell(32)));
				archivoSICI.setSaldoAcumuladoViaEmail(formatter.formatCellValue(row.getCell(33)));
				archivoSICI.setAnunciosInteresViaEmail(formatter.formatCellValue(row.getCell(34)));
				archivoSICI.setConsejosAhorroViaEmail(formatter.formatCellValue(row.getCell(35)));
				archivoSICI.setNotificacionEstadoCtaViaSMS(formatter.formatCellValue(row.getCell(36)));
				archivoSICI.setAvisoCargoViaSMS(formatter.formatCellValue(row.getCell(37)));
				archivoSICI.setRecordatorioProximoCargoViaSMS(formatter.formatCellValue(row.getCell(38)));
				archivoSICI.setSaldoAcumuladoViaSMS(formatter.formatCellValue(row.getCell(39)));
				archivoSICI.setAnunciosInteresViaSMS(formatter.formatCellValue(row.getCell(40)));
				archivoSICI.setConsejosAhorroViaSMS(formatter.formatCellValue(row.getCell(41)));
				//archivoSICI.setCambioNotificacion1(formatter.formatCellValue(row.getCell(47))); //TODO: 11/16/2017 DHDF DESCARTAR, YA NO SE MANEJA
				//archivoSICI.setCambioNotificacion2(formatter.formatCellValue(row.getCell(48))); //TODO: 11/16/2017 DHDF DESCARTAR, YA NO SE MANEJA
				//archivoSICI.setCambioNotificacion3(formatter.formatCellValue(row.getCell(49))); //TODO: 11/16/2017 DHDF DESCARTAR, YA NO SE MANEJA
				archivoSICI.setCorreoElectronico(formatter.formatCellValue(row.getCell(42)));
				archivoSICI.setNumeroCelular(formatter.formatCellValue(row.getCell(43)));
				archivoSICI.setCompaniaCelular(formatter.formatCellValue(row.getCell(44)));
				archivoSICI.setNumeroTelefonoFijo(formatter.formatCellValue(row.getCell(45)));
				archivoSICI.setHorarioContacto(formatter.formatCellValue(row.getCell(46)));
				archivoSICI.setTipoMovimiento(formatter.formatCellValue(row.getCell(47)));
				archivoSICI.setOrigenPreSolicitud(formatter.formatCellValue(row.getCell(48)));
				archivoSICI.setFechaVigenciaTarjetaCredito(formatter.formatCellValue(row.getCell(49)));
				archivoSICI.setEstatusDomi(clUtil.ESTATUSDOMI_SICI_INACTIVO);
				archivoSICI.setVobo("NO");
								
				if(archivoSICI.getCurp().trim().equals("") || 
					archivoSICI.getNombre().trim().equals("") || 
					archivoSICI.getApellidoPaterno().trim().equals("") || 
					archivoSICI.getApellidoMaterno().trim().equals("") || 
					archivoSICI.getFolioPreSolicitud().trim().equals("") || 
					archivoSICI.getEstadoPreSolicitud().trim().equals("") || 
					archivoSICI.getFechaRegistroPreSolicitudProcesar().trim().equals("") || 
					archivoSICI.getCuenta().trim().equals("") || 
					archivoSICI.getTitularCuenta().trim().equals("") || 
					archivoSICI.getBancoEmisor().trim().equals("") || 
					archivoSICI.getMontoAportacion().trim().equals("") || 
					archivoSICI.getDeducibilidadAportacion().trim().equals("") || 
					archivoSICI.getPeriodicidadAportacion().trim().equals("") || 
					archivoSICI.getCorreoElectronico().trim().equals("") || 
					(archivoSICI.getNumeroCelular().trim().equals("") && 
					archivoSICI.getNumeroTelefonoFijo().trim().equals("") )|| 
					archivoSICI.getHorarioContacto().trim().equals("") || 
					archivoSICI.getTipoMovimiento().trim().equals("")){
					
					//cargarArchivo = false;
					resultado.append("\nFaltan datos en la línea: " 
							+ contadorLineas.toString() 
							+ ", No se carga el registro en la base de datos");
					errores++;
				} else {
					if(clUtil.validarFolioSolicitudDuplicada(archivoSICI.getFolioPreSolicitud())){
						resultado.append("\n El folio: " 
								+archivoSICI.getFolioPreSolicitud()
								+ " de la linea: "
								+ contadorLineas.toString()
								+ " ya existe en la base de datos, no se carga");
						errores++;
					}else{
						
					if (!clUtil.validarNSSSExisteDatosGenerales(archivoSICI.getNss(), archivoSICI.getCurp())) {
						resultado.append("\nNo existe el siguiente NSS / CURP en Datos Generales: " + archivoSICI.getNss() + "/" + archivoSICI.getCurp() + " en la linea: " +contadorLineas.toString());
						cargarArchivo = false;
						errores++;
					} else {
						//Valida si es modif o cancelacion para poder modificar algo o cancelar algo que ya exista
						
						if(archivoSICI.getEstadoPreSolicitudAdmin().equals("5")||archivoSICI.getEstadoPreSolicitudAdmin().equals("8")){
							String estatusUltimaSolicitud = clUtil.validarExistenciaSolicitud(archivoSICI.getNss(), archivoSICI.getCurp());
							if (estatusUltimaSolicitud.equals("0")) {
								resultado.append("\nNo existe una solicitud que modificar/cancelar para : " + archivoSICI.getNss() + "/" + archivoSICI.getCurp() + " en la linea: " +contadorLineas.toString());
								cargarArchivo = false;
								errores++;
							}
							
						}
							
						archivoSICI.setEstadoPreSolicitud(clUtil.traeClaveCatalogo("TCATTIPOTRANSACCION", " WHERE EDOPRESOL = '" + archivoSICI.getEstadoPreSolicitud()+ "' AND DESCRIPCION LIKE 'Pre%' ORDER BY CLAVE ASC"));
						archivoSICI.setOrigenPreSolicitud(clUtil.traeClaveCatalogo("TCATORIGEN" , " WHERE DESCRIPCION = '" + clUtil.ORIGEN_ARCHIVO_E_SAR + "'"));
						if (!clUtil.validarNSSEstatusDatosGenerales(archivoSICI.getNss(), archivoSICI.getCurp())) {
							resultado.append("\nEl siguiente NSS/CURP se registra pero queda en estatus de Rechazado: " + archivoSICI.getNss() + "/" + archivoSICI.getCurp() + " en la linea: " +contadorLineas.toString());
							archivoSICI.setDiagnostico(clUtil.DIAGNOSTICO_RECHAZADO);
							archivoSICI.setEstatusDomi(clUtil.ESTATUSDOMI_SICI_INACTIVO);
							archivoSICI.setMotivo(clUtil.traeClaveCatalogo("TCATMOTRECHAZO", " WHERE CVEORIGEN = '" + clUtil.CVEORIGEN_MOTIVO_RECHAZO_SICI + "' AND CATTIPOARCHIVO = '" + claveArchivo +"'"));
							nssAceptados.add(archivoSICI);
							registros++;
						} else {
							archivoSICI.setDiagnostico("");
							archivoSICI.setMotivo(null);
							nssAceptados.add(archivoSICI);
							registros++;
						}
					}
				}
				}
				contadorLineas++;
			}
			if(cargarArchivo == true) {
				Integer numeroBitacora = clUtil.insertarRegistroBitacoraArchivos(total, registros, 0, errores, resultado, claveArchivo, fileName, "");  
				for (Iterator<ArchivoSICI> iterator = nssAceptados.iterator(); iterator.hasNext();) {
					ArchivoSICI objecto = (ArchivoSICI) iterator.next();
					insertarRegistroSICIconBitacora(objecto, numeroBitacora);
				}
			} else {
				resultado.append("\nNo se carga el archivo debido a la inconsistencia de Datos");
			}
		} catch(Exception e){
			e.printStackTrace();
			resultado.append("\nError en la línea " + contadorLineas.toString() + ":\n" + e.getMessage());
		}
		return resultado.toString();
	}
	
	/**INSERT DE LA INFORMACION DE THISTORIALABONOS*/
	private void insertarRegistroArchivoDetApoVol(ArchivoDetApoVol objecto, Integer registroBitacora) {
		String sql = "INSERT INTO THISTORIALABONOS (CONSECUTIVO, NUM, FECOPERACION, NSS, NOMBRECOMPLETO, "
				+ "TB, TIPODEPOSITO, NUMEROAUTORIZACION, SUCURSAL, NOMBRESUCURSAL, SUBCUENTA, "
				+ "PRECIOACCION, ACCIONESVENTANILLA, IMPORTEVENTANILLA, PRECIODOLAR, "
				+ "IMPORTEDOLAR, LAVADODINERO, SIEFORE, IDARCHIVO) VALUES ("
				+ "STHISTORIALABONOS.NEXTVAL, "
				+ "'" + objecto.getNumero() + "', "
				+ "'" + objecto.getFechaOperacion() + "', "
				+ "'" + objecto.getNss() + "', "
				+ "'" + objecto.getNombreCompleto() + "', "
				+ "'" + objecto.getTb() + "', "
				+ "'" + objecto.getTipoDeposito() + "', "
				+ "to_number('" + objecto.getNumeroAutorizacion().trim() + "','999999999999999'), "
				+ "to_number('" + objecto.getSucursal().trim() + "','999999'), "
				+ "'" + objecto.getNombreSucursal() + "', "
				+ "'" + objecto.getSubCuenta() + "', "
				+ "to_number('" + objecto.getPrecioAccion().trim() + "','9999999999.999999'), "
				+ "to_number('" + objecto.getAccionesVentanilla().trim() + "','99999999999999.999999'), "
				+ "to_number('" + objecto.getImporteVentanilla().trim() + "','9999999999999.99'), "
				+ "to_number('" + objecto.getPrecioDolar().trim()+ "','9999999999.999999'), "
				+ "to_number('" + objecto.getImporteDolar().trim() + "','9999999999999.99'), "
				+ "'" + objecto.getLavadoDinero() + "', "
				+ "'" + objecto.getSiefore() + "', "
				+ registroBitacora + ")";
			bdconn.Ejecuta(sql);
	}
	
	/**INSERT DE LA INFORMACION DE SICI*/
	private void insertarRegistroSICIconBitacora(ArchivoSICI objecto, Integer registroBitacora) {
		Integer nuevoRegistro = new Integer(0);
		nuevoRegistro = ObtenConsecutivo();
		String sql ="INSERT INTO TSOLDOMICILIACION (IDSOLDOMICILIACION, NSS, CURP, NOMBRE, APATERNO, AMATERNO, "
				+ "TRANSACCION, TIPOCUENTA, BANCO, CUENTA,"
				+ " MONTO, PERIODICIDAD, DIAGNOSTICO, MOTIVO, ORIGEN, FOLIOESAR, FECCARGA, ESTATUSDOMI, VOBO) VALUES ("
				+ nuevoRegistro + ", " 
				+ "'" + objecto.getNss() + "', " 
				+ "'" + objecto.getCurp() +  "', " 
				+ "'" + objecto.getNombre() + "', " 
				+ "'" + objecto.getApellidoPaterno() + "', " 
				+ "'" + objecto.getApellidoMaterno() + "', "  
				+ objecto.getEstadoPreSolicitud() + ", "
				+ objecto.getTipoCuenta() + ", "
				+ "'" + objecto.getBancoEmisor() + "', "
				+ "'" + objecto.getCuenta() + "', "
				+ "to_number('" + Func.LimpiaCaracterPesos(objecto.getMontoAportacion()).trim() + "','99999999.99'), "
				+ objecto.getPeriodicidadAportacion() + ", "
				+ "'" + objecto.getDiagnostico() + "', "
				+ objecto.getMotivo() + ", "
				+ objecto.getOrigenPreSolicitud() + ", " 
				+ "'" + objecto.getFolioPreSolicitud() + "', "
				+ "sysdate, "  
				+ "'" +objecto.getEstatusDomi() +"', "
				+ "'" + objecto.getVobo() + "')";
	    //System.out.println("TSOLDOMICILIACION: " + sql);
    	bdconn.Ejecuta(sql);
    	sql ="INSERT INTO TSDOMIDETESAR(IDSOLDOMICILIACION, MOTRECHAZOPROCESAR, FECRESSPADM, FECREGPRESOL, FECRESPADM, EDOPRESOL, "
    			+ "MOTRECHAZO1, MOTRECHAZO2, MOTRECHAZO3, TITULARCTA, AFORE, "
    			+ "DEDUCIBILIDAD, FECDESCUENTO, DIADESCUENTO1, DIADESCUENTO2, "
    			+ "IINCREMENTALIDAD, OPINCREMEN, PORCINCREMEN, MONTOINCREMEN, PERIOINCREMEN, "
    			+ "NOTIEDOCTAMAIL, AVISOCARGOMAIL, RECORCARGOMAIL, SALDOACUMMAIL, ANUNCIOSMAIL, CONSEJOSMAIL, "
    			+ "NOTIEDOCTASMS, AVISOCARGOSMS, RECORCARGOSMS, SALDOACUMSMS, ANUNCIOSSMS, CONSEJOSSMS, "
    			+ "CORREOELECTRONICO, TELCELULAR, CIACELULAR, TELFIJO, HORARIOCONTACTO, "
    			+ "TIPOMOVIMIENTO, ORIGENPRESOLICITUD, FECVIGENCIATDDC,"
    			+ "IDARCHIVO) VALUES (" 
    			+ nuevoRegistro + "," 
    			+ "'" + objecto.getMotivoRechazoProcesar() + "', " 
    			+ "to_date('" + objecto.getFechaRespuestaAdministradora() + "', 'DD/MM/YYY'), " 
    			+ "to_date('" + objecto.getFechaRegistroPreSolicitudProcesar() + "', 'DD/MM/YYYY'), " 
    			+ "to_date('" + objecto.getFechaRespuestaAdministradora() + "', 'DD/MM/YYYY'), "
    			+ "'" + objecto.getEstadoPreSolicitudAdmin() + "', "
    			+ "'" + objecto.getMotivoRechazo1() + "', "
    			+ "'" + objecto.getMotivoRechazo2() + "', "
    			+ "'" + objecto.getMotivoRechazo3() + "', " 
    			+ "'" + objecto.getTitularCuenta() + "', "
    			+ "'" + objecto.getAfore() +"', "
    			+ LimpiaCampo(objecto.getDeducibilidadAportacion()) + ", "
    			+ "to_date('" + objecto.getFechaDescuento() + "', 'DD/MM/YYYY'), '"
    			+ LimpiaCampo(objecto.getDiaDescuento1()) + "', "
    			+ "'" + objecto.getDiaDescuento2() + "', "
    			+ LimpiaCampo(objecto.getIncrementalidad()) + ", "
    			+ LimpiaCampo(objecto.getOpcionIncrementalidadMontoPorcentaje()) + ", "
    			+ "'" + objecto.getPorcentajeIncrementar() + "', "
    			+ "to_number('" + Func.LimpiaCaracterPesos(objecto.getMontoAportacion()).trim() + "','99999999.99'), "
    			+ LimpiaCampo(objecto.getPeriodicidadIncrementalidad()) + ","
    			+ "'" + objecto.getNotificacionEstadoCtaEmail() + "',"
    			+ "'" + objecto.getAvisoCargoViaEmail() + "',"
    			+ "'" + objecto.getRecordatorioProximoCargoViaEmail() + "',"
    			+ "'" + objecto.getSaldoAcumuladoViaEmail() + "',"
    			+ "'" + objecto.getAnunciosInteresViaEmail() + "',"
    			+ "'" + objecto.getConsejosAhorroViaEmail() + "',"
    			+ "'" + objecto.getNotificacionEstadoCtaViaSMS() + "',"
    			+ "'" + objecto.getAvisoCargoViaSMS() + "',"
    			+ "'" + objecto.getRecordatorioProximoCargoViaSMS() + "',"
    			+ "'" + objecto.getSaldoAcumuladoViaSMS() + "', "
    			+ "'" + objecto.getAnunciosInteresViaSMS() + "', "
    			+ "'" + objecto.getConsejosAhorroViaSMS() + "', "
    			+ "'" + objecto.getCorreoElectronico() + "', "
    			+ LimpiaCampo(objecto.getNumeroCelular()) + ", " 
    			+ LimpiaCampo(objecto.getCompaniaCelular()) + ", "
    			+ LimpiaCampo(objecto.getNumeroTelefonoFijo()) + ", "
    			+ "'" + objecto.getHorarioContacto() + "', "
    			+ "'" + objecto.getTipoMovimiento() + "', "
    			+ objecto.getOrigenPreSolicitud() + ", "
    			+ "'" + objecto.getFechaVigenciaTarjetaCredito() + "', "
    			+ registroBitacora + ")";
    	System.out.println("TSDOMIDETESAR: " + sql);
    	System.out.println("insercion: "+objecto.getDiaDescuento2());
    	System.out.println("insercion: "+objecto.getDiaDescuento1());
    	
	    	bdconn.Ejecuta(sql);
	}
	
	private String CalcularBanco(int codigo, String cuenta){
		String resultado = "";
		switch(codigo){
			case 1:
				resultado= "002";
				break;
			case 3:
				resultado= "002";
				break;
			case 40:
				resultado= cuenta.substring(0,3);
				break;
		}
		return resultado;
	}

   
    
    private String ProcesaDatosGenerales(String fileName){
		StringBuilder resultado = new StringBuilder();
		Integer contadorLineas = 1;//Saltamos el encabezados
		try{
			InputStream myxls = new FileInputStream(fileName);
			HSSFWorkbook wb     = new HSSFWorkbook(myxls);
			HSSFSheet sheet = wb.getSheetAt(0);
			DataFormatter formatter = new DataFormatter();
			
        	Integer registros = 0;
    		Integer errores = 0;    		
    		String Apo = "'";
    		String IDARCHIVO = "2";
    				
			while(true){
				HSSFRow row = sheet.getRow(contadorLineas);
				
				if(row==null || formatter.formatCellValue(row.getCell(0)).equals("") ){
					Integer total = registros + errores;
					resultado.append("\nResumen: Procesa Datos Generales"+
        					"\nTotal Registros:" + total.toString() + 
        					"\nRegistros correctos:" + registros.toString() + 
        					"\nRegistros con errores:" + errores.toString() + "\n");
					break;
				}
				
				String CONSECUTIVO = "STDATOSCLIENTES.NEXTVAL";
				String CONS = formatter.formatCellValue(row.getCell(0));
				String NSS = LimpiaCampo2(formatter.formatCellValue(row.getCell(1)));
				String SOLICITUD = LimpiaCampo2(formatter.formatCellValue(row.getCell(2)));
				String CURP = LimpiaCampo2(formatter.formatCellValue(row.getCell(3)));
				String DOCUMENTO = LimpiaCampo2(formatter.formatCellValue(row.getCell(4)));
				String SIRH = LimpiaCampo2(formatter.formatCellValue(row.getCell(5)));				
				String ESTACION = LimpiaCampo2(formatter.formatCellValue(row.getCell(6)));
				String CAJA = LimpiaCampo2(formatter.formatCellValue(row.getCell(7)));
				String POSICION = LimpiaCampo2(formatter.formatCellValue(row.getCell(8)));
				String CONTRATO = LimpiaCampo2(formatter.formatCellValue(row.getCell(9)));
				String NOMBRECOMPLETO = LimpiaCampo2(formatter.formatCellValue(row.getCell(10)));
				String APELLIDOPATERNO = LimpiaCampo2(formatter.formatCellValue(row.getCell(11)));
				String APELLIDOMATERNO = LimpiaCampo2(formatter.formatCellValue(row.getCell(12)));
				String NOMBRE = LimpiaCampo2(formatter.formatCellValue(row.getCell(13)));
				String FECNACIMIENTO = formatter.formatCellValue(row.getCell(14));
				String SEXO = LimpiaCampo2(formatter.formatCellValue(row.getCell(15)));
				String RFC = LimpiaCampo2(formatter.formatCellValue(row.getCell(16)));
				String ENAC = LimpiaCampo2(formatter.formatCellValue(row.getCell(17)));
				String PROBATORIO = LimpiaCampo2(formatter.formatCellValue(row.getCell(18)));
				String AUTCONSAR = LimpiaCampo2(formatter.formatCellValue(row.getCell(19)));
				String SALDIARIO = LimpiaCampo2(formatter.formatCellValue(row.getCell(20)));
				String ALERTA = LimpiaCampo2(formatter.formatCellValue(row.getCell(21)));
				String NACIONALIDAD = LimpiaCampo2(formatter.formatCellValue(row.getCell(22)));
				String CLIENTE = LimpiaCampo2(formatter.formatCellValue(row.getCell(23)));
				String ESTATUS = LimpiaCampo2(formatter.formatCellValue(row.getCell(24)));
				String DESCRIPCIONESTATUS = LimpiaCampo2(formatter.formatCellValue(row.getCell(25)));
				String ESTATUSCOMPLETO = LimpiaCampo2(formatter.formatCellValue(row.getCell(26)));
				String ALT = LimpiaCampo2(formatter.formatCellValue(row.getCell(27)));
				String CAL = LimpiaCampo2(formatter.formatCellValue(row.getCell(28)));
				String DES = LimpiaCampo2(formatter.formatCellValue(row.getCell(29)));
				String ENVIOCARTA = LimpiaCampo2(formatter.formatCellValue(row.getCell(30)));
				String LIQTRAS = LimpiaCampo2(formatter.formatCellValue(row.getCell(31)));
				String CAPTURA = LimpiaCampo2(formatter.formatCellValue(row.getCell(32)));
				String PROMOTOR = LimpiaCampo2(formatter.formatCellValue(row.getCell(33)));
				String ULTIMOCAMBIO = LimpiaCampo2(formatter.formatCellValue(row.getCell(34)));
				String COMENTARIOS = LimpiaCampo2(formatter.formatCellValue(row.getCell(35)));
					
				boolean insertar = true;
				
				if(NSS.trim().equals("") || 
					SOLICITUD.trim().equals("") || 
					CURP.trim().equals(""))
					{
					
					insertar = false;
					resultado.append("\nFaltan datos en la línea " + contadorLineas.toString());
				}
				
            	if(insertar == true){
	            	registros++;
	            	//Integer nuevoRegistro = ObtenConsecutivo();
            		String sql ="INSERT INTO TDATOSCLIENTES(CONSECUTIVO,CONS,NSS,SOLICITUD,CURP,DOCUMENTO,SIRH,ESTACION,CAJA,POSICION,CONTRATO,NOMBRECOMPLETO,APELLIDOPATERNO,APELLIDOMATERNO,NOMBRE,FECNACIMIENTO,SEXO,RFC,ENAC,PROBATORIO,AUTCONSAR,SALDIARIO,ALERTA,NACIONALIDAD,CLIENTE,ESTATUS,DESCRIPCIONESTATUS,ESTATUSCOMPLETO,ALT,CAL,DES,ENVIOCARTA,LIQTRAS,CAPTURA,PROMOTOR,ULTIMOCAMBIO,COMENTARIOS,IDARCHIVO,FECCARGADG) VALUES(" +             				
            				CONSECUTIVO + "," +
            				CONS + "," +
            				Apo + NSS + Apo + "," +
            				Apo + SOLICITUD + Apo + "," +
            				Apo + CURP + Apo + "," +
            				Apo + DOCUMENTO + Apo + "," +
            				Apo + SIRH + Apo + "," +
            				Apo + ESTACION + Apo + "," +
            				Apo + CAJA + Apo + "," +
            				Apo + POSICION + Apo + "," +
            				Apo + CONTRATO + Apo + "," +
            				Apo + NOMBRECOMPLETO + Apo + "," +
            				Apo + APELLIDOPATERNO + Apo + "," +
            				Apo + APELLIDOMATERNO + Apo + "," +
            				Apo + NOMBRE + Apo + "," +
            				"TO_DATE('" + FECNACIMIENTO + "', 'YYYYMMDD')," + 
            				Apo + SEXO + Apo + "," +
            				Apo + RFC + Apo + "," +
            				Apo + ENAC + Apo+ "," +
            				Apo+ PROBATORIO + Apo + "," +
            				Apo + AUTCONSAR + Apo + "," +
            				Apo + SALDIARIO + Apo + "," +
            				Apo + ALERTA + Apo + "," +
            				Apo + NACIONALIDAD + Apo + "," +
            				Apo + CLIENTE + Apo + "," +
            				Apo + ESTATUS + Apo + "," +
            				Apo + DESCRIPCIONESTATUS + Apo + "," +
            				Apo + ESTATUSCOMPLETO + Apo + "," +
            				Apo + ALT + Apo + "," +
            				Apo + CAL + Apo + "," +
            				Apo + DES + Apo + "," +
            				Apo + ENVIOCARTA + Apo + "," +
            				Apo + LIQTRAS + Apo + "," +
            				Apo + CAPTURA + Apo + "," +
            				Apo + PROMOTOR + Apo + "," +
            				Apo + ULTIMOCAMBIO + Apo + "," +
            				Apo + COMENTARIOS + Apo + "," +
            				Apo + IDARCHIVO + Apo + "," +
            				"SYSDATE" + ")"; 
                    	
            		    System.out.print(sql);
	            		bdconn.Ejecuta(sql);            		
	            		}
            	else{
            		errores++;
            	}
            	contadorLineas++;
			}
		}
		catch(Exception e){
			e.printStackTrace();
			resultado.append("\nError en la línea " + contadorLineas.toString() + ":\n" + e.getMessage());
		}
		return resultado.toString();
	}

	private String ProcesaDomicilioCorreo(String fileName){
		StringBuilder resultado = new StringBuilder();
		Integer contadorLineas = 1;//Saltamos el encabezados
		try{
			InputStream myxls = new FileInputStream(fileName);
			HSSFWorkbook wb     = new HSSFWorkbook(myxls);
			HSSFSheet sheet = wb.getSheetAt(0);
			DataFormatter formatter = new DataFormatter();
			
        	Integer registros = 0;
    		Integer errores = 0;    		
    		String Apo = "'";
    		String IDARCHIVO = "3";
    		
			while(true){
				HSSFRow row = sheet.getRow(contadorLineas);
				
				if(row==null || formatter.formatCellValue(row.getCell(0)).equals("") ){
					Integer total = registros + errores;
					resultado.append("\nResumen: Procesa Domicilio Correo"+
        					"\nTotal Registros:" + total.toString() + 
        					"\nRegistros correctos:" + registros.toString() + 
        					"\nRegistros con errores:" + errores.toString() + "\n");
					break;
				}
				
				String CONSECUTIVO = "STDATOSCLIENTES.NEXTVAL";
				String CONS = formatter.formatCellValue(row.getCell(0));
				String NSS = LimpiaCampo2(formatter.formatCellValue(row.getCell(1)));
				String FOLIO = LimpiaCampo2(formatter.formatCellValue(row.getCell(2)));								
				String CURP = LimpiaCampo2(formatter.formatCellValue(row.getCell(3)));
				String ESTATUS = LimpiaCampo2(formatter.formatCellValue(row.getCell(4)));				
				String CALLE = LimpiaCampo2(formatter.formatCellValue(row.getCell(5)));				
				String COLONIA = LimpiaCampo2(formatter.formatCellValue(row.getCell(6)));
				String CP = LimpiaCampo2(formatter.formatCellValue(row.getCell(7)));
				String MUNICIPIO = LimpiaCampo2(formatter.formatCellValue(row.getCell(8)));
				String ESTADO = LimpiaCampo2(formatter.formatCellValue(row.getCell(9)));
				String NOTIFEMAIL = LimpiaCampo2(formatter.formatCellValue(row.getCell(10)));
				String FECHAMARCA = LimpiaCampo2(formatter.formatCellValue(row.getCell(11)));
				String ACCESOPORTAL = LimpiaCampo2(formatter.formatCellValue(row.getCell(12)));
				String EDOCTAFECMARCA = LimpiaCampo2(formatter.formatCellValue(row.getCell(13)));
				String EDOCTAFECDESMARC = formatter.formatCellValue(row.getCell(14));				
				String OTROSFECMARCA = LimpiaCampo2(formatter.formatCellValue(row.getCell(15)));
				String OTROSFECDESMARCA = LimpiaCampo2(formatter.formatCellValue(row.getCell(16)));
				String CORREOELECTRONICO = LimpiaCampo2(formatter.formatCellValue(row.getCell(17)));
				String LADATEL1 = LimpiaCampo2(formatter.formatCellValue(row.getCell(18)));
				String TEL1 = LimpiaCampo2(formatter.formatCellValue(row.getCell(19)));
				String EXT1 = LimpiaCampo2(formatter.formatCellValue(row.getCell(20)));
				String LADATEL2 = LimpiaCampo2(formatter.formatCellValue(row.getCell(21)));
				String TEL2 = LimpiaCampo2(formatter.formatCellValue(row.getCell(22)));
				String EXT2 = LimpiaCampo2(formatter.formatCellValue(row.getCell(23)));
				String SIRHMODIF = LimpiaCampo2(formatter.formatCellValue(row.getCell(24)));
				String OPERMODIF = LimpiaCampo2(formatter.formatCellValue(row.getCell(25)));
				String FECMODIF = LimpiaCampo2(formatter.formatCellValue(row.getCell(26)));
				String HRMODIF = LimpiaCampo2(formatter.formatCellValue(row.getCell(27)));
				String CLAVEMARCA = LimpiaCampo2(formatter.formatCellValue(row.getCell(28)));
				String LEYENDA = LimpiaCampo2(formatter.formatCellValue(row.getCell(29)));				
					
				boolean insertar = true;
				
				if(NSS.trim().equals("") || FOLIO.trim().equals("") || CURP.trim().equals(""))
				{
										 					
					insertar = false;
					resultado.append("\nFaltan datos en la línea " + contadorLineas.toString());
				}
				
				String Encontrado = clUtil.TraeRegistro("NSS", "TDATOSCLIENTES", " WHERE NSS = '"+ NSS +"'");
				
				if (Encontrado!=null) {
					insertar = true;					
				}
				else{
					insertar = false;
					resultado.append("\nNo se encontro NSS en la línea " + contadorLineas.toString());
				}
								
            	if(insertar == true){
	            	registros++;
            		String sql = "UPDATE TDATOSCLIENTES " +
            				"SET FOLIO=" + Apo + FOLIO + Apo + "," +   
            				"CALLE=" + Apo + CALLE + Apo + "," +
            				"COLONIA=" + Apo + COLONIA + Apo + "," +
            				"CP=" + Apo + CP + Apo + "," +
            				"MUNICIPIO=" + Apo + MUNICIPIO + Apo + "," + 
            				"ESTADO=" + Apo + ESTADO + Apo + "," +
            				"NOTIFEMAIL=" + Apo + NOTIFEMAIL + Apo + "," +
            				"FECHAMARCA=" + "TO_DATE('" + FECHAMARCA + "', 'YYYYMMDD')," +
            				"ACCESOPORTAL=" + Apo + ACCESOPORTAL + Apo + "," +
            				"EDOCTAFECMARCA=" + Apo + EDOCTAFECMARCA + Apo + "," +
            				"EDOCTAFECDESMARC=" + Apo + EDOCTAFECDESMARC + Apo + "," +
            				"OTROSFECMARCA=" + Apo + OTROSFECMARCA + Apo + "," +
            				"OTROSFECDESMARCA=" + Apo + OTROSFECDESMARCA + Apo + "," +
            				"CORREOELECTRONICO=" + Apo + CORREOELECTRONICO + Apo+ "," +
            				"LADATEL1=" + Apo+ LADATEL1 + Apo + "," +
            				"TEL1=" + Apo + TEL1 + Apo + "," +
            				"EXT1=" + Apo + EXT1 + Apo + "," +
            				"LADATEL2=" + Apo + LADATEL2 + Apo + "," +
            				"TEL2=" + Apo + TEL2 + Apo + "," +
            				"EXT2=" + Apo + EXT2 + Apo + "," +
            				"SIRHMODIF=" + Apo + SIRHMODIF + Apo + "," +
            				"OPERMODIF=" + Apo + OPERMODIF + Apo + "," +
            				"FECMODIF=" + Apo + FECMODIF + Apo + "," +
            				"HRMODIF=" + Apo + HRMODIF + Apo + "," +
            				"CLAVEMARCA=" + Apo + CLAVEMARCA + Apo + "," +
            				"LEYENDA=" + Apo + LEYENDA + Apo + "," +
            				"FECCARGADOM=" + "SYSDATE " +  
            				"WHERE NSS=" + Apo + NSS + Apo;             				 
            		
            		    System.out.print(sql);
	            		bdconn.Ejecuta(sql);            		
	            		}
            	else{
            		errores++;
            	}
            	contadorLineas++;
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
			resultado.append("\nError en la línea " + contadorLineas.toString() + ":\n" + e.getMessage());
		}
		return resultado.toString();
	}

	private String ProcesaDomiCorreoCargoAbono(String fileName) {
		System.out.println("entra a ProcesaDomiCorreoCargoAbono_MODIFF");
		StringBuilder resultado = new StringBuilder();
		Integer contadorLineas = 1;// Saltamos el encabezado
		String nomArc = "";
		int idArchivo =0;
		int registrosActualizados =0;
		try {
			InputStream myxls = new FileInputStream(fileName);
			HSSFWorkbook wb = new HSSFWorkbook(myxls);
			HSSFSheet sheet = wb.getSheetAt(0);
			DataFormatter formatter = new DataFormatter();

			Integer registros_upd = 0;
			Integer registros_ins = 0;
			Integer errores = 0;

			HSSFCellStyle estiloTarjeta = wb.createCellStyle();
			HSSFDataFormat df = wb.createDataFormat();
			estiloTarjeta.setDataFormat(df.getFormat("0000000000000000"));
			
			
			//Obtenemos el nombre del archivo:
	        nomArc = clUtil.obtieneNombreArchivo(fileName);
	        String nombreArchivo = clUtil.TraeRegistro("NOMBREARCH", "TBITARCHIVOS"," WHERE NOMBREARCH = '"+clUtil.obtieneNombreArchivo(fileName)+"'");
            if (clUtil.obtieneNombreArchivo(fileName).equals(String.valueOf(nombreArchivo))) {
                    return "El Archivo ya se ha cargado anteriormente, seleccione otro archivo";
            }
            //Insertamos el registro inicial en la bitácora, nos regresa el idArchivo
            idArchivo = clUtil.insertarRegistroBitacoraArchivos(clUtil.ID_ARCHIVO_TCATARCHIVOS_DOMICARGOABONO, fileName);
			System.out.println("idArchivo inicial: "+idArchivo);
			
			while (true) {
				HSSFRow row = sheet.getRow(contadorLineas);

				if (row == null
						|| formatter.formatCellValue(row.getCell(0)).equals("")) {
					Integer total = registros_upd+registros_ins+errores;
					resultado.append("\nResumen:" + "\nTotal Registros:"
							+ total.toString() + "\nRegistros actualizados:"
							+ registros_upd.toString() + "\nRegistros insertados:"
							+ registros_ins.toString() + "\nRegistros con errores:"
							+ errores.toString() +"\n");
					break;
				}
				
				String cons = formatter.formatCellValue(row.getCell(0));
				String nombreCompleto = formatter.formatCellValue(row.getCell(1));
				String calle = formatter.formatCellValue(row.getCell(2));
				String colonia = formatter.formatCellValue(row.getCell(3));
				String cp = formatter.formatCellValue(row.getCell(4));
				String municipio = formatter.formatCellValue(row.getCell(5));
				String estado = formatter.formatCellValue(row.getCell(6));
				String notifEmail = formatter.formatCellValue(row.getCell(7));
				String fechaMarca = formatter.formatCellValue(row.getCell(8));
				String correoElectronico = formatter.formatCellValue(row.getCell(9));
				String ladaTel1 = formatter.formatCellValue(row.getCell(10));
				String tel1 = formatter.formatCellValue(row.getCell(11));
				String ext1 = formatter.formatCellValue(row.getCell(12));
				String ladaTel2 = formatter.formatCellValue(row.getCell(13));
				String tel2 = formatter.formatCellValue(row.getCell(14));
				String ext2 = formatter.formatCellValue(row.getCell(15));
				String nss = formatter.formatCellValue(row.getCell(16));
				//String.format("%011d", Integer.parseInt(nss));
				nss = String.format("%11s", nss).replace(' ', '0');
				String curp = formatter.formatCellValue(row.getCell(17));
				String rfc = formatter.formatCellValue(row.getCell(18));
				String estatus = formatter.formatCellValue(row.getCell(19));
				String tipoNotificacion = formatter.formatCellValue(row.getCell(20));
				String medio = formatter.formatCellValue(row.getCell(21));
				
				boolean update = false;
				boolean insertar = false;
				//System.out.print("nss excel:"+nss + "\n");
				String existente = clUtil.TraeRegistro("NSS", "TDATOSCLIENTES", " where NSS = '"+ nss +"'"); 
				//"select 'X' from TDATOSCLIENTES where NSS = '"+ nss +"'";
				//System.out.print("nss existente:"+existente + "\n");
				
				if (existente==null) {
					insertar = true;
				}else{
					update = true;
				}
				
				if (update == true) {
					
					//System.out.print("entra a update"+ "\n");
					String sqlUpd = "update TDATOSCLIENTES set NOMBRECOMPLETO='"	

									+nombreCompleto
									+"',CALLE='"
									+calle
									+"',COLONIA='"
									+colonia
									+"',CP='" 
									+cp
									+"',MUNICIPIO='"	
									+municipio
									+"',ESTADO='"
									+estado
									+"',NOTIFEMAIL='"
									+notifEmail
									+"',FECHAMARCA='"
									+fechaMarca
									+"',CORREOELECTRONICO='"
									+correoElectronico
									+ "',LADATEL1='"
									+ladaTel1
									+ "',TEL1='"
									+tel1
									+ "',EXT1='"
									+ext1
									+ "',LADATEL2='"
									+ladaTel2
									+ "',TEL2='"
									+tel2
									+ "',EXT2='"
									+ext2
									+ "',CURP='"
									+curp
									+ "',RFC='"
									+rfc
									+ "',ESTATUS='"
									+estatus
									+ "',TIPONOTIFICACION="
									+ tipoNotificacion
									+ ",MEDIO="
									+medio
									+",IDARCHIVO="
									+idArchivo
									+",FECCARGADOMCA=to_date(sysdate,'DD/MM/YYYY')"
									+" where lpad(NSS,11,'0') = lpad('"
									+ nss
									+"',11,'0')";
					registrosActualizados = bdconn.EjecutaUpdate(sqlUpd);
					registros_upd = registros_upd + registrosActualizados;
					//System.out.print("UPDATE: "+sqlUpd);
					//System.out.print("registrosActualizados: "+registrosActualizados);
//				} else {
//					errores_upd++;
				} else if (insertar == true) {
					
					//System.out.print("entra a insertar"+ "\n");
					String sqlInsert = "INSERT INTO TDATOSCLIENTES (CONSECUTIVO, CONS, NSS, SOLICITUD, CURP, DOCUMENTO, SIRH, ESTACION, CAJA, POSICION, CONTRATO, NOMBRECOMPLETO, APELLIDOPATERNO, APELLIDOMATERNO, NOMBRE, FECNACIMIENTO, SEXO, RFC, ENAC, PROBATORIO, AUTCONSAR, SALDIARIO, ALERTA, NACIONALIDAD, CLIENTE, ESTATUS, DESCRIPCIONESTATUS, ESTATUSCOMPLETO, ALT, CAL, DES, ENVIOCARTA, LIQTRAS, CAPTURA, PROMOTOR, ULTIMOCAMBIO, COMENTARIOS, FOLIO, CALLE, COLONIA, CP, MUNICIPIO, ESTADO, NOTIFEMAIL, FECHAMARCA, ACCESOPORTAL, EDOCTAFECMARCA, EDOCTAFECDESMARC, OTROSFECMARCA, OTROSFECDESMARCA, CORREOELECTRONICO, LADATEL1, TEL1, EXT1, LADATEL2, TEL2, EXT2, SIRHMODIF, OPERMODIF, FECMODIF, HRMODIF, CLAVEMARCA, LEYENDA, IDARCHIVO, FECCARGADG, FECCARGADOM, FECCARGADOMCA,MEDIO,TIPONOTIFICACION) VALUES("
							+ "STDATOSCLIENTES.NEXTVAL"
							+ ",'"
							+cons
							+ "',lpad('"
							+nss
							+ "',11,'0'),'"
							+ "','"
							+curp
							+ "',' '"
							+ ",' '"
							+ ",' '"
							+ ",' '"
							+ ",' '"
							+ ",' '"
							+ ",'"
							+ nombreCompleto
							+ "',' '"
							+ ",' '"
							+ ",' '"
							+ ", to_date(sysdate,'DD/MM/YYYY')"
							+ ",' '"
							+ ",'"
							+ rfc
							+ "',' '"
							+ ",' '"
							+ ",' '"
							+ ",' '"
							+ ",' '"
							+ ",' '"
							+ ",' '"
							+ ",'"
							+estatus
							+ "',' '"
							+ ",' '"
							+ ",' '"
							+ ",' '"
							+ ",' '"
							+ ",' '"
							+ ",' '"
							+ ",' '"
							+ ",' '"
							+ ",' '"
							+ ",' '"
							+ ",' '"
							+ ",'"
							+calle
							+ "','"
							+colonia
							+ "','"
							+cp
							+ "','"
							+municipio
							+ "','"
							+estado
							+ "','"
							+notifEmail
							+ "','"
							+fechaMarca
							+ "','"
							+ "','"
							+ "','"
							+ "','"
							+ "','"
							+ "','"
							+correoElectronico
							+ "','"
							+ladaTel1
							+ "','"
							+tel1
							+ "','"
							+ext1
							+ "','"
							+ladaTel2
							+ "','"
							+tel2
							+ "','"
							+ext2
							+ "','"
							+ "','"
							+ "','"
							+ "','"
							+ "','"
							+ "','"
							+ "',"
							+idArchivo
							+","
							+ "to_date('','DD/MM/YYYY'),"
							+ "to_date('','DD/MM/YYYY'),"
							+ "to_date(sysdate,'DD/MM/YYYY'),"
							+ medio
							+ ","
							+ tipoNotificacion
							+ ")";
					registrosActualizados = bdconn.EjecutaUpdate(sqlInsert);
					registros_ins = registros_ins+ registrosActualizados;
					//System.out.print(sqlInsert);

				} else {
					errores ++;
				}
				contadorLineas++;
			}
			clUtil.actualizaRegistroBitacoraArchivos(idArchivo,contadorLineas-1, registros_upd+registros_ins , 0, errores, resultado);
			
		} catch (Exception e) {
			e.printStackTrace();
			resultado.append("\nError en la línea " + contadorLineas.toString()
					+ ":\n" + e.getMessage());
		}
		return resultado.toString();
	}
	
	//Inicia Metodo GetDiagnostico
	private String getDiagnostico(String cadena){
			String diagnostico = "NO APLICA";
			if (cadena.trim().equalsIgnoreCase("PA")||cadena.trim().equalsIgnoreCase("PM")||cadena.trim().equalsIgnoreCase("PB")) {
				diagnostico = "ACEPTADO";
			}else if (cadena.trim().equalsIgnoreCase("RE")) {
				diagnostico = "RECHAZADO";
			}		
			return diagnostico;
		}
	//Termina Metodo GetDiagnostico
	
	
		//Inicia Metodo Crear Log @
	private void crearLog(String entrada,String nombre){
		BufferedWriter writer = null;
        try {
            
            String timeLog = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            File logFile = new File(nombre+timeLog+".txt");

            
            System.out.println(logFile.getCanonicalPath());

            writer = new BufferedWriter(new FileWriter(logFile));
            writer.write(entrada);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        } 
	}
	
	//Termina Metodo Crear Log
	
	//Incia Metodo getBancoID
	public int getBancoID(String numero, String tipoCuenta){
	      int bancoId = 2;
	      if(tipoCuenta.equals("40")){ 	
	    	bancoId = Integer.parseInt(numero.substring(2,numero.length()).substring(0,3));
	        }
	      return bancoId; 
	  }
	//Termina Metodo getBancoID
	
	public String getTipoCuenta(String dato){
		String rs = "";
		if (dato.equals("1")) {
			rs = "3";
		}
		if (dato.equals("2")) {
			rs = "2";
		}
		if (dato.equals("3")) {
			rs = "40";
		}
		return rs;
	}
	
	
	public String getPeriodicidad(String dato){
		String rs = "";
		if (dato.equals("1")) {
			rs = "1";
		}
		if (dato.equals("2")) {
			rs = "2";
		}
		if (dato.equals("3")) {
			rs = "3";
		}
		if (dato.equals("4")) {
			rs = "4";
		}
		if (dato.equals("5")) {
			rs = "6";
		}
		return rs;
	}
	
	public String getBanco(String dato){
		String rs = "";
		if (dato.equals("002")) {
			rs = "Banco Nacional De México, S.A.";
		}
		if (dato.equals("Z01")) {
			rs = "Otro";
		}
		return rs;
	}
	
	
	

	private String removerAmPmHora(String hora){
		hora = hora.replace(".","").toUpperCase();
		System.out.println(hora);
		String horaFinal = hora;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss aa");
			SimpleDateFormat sdfParse = new SimpleDateFormat("HH:mm:ss");
			Date horaActual = sdf.parse(hora);
			horaFinal = sdfParse.format(horaActual);
		} catch (ParseException e) {
			
		}
		return horaFinal.trim();
		
	}
    private String LimpiaCampo(String substring) {
    	String result = substring.trim().replaceFirst("^0+(?!$)", "");
    	if(result.trim().equals("")){
    		result = "0";
    	}
		return result;
	}
    private String LimpiaCampoNss(String substring) {
    	String result = substring.trim().replaceFirst("^0+(?!$)", "");
    	if(result.trim().equals("")){
    		result = "";
    	}
		return result;
	}    
    
    private int validaNumerosNull(String substring) {
    	if (substring == null) {
    		return 0;
    	}
    	return new Integer(substring).intValue();
    }

 private String LimpiaCaracterPesos(String subString) {
    	String result = subString.trim().replace("$", "");
    	if(result.trim().equals("")){
    		result = "0";
    	}
		return result;
    }
    
    private String rellenarNSS(String nss) {
    	if (nss.length() < 11) {
    		nss = "0" + nss;
    		rellenarNSS(nss);
    	}
    	return nss;
    }
   private String LimpiaCampo2(String substring) {
    	String result = substring.trim().replaceFirst("^+(?!$)", "");
    	if(result.trim().equals("")){
    		result = "";
    	}
		return result;
	}
	
	private int getStatusOperacion(String numero) {
		int number = Integer.parseInt(numero);
		int numeroFinal = 0;
		switch (number) {
		case 0:
			numeroFinal = 5;
			break;
		case 1:
			numeroFinal = 8;
			break;

		case 4:
			numeroFinal = 6;
			break;
		case 5:
			numeroFinal = 7;
			break;
		}
		return numeroFinal;
	}

	void btnProcesar_actionPerformed(ActionEvent e) {
  
        if (Func.fbExisteArchivo(txtArchivo.getText()) == true) {
            try {
                
                switch(cboCarga.getSelectedIndex()){
	            case 0:
					break;
				case 1:
					Func.fiMensajeInformacion(cargaInicial(txtArchivo.getText()), "Resultado");
					break;
				case 2:
					Func.fiMensajeInformacion(ProcesaDatosGenerales(txtArchivo.getText()),"Resultado");
					break;
				case 3:
					Func.fiMensajeInformacion(ProcesaDomicilioCorreo(txtArchivo.getText()), "Resultado");
					break;
				case 4:
					Func.fiMensajeInformacion(ProcesaSICI(txtArchivo.getText()), "Resultado");
					break;
				case 5:
					Func.fiMensajeInformacion(ProcesaDBA(txtArchivo.getText()),"Resultado");
					break;
				//case 6:
				//	Func.fiMensajeInformacion(ProcesaDBA(txtArchivo.getText()),"Resultado");
				//	break;
				case 6:
					Func.fiMensajeInformacion(cargaRespuestaContactacion(txtArchivo.getText()),"Resultado");
					break;
				case 7:
					Func.fiMensajeInformacion(ProcesaDBC(txtArchivo.getText()),"Resultado");
					break;
				case 8:
					Func.fiMensajeInformacion(ProcesaDetApoVol(txtArchivo.getText()),"Resultado");
					break;
				case 9:
					Func.fiMensajeInformacion(ProcesaDomiCorreoCargoAbono(txtArchivo.getText()),"Resultado");
					break;
                }
            } catch(Exception ex) {
            	ex.printStackTrace();
            } finally{
            	Func.fiMensajeInformacion("Proceso Terminado", "Fin");
            }
        }
    }

    private void Posiciona(Component componente, int fila, int columna, int ancho, int altura,
                           XYLayout xYLayout1) {
        XYConstraints restricciones = new XYConstraints();
        restricciones.setX(columna);
        restricciones.setY(fila);
        restricciones.setWidth(ancho);
        restricciones.setHeight(altura);
        xYLayout1.addLayoutComponent(componente, restricciones);
    }
}

class frmCargaArchivoDomi_btnBuscar_actionAdapter
        implements java.awt.event.ActionListener {
    frmCargaArchivoDomi adaptee;

    frmCargaArchivoDomi_btnBuscar_actionAdapter(frmCargaArchivoDomi adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.btnBuscar_actionPerformed(e);
    }
}

class frmCargaArchivoDomi_btnSalir_actionAdapter
        implements java.awt.event.ActionListener {
    frmCargaArchivoDomi adaptee;

    frmCargaArchivoDomi_btnSalir_actionAdapter(frmCargaArchivoDomi adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.btnSalir_actionPerformed(e);
    }
}

class frmCargaArchivoDomi_btnProcesar_actionAdapter
        implements java.awt.event.ActionListener {
    frmCargaArchivoDomi adaptee;

    frmCargaArchivoDomi_btnProcesar_actionAdapter(frmCargaArchivoDomi adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.btnProcesar_actionPerformed(e);
    }
}

class frmCargaArchivoDomi_cboCarga_itemAdapter
        implements java.awt.event.ItemListener {
    frmCargaArchivoDomi adaptee;

    frmCargaArchivoDomi_cboCarga_itemAdapter(frmCargaArchivoDomi adaptee) {
        this.adaptee = adaptee;
    }

    public void itemStateChanged(ItemEvent e) {
        //adaptee.cboCarga_itemStateChanged(e);
    }
}