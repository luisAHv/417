package com.afbanamex.utilerias;

import java.awt.Cursor;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.afbanamex.GestionCorrespondencia.Catalogo;
import com.afbanamex.GestionCorrespondencia.clsFuncGrales;
import com.jonima.util.OC4JLogger;
import com.jonima.utils.guiclient.ContextoAplicacion;
import com.jonima.utils.guiclient.IJonStartableFrame;
import com.jonima.utils.guiclient.JonLoaderResources;

import jxl.read.biff.BiffException;
import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WriteException;

/**
 * <p>Title: Clase clutilerias</p>
 * <p>Description: Esta clase contiene rutinas de uso común</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Afore banamex</p>
 * @author: Gabriel Aguirre
 * @version 1.0
 */

public class clUtilerias{
    BDConnect bdconn = new BDConnect();
    static String[][] RS;

    OC4JLogger logger = new OC4JLogger(15);
    String JNDIName = "ejb/afbanamex/appcorreo/server/ServerEntry";
    ContextoAplicacion capp = ContextoAplicacion.instancia();
	Properties props = (Properties) capp.getValue("properties");
	String nameUser = props.getProperty("USERID");
	clsFuncGrales Func = new clsFuncGrales();
	public String ESTATUS_CLIENTE_POR_LIQUIDAR = "15";
	public String ESTATUS_CLIENTE_UNIFICADO = "22";
	public String ESTATUS_CLIENTE_CANCELADO = "09";
	public String ESTATUS_CLIENTE_DESCONOCIDO = "99";
	public String ESTATUS_CLIENTE_TRASPASAR = "05";
	public String ESTATUS_CLIENTE_ACLR_SEP = "13";
	public String ESTATUS_CLIENTE_PROC_SEP = "14";
	public String ESTATUS_CLIENTE_TRASPASADO = "02";
	public String DIAGNOSTICO_RECHAZADO = "RECHAZADO";
	public String DIAGNOSTICO_ACEPTADO = "ACEPTADO";
	public String CVEORIGEN_MOTIVO_RECHAZO_SICI = "0";
	public String ESTATUSDOMI_SICI_INACTIVO = "INACTIVO";
	public String ESTATUSDOMI_SICI_ACTIVO = "ACTIVA";
	public String ESTATUS_SOLICITUD_RECHAZADO = "RECHAZADO";
	public String TIPO_CUENTA_CHEQUES = "1";
	public String TIPO_CUENTA_DEBITO = "3";
    public String TIPO_CUENTA_INTERBANCARIA = "40";
    public String ORIGEN_ARCHIVO_E_SAR = "E-SAR";
    public String ORIGEN_CAPTURA = "CAPTURA";
    public String TIPO_ARCHIVO_TCATARCHIVOS_SICI = "Archivo Sici";
    public String TIPO_ARCHIVO_TCATARCHIVOS_DETAPOVOL = "Archivo DETAPOVOL";
    public String ID_ARCHIVO_TCATARCHIVOS_DOMICARGOABONO = "11";
    public String ID_ARCHIVO_TCATARCHIVOS_DBC = "9";
    public String ESTADO_ALTA = "1";
    public String ESTADO_MODIFICADO = "2";
    public String ESTADO_CANCELADO = "4";
    public String PRE_ESTADO_ACTIVO = "5";
    public String PRE_ESTADO_MODIFICACION = "6";
    public String PRE_ESTADO_CANCELACION = "8";

    public clUtilerias() {
    }

    /** Rutina para Consulta de un solo campo y un solo registro. Utilizada para obtener IDs y nombres de objetos */
    public String TraeRegistro(String campo, String tabla,
                               String condiciones) throws
            IllegalAccessException,
            InstantiationException, SQLException, ClassNotFoundException {
        RS = bdconn.Lista(campo, tabla, condiciones);
        String Elemento = null;
        if (!RS[0][0].equals("BOF")) {
            Elemento = RS[1][0];
        }
        return Elemento;
    }

    /** Rutina para Creación de Claves como ID's */
    public String CreaClave(String prefijo, String campo, String tabla,
                            int longitud, String parametros) throws
            IllegalAccessException,
            InstantiationException, SQLException, ClassNotFoundException {
    	String clave = null;
        /** Se extrae el mayor número de registro, se eliminan los caracteres alfanuméricos y se suma uno */    	
    	RS = bdconn.Lista("to_number(nvl(max("+campo + "),'0')) + 1 conteo", tabla, parametros);
    	
        if (!RS[0][0].equals("BOF")) {
            clave = RS[1][0];
        }                    
        
        /** Se agrega el prefijo de la clave */
        clave = prefijo + rellenarEspacios( clave, longitud);
        return clave;
    }

