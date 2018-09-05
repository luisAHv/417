package com.afbanamex.GestionCorrespondencia;

import java.awt.*;
import java.awt.event.*;
import java.io.EOFException;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.afbanamex.utilerias.*;
//import com.borland.jbcl.layout.*;
import com.jonima.utils.guiclient.*;
//import com.afbanamex.general.clientshell.*;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

import com.afbanamex.utilerias.calendario.RCalendarField;

/**
 * <p>Title: HistoricosRetiroParcial</p>
 * <p>Description: Modulo para consultar datos de Retiros parciales</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Afore Banamex</p>
 * @author DOC
 * @version 1.0
 */

public class frmHistoricosRetiroParcial extends JInternalFrame implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	/** Variable indicador para evitar que los combos efectúen la función de cambio de estado cuando se están llenando por primera vez */
    String wListsReady;
    private int sort = 1;//1 Asc, 2 Desc
    ContextoAplicacion capp = ContextoAplicacion.instancia();
    JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem();
    //Para facultades
//    DataSvcAccess svcShell = new DataSvcAccess();
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
    String vSQL;
    /** Controles de Calendario */
    Calendar date = Calendar.getInstance();
    RCalendarField CbFechaCarga1= new RCalendarField();    
    RCalendarField CbFechaCarga2= new RCalendarField();
    SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
    String busqueda; //Enviado o Capturado
    String busqueda2;

//    XYConstraints restricciones1 = new XYConstraints();

    JPanel jPanel1 = new JPanel();
//    XYLayout xYLayout1 = new XYLayout();
    JPanel jPanel2 = new JPanel();
    Border border1;
    JLabel lblConsultaFechaIni = new JLabel();
    JLabel lblConsultaFechaFin = new JLabel();
//    XYLayout xYLayout2 = new XYLayout();
    JPanel jPanel3 = new JPanel();
    Border border2;
//    XYLayout xYLayout3 = new XYLayout();

    private JButton btnBuscarFecha = new JButton();
//    private JButton btnSalir = new JButton();
    private JButton btnOrdenarTran = new JButton();
    private JButton btnOrdenarFec = new JButton();

    JTable table = null;
    DefaultTableModel dtm = null;
    
    private frmGenConsultasModif modif = null;

    public frmHistoricosRetiroParcial() { 
    	enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    }

    void jbInit() throws Exception {
        border1 = new EtchedBorder(EtchedBorder.RAISED, Color.white,new Color(148, 145, 140));
        border2 = BorderFactory.createEtchedBorder(Color.white,new Color(148, 145, 140));
        jPanel1.setDebugGraphicsOptions(0);
        jPanel1.setLayout(new FlowLayout());
        jPanel1.setPreferredSize(new Dimension(1000,550));
        jPanel2.setBorder(border1);
        jPanel2.setLayout(new FlowLayout());
        jPanel2.setPreferredSize(new Dimension(990,60));
        

        lblConsultaFechaIni.setText("Fechas Inicial");
        lblConsultaFechaFin.setText("Fechas Fin");
        jPanel3.setBorder(border2);
        jPanel3.setLayout(new FlowLayout());
        jPanel3.setPreferredSize(new Dimension(990,480));
        

        btnBuscarFecha.setText("Buscar");
//        btnSalir.setText("Salir");
        btnOrdenarTran.setText("Ordenar Estatus");        
        btnOrdenarFec.setText("Ordenar por Fecha");
        btnOrdenarFec.setVisible(false);
        btnOrdenarTran.setVisible(false);
        this.setTitle("Consulta de Domiciliación");
        this.setSize(new Dimension(1000, 500));
        this.setClosable(true);      
        this.getContentPane().add(jPanel1, BorderLayout.CENTER);
        jPanel1.add(jPanel2);
        lblConsultaFechaIni.setSize(new Dimension(130, 20));
        jPanel2.add(lblConsultaFechaIni);   
        lblConsultaFechaFin.setSize(new Dimension(130, 20));
        jPanel2.add(lblConsultaFechaFin);
        CbFechaCarga1.setSize(new Dimension(150, 20));
        jPanel2.add(CbFechaCarga1);
        CbFechaCarga2.setSize(new Dimension(150, 20));
        jPanel2.add(CbFechaCarga2);
        btnBuscarFecha.setSize(new Dimension(93, 20));
        jPanel2.add(btnBuscarFecha);   
        
        jPanel1.add(jPanel3);
//        btnSalir.setSize(new Dimension(93, 27));
//        jPanel2.add(btnSalir);
        btnOrdenarTran.setSize(new Dimension(120, 27));     
        jPanel2.add(btnOrdenarTran);
        btnOrdenarTran.setSize(new Dimension(170, 27));
        jPanel2.add(btnOrdenarFec);
        

        btnBuscarFecha.setActionCommand("BuscarFecha");
        btnBuscarFecha.addActionListener(this);
        btnOrdenarTran.setActionCommand("ordT");
        btnOrdenarTran.addActionListener(this);
        btnOrdenarFec.setActionCommand("ordF");
        btnOrdenarFec.addActionListener(this);
        
//        btnSalir.addActionListener(new frmHistoricosRetiroParcial_btnSalir_actionAdapter(this));
        
        table = new JTable();
        dtm = new DefaultTableModel(0,10);
        String header[] = new String[] {"ID_HISTORICO","HISTO_OPERACION20","HISTO_URLARCHIVO20",
        		                        "HISTO_FEOPE20","HISTO_DERIVADA164","HISTO_FEDER164",
        		                        "HISTO_DERIVADA071","HISTO_FEDER071","HISTO_OPERACION85",
        		                        "HISTO_URLARCHIVO85","HISTO_FEOPE85"};
        dtm.setColumnIdentifiers(header);
        table.setModel(dtm);
        table.setRowSorter(new TableRowSorter<TableModel>(table.getModel()));

        final JPopupMenu popupMenu = new JPopupMenu();
        
        JMenuItem deleteItem = new JMenuItem("Borrar");
        deleteItem.setActionCommand("DeleteData");
        deleteItem.addActionListener(this);
        
        JMenuItem updateItem = new JMenuItem("Modificar");
        updateItem.setActionCommand("UpdateData");
        updateItem.addActionListener(this);
        
        JMenuItem detailItem = new JMenuItem("Ver Detalle");
        detailItem.setActionCommand("DetailData");
        detailItem.addActionListener(this);
        
        popupMenu.add(deleteItem);
        popupMenu.add(updateItem);
        popupMenu.add(detailItem);
        table.setComponentPopupMenu(popupMenu);
        table.addMouseListener(new TableMouseListener2(table));

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(120);  //ID_HISTORICO
        columnModel.getColumn(1).setPreferredWidth(95);  //HISTO_OPERACION20
        columnModel.getColumn(2).setPreferredWidth(140); //HISTO_URLARCHIVO20
        columnModel.getColumn(3).setPreferredWidth(80);  //HISTO_FEOPE20
        columnModel.getColumn(4).setPreferredWidth(85);  //HISTO_DERIVADA164
        columnModel.getColumn(5).setPreferredWidth(100);  //HISTO_FEDER164
        columnModel.getColumn(6).setPreferredWidth(60);  //HISTO_DERIVADA071
        columnModel.getColumn(7).setPreferredWidth(110); //HISTO_FEDER071
        columnModel.getColumn(8).setPreferredWidth(60);  //HISTO_OPERACION85
        columnModel.getColumn(9).setPreferredWidth(65);  //HISTO_URLARCHIVO85
        columnModel.getColumn(10).setPreferredWidth(60); //HISTO_FEOPE85
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        
        table.getRowSorter().setSortKeys(Collections.singletonList(new RowSorter.SortKey(0, SortOrder.ASCENDING)));
        table.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        		JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(950, 400));
//        table.setLayout(new FlowLayout());
//        table.setSize(new Dimension(990, 400));
       
        jPanel3.add(scrollPane);
    }

    public void start(int int0, int int1) {
    }

    public void startInternal(String title) {
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
     * actionPerformed
     *
     * @param e ActionEvent
     */
    public void actionPerformed(ActionEvent e) {
//        if (e.getSource() == btnSalir) {
//            cancel();
//        }
        if (e.getActionCommand().equalsIgnoreCase("Buscar")){
        	table.getRowSorter().setSortKeys(Collections.singletonList(new RowSorter.SortKey(0, SortOrder.UNSORTED)));
//        	buscar();
        	return;
        }
        else if (e.getActionCommand().equalsIgnoreCase("BuscarFecha")){
        	table.getRowSorter().setSortKeys(Collections.singletonList(new RowSorter.SortKey(0, SortOrder.UNSORTED)));
        	buscarfecha();
        	return;
        }
        	
        int selrow = table.getSelectedRow();        
         
        if(e.getActionCommand().equalsIgnoreCase("DeleteData")){
        	int resp = JOptionPane.showConfirmDialog(table, "¿Eliminar elemento seleccionado?");
            if(resp==0)
            	deleteFile(selrow);
        }
        
        if(e.getActionCommand().equalsIgnoreCase("UpdateData")){
        	updateData(selrow, getRow(selrow));
    
        }
        if(e.getActionCommand().equalsIgnoreCase("DetailData")){
        	showDetails(selrow);
        }
        
      

    }

    private String[] getRow(int selrow){
    	
    	String[] data = new String[17];
    	data[0]= isNull(dtm.getValueAt(selrow, 0)).toString().trim();   //ID_HISTORICO
    	data[1]= isNull(dtm.getValueAt(selrow, 1)).toString().trim();   //HISTO_OPERACION20
    	data[2]= isNull(dtm.getValueAt(selrow, 2)).toString().trim();   //HISTO_URLARCHIVO20
    	data[3]= isNull(dtm.getValueAt(selrow, 3)).toString().trim();   //HISTO_FEOPE20
    	data[4]= isNull(dtm.getValueAt(selrow, 4)).toString().trim();   //HISTO_DERIVADA164
    	data[5]= isNull(dtm.getValueAt(selrow, 5)).toString().trim();   //HISTO_FEDER164
    	data[6]= isNull(dtm.getValueAt(selrow, 6)).toString().trim();   //HISTO_DERIVADA071
    	data[7]= isNull(dtm.getValueAt(selrow, 7)).toString().trim();   //HISTO_FEDER071
    	data[8]= isNull(dtm.getValueAt(selrow, 8)).toString().trim();   //HISTO_OPERACION85
    	data[9]= isNull(dtm.getValueAt(selrow, 9)).toString().trim();   //HISTO_URLARCHIVO85
    	data[10]= isNull(dtm.getValueAt(selrow, 10)).toString().trim(); //HISTO_FEOPE85
    	
    	return data;
    }
    
    
    private void updateData(int selrow, String[] data) {
    	modif = null;
    	modif = new frmGenConsultasModif(capp, dtm.getValueAt(selrow, 0).toString().trim(), data);
    	modif.startInternal("Modificación del Registro ID " + dtm.getValueAt(selrow, 0).toString().trim());
	}

	private void ordenar(int col){
    	SortOrder sortOrder = sort==1?SortOrder.ASCENDING:SortOrder.DESCENDING;
    	sort = sort==1?2:1;
    	table.getRowSorter().setSortKeys(Collections.singletonList(new RowSorter.SortKey(col, sortOrder)));
        table.getTableHeader().setReorderingAllowed(false);
    }

  
    
    private void buscarfecha() {
    	   	
    	consultafecha();
    }
    
    private void actualizarvobo() {
    if (!busqueda.equalsIgnoreCase("")) {
    	
    	if(busqueda.matches("[0-9]*")){
    		vSQL="";
    		vSQL= "UPDATE TSOLDOMICILIACION SET VOBO = 'SI',FECULTACTUALIZACION = TO_DATE(SYSDATE,'DD/MM/YYYY'),ESTATUSDOMI='ACTIVA' WHERE NSS = '" + busqueda.trim() + "'";
    		
    		bdconn.Ejecuta(vSQL);
    		
    		vSQL="";
    		vSQL= "UPDATE TSOLDOMICILIACION SET TRANSACCION = 1 " +   
    	    		  "WHERE NSS = '" + busqueda + "' AND TRANSACCION = 5";
    		
    		bdconn.Ejecuta(vSQL);
    		
    		vSQL="";
    		vSQL= "UPDATE TSOLDOMICILIACION SET TRANSACCION = 2 " +   
  	    		  "WHERE NSS = '" + busqueda + "' AND TRANSACCION = 6";
  		
    		bdconn.Ejecuta(vSQL);
    		
    		vSQL="";
    		vSQL= "UPDATE TSOLDOMICILIACION SET TRANSACCION = 3 " +   
    	    		  "WHERE NSS = '" + busqueda + "' AND TRANSACCION = 7";
    		
      		bdconn.Ejecuta(vSQL);
  		
      		vSQL="";
      		vSQL= "UPDATE TSOLDOMICILIACION SET TRANSACCION = 4 " +   
    	    		  "WHERE NSS = '" + busqueda + "' AND TRANSACCION = 8";
    		
      		bdconn.Ejecuta(vSQL);
      		
    		System.out.println("Actualizacion Vobo NSS:--> ");
    		    		
    	}
    	else{
    		vSQL="";
    		vSQL= "UPDATE TSOLDOMICILIACION SET VOBO = 'SI',FECULTACTUALIZACION = TO_DATE(SYSDATE,'DD/MM/YYYY'),ESTATUSDOMI='ACTIVA' " +
        	      "WHERE CURP = '" + busqueda.trim() + "'";
    	    		
    		bdconn.Ejecuta(vSQL);
    		
    		vSQL="";
    		vSQL= "UPDATE TSOLDOMICILIACION SET TRANSACCION = 1 " +   
  	    		  "WHERE CURP = '" + busqueda + "' AND TRANSACCION = 5";
    		
    		bdconn.Ejecuta(vSQL);
    		
    		vSQL="";
    		vSQL= "UPDATE TSOLDOMICILIACION SET TRANSACCION = 2 " +   
    	    		  "WHERE CURP = '" + busqueda + "' AND TRANSACCION = 6";
      		
      		bdconn.Ejecuta(vSQL);
      		
      		vSQL="";
      		vSQL= "UPDATE TSOLDOMICILIACION SET TRANSACCION = 3 " +   
  	    		  "WHERE CURP = '" + busqueda + "' AND TRANSACCION = 7";
    		
    		bdconn.Ejecuta(vSQL);
    	
    		vSQL="";
    		vSQL= "UPDATE TSOLDOMICILIACION SET TRANSACCION = 4 " +   
  	    		  "WHERE CURP = '" + busqueda + "' AND TRANSACCION = 8";
    		
    		bdconn.Ejecuta(vSQL);
    		
    		System.out.println("Actualizacion Vobo CURP:--> ");
    		
    	}
    }
    //FECHAS
    else{    	
    	vSQL="";
    	vSQL= "UPDATE TSOLDOMICILIACION SET VOBO = 'SI',FECULTACTUALIZACION = TO_DATE(SYSDATE,'DD/MM/YYYY'),ESTATUSDOMI='ACTIVA' " +
    	      "WHERE FECCARGA BETWEEN TO_DATE('" + CbFechaCarga1.getText() + "','DD/MM/YYYY') AND TO_DATE('" + CbFechaCarga2.getText() + "','DD/MM/YYYY')";

    	bdconn.Ejecuta(vSQL);
    	
    	vSQL="";
    	vSQL= "UPDATE TSOLDOMICILIACION SET TRANSACCION = 1 " +   
    		  "WHERE FECCARGA BETWEEN TO_DATE('" + CbFechaCarga1.getText() + "','DD/MM/YYYY') AND TO_DATE('" + CbFechaCarga2.getText() + "','DD/MM/YYYY') AND TRANSACCION = 5";
    	
    	bdconn.Ejecuta(vSQL);
    	
    	vSQL="";
    	vSQL= "UPDATE TSOLDOMICILIACION SET TRANSACCION = 2 " +   
      		  "WHERE FECCARGA BETWEEN TO_DATE('" + CbFechaCarga1.getText() + "','DD/MM/YYYY') AND TO_DATE('" + CbFechaCarga2.getText() + "','DD/MM/YYYY') AND TRANSACCION = 6";    	    	

      	bdconn.Ejecuta(vSQL);
      	
      	vSQL="";
      	vSQL= "UPDATE TSOLDOMICILIACION SET TRANSACCION = 3 " +   
        	  "WHERE FECCARGA BETWEEN TO_DATE('" + CbFechaCarga1.getText() + "','DD/MM/YYYY') AND TO_DATE('" + CbFechaCarga2.getText() + "','DD/MM/YYYY') AND TRANSACCION = 7";    	    	

      	bdconn.Ejecuta(vSQL);
      	
      	vSQL="";
      	vSQL= "UPDATE TSOLDOMICILIACION SET TRANSACCION = 4 " +   
      		  "WHERE FECCARGA BETWEEN TO_DATE('" + CbFechaCarga1.getText() + "','DD/MM/YYYY') AND TO_DATE('" + CbFechaCarga2.getText() + "','DD/MM/YYYY') AND TRANSACCION = 8";    	    	
    	
      	bdconn.Ejecuta(vSQL);
      	
     	
    	}   
    }

//    void btnSalir_actionPerformed(ActionEvent e) {
//        if (e.getSource() == btnSalir) {
//            cancel();
//        }
//
//    }

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

    private void consulta(){
    	
    	
    	BDConnect bdconn = new BDConnect();
    	String [][] lista = null;
//    	String consulta = txtConsulta.getText().trim();
    	String campos = "IDSOLDOMICILIACION,NVL(NSS,' '),NVL(CURP,' ') AS CURP,NVL(ESTATUSDOMI,' ') AS ESTATUSDOMI,TRANSACCION,TIPOCUENTA,NVL(BANCO,' '),CUENTA,NVL(SUCURSAL,0) AS SUCURSAL,PERIODICIDAD,NVL(MOTIVO,0) AS MOTIVO,ORIGEN,VOBO,NVL(FOLIOESAR,' '),MONTO,FECCARGA,DIAGNOSTICO"; 
    	try{
   
    		
    			lista = bdconn.Lista(campos,"TSOLDOMICILIACION", " WHERE CURP = 'luis'");
    		    		
    		    		
    		
    	} catch(Exception ex){
    		System.out.println(ex.getMessage());
    		JOptionPane.showMessageDialog(jPanel2, "Error al realizar la consulta", "Error", 3);
    	}
    	
    	
    	if (lista==null || lista.length<=1){
    		JOptionPane.showMessageDialog(jPanel1, "No se encontró registro", "Registro no encontrado", 1);
    		return;
    	}  
    	//Limpia El jTable desde el DefaultTableModel
    	dtm.setRowCount(0);
    	NumberFormat numberFormat = new DecimalFormat("#0.00");
    	
    	
    	for(int i = 1; i<lista.length; i++){    		
    		String[] row = lista[i];
    		try {
    			String transaccion = clUtil.TraeRegistro("DESCRIPCION", "TCATTIPOTRANSACCION"," WHERE CLAVE = '"+isNull(row[4])+"'");
        		String tipoCuenta = clUtil.TraeRegistro("DESCRIPCION", "TCATTIPOCUENTA"," WHERE CLAVE = '"+isNull(row[5])+"'");
        		String periodicidad = clUtil.TraeRegistro("DESCRIPCION", "TCATPERIODICIDAD"," WHERE CLAVE = '"+isNull(row[9])+"'");
        		String motivoRechazo = clUtil.TraeRegistro("DESCRIPCION", "TCATMOTRECHAZO"," WHERE CLAVE = '"+isNull(row[10])+"'");
        		if(motivoRechazo=="ACEPTADO"){
        			System.out.println(motivoRechazo);
        			motivoRechazo="";
        		}
        			
        		String origen = clUtil.TraeRegistro("DESCRIPCION", "TCATORIGEN"," WHERE CLAVE = '"+isNull(row[11])+"'");
        		
        		dtm.addRow(new Object[]{row[0], row[1], row[2], row[3],transaccion,tipoCuenta,row[6], row[7],
    				   row[8], periodicidad, motivoRechazo,origen, row[12], row[13],numberFormat.format(row[14].equals("null")?0.00:Double.parseDouble(row[14])),row[15].substring(0,10),isNull(row[16])});	
    		} catch (Exception e) {
				e.printStackTrace();
			}
    		
    	}
    }

    private void consultafecha(){
    	BDConnect bdconn = new BDConnect();
    	String [][] lista = null;
    	String consulta1 = CbFechaCarga1.getText();
    	String consulta2 = CbFechaCarga2.getText();
    	String campos = "IDSOLDOMICILIACION,NVL(NSS,' '),NVL(CURP,' ') AS CURP,NVL(ESTATUSDOMI,' ') AS ESTATUSDOMI,TRANSACCION,TIPOCUENTA,NVL(BANCO,' '),CUENTA,NVL(SUCURSAL,0) AS SUCURSAL,PERIODICIDAD,NVL(MOTIVO,0) AS MOTIVO,ORIGEN,VOBO,NVL(FOLIOESAR,' '),MONTO,FECCARGA,DIAGNOSTICO"; 
    	try{
    			String alterFormat = "ALTER SESSION SET NLS_DATE_FORMAT = 'DD/MM/YYYY'";
    			bdconn.Ejecuta(alterFormat);
    			lista = bdconn.Lista(campos,"TSOLDOMICILIACION", " WHERE to_date(FECCARGA, 'dd/mm/yyyy') BETWEEN TO_DATE('" + consulta1 + "','dd/mm/yyyy') AND TO_DATE('" + consulta2 + "','dd/mm/yyyy')"); 
    		
    	} catch(Exception ex){
    		System.out.println(ex.getMessage());
    		JOptionPane.showMessageDialog(jPanel2, "Error al realizar la consulta", "Error", 3);
    	}
    	int rowCount = dtm.getRowCount();
    
    	if (lista==null || lista.length<=1){
    		JOptionPane.showMessageDialog(jPanel1, "No se encontró registro", "Registro no encontrado", 1);
    		return;
    	}    	
    	
    	//Limpia El jTable desde el DefaultTableModel
    	dtm.setRowCount(0);
    	NumberFormat numberFormat = new DecimalFormat("#0.00");
    	
    	for(int i = 1; i<lista.length; i++){    		
    		String[] row = lista[i];
    		try {
    			String transaccion = clUtil.TraeRegistro("DESCRIPCION", "TCATTIPOTRANSACCION"," WHERE CLAVE = '"+isNull(row[4])+"'");
        		String tipoCuenta = clUtil.TraeRegistro("DESCRIPCION", "TCATTIPOCUENTA"," WHERE CLAVE = '"+isNull(row[5])+"'");
        		String periodicidad = clUtil.TraeRegistro("DESCRIPCION", "TCATPERIODICIDAD"," WHERE CLAVE = '"+isNull(row[9])+"'");
        		String motivoRechazo = clUtil.TraeRegistro("DESCRIPCION", "TCATMOTRECHAZO"," WHERE CLAVE = '"+isNull(row[10])+"'");
        		String origen = clUtil.TraeRegistro("DESCRIPCION", "TCATORIGEN"," WHERE CLAVE = '"+isNull(row[11])+"'");
        		
        		dtm.addRow(new Object[]{row[0], row[1], row[2], row[3],transaccion,tipoCuenta,row[6], row[7],
    				   row[8], periodicidad, motivoRechazo,origen, row[12], row[13],numberFormat.format(row[14].equals("null")?0.00:Double.parseDouble(row[14])),row[15].substring(0,10),isNull(row[16])});	
    		} catch (Exception e) {
				e.printStackTrace();
			}
    		
    	}
    }
   
    
    private frmGenConsultasDetail detalle = null;
    
    private void showDetails(int selrow){
    	detalle = null;
    	detalle = new frmGenConsultasDetail(capp, dtm.getValueAt(selrow, 0).toString().trim(),dtm.getValueAt(selrow, 11).toString().trim());
    	detalle.startInternal("Detalle del registro ID " + dtm.getValueAt(selrow, 0).toString().trim());
    }
    
    public String isNull(Object object){
    	if (object== null||object.toString().equalsIgnoreCase("null")) {
    		return "";
    	}
    	return object.toString();
    }
    
    private void deleteFile(int selrow){   	
    	String Origen = dtm.getValueAt(selrow, 11).toString();
    	String Detalle;
    	System.out.println("Origen:"+Origen); 
    	
    	if (Origen.equals("1")) {
    		Detalle = "Detalle eSar";
    		bdconn.Ejecuta("DELETE FROM TSDOMIDETESAR WHERE IDSOLDOMICILIACION = " + dtm.getValueAt(selrow, 0).toString().trim());
    	}
    	else{
    		Detalle = "Detalle Bancanet";
    		bdconn.Ejecuta("DELETE FROM TSDOMIDETBANCANET WHERE IDSOLDOMICILIACION = " + dtm.getValueAt(selrow, 0).toString().trim());
    	}
    		
    	bdconn.Ejecuta("DELETE FROM TSOLDOMICILIACION WHERE IDSOLDOMICILIACION = " + dtm.getValueAt(selrow, 0).toString().trim());
    	
    	dtm.removeRow(selrow);
    	JOptionPane.showMessageDialog(jPanel1, "Registro Eliminado de Domiciliación y " + Detalle, "Registros Eliminados", 1);
    }

    
}

//class frmHistoricosRetiroParcial_btnSalir_actionAdapter
//        implements java.awt.event.ActionListener {
//	frmHistoricosRetiroParcial adaptee;
//
//    frmHistoricosRetiroParcial_btnSalir_actionAdapter(frmHistoricosRetiroParcial frmHistoricosRetiroParcial) {
//        this.adaptee = frmHistoricosRetiroParcial;
//    }
//
//    public void actionPerformed(ActionEvent e) {
//        adaptee.btnSalir_actionPerformed(e);
//    }
//}

class TableMouseListener2 extends MouseAdapter {
    
    private JTable table;
     
    public TableMouseListener2(JTable table) {
        this.table = table;
    }
     
    @Override
    public void mousePressed(MouseEvent event) {
        // selects the row at which point the mouse is clicked
        Point point = event.getPoint();
        int currentRow = table.rowAtPoint(point);
        table.setRowSelectionInterval(currentRow, currentRow);
    }
}
