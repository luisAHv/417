package com.afbanamex.utilerias;

import java.sql.*;
import com.jonima.utils.server.*;
import com.jonima.utils.guiclient.*;
import javax.naming.*;
import java.util.*;

import com.jonima.utils.*;
import javax.rmi.*;
import java.awt.Component;
import java.io.*;
import javax.swing.*;
import javax.swing.event.EventListenerList;

import com.afbanamex.appcorreo.server.*;


/**
 * <p>Title: Clase BDConnect</p>
 * <p>Description: Esta clase incluye todas las rutinas para conexión a la base de datos y ejecución de statements</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Afore banamex</p>
 * @author: Jahaziel Crisanto
 * @version 1.0
 */

public class BDConnect {
    private ServerEntry server = null;
    private static final int MAX_RETRIES = 10;
    OC4JLogger logger = new OC4JLogger(15);
    String JNDIName = "ejb/afbanamex/appcorreo/server/ServerEntry";
    ContextoAplicacion capp = ContextoAplicacion.instancia();

    public BDConnect() {
    }

    /** Esta rutina se encarga de la conexión mediante JDBC thin client a la base de datos de Oracle */
    /*static Connection BDConnect() throws SQLException, ClassNotFoundException,
            IllegalAccessException, InstantiationException {
      Connection conn = null;
      Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
      conn = DriverManager.getConnection("jdbc:oracle:thin:@afore2:1524:SIG",
                                         "CONTACTMGB", "CONTACTMGB");
      return conn;
       }*/

    /** Rutina para cerrar la conexión a la base de datos de Oracle */
    /*public static void BDClose() throws ClassNotFoundException, SQLException,
        InstantiationException, IllegalAccessException, ClassNotFoundException,
        SQLException {
      BDConnect().close();
       }*/

    /** Rutina para ejecución de sentencias SQL */
    /* public static void BDExecute(String sSQL) throws InstantiationException,
         IllegalAccessException, ClassNotFoundException, SQLException {
       Statement stmt = BDConnect().createStatement();
       stmt.executeQuery(sSQL);
        }*/

    /**
     * Obtener contexto inicial
     * @return
     * @throws java.lang.Exception
     */
    private Context getInitialContext(String appName) throws Exception {

        Hashtable env = new Hashtable();

        Properties props = (Properties) capp.getValue("properties");
        String commMethod = props.getProperty("USEIIOP", "0");
        String protocolo = "";
        String hostName = props.getProperty("IPServer");
        String hostPort = props.getProperty("JNDIPort");
        String useURL = "t3s://"+ hostName + ":" + hostPort + "/" + appName;
        String userCode = "";
        RecuperaAmbienteLoc ra = new RecuperaAmbienteLoc();
        userCode = ra.dameAmbiente();
        // commMethod = "0";
        if (commMethod.equals("0")) {
        	protocolo = "t3://";
    		useURL = protocolo + hostName + ":" + hostPort;
            env.put(javax.naming.Context.PROVIDER_URL, useURL);
            env.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY,
                    "weblogic.jndi.WLInitialContextFactory");
            env.put(javax.naming.Context.SECURITY_AUTHENTICATION, "simple");
            env.put(javax.naming.Context.SECURITY_PRINCIPAL, "afusuario");
            env.put(javax.naming.Context.SECURITY_CREDENTIALS, userCode);
        }
        else {
        	protocolo = "t3s://";
    		useURL = protocolo + hostName + ":" + hostPort;
            env.put(javax.naming.Context.PROVIDER_URL, useURL);
            env.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY,
                    "weblogic.jndi.WLInitialContextFactory");
            env.put(javax.naming.Context.SECURITY_AUTHENTICATION, "simple");
            env.put(javax.naming.Context.SECURITY_PRINCIPAL, "afusuario");
            env.put(javax.naming.Context.SECURITY_CREDENTIALS, userCode);
        }
        Context ctx = null;
        int retries = 0;
        while (ctx == null) {
            try {
                ctx = new InitialContext(env);
            }
            catch (Exception ex) {
                retries++;
                if (retries > MAX_RETRIES) {
                    throw ex;
                }
                logger.doLog(logger.lWarn(), " Reintento " + retries +
                             " getInitCtx por: " + ex, "connectDB", null);
            }
        }
        return ctx;
    }

    public ServerEntry getServerEntry() {
        Context ctx = null;
        if (server != null) {
            return server;
        }
        ContextoAplicacion capp = ContextoAplicacion.instancia();
        Properties props = (Properties) capp.getValue("properties");

        try {
            ctx = getInitialContext("AppCorreoServer");
        }
        catch (Exception ex) {
            ctx = null;
        }
        if (ctx == null) {
            logger.doLog(4, "Error de sistema 1 al obtener ServerEntry home",
                         "connectDB", null);
            return null;
        }

        // Ahora obtener proxy
        Object obj = null;
        try {
            boolean ready = false;
            int retries = 0;
            while (!ready) {
                try {
                    obj = ctx.lookup(JNDIName);
                    ready = true;
                }
                catch (Exception ex) {
                    retries++;
                    if (retries > MAX_RETRIES) {
                        throw ex;
                    }
                    logger.doLog(4,
                                 "Reintento " + retries +
                                 " obtener home de ASDataServices por: " +
                                 ex,
                                 "connectDB", null);
                }
            }
        }
        catch (Exception e) {
            logger.doLog(4, "Error al obtener home de ASDataServices",
                         "connectDB", e);
            return null;
        }
        ServerEntryHome dataServicesHome = null;

        // Create a new Session
        try {
            obj = PortableRemoteObject.narrow(obj, ServerEntryHome.class);
            dataServicesHome = (ServerEntryHome) obj;
            server = dataServicesHome.create();
        }
        catch (Exception e) {
            server = null;
            logger.doLog(4, "Error de sistema 3 al obtener ASDataServices",
                         "connectDB", e);
            return null;
        }
        return server;

    }

    public Object deSerializa(byte[] source) {
        String myName = "ASDataServicesBean.deSerializa";
        Object obj = null;
        if (source == null) {
            return null;
        }
        ByteArrayInputStream bs = new ByteArrayInputStream(source);
        ObjectInputStream os = null;
        try {
            os = new ObjectInputStream(bs);
            obj = os.readObject();
            os.close();
            os = null;
        }
        catch (ClassNotFoundException cnfe) {
            logger.doLog(4, "Error al deserializar un objeto recibido", myName,
                         cnfe);
            obj = null;
        }
        catch (IOException ex) {
            logger.doLog(4, "Error 2 al deserializar un objeto recibido",
                         myName, ex);
            obj = null;
        }
        finally {
            if (os != null) {
                try {
                    os.close();
                }
                catch (IOException ex1) {}
            }
        }
        return obj;
    }