    /** Se agregan ceros a la clave para rellenar espacios vacíos */
    public static String rellenarEspacios(String cad,int longitud){
    	
    	 for (int turno = cad.length() + 1; turno <= longitud; turno++) {
    		 cad = "0" + cad;
         }    	 
    	 return cad;
    }
    
    /** Rutina para Llenado de parrillas */
    public void LlenaGrid(JTable tablaGrid, String ListCol, String tabla,
                          String condiciones, char UsaBotones,
                          int numberOfColumns,
                          JInternalFrame parent) throws
            SQLException,
            ClassNotFoundException, IllegalAccessException,
            InstantiationException,
            SQLException, ClassNotFoundException {
        parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        String[][] RS=null;
        if (tabla.indexOf("GCPRODUCTOS")!= -1) {
            RS = bdconn.EjecutaConsulta("Select " + ListCol +
                    " From " + tabla + condiciones,
                    "Select count(*) From " + tabla + condiciones);

        }
        else {
            RS = bdconn.Lista(ListCol, tabla, condiciones);
        }
        int anchotabla = 0;
        int columnaIni = -1;
        int renglon = 0;
        int[] AnchoCols;
        AnchoCols = new int[numberOfColumns];
        /** Se define el modelo que se aplicará a la tabla */
        DefaultTableModel t = new DefaultTableModel();
        tablaGrid.setAutoCreateColumnsFromModel(true);
        tablaGrid.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tablaGrid.setModel(t);
        TableColumn column = null;
        /** Se declaran las etiquetas que contendrán los botones a mostrar en el emparrillado */
        ImageIcon IBorrar = new ImageIcon(JonLoaderResources.getResource(this.
                getClass(), "imagenes/borrar.gif"));
        ImageIcon IActualizar = new ImageIcon(JonLoaderResources.getResource(this.
                getClass(), "imagenes/actualizar.gif"));
        ImageIcon IDetalle = new ImageIcon(JonLoaderResources.getResource(this.
                getClass(), "imagenes/detalle.gif"));
        JLabel labelBorrar = new JLabel("", IBorrar, JLabel.CENTER);
        JLabel labelActualizar = new JLabel("", IActualizar, JLabel.CENTER);
        JLabel labelDetalle = new JLabel("", IDetalle, JLabel.CENTER);
        tablaGrid.setVisible(false);
        /** Mientras el recordset no esté vacío o no se haya llegado al fin del mismo */
        if (RS!=null){
        for (int NumRen = 1; NumRen <= RS.length - 1; NumRen++) {
            /** Si es el primer registro */
            if (NumRen == 1) {
                switch (UsaBotones) {
                    /** En caso de que se elija usar los tres botones */
                    case 'S':
                        tablaGrid.setRowHeight(30);
                        for (int i = 1; i <= 3; i++) {
                            t.addColumn(" ");
                        }
                        anchotabla = 90;
                        columnaIni = 2;
                        break;
                        /** En caso de que se elija usar solo un botón */
                    case '1':
                    case 'A':
                    case 'E':
                        tablaGrid.setRowHeight(30);
                        t.addColumn(" ");
                        anchotabla = 30;
                        columnaIni = 0;
                        break;
                }
                /** Se toma el ancho de cada nombre de columna y se almacena en la variable arreglo AnchoCols */
                for (int cuentacolumna = 1; cuentacolumna <= numberOfColumns;
                     cuentacolumna++) {
                    /** Se agrega una nueva columna */
                    t.addColumn(RS[0][cuentacolumna - 1]);
                    AnchoCols[cuentacolumna -
                            1] = RS[0][cuentacolumna - 1].length();
                }
            }
            for (int cuentacolumna = 1; cuentacolumna <= numberOfColumns;
                 cuentacolumna++) {
                if (cuentacolumna == 1) {
                    /** Se agrega un nuevo renglón */
                    t.addRow(new Object[] {""});
                    /** Se colocan los botones necesarios por renglón */
                    switch (UsaBotones) {
                        case 'S':
                            t.setValueAt(labelBorrar, renglon, 0);
                            t.setValueAt(labelActualizar, renglon, 1);
                            t.setValueAt(labelDetalle, renglon, 2);
                            break;
                        case '1':
                            t.setValueAt(labelDetalle, renglon, 0);
                            break;
                        case 'A':
                            t.setValueAt(labelActualizar, renglon, 0);
                            break;
                        case 'E':
                            t.setValueAt(labelBorrar, renglon, 0);
                            break;

                    }
                }
                tablaGrid.setValueAt(RS[NumRen][cuentacolumna - 1], renglon,
                                     cuentacolumna + columnaIni);
                if (RS[NumRen][cuentacolumna - 1] != null) {
                    /** Se toma el ancho de cada contenido de columna, se compara contra el registrado en la variable arreglo AnchoCols y se reemplaza si es mayor al previamente almacenado */
                    if (AnchoCols[cuentacolumna - 1] <
                        RS[NumRen][cuentacolumna - 1].length()) {
                        AnchoCols[cuentacolumna -
                                1] = RS[NumRen][cuentacolumna - 1].length();
                    }
                }
            }
            renglon++;
        }
            }
        /** Al terminar el llenado de la tabla, si hubo uno o más registros */
        if (renglon > 0) {
            /** Se suman los anchos de columna registrados en AnchoCols y se multiplican por 10 para obtener el ancho total de la tabla */
            for (int cuentacolumna = 0; cuentacolumna <= numberOfColumns - 1;
                 cuentacolumna++) {
                if (UsaBotones != 'N' && cuentacolumna <= 2) {
                    anchotabla = anchotabla;
                }
                else {
                    anchotabla = anchotabla + (10 * AnchoCols[cuentacolumna]);
                }
            }
       //     tablaGrid.setPreferredSize(new Dimension(anchotabla, renglon * 30));
            /** Se asignan 30 como ancho de columna a cada botón si los hubiere */
            for (int cuentacolumna = 0; cuentacolumna <= columnaIni;
                 cuentacolumna++) {
                column = tablaGrid.getColumnModel().getColumn(cuentacolumna);
                column.setPreferredWidth(30);
                column.setResizable(false);
            }
            /** Se asignan los anchos de columna registrados en AnchoCols a cada columna */
            for (int cuentacolumna = 0; cuentacolumna <= numberOfColumns - 1;
                 cuentacolumna++) {
                column = tablaGrid.getColumnModel().getColumn(cuentacolumna +
                        columnaIni + 1);
                if (UsaBotones != 'N' && cuentacolumna <= 2) {
                    column.setMaxWidth(0);
                    column.setMinWidth(0);
                    tablaGrid.getTableHeader().getColumnModel().getColumn(
                            cuentacolumna +
                            columnaIni + 1).setMaxWidth(0);
                    tablaGrid.getTableHeader().getColumnModel().getColumn(
                            cuentacolumna +
                            columnaIni + 1).setMinWidth(0);
                }
                else {
                    column.setPreferredWidth(10 * AnchoCols[cuentacolumna]);
                }
            }
            for (int cuentacolumna = 0; cuentacolumna < 3; cuentacolumna++) {
                column = tablaGrid.getColumnModel().getColumn(cuentacolumna +
                        columnaIni + 1);
                TableColumnModel ColMod = tablaGrid.getColumnModel();
            }
            tablaGrid.setVisible(true);
        }
        parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /** Rutina para Exportar a Excel el contenido de un JTable */
    public void ExportaExcel(JTable tablaGrid, String nombreArchivo,
                             JInternalFrame parent, int saltaColumnas) throws
            BiffException, IOException {
        Directorio dir = new Directorio();
        try {
        	System.out.println("ExportaExcel--> URL:"+dir.RutaS);
        	System.out.println("ExportaExcel--> p1:"+(dir.RutaS != "")+" p2="+(!dir.RutaS.equals("")));
        	
            if (!dir.RutaS.equals("")) {
            	 System.out.println("ExportaExcel--> ENTRO");
                String[][] RS = bdconn.Lista(
                        "TO_CHAR(sysdate, 'YYYYMMDD') as FechArc",
                        "dual", "");
                System.out.println("ExportaExcel--> ENTRO_1");
                Workbook templateworkbook = Workbook.getWorkbook(new File(dir.
                        RutaS +
                        "/" + nombreArchivo + ".xls"));
                System.out.println("ExportaExcel--> ENTRO_2");
                WritableWorkbook workbook = Workbook.createWorkbook(new File(
                        dir.RutaS + "/" + nombreArchivo + " " + RS[1][0] +
                        ".xls"),
                        templateworkbook);
                WritableSheet sheet = workbook.getSheet(0);                
                //String Celda;
                for (int columna = saltaColumnas;
                     columna < tablaGrid.getColumnCount(); columna++) {
                    EscribeCelda(sheet, tablaGrid.getColumnName(columna),
                                 columna -
                                 saltaColumnas, 4, "Label");
                    for (int renglon = 0; renglon < tablaGrid.getRowCount();
                         renglon++) {
                        EscribeCelda(sheet,
                                     (String) tablaGrid.getValueAt(renglon,
                                columna), columna -
                                     saltaColumnas, renglon + 4,
                                     "Label");
                    }
                }
                workbook.write();
                workbook.close();
                JOptionPane.showMessageDialog(parent,
                                              "Archivo creado exitosamente",
                                              "Extracción de datos",
                                              JOptionPane.INFORMATION_MESSAGE);
            }
        }
        /*catch (IllegalAccessException ex) {
        }
        catch (InstantiationException ex) {
        }*/
        catch (Exception ex) {
            ex.printStackTrace();
        }
        /*catch (ClassNotFoundException ex) {
        }*/
    }

    /** Rutina para Obtener tanto ancho como alto de la pantalla para efectos de centrado de las formas */
    public int Pantalla(String Detalle) {
        GraphicsEnvironment ge = GraphicsEnvironment.
                getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        DisplayMode dm = gs[0].getDisplayMode();
        int Tamano = 0;
        if (Detalle == "Ancho") {
            Tamano = dm.getWidth();
        }
        if (Detalle == "Altura") {
            Tamano = dm.getHeight();
        }
        return Tamano;
    }

    public static void MensajeGrabado(JInternalFrame parent) {
        JOptionPane.showMessageDialog(parent, "Registro grabado exitosamente",
                                      "Gestión de Correspondencia",
                                      JOptionPane.INFORMATION_MESSAGE);
    }

    public static void MensajeNoGrabado(JInternalFrame parent) {
        JOptionPane.showMessageDialog(parent,
                                      "El registro no puede ser grabado por existir uno con el mismo nombre",
                                      "Gestión de Correspondencia",
                                      JOptionPane.ERROR_MESSAGE);
    }

    public static void LimitaTexto(JTextField Campo, int Longitud) {
        Campo.setDocument(new LimitDocument(Longitud));
        Campo.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if ( (e.getKeyChar() == e.VK_COMMA) || (e.getKeyChar() == 39)) {
                    e.consume();
                }
            }
        });
    }

    public static void LimitaTextoNumerico(JTextField Campo, int Longitud) {
        Campo.setDocument(new LimitDocument(Longitud));
        Campo.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if ( ( (e.getKeyChar() > 39) && (e.getKeyChar() < 58)) ||
                    e.getKeyChar() == 8) {
                    if ( (e.getKeyChar() > 41) && (e.getKeyChar() < 45)) {
                        e.consume();
                    }
                }
                else {
                    e.consume();
                }
            }
        });
    }

    public static void LimitaAreaTexto(JTextArea Campo, int Longitud) {
        Campo.setDocument(new LimitDocument(Longitud));
        Campo.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if ( (e.getKeyChar() == e.VK_COMMA) || (e.getKeyChar() == 39)) {
                    e.consume();
                }
            }
        });
    }

    public static void EscribeCelda(WritableSheet sheet, String Celda,
                                    int Columna,
                                    int Renglon,
                                    String TipoDato) {
        jxl.write.Label label;
        jxl.write.Formula formula;
        jxl.write.Number number;
        try {
            WritableCell cell = sheet.getWritableCell(Columna, Renglon);
            if (TipoDato == "Formula") {
                if (cell.getCellFormat() == null) {
                    formula = new jxl.write.Formula(Columna, Renglon, Celda);
                }
                else {
                    formula = new jxl.write.Formula(Columna, Renglon, Celda,
                            cell.getCellFormat());
                }
                sheet.addCell(formula);
            }
            else {
                if (TipoDato == "Numero") {
                    if (cell.getCellFormat() == null) {
                        number = new jxl.write.Number(Columna, Renglon,
                                Double.valueOf(Celda.trim()).doubleValue());
                    }
                    else {
                        number = new jxl.write.Number(Columna, Renglon,
                                Double.valueOf(Celda.trim()).doubleValue(),
                                cell.getCellFormat());
                    }
                    sheet.addCell(number);
                }
                else {
                    if (TipoDato == "Mes") {
                        String Mes = "";
                        if (Celda.equals("1") || Celda.equals("13")) {
                            Mes = "Enero";
                        }
                        if (Celda.equals("2")) {
                            Mes = "Febrero";
                        }
                        if (Celda.equals("3")) {
                            Mes = "Marzo";
                        }
                        if (Celda.equals("4")) {
                            Mes = "Abril";
                        }
                        if (Celda.equals("5")) {
                            Mes = "Mayo";
                        }
                        if (Celda.equals("6")) {
                            Mes = "Junio";
                        }
                        if (Celda.equals("7")) {
                            Mes = "Julio";
                        }
                        if (Celda.equals("8")) {
                            Mes = "Agosto";
                        }
                        if (Celda.equals("9")) {
                            Mes = "Septiembre";
                        }
                        if (Celda.equals("10")) {
                            Mes = "Octubre";
                        }
                        if (Celda.equals("11")) {
                            Mes = "Noviembre";
                        }
                        if (Celda.equals("12")) {
                            Mes = "Diciembre";
                        }
                        if (cell.getCellFormat() == null) {
                            label = new jxl.write.Label(Columna, Renglon, Mes);
                        }
                        else {
                            label = new jxl.write.Label(Columna, Renglon, Mes,
                                    cell.getCellFormat());
                        }
                        sheet.addCell(label);
                    }
                    else {
                        if (cell.getCellFormat() == null) {
                            label = new jxl.write.Label(Columna, Renglon, Celda);
                        }
                        else {
                            label = new jxl.write.Label(Columna, Renglon, Celda,
                                    cell.getCellFormat());
                        }
                        sheet.addCell(label);
                    }
                }
            }
        }
        catch (WriteException ex) {
        }
    }

    public static void filecopy(File source, File dest) throws IOException {
        FileChannel in = null, out = null;
        try {
            in = new FileInputStream(source).getChannel();
            out = new FileOutputStream(dest).getChannel();

            long size = in.size();
            MappedByteBuffer buf = in.map(FileChannel.MapMode.READ_ONLY, 0,
                                          size);

            out.write(buf);

        }
        finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }
    
    /**
     * Metodo que se encarga de registrar la bitacora de Archivos
     * @param registrosLeidos Numero de registros leidos
     * @param registrosCargados Numero de registros cargados
     * @param registrosGenerados Numero de registros generados
     * @param registrosErrores Numero de registros errores
     * @param mensajesError 
     * @param claveArchivo
     * @param nombreArchivo
     * @param usuario
     * @return Regresa el numero de la bitacora.
     */
    public int insertarRegistroBitacoraArchivos(int registrosLeidos, int registrosCargados, int registrosGenerados, int registrosErrores, StringBuilder mensajesError, String claveArchivo, String nombreArchivo, String usuario) {
		Integer nuevoRegistro = new Integer(0);
		nuevoRegistro = obtenerConsecutivoSTBITARCHIVOS();
		String sql = "INSERT INTO TBITARCHIVOS ("
				+ "IDARCHIVO, IDCATARCHIVO, NOMBREARCH, FECINICIO, FECFIN, USUARIO, REGISTROSLEIDOS, "
				+ "REGISTROSCARGADOS, REGISTROSGENERADOS, REGISTROSERROR, MENSAJEERROR) VALUES ("
		+ nuevoRegistro + ", "
		+ claveArchivo + ", "
		+ "'" + obtieneNombreArchivo(nombreArchivo) + "', "
		+ "SYSDATE, "
		+ "SYSDATE, "
		+ "'" + getUserName() + "', "
		+ registrosLeidos + ", "
		+ registrosCargados + ", "
		+ registrosGenerados + ", "
		+ registrosErrores + ", "
		+ "'" + mensajesError + "')";
		bdconn.Ejecuta(sql);
		return nuevoRegistro;
	}
    public int insertarRegistroBitacoraArchivos(String claveArchivo, String nombreArchivo) {
		Integer nuevoRegistro = new Integer(0);
		nuevoRegistro = obtenerConsecutivoSTBITARCHIVOS();
		String sql = "INSERT INTO TBITARCHIVOS ("
				+ "IDARCHIVO, IDCATARCHIVO, NOMBREARCH, FECINICIO, USUARIO) VALUES ("
		+ nuevoRegistro + ", "
		+ claveArchivo + ", "
		+ "'" + obtieneNombreArchivo(nombreArchivo) + "', "
		+ "SYSDATE, "
		+ "'" + getUserName() + "')";
		bdconn.Ejecuta(sql);
		return nuevoRegistro;
	}
    
    
    
    
    public int actualizaRegistroBitacoraArchivos(int idArchivo, int registrosLeidos, int registrosCargados, int registrosGenerados, int registrosErrores, StringBuilder mensajesError) {
		Integer nuevoRegistro = new Integer(0);
		nuevoRegistro = obtenerConsecutivoSTBITARCHIVOS();
		int registros =0;
		String sql = "UPDATE TBITARCHIVOS SET REGISTROSLEIDOS = "+
				registrosLeidos +
				" ,REGISTROSCARGADOS = "+
				registrosCargados +
				" ,REGISTROSGENERADOS = "+
				registrosGenerados +
				" ,REGISTROSERROR = " +
				registrosErrores +
				", MENSAJEERROR = '" +
				mensajesError + "', FECFIN = SYSDATE "+
				" WHERE IDARCHIVO="+
				idArchivo;
		//System.out.println("update: "+sql);
		registros = bdconn.EjecutaUpdate(sql);
		
		//System.out.println("registros actualizados: "+registros);
		return nuevoRegistro;
	}
    
    
    
    
    /**
     * Metodo que se encarga de registrar la bitacora de Archivos
     * @param registrosLeidos Numero de registros leidos
     * @param registrosCargados Numero de registros cargados
     * @param registrosGenerados Numero de registros generados
     * @param registrosErrores Numero de registros errores
     * @param mensajesError 
     * @param claveArchivo
     * @param nombreArchivo
     * @param usuario
     * @return Regresa el numero de la bitacora.
     */
    public int insertarRegistroBitacoraArchivos(int idArchivo,int registrosLeidos, int registrosCargados, int registrosGenerados, int registrosErrores, StringBuilder mensajesError, String claveArchivo, String nombreArchivo, String usuario) {

		String sql = "INSERT INTO TBITARCHIVOS ("
				+ "IDARCHIVO, IDCATARCHIVO, NOMBREARCH, FECINICIO, FECFIN, USUARIO, REGISTROSLEIDOS, "
				+ "REGISTROSCARGADOS, REGISTROSGENERADOS, REGISTROSERROR, MENSAJEERROR) VALUES ("
		+ idArchivo + ", "
		+ claveArchivo + ", "
		+ "'" + obtieneNombreArchivo(nombreArchivo) + "', "
		+ "SYSDATE, "
		+ "SYSDATE, "
		+ "'" + getUserName() + "', "
		+ registrosLeidos + ", "
		+ registrosCargados + ", "
		+ registrosGenerados + ", "
		+ registrosErrores + ", "
		+ "'" + mensajesError + "')";
		bdconn.Ejecuta(sql);
		return idArchivo;
	}
    
    /**
     * Metodo para validar si existe el registro en Bitacoras    
     * @param nombreArchivo Nombre del archivo
     * @param claveArchivo la Clave correspondiente
     * @return 
     * Regresa verdadero cuando no existe el archivo
     * Regresa falso cuando ya existe el archivo.
     */
    public boolean siExisteCargaArchivoBitacora(String nombreArchivo, String claveArchivo) {
    	boolean encuentra = false;
    	int resultado = 0;
    	try {
			resultado = Integer.parseInt(this.TraeRegistro("COUNT(*)", "TBITARCHIVOS", " WHERE NOMBREARCH = '" + nombreArchivo + "' AND IDCATARCHIVO =" + claveArchivo));
			if (resultado != 0) {
				return true;
			}
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
    	return encuentra;
    }
    
    /**Obtiene el consecutivo de la bitacora*/
	public int obtenerConsecutivoSTBITARCHIVOS() {
		int resultado = 0;
		try {
			resultado = Integer.parseInt(this.TraeRegistro("STBITARCHIVOS.NEXTVAL", "DUAL", " "));
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
	
	/**METODO QUE OBTIENE LA CLAVE DEL ARCHIVO TCATARCHIVOS*/
	public String traeClaveArchivo(String valor) {
		String valorDevuelto = "";
		try {
			valorDevuelto = this.TraeRegistro("IDCATARCHIVO", "TCATARCHIVOS", " WHERE NOMBREARCHIVO = '" +valor+ "'");
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
		return valorDevuelto;
	}
	
	public String traeClaveCatalogo(String tabla, String condicion) {
		String valorDevuelto = "";
		try {
			//System.out.println("SQL: " + tabla + condicion);
			valorDevuelto = this.TraeRegistro("CLAVE", tabla, condicion);
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
		return valorDevuelto;
	}
	
	public String getUserName(){
		return this.nameUser;
	}
	
	public String obtieneNombreArchivo(String nombreArchivo){
		   StringBuilder texto = new StringBuilder();
 			 for(int i = nombreArchivo.length();i>0;i--){
             char ansi = nombreArchivo.charAt(i-1);
             texto.append(ansi);
             if(ansi==92){
               break;
             }
           }
  		return texto.reverse().toString().replace("\\","");
	}
	
	public void limitaCaracteres(KeyEvent e, JTextField txt,int limite){
		if (txt.getText().length()==limite) {
			e.consume();
		}
				
	}
		
	
	public void llenarComboBoxGenerico(JComboBox<Catalogo> combo, String tabla, String where){
        try {
              ArrayList<Catalogo> catalogo = new ArrayList<Catalogo>();
              Catalogo cat = new Catalogo();
              cat.setClave("0");
              cat.setDescripcion("Seleccione uno");
                    combo.addItem(cat);
                    
              String [][] resultados = null;
              
              resultados = bdconn.Lista("CLAVE, DESCRIPCION", tabla, where);
    
              if (resultados != null && resultados.length>1)     {
                        for(int r = 1; r<resultados.length; r++) {
                             String[] registro = resultados[r];
                             Catalogo catalogo2 = new Catalogo();
                             catalogo2.setClave(registro[0]);
                             catalogo2.setDescripcion(registro[1]);
                             combo.addItem(catalogo2);
                        }
                  }          
        } catch (Exception e) {
              e.printStackTrace();
        }
  }
	
	/** 
	 * Metodo que aplica la siguiente regla:
	 * 4. Validar que el cliente no se encuentre en un proceso operativo que impida su tramite de Domiciliacion, 
	 * 	debe validar el estatus de la cuenta individual en el archivo de Datos Generales en el campo ESTATUS
	 * @param nss Recibe el NSS (es llave de consulta)
	 * @param curp Recibe el CURP (es llave de consulta)
	 * @return 
	 * Regresa verdadero cuando el cliente no tiene ningun estatus invalido
	 * Regresa falso cuando el cliente tiene uno de los siguientes estados:
	 *	a. 15 X LIQUIDAR
	 *	b. 22 UNIFICADO
	 *	c. 09 CANCELADO
	 *	d. 99 DESCONOCIDO
	 *	e. 05 X TRASPASAR
	 *	f. 13 ACLR SEP
	 *	g. 14 PROC SEP
	 *	h. 02 TRASPASADO
	 */
	public boolean validarNSSEstatusDatosGenerales(String nss, String curp) {
		try {
			String llave = "";
			if (!Func.isVaciaCadena(nss)) {
                llave = " NSS = '" + nss + "'";
			} else {
				llave = " CURP = '" + curp + "'";
			}
			//System.out.println("LLAVE: " + llave);
			String estatusCompleto = (this.TraeRegistro("ESTATUS", "TDATOSCLIENTES", " WHERE " + llave));
			String estatus = estatusCompleto.substring(0, 2);
			//System.out.println("EstatusCompleto: " + estatusCompleto + " Estatus: " + estatus);
			if (estatus.equals(this.ESTATUS_CLIENTE_POR_LIQUIDAR) ||
					estatus.equals(this.ESTATUS_CLIENTE_UNIFICADO) ||
					estatus.equals(this.ESTATUS_CLIENTE_CANCELADO) ||
					estatus.equals(this.ESTATUS_CLIENTE_DESCONOCIDO) ||
					estatus.equals(this.ESTATUS_CLIENTE_TRASPASAR) ||
					estatus.equals(this.ESTATUS_CLIENTE_ACLR_SEP) ||
					estatus.equals(this.ESTATUS_CLIENTE_PROC_SEP) ||
					estatus.equals(this.ESTATUS_CLIENTE_TRASPASADO)) {
				return false;
			} else {
				return true;
			}
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
		return false;
	}
	
	/**3. Validar que se encuentre registrado en la tabla Datos Generales, 
	 * en caso de que no se encuentre el NSS en la tabla, 
	 * descartara el archivo y se reflejara en las cifras de control, identificando los NSS´s faltantes
	 * @param nss Recibe el NSS (es llave de consulta)
	 * @param curp Recibe el CURP (es llave de consulta)
	 * @return 
	 * Regresa verdadero cuando existe el cliente 
	 * Regresa falso cuando no existe el cliente.
	 * */
	public boolean validarNSSSExisteDatosGenerales(String nss, String curp) {
		String valor = "";
		String llave = "";
		try {
			if (!Func.isVaciaCadena(nss)) {
                llave = " NSS = '" + nss + "'";
			} else {
				llave = " CURP = '" + curp + "'";
			}
			
			valor = this.TraeRegistro("COUNT(*)", "TDATOSCLIENTES", " WHERE " + llave);
		 //System.out.println("valor "+valor);
			if (!valor.equals("0")) {
				return true;
			}
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
		return false;
	}
	
	/**
	 * Este metodo aplica la siguiente regla:
	 * 3. Validar si ya se tiene una domiciliacion activa, 
	 * 	esta validacion se realiza contra la tabla de domiciliaciones, 
	 *  esta tabla debe estar poblada con la carga inicial y la regla a 
	 *  validar consistira en buscar el NSS/CURP y el estatus de la domiciliacion debe ser ACTIVA
	 * @param nss Recibe el NSS (es llave de consulta)
	 * @param curp Recibe el CURP (es llave de consulta)
	 * @return
	 * Regresa 0 cuando no existe registro
	 * Regresa Regresara el valor de la ultima transacción vigente
	 */
	public String buscaIdSolicitudExistente(String nss, String curp) {
		String valor = "0";
		String llave = "";
		try {
			if (!Func.isVaciaCadena(nss)) {
                llave = " AND NSS = '" + nss + "'";
			} else {
				llave = " AND CURP = '" + curp + "'";
			}
			String where = " WHERE ESTATUSDOMI = '" 
						 + this.ESTATUSDOMI_SICI_ACTIVO
						 + "' "
						 + "AND TRANSACCION IN(1,2,3)"
						 + llave
						 + " ORDER BY FECCARGA DESC";
			
			valor = this.TraeRegistro("IDSOLDOMICILIACION", "TSOLDOMICILIACION", where);
			System.out.println("Solicitud anterior: "+valor);
			
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
		return valor;
	}
	
	
	
	/**
	 * Este metodo aplica la siguiente regla:
	 * 3. Validar si ya se tiene una domiciliacion activa, 
	 * 	esta validacion se realiza contra la tabla de domiciliaciones, 
	 *  esta tabla debe estar poblada con la carga inicial y la regla a 
	 *  validar consistira en buscar el NSS/CURP y el estatus de la domiciliacion debe ser ACTIVA
	 * @param nss Recibe el NSS (es llave de consulta)
	 * @param curp Recibe el CURP (es llave de consulta)
	 * @return
	 * Regresa 0 cuando no existe registro
	 * Regresa Regresara el valor de la ultima transacción vigente
	 */
	public String validarExistenciaSolicitud(String nss, String curp) {
		String valor = "0";
		String llave = "";
		try {
			if (!Func.isVaciaCadena(nss)) {
                llave = " AND NSS = '" + nss + "'";
			} else {
				llave = " AND CURP = '" + curp + "'";
			}
			String where = " WHERE ESTATUSDOMI = '" 
						 + this.ESTATUSDOMI_SICI_ACTIVO
						 + "' " 
						 + llave
						 + " ORDER BY FECCARGA DESC";
			
			valor = this.TraeRegistro("COUNT(*)", "TSOLDOMICILIACION", where);
			if (!valor.equals("0")) {
				valor = this.TraeRegistro("TRANSACCION", "TSOLDOMICILIACION", where);
				return valor;
			}
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
		return valor;
	}
	
	/**
	 * Este metodo aplica la siguiente regla:
	 * 3. Validar si ya existe la solicitud para sici, ya que los archivos
	 * pueden contener solicitudes de días anteriores
	 * y estas solicitudes ya no es necesario cargarlas
	 * @param foliosolicitud
	 * @return
	 * Regresa true cuando ya existe la solicitud
	 * Regresa false cuando no existe la solicitud
	 */
	public boolean validarFolioSolicitudDuplicada(String folio) {
		String valor = "0";
		String llave = "";
		boolean existe = false;
		try {
			
			String where = " WHERE FOLIOESAR = " 
						 + folio;
			
			valor = this.TraeRegistro("COUNT(*)", "TSOLDOMICILIACION", where);
			if(!valor.equals("0")){
				existe = true;
			}
			
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
		return existe;
	}	
	
	public boolean validarExistenciaDomicilios(String nss, String curp) {
		boolean banderaCorreo = true;
		boolean banderaCalle = true;
		String campos = " NVL(CORREOELECTRONICO, ' ') AS CORREOELECTRONICO,"
				+ " NVL(CALLE, ' ') AS CALLE,"
				+ " NVL(COLONIA, ' ') AS COLONIA,"
				+ " NVL(CP, ' ') AS CP,"
				+ " NVL(MUNICIPIO, ' ') AS MUNICIPIO,"
				+ " NVL(ESTADO, ' ') AS ESTADO";
		String llave = "";
		String [][] resultados = null;
		try {
			if (!Func.isVaciaCadena(nss)) {
		        llave = " WHERE NSS = '" + nss + "'";
			} else {
				llave = " WHERE CURP = '" + curp + "'";
			}
			
			resultados = bdconn.Lista(campos, "TDATOSCLIENTES", llave);
			if (resultados != null)	{
				for(int r = 1; r<resultados.length; r++){
					String[] registro = resultados[r];
					
					if ((Func.isVaciaCadena(registro[0]))) {
					banderaCorreo = false;
					}
					
					if (
							Func.isVaciaCadena(registro[1]) &&
							Func.isVaciaCadena(registro[2]) &&
							Func.isVaciaCadena(registro[3]) &&
							Func.isVaciaCadena(registro[4]) &&
							Func.isVaciaCadena(registro[5])) {
					banderaCalle = false;
					}
				}
			}
			
			
			if ((banderaCalle==true)||(banderaCorreo==true)) {
				return true;
			}
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
		return false;		
	}
}