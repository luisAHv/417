package com.afbanamex.GestionCorrespondencia;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
//import java.util.Collections;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
//import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
//import javax.swing.JPopupMenu;
//import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
//import javax.swing.RowSorter;
//import javax.swing.SortOrder;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
//import javax.swing.table.TableColumnModel;
//import javax.swing.table.TableModel;
//import javax.swing.table.TableRowSorter;
//import javax.swing.table.TableColumnModel;
import javax.swing.table.TableColumnModel;

import com.jonima.utils.guiclient.ContextoAplicacion;

public class frmDerivada164RetiroParcial extends JInternalFrame implements ActionListener {

	 /**
	 * 
	 */
	 private static final long serialVersionUID = 1L;
	 String wtitle;
	 String wListsReady;
	 ContextoAplicacion capp = ContextoAplicacion.instancia();
	 
	 //Componentes
	 JPanel jPanel1 = new JPanel();
	 JPanel jPanel2 = new JPanel();
	 JPanel jPanel3 = new JPanel();
	 Border border1;
	 Border border2;
	 JLabel lblruta = new JLabel();

	 JTextField ruta=new JTextField(30);
	 private JButton btnCargarArchivo = new JButton();

	 JTable table = null;
	 DefaultTableModel dtm = null;
	 
	
	 public frmDerivada164RetiroParcial() { 
	 }
	 
	 
	
	public void startInternal(String string) {
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
	
	
	void jbInit() throws Exception {
		border1 = new EtchedBorder(EtchedBorder.RAISED, Color.white,new Color(148, 145, 140));
        border2 = BorderFactory.createEtchedBorder(Color.white,new Color(148, 145, 140));
        
        jPanel1.setDebugGraphicsOptions(0);
        jPanel1.setLayout(new FlowLayout());
        jPanel1.setPreferredSize(new Dimension(400,550));
        
        jPanel2.setBorder(border1);
        jPanel2.setLayout(new FlowLayout());
        jPanel2.setPreferredSize(new Dimension(390,60));

        jPanel3.setBorder(border2);
        jPanel3.setLayout(new FlowLayout());
        jPanel3.setPreferredSize(new Dimension(390,480));
        
        
        this.setTitle("Derivada 164");
        this.setSize(new Dimension(400, 500));
        
        
        jPanel1.add(jPanel2);
        lblruta.setText("Seleccionar Archivo");
        lblruta.setSize(new Dimension(130, 20));
        jPanel2.add(lblruta);
        ruta.setText("");
        ruta.setSize(new Dimension(490,20));
        jPanel2.add(ruta);
        
        btnCargarArchivo.setText("Seleccionar...");
        btnCargarArchivo.setSize(new Dimension(490,20));
        jPanel2.add(btnCargarArchivo);
        
        jPanel1.add(jPanel3);
        
        this.setClosable(true);      
        this.getContentPane().add(jPanel1, BorderLayout.CENTER);
        
        btnCargarArchivo.setActionCommand("Seleccionar...");
        btnCargarArchivo.addActionListener(this);
        
        
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(120);  //ID_DER164
        columnModel.getColumn(1).setPreferredWidth(120);  //DER164_NSS
        columnModel.getColumn(2).setPreferredWidth(120); //DER164_CURP
        columnModel.getColumn(3).setPreferredWidth(80); //DER164_NRESO
        
        table.getRowSorter().setSortKeys(Collections.singletonList(new RowSorter.SortKey(0, SortOrder.ASCENDING)));
        table.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
        		JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(950, 400));

       
        jPanel3.add(scrollPane);

	        
	}
	//Metodo para elegir archivo
	@SuppressWarnings("deprecation")
	public void ElejirArchivo(){
		
		JFileChooser fc=new JFileChooser();

        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

        FileFilter filtro = new FileNameExtensionFilter("Archivo .txt", "txt");

        fc.setFileFilter(filtro);
        

        int seleccion=fc.showOpenDialog(getContentPane());

        if(seleccion==JFileChooser.APPROVE_OPTION){

            File fichero=fc.getSelectedFile();

            String rutaArchivo=fichero.getAbsolutePath();
            
            ruta.disable();
            ruta.setText(rutaArchivo);
                       
            obtenerDatos(rutaArchivo);
        }
		
	}
	
	//obtener Datos de Un archivo
	public void obtenerDatos(String archivoTxt){

	        BufferedReader br = null;
	        String line = "";
	        int lineas=0;

	            try {
	                br = new BufferedReader(new FileReader(archivoTxt));
	                while ((line = br.readLine()) != null) {                
	                    String[] datos = line.split(";");
	                    if(line.equals("")){
	                    	System.out.println("Archivo vacio");
	                    }
	                   System.out.println(datos[0] + ", " + datos[1] + ", " + datos[2] + ", " + datos[3]);
	                   lineas++;
	                }
	                System.out.println("lineas-->"+lineas);
	            }catch (FileNotFoundException e) {
	                e.printStackTrace();
	            }catch (IOException e) {
	                e.printStackTrace();
	            }finally{
	                if (br != null){
	                    try {
	                        br.close();
	                    }catch (IOException e){
	                        e.printStackTrace();
	                    }
	                }
	            }
	    
	}
	/***Mis Eventos***/
	 @Override
		public void actionPerformed(ActionEvent e) {

		 String ab = e.getActionCommand();
		 
		 if (ab.equals("Seleccionar...")){
	        	
	        	ElejirArchivo();
	        }
		}

}