// Serilización y deSerialización de un Objeto
    public byte[] serializa(Object source) {
        String myName = "AbstractSessionBean.serializa";
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ObjectOutputStream os = null;
        try {
            os = new ObjectOutputStream(bs);
            os.writeObject(source);
            byte[] bytes = bs.toByteArray();
            os.close();
            os = null;
            return bytes;
        }
        catch (IOException ex1) {
            logger.doLog(4, "Error de sistema al serializar resultado", myName,
                         ex1);
        }
        finally {
            if (os != null) {
                try {
                    os.close();
                }
                catch (Exception ex) {}
            }
        }
        return null;
    }

    /**
     * Invoca al metodo de validacion de sesion en el servidor
     */
    public void validaSesion(){
    	 try {
    		 if (server == null) {
                 server = getServerEntry();
             }
    		 server.validaSesion((Vector) capp.getValue("usercontext"));
    	 } catch (Exception e) {    		 
	           e.printStackTrace();
	           
	     }	 
    }
    
    //Manteniiento de Tipo de Producto

    public void MantCatTipoPto(CatTipoPtoDataBean dataBean, int numope) {
        byte[] bDatos = null;
        try {
            //Obtener servicio
            if (server == null) {
                server = getServerEntry();
            }
            Vector UserContext = (Vector) capp.getValue("usercontext");
            //Serializo la información para guardarla
            bDatos = serializa(dataBean);

            server.UpdateTabla(UserContext, bDatos, numope);
        }
        catch (Exception e) {
            System.out.println(e.toString());
            System.out.println("Error al Capturar el Tipo de Producto");
        }
    }

    //Manteniiento de Causas de Devolución

    public void MantCatCausaDev(CatCausaDevDataBean dataBean, int numope) {
        byte[] bDatos = null;
        try {
            //Obtener servicio
            if (server == null) {
                server = getServerEntry();
            }
            Vector UserContext = (Vector) capp.getValue("usercontext");
            //Serializo la información para guardarla
            bDatos = serializa(dataBean);

            server.UpdateTabla(UserContext, bDatos, numope);
        }
        catch (Exception e) {
            System.out.println(e.toString());
            System.out.println("Error al Capturar la Causa de Devolución");
        }
    }

    //Manteniiento de Parámetros

    public void MantCatParam(CatParametrosDataBean dataBean, int numope) {
        byte[] bDatos = null;
        try {
            //Obtener servicio
            if (server == null) {
                server = getServerEntry();
            }
            Vector UserContext = (Vector) capp.getValue("usercontext");
            //Serializo la información para guardarla
            bDatos = serializa(dataBean);

            server.UpdateTabla(UserContext, bDatos, numope);
        }
        catch (Exception e) {
            System.out.println(e.toString());
            System.out.println("Error al Capturar el Parámetro");
        }
    }

    //Manteniiento de Estatus de Rechazo de Tarjetas
    public void MantCatFestivos(CatFestivosDataBean dataBean, int numope) {
        byte[] bDatos = null;
        try {
            //Obtener servicio
            if (server == null) {
                server = getServerEntry();
            }
            Vector UserContext = (Vector) capp.getValue("usercontext");
            //Serializo la información para guardarla
            bDatos = serializa(dataBean);

            server.UpdateTabla(UserContext, bDatos, numope);
        }
        catch (Exception e) {
            System.out.println(e.toString());
            System.out.println("Error al Capturar el Día Festivo");
        }
    }

    public void Ejecuta(String sql) {
        if (server == null) {
            server = getServerEntry();
        }
        try {
            server.ExecQuery((Vector) capp.getValue("usercontext"),sql);
        }
        catch (Exception ex) {
            System.out.println(ex.toString());
            ex.printStackTrace();
            System.out.println("Error al leer la información solicitada");

        }

    }
    public int EjecutaUpdate(String sql) {
    	int cuenta =0;
        if (server == null) {
            server = getServerEntry();
        }
        try {
        	cuenta =  server.ExecUpdateQuery((Vector) capp.getValue("usercontext"),sql);
        }
        catch (Exception ex) {
            System.out.println(ex.toString());
            ex.printStackTrace();
            System.out.println("Error al leer la información solicitada");

        }
        
        return cuenta;

    }    

    public String[][] EjecutaConsulta(String SQL, String SQLcount) {
        String[][] rs = null;
        byte Datos[] = null;
        if (server == null) {
            server = getServerEntry();
        }
        try {
            if (SQLcount.toUpperCase().indexOf("ORDER BY") != -1) {
                SQLcount = SQLcount.substring(0,
                                              SQLcount.toUpperCase().indexOf(
                        "ORDER BY") -
                                              1);
            }

            Datos = server.ExecConsulta((Vector) capp.getValue("usercontext"),SQL, SQLcount);
            System.out.println(SQL);
            System.out.println(SQLcount);
            rs = (String[][])this.deSerializa(Datos);

        }

        catch (Exception ex) {
            System.out.println(ex.toString());
            ex.printStackTrace();
            System.out.println("Error al leer la información solicitada");

        }

        return rs;
    }

    public void EjecutaSP(String nomStoreProc, String parametros) {
        if (server == null) {
            server = getServerEntry();
        }
        try {
            server.ExecStore((Vector) capp.getValue("usercontext"),nomStoreProc, parametros);
        }
        catch (Exception ex) {
            System.out.println(ex.toString());
            ex.printStackTrace();
            System.out.println("Error al leer la información solicitada");

        }

    }

    /** Rutina para listado de registros en tabla */
    public String[][] Lista(String ListaCol, String tabla,
                            String condiciones) throws
            ClassNotFoundException,
            SQLException, InstantiationException, IllegalAccessException,
            ClassNotFoundException, SQLException {

        String[][] RS = null;
        byte[] datos = null;
        StringBuffer sb=new StringBuffer();
        try {
            //Obtener servicio
            if (server == null) {
                server = getServerEntry();
            }
         
          
            sb.append("Select ").append(ListaCol).append(" from ").append(tabla).append(condiciones);
            //System.out.println(sb.toString());
            datos = server.getQueryAll((Vector) capp.getValue("usercontext"),sb.toString());
            //System.out.println(sb.toString());
            RS = (String[][])this.deSerializa(datos);
          
        	
        }
        catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            System.out.println("Error al leer la información solicitada");
        }
        return RS;
    }

    /** Rutina para llenado de Combo Boxes */
    public void LlenaCombo(JComboBox ComboName, String ListaCol,
                           String tabla, String condiciones,
                           String Todos) throws
            IllegalAccessException, InstantiationException, SQLException,
            ClassNotFoundException {
        /** Carga del recordset */
        String[][] RS;
        RS = Lista(ListaCol, tabla, condiciones);
        /** Si se elige la opción para desplegar TODOS */
        if (Todos == "S") {
            ComboName.addItem("TODOS");
        }
        /** Llenado del combo a partir del recordset */
        if (!RS[0][0].equals("BOF")) {
            for (int renglon = 1; renglon < RS.length; renglon++) {
                ComboName.addItem(RS[renglon][0]);
            }
        }
    }

    
    /**
     * Rutina para llenado de ComboBox con id y value
     * 
     * @param comboName es el objeto ComboBox a llenar
     * @param listaCol, contiene los campos que queremos traer
     * @param tabla, es el nombre de la tabla
     * @param condiciones para la obtención de datos
     * @param todos , se adiciona la opcion 'TODOS' al ComboBox
     * 
     * @exception  IllegalAccessException, InstantiationException, SQLException,
            	   ClassNotFoundException
     */
    @SuppressWarnings({ "serial", "unchecked" })
    public void llenaComboModoIdValue(JComboBox comboName, String listaCol,
                           String tabla, String condiciones,
                           String todos) throws
            IllegalAccessException, InstantiationException, SQLException,
            ClassNotFoundException {
    	

        Map<String,String> map= new HashMap<String,String>();
        /** Carga del recordset */
        String[][] RS;
        RS = Lista(listaCol, tabla, condiciones);
        
        
        /** Si se elige la opción para desplegar TODOS */
        if (todos == "S") {
        	map.put("-1", "TODOS");          
        }
        /** Llenado del combo a partir del recordset */
        if (!RS[0][0].equals("BOF")) {
            for (int renglon = 1; renglon < RS.length; renglon++) {               
            	map.put(RS[renglon][0], RS[renglon][1]);            	
            }
        }
        KeyValueComboboxModel  model = new KeyValueComboboxModel(); 
        model.putAll(map); 
        comboName.setModel(model);
        comboName.setRenderer(new DefaultListCellRenderer(){         	 
            @Override 
            public Component getListCellRendererComponent(JList list, Object value, int index, 
                    boolean isSelected, boolean cellHasFocus) { 
                if(value instanceof Map.Entry){ 
                    Map.Entry<String,String> entry = (java.util.Map.Entry<String, String>) value; 
                    String str = "" + entry.getValue(); 
                    return super.getListCellRendererComponent(list, str, index, isSelected, cellHasFocus); 
                } 
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus); 
            } 
        }); 
        comboName.setSelectedIndex(0);        
        
    }
    
    
}
