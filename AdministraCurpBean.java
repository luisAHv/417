package mx.com.aforebanamex.pes169.presentation.backend;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;

import com.itextpdf.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

import mx.com.aforebanamex.pes169.business.backend.BackendFacadeBean;
import mx.com.aforebanamex.pes169.service.beans.EdoCta;
import mx.com.aforebanamex.pes169.service.beans.Estatus;
import mx.com.aforebanamex.pes169.service.beans.beansextras.ErrorArchivoEdoCta;
import mx.com.aforebanamex.pes169.service.beans.jpa.Parametro;
import mx.com.aforebanamex.pes169.service.beans.jpa.TblCurpContrasena;
import mx.com.aforebanamex.pes169.service.beans.jpa.TblHistoricoEdocta;
import mx.com.aforebanamex.pes169.service.exception.BusinessException;
import mx.com.aforebanamex.pes169.service.exception.DuplicidadClienteException;
import mx.com.aforebanamex.pes169.service.exception.NoTieneEstadoDeCuentaException;
import mx.com.aforebanamex.pes169.service.util.ValidacionesUtil;
import mx.com.banamex.afore.seguridad.BasicCipher;

@ManagedBean(name = "administraCurpBean")
@SessionScoped
public class AdministraCurpBean extends BaseBackendBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1148064247130842572L;

	private static final Log LOG = LogFactory.getLog(AdministraCurpBean.class);
	
	private List<Parametro> catalogoPeriodosEstadoDeCuenta;

	public static final Long TIPO_ARCHIVO = new Long(0);

	@EJB
	private transient BackendFacadeBean fachada;

	@ManagedProperty(value = "#{navegacionBean}")
	private NavegacionBean navegacionBean;



	
	@ManagedProperty(value = "#{visit}")
	private VisitBackend visit;

	private String estatus;


	private int uploadsAvailable = 1;

	private boolean autoUpload = false;

	private boolean uploadCompleted = false;

	private boolean uploadPanelOpen;

	private boolean multiUpload = false;

	private String mensajeInicio = "";

	private String mensajeInicio2 = "";

	private String mensajeServicioNoDisponible = "";


	List<ErrorArchivoEdoCta> registrosError;
	List<TblHistoricoEdocta> edoCtaValidos;
	

	private String styleBtnMonitor = "monitorDisable";

	private String listaErrores = "monitorDisable";
	
	List<ErrorArchivoEdoCta> listaMensajes;
	
	int linea = 0;

	/**
	 * @return the listaErrores
	 */
	public String getListaErrores() {
		return listaErrores;
	}

	/**
	 * @param listaErrores
	 *            the listaErrores to set
	 */
	public void setListaErrores(String listaErrores) {
		this.listaErrores = listaErrores;
	}



	public String getEstatus() {
		return estatus;
	}

	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}

	

	
	public NavegacionBean getNavegacionBean() {
		return navegacionBean;
	}

	public void setNavegacionBean(NavegacionBean navegacionBean) {
		this.navegacionBean = navegacionBean;
	}


	public VisitBackend getVisit() {
		return visit;
	}

	public void setVisit(VisitBackend visit) {
		this.visit = visit;
	}



	
	public boolean isAutoUpload() {
		return autoUpload;
	}

	public void setAutoUpload(boolean autoUpload) {
		this.autoUpload = autoUpload;
	}

	public boolean isMultiUpload() {
		return multiUpload;
	}

	public void setMultiUpload(boolean multiUpload) {
		this.multiUpload = multiUpload;
	}

	public boolean isUploadCompleted() {
		return uploadCompleted;
	}

	public void setUploadCompleted(boolean uploadCompleted) {
		this.uploadCompleted = uploadCompleted;
	}

	public boolean isUploadPanelOpen() {
		return uploadPanelOpen;
	}

	public void setUploadPanelOpen(boolean uploadPanelOpen) {
		this.uploadPanelOpen = uploadPanelOpen;
	}

	public int getUploadsAvailable() {
		return uploadsAvailable;
	}

	public void setUploadsAvailable(int uploadsAvailable) {
		this.uploadsAvailable = uploadsAvailable;
	}

	public String getMensajeInicio() {
		return mensajeInicio;
	}

	public void setMensajeInicio(String mensajeInicio) {
		this.mensajeInicio = mensajeInicio;
	}

	
	public String getStyleBtnMonitor() {
		return styleBtnMonitor;
	}

	public void setStyleBtnMonitor(String styleBtnMonitor) {
		this.styleBtnMonitor = styleBtnMonitor;
	}

	public String getMensajeInicio2() {
		return mensajeInicio2;
	}

	public void setMensajeInicio2(String mensajeInicio2) {
		this.mensajeInicio2 = mensajeInicio2;
	}

	
	public List<ErrorArchivoEdoCta> getListaMensajes() {
		return listaMensajes;
	}

	public void setListaMensajes(List<ErrorArchivoEdoCta> listaMensajes) {
		this.listaMensajes = listaMensajes;
	}

	public AdministraCurpBean() {

		// init();
	}

	@PostConstruct
	public void init() {
		edoCtaValidos = new ArrayList<TblHistoricoEdocta>();

		registrosError = new ArrayList<ErrorArchivoEdoCta>();
		listaMensajes = new ArrayList<ErrorArchivoEdoCta>();
		clearUploadData();
		
	}

	

	private void agregaMensajeDeError(FacesContext fc, String mensajeError) {

		fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, mensajeError, "error"));
		navegacionBean.setMessanges(Boolean.TRUE);

	}

	public SelectItem[] getEstatuSelectItems() {

		SelectItem[] items = new SelectItem[Estatus.values().length - 1];
		int i = 0;

		for (Estatus e : Estatus.values()) {
			if (!e.getValor().equals("-1")) {
				items[i++] = new SelectItem(e.getValor(), e.getDescripcion());
			}
		}
		return items;
	}

	public void listener(FileUploadEvent event) {
		
		LOG.debug("Consultar y enviar estados de cuenta en backend");
		FacesContext fc = FacesContext.getCurrentInstance();
		clearUploadData();
		byte[] arregloBytes;
		byte[] arrayEdoctaSalida = null;
		UploadedFile item = event.getUploadedFile();
		String nombreArchivo = item.getName();
		boolean existError = false;
		
		
		try {
			//arregloBytes = item.getData();
			existError = readFile(item);
			if (!existError) {
				LOG.info("el archivo no contiene errores, se procede con la busqueda de estados de cuenta, registros a procesar: "+edoCtaValidos.size());
			}else{
				LOG.info("existen errores en el archivo: "+registrosError.size()+" errores "+edoCtaValidos.size()+ " correctos" );
			}
			
			int enviados=0;
			int noEnviados = 0;
			for (TblHistoricoEdocta historicoEdocta : edoCtaValidos) {
				
				try{
					
					fachada.insertarHistoricoEdoCta(historicoEdocta);
					
					arrayEdoctaSalida = null;
					arregloBytes = null;
					
					// buscar PDF por curp 
					arregloBytes = obtenerEstadoDeCuenta(historicoEdocta);
					
					if(arregloBytes != null && arregloBytes.length != 0){
						
						//Buscar por curp
						List<TblCurpContrasena> curpCont=  fachada.getCurpContrasena(historicoEdocta.getVscurp());
						String pwdEncontrado = null ;
						if(!curpCont.isEmpty() && curpCont.size() ==1 ){
							pwdEncontrado = curpCont.get(0).getVscontrasenia();
							LOG.debug("pwdEncontrado: "+pwdEncontrado);						
						}
						
						BasicCipher basicCipher =  new BasicCipher();
						byte[] cve = new byte[24];
						String password = null;
						if(pwdEncontrado == null){
							basicCipher.setKey(cve, true);
	
							password= ValidacionesUtil.generatePwd(historicoEdocta.getVscurp());
							
							LOG.debug("EL PASSWORD generado es: " + password);
							byte[] encript = basicCipher.encrypt(password.getBytes());
							
							String pwdEncript = (new BigInteger(encript)).toString(16);
							LOG.debug("EL PASSWORD encriptado es: " + pwdEncript);
							
							TblCurpContrasena curpcontrasena = new TblCurpContrasena();
							curpcontrasena.setVscontrasenia(pwdEncript);
							curpcontrasena.setVscurp(historicoEdocta.getVscurp());
							curpcontrasena.setVsmail(historicoEdocta.getVscorreo());
							curpcontrasena.setNidstatus(1);
							fachada.insertaCurpContrasena(curpcontrasena);
						}else{
							
							basicCipher.setKey(cve, false);
							BigInteger bytesPrivadaEnc = new BigInteger(pwdEncontrado, 16);
							byte[] bP = basicCipher.decrypt(bytesPrivadaEnc.toByteArray());
							password = new String(bP);
							
							LOG.debug("El password desencriptado es: "+password);
						}	
						
						
						arrayEdoctaSalida = agregarPassPdfArray(arregloBytes, password);
						historicoEdocta.setNstatus(4L);
						
						boolean envioPdf = false;
						
						try {
							if(arrayEdoctaSalida!= null && arrayEdoctaSalida.length != 0){
								fachada.envioEmailPdf(historicoEdocta.getVscorreo(), arrayEdoctaSalida);
								historicoEdocta.setNstatus(5L);
								envioPdf = true;
								enviados++;
							}else{
								noEnviados++;
							}
						}catch (BusinessException businessException) {
							historicoEdocta.setNstatus(4L);
							LOG.error("***ERROOOOOR AL ENVIAR CORREO con el estado de cuenta**** "+businessException);
							noEnviados++;
						}						
						
						try {
							if(arregloBytes!= null && arregloBytes.length != 0 && envioPdf == true){
								LOG.debug("password que se enviara al correo: "+password);
								LOG.debug("curp: "+historicoEdocta.getVscorreo());
								
								fachada.envioEmail(historicoEdocta.getVscorreo(), password);
								historicoEdocta.setNstatus(6L);
							}							
						}catch (BusinessException businessException) {
							LOG.error("***Error al enviar contraseÃ±a**** "+ businessException);
							historicoEdocta.setNstatus(5L);						
						}
					}
				}catch (ParseException e) {
					LOG.info("Error de parseo para fechas*********** "+ e.getMessage());
					historicoEdocta.setNstatus(3L);
					noEnviados++;
					
				}catch (NoTieneEstadoDeCuentaException e) {
					LOG.debug("No tiene estados de cuenta***********: "+ e.getMessage());
					historicoEdocta.setNstatus(1L);
					noEnviados++;
					
				} catch (DuplicidadClienteException e1) {
					LOG.debug("***dos nss para el mismo curp**** "+historicoEdocta.getVscurp());
					historicoEdocta.setNstatus(2L);
					noEnviados++;
					
				}catch (BusinessException e) {
					LOG.error("***error de systema**** " + e);
					noEnviados++;
				}
			
				try{
					fachada.updateStatus(historicoEdocta);
				} catch (BusinessException businessException1) {
					LOG.error("***ERROOOOOR AL ACTUALIZAR HISTORICO**** ");
				}
			
			}
			
			//Termina ciclo for
				
			if (!existError) {
				saveFile(nombreArchivo,enviados, noEnviados);
			} else {
				noSaveFile(registrosError.size(), nombreArchivo, enviados, noEnviados);
			}
		}catch(IOException ioe){
			LOG.error("No fue posible el procesar el archivo", ioe);
			mensajeServicioNoDisponible = "No fue posible el procesar el archivo";
			agregaMensajeDeError(fc, "No fue posible el procesar el archivo");
		}
	}

	private byte[] obtenerEstadoDeCuenta(TblHistoricoEdocta historicoEdocta) throws NoTieneEstadoDeCuentaException, DuplicidadClienteException, BusinessException, ParseException   {
		byte[] arrayEdocta = null;
		Map<String,String> pdfList = null;
		EdoCta edoCta = null;
		
		pdfList = fachada.consultarSizeDocsCurp(historicoEdocta.getVscurp());
		
		edoCta = ultimoEdoCuentaUltimoPeriodo(pdfList);
			
		if(null!= edoCta && edoCta.getMemento()!= null && !edoCta.getMemento().contentEquals("")){
			String memento = edoCta.getMemento();
			LOG.debug("memento del curp "+historicoEdocta.getVscurp()+" memento: "+ memento);
			LOG.debug("periodo "+edoCta.getPdf());
			try {
				arrayEdocta = fachada.consultaEstadoPorIndice(memento);
			} catch (BusinessException e) {
				LOG.info("Error al obtener el pdf*********** "+ e.getMessage());
				throw new NoTieneEstadoDeCuentaException("Estado de cuenta no encontrado **"+historicoEdocta.getVscurp());
			}
			
		}else{
			LOG.debug("Estado de cuenta no encontrado **"+historicoEdocta.getVscurp() );
			throw new NoTieneEstadoDeCuentaException("Estado de cuenta no encontrado **"+historicoEdocta.getVscurp());
		}

		return arrayEdocta;
		
	}

	private EdoCta ultimoEdoCuentaUltimoPeriodo(Map pdfList) throws ParseException {
		EdoCta estadoCuenta = null;
		List<EdoCta> listEdosCtas = new ArrayList<EdoCta>();
		
		Iterator it = pdfList.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry e = (Map.Entry) it.next();
			EdoCta edoCta = new EdoCta();				
			edoCta.setPdf(getPeriodoString((String) e.getKey()));				
			edoCta.setMemento((String) e.getValue());
			listEdosCtas.add(edoCta);
		}
		
		Collections.sort(listEdosCtas, new Comparator() {//ordena la lista 
			public int compare(Object estadoDeCuenta1,
					Object estadoDeCuenta2) {
				EdoCta documentoUno = (EdoCta) estadoDeCuenta1;
				EdoCta documentoDos = (EdoCta) estadoDeCuenta2;
				Integer numUno;
				Integer numDos;
				String anioUno;
				String anioDos;
				
				int iniUno = documentoUno.getPdf().indexOf(" ");
				int finUno = documentoUno.getPdf().lastIndexOf(" ");
				anioUno =  documentoUno.getPdf().substring(8).trim();
				int iniDos = documentoDos.getPdf().indexOf(" ");
				int finDos = documentoDos.getPdf().lastIndexOf(" ");
				anioDos =  documentoDos.getPdf().substring(8).trim();
				
				
				//si no tiene periodo el primer archivo regresamos el segundo
				if(anioUno.equals("odo")){
					numDos = new Integer(anioDos+documentoDos.getPdf().substring(iniDos,finDos).trim());
					return numDos;
					//si no tiene periodo el segundo archivo regresamos el primero
				}else if(anioDos.equals("odo")){
					numUno = new Integer(anioUno+documentoUno.getPdf().substring(iniUno,finUno).trim());
					return numUno;
				}
				
				numUno = new Integer(anioUno+documentoUno.getPdf().substring(iniUno,finUno).trim());
				numDos = new Integer(anioDos+documentoDos.getPdf().substring(iniDos,finDos).trim());
				
				return numUno.compareTo(numDos);
			}
		});
		
		if(listEdosCtas!=null && !listEdosCtas.isEmpty()){
		
			estadoCuenta = listEdosCtas.get(listEdosCtas.size()-1);
		}		
		return estadoCuenta;		
	}
	
	private boolean validaNssUnicoMap(Map<String,String> pdfList){
		boolean nssUnico = true;
		String acount = "";
		HashSet<String> acounts = new HashSet<String>();
		
		for(String memento : pdfList.values()){
			
			acount = (memento.split("&")[1]).split("=")[1];
			acounts.add(acount);
		}
		
		if(acounts.size() > 1){
			LOG.debug("existe mas de un nss para el curp consultado nss: "+acount);
			nssUnico = false;
		}else{
			LOG.debug("nss unico: "+acount);
		}
		return nssUnico;
		
	}
	
private String getPeriodoString(String dateEcString) throws ParseException{
		
		if(catalogoPeriodosEstadoDeCuenta == null){
			setPeriodoEdoCuenta();			
		}		
		String stringPeriodo = "sin periodo";
		LOG.warn("Fecha de documento...."+dateEcString);
		for (Parametro element : catalogoPeriodosEstadoDeCuenta) {					
			String[] arrValPer = (element.getValor()).split("\\.");						
			if(getYear(dateEcString).equals(getYear(arrValPer[0]))){//confirma que el aÃ±o del periodo y el aï¿½o de la fecha del documento sean iguales
								
				int numPerido = 1;
					LOG.warn("...."+arrValPer[arrValPer.length-2]+"...."+arrValPer[arrValPer.length-1]);					
					String stringPeriodo1 = getPeriodo(dateEcString,arrValPer[arrValPer.length-2],arrValPer[arrValPer.length-1],numPerido);
					numPerido++;
					if(stringPeriodo1 != null){
						stringPeriodo = stringPeriodo1;
						return stringPeriodo1;						
					}	
			}		
		}
		
		
		return stringPeriodo;
		
	}

private String getPeriodo(String dateDocString, String iniPeriodo, String finPeriodo, int numPeriodo) throws ParseException{
	DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
	Date dateEc = df.parse(dateDocString);
	Date dateIniPer = df.parse(iniPeriodo);
	Date dateFinPer = df.parse(finPeriodo);
	String nomPeriodo = null;
	
	if(dateEc.equals(dateIniPer) || dateEc.equals(dateFinPer) || (dateEc.after(dateIniPer) && dateEc.before(dateFinPer))) { 
		
		nomPeriodo = getNomPeriodo(iniPeriodo,finPeriodo)+" "+numPeriodo+" "+dateDocString.substring(0, 4);       
		  		
    }
		
	return nomPeriodo;
}

public String getNomPeriodo(String iniPeriodo, String finPeriodo) throws ParseException{//obtiene el periodo dependiendo de los parametro en BD
	DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
	Date dateIniPeriodo = df.parse(iniPeriodo);
	Date dateFinPeriodo = df.parse(finPeriodo);		
	long num = dateFinPeriodo.getTime()-dateIniPeriodo.getTime();		
	Long nums = new Long("2592000000");//milisegundos de 30 dias
	Long mes = Math.round(new Double((num*1.0)/(nums*1.0)));
	int mesInt = mes.intValue();
	String nomPeriodo = "";
	switch (mesInt) {
	case 12:
		nomPeriodo = "ANU";
		break;
	case 2:
		nomPeriodo = "BIM";
		break;
	case 3:
		nomPeriodo = "TRI";
		break;
	case 4:
		nomPeriodo = "CUA";
		break;
	case 6:
		nomPeriodo = "SEM";
		break;

	default:
		nomPeriodo = "SIN PERIODO";
		break;
	}

	return nomPeriodo;
}


private void setPeriodoEdoCuenta(){
	
	try {		
		catalogoPeriodosEstadoDeCuenta = fachada.getCatalogoPeriodosEstadoDeCuenta();
		
		for (Parametro element : catalogoPeriodosEstadoDeCuenta) {
			LOG.warn(".........."+element.getValor());
		}
		} catch (Exception e) {
			LOG.error("Error al cargar el catalogo de Periodos de Estado de Cuenta"+ e.getMessage());
		}
}


private String getYear(String dateString) throws ParseException{				    
	return dateString.substring(0, 4);		
}

	private byte[] agregarPassPdfArray(byte[] arrayEdocta, String pass) throws BusinessException  {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			PdfReader pdf =  new PdfReader(arrayEdocta);
			baos = new ByteArrayOutputStream();
			PdfStamper stamper = new PdfStamper(pdf, baos);
			stamper.setEncryption(pass.getBytes(), pass.getBytes(),PdfWriter.ALLOW_COPY, PdfWriter.ENCRYPTION_AES_128);
		      stamper.close();
		      pdf.close();
		      LOG.info("se agrego la contraseÃ±a exitosamente");
		     
		} catch (com.lowagie.text.DocumentException e) {
			LOG.error(e);
			throw new BusinessException("No fue posible modificar el documento");
			 
		} catch (IOException e) {
			LOG.error(e);
			throw new BusinessException("No fue posible leer el documento");
		}
		return baos.toByteArray();
	}
	
	private boolean readFile(UploadedFile item) throws IOException {
		boolean existError = false;
		boolean salida = false;
		String line;
		String curpbd;
		String emailBd;
		InputStream is = item.getInputStream();
		BufferedReader bf = new BufferedReader(new InputStreamReader(is));
		linea = 0;
		while (bf.ready()) {

			line = bf.readLine();
			linea++;
			LOG.debug("linea " + line);
			String[] estadoCtaCurp = line.split(";");
			LOG.debug("tamaÃ±o arreglo " + estadoCtaCurp.length);


			if (estadoCtaCurp.length <= 3) {
				registrosError
						.add(new ErrorArchivoEdoCta("El registro no cumple con el formato correcto: "+line,linea));
				existError = true;
				LOG.debug("ERROR longitud de linea: " + linea);
			}else {
				
				LOG.debug(" **** entro a validar ***");
				
				LOG.debug("CURP " + estadoCtaCurp[2]);
				LOG.debug("EMAIL " + estadoCtaCurp[3]);
				curpbd = estadoCtaCurp[2];
				emailBd = estadoCtaCurp[3];

				if (!ValidacionesUtil.validarCurp(curpbd)) {
					LOG.debug("Error en formato de Curp");
					registrosError.add(new ErrorArchivoEdoCta("Error en formato curp en la linea: " + line , linea));
					existError = true;

				} 

				if (!ValidacionesUtil.validarEmailFile(emailBd)) {
					LOG.debug("Error en formato de email");
					registrosError.add(new ErrorArchivoEdoCta("Error en formato  email en la linea: " + line , linea));
					existError = true;
				}
				

				if (!existError) {
					LOG.debug("***formato de linea correcto**** ");
					TblHistoricoEdocta edoCta = new TblHistoricoEdocta();
					edoCta.setVscurp(curpbd);
					edoCta.setVscorreo(emailBd);
					edoCta.setNstatus(1L);
					edoCta.setDtfechaOperacion(new Date());

					edoCtaValidos.add(edoCta);
				}else{
					
					LOG.debug("***ERROOOOOR en la linea**** " +linea);
					salida = existError;
					existError = false;
				}
			}
			

		}
		return salida;
	}

	private void saveFile(String nombreArchivo,int enviados, int noEnviados){
		listaMensajes = new ArrayList<ErrorArchivoEdoCta>();
		LOG.info("Se cargo el archivo " + nombreArchivo);
		mensajeInicio = "Procesamiento del archivo exitoso";
		listaMensajes.add(new ErrorArchivoEdoCta("Registros del archivo: "+linea,1));
		listaMensajes.add(new ErrorArchivoEdoCta("Registros correctos a procesar: "+(edoCtaValidos.size()),2));
		listaMensajes.add(new ErrorArchivoEdoCta("Solicitudes enviadas: "+enviados,3));
		listaMensajes.add(new ErrorArchivoEdoCta("Solicitudes no enviadas: "+noEnviados,4));
		
//		mensajeInicio2 = "Solicitudes procesados: "+edoCtaValidos.size()+"\n"
//				+ "Solicitudes enviadas: "+enviados +"\n"
//				+ "Solicitudes no enviadas: "+noEnviados ;
		styleBtnMonitor = "monitor";

	}

	private void noSaveFile(int registrosError, String nombreArchivo, int enviados, int noEnviados){
		listaMensajes= new ArrayList<ErrorArchivoEdoCta>();
		LOG.info("El archivo contiene errores " + nombreArchivo);
		mensajeInicio =" El Procesamiento del Archivo contiene errores";
		listaMensajes.add(new ErrorArchivoEdoCta("Registros del archivo: "+linea,1));
		listaMensajes.add(new ErrorArchivoEdoCta("Registros correctos a procesar: "+(edoCtaValidos.size()),2));
		listaMensajes.add(new ErrorArchivoEdoCta("Solicitudes enviadas: "+enviados,3));
		listaMensajes.add(new ErrorArchivoEdoCta("Solicitudes no enviadas: "+noEnviados,4));
//		mensajeInicio2 = "Solicitudes procesados: "+edoCtaValidos.size()+"\n"
//				+ "Solicitudes enviadas: "+enviados +"\n"
//				+ "Solicitudes no enviadas: "+noEnviados +"\n"
//				+ "Registros con error: "+registrosError;
		styleBtnMonitor = "monitor";
		LOG.debug(listaMensajes.get(0));
	}

	public void clean() {
		
		this.estatus = null;
		this.mensajeInicio="";
		registrosError.clear();
		edoCtaValidos.clear();
		this.mensajeInicio2="";
		listaMensajes.clear();
	}

	public void cleanConsulta() {
		this.mensajeInicio="";
		registrosError.clear();
		edoCtaValidos.clear();
	}

	private Estatus obtenerEstatusSeleccionado() {
		Estatus estatusSeleccionado = Estatus.NINGUNO;
		if (estatus.equals(Estatus.INACTIVO.getValor())) {
			estatusSeleccionado = Estatus.INACTIVO;
		} else if (estatus.equals(Estatus.ACTIVO.getValor())) {
			estatusSeleccionado = Estatus.ACTIVO;
		}
		return estatusSeleccionado;
	}




	public void uploadComplete() {
		uploadCompleted = true;
		this.mensajeInicio="";
		registrosError.clear();
		edoCtaValidos.clear();
	}

	public void clearUploadData() {
		uploadsAvailable = 1;
		mensajeInicio = "";
		mensajeInicio2 = "";
		mensajeServicioNoDisponible = "";
		styleBtnMonitor = "monitorDisable";
		registrosError.clear();
		edoCtaValidos.clear();
		listaMensajes.clear();
		
	}
	
	public boolean isEmpty() {
	    return registrosError.isEmpty();
	}


	public String getMensajeServicioNoDisponible() {
		return mensajeServicioNoDisponible;
	}

	public void setMensajeServicioNoDisponible(String mensajeServicioNoDisponible) {
		this.mensajeServicioNoDisponible = mensajeServicioNoDisponible;
	}

	/**
	 * @return the registrosError
	 */
	public List<ErrorArchivoEdoCta> getRegistrosError() {
		return registrosError;
	}

	/**
	 * @param registrosError
	 *            the registrosError to set
	 */
	public void setRegistrosError(List<ErrorArchivoEdoCta> registrosError) {
		this.registrosError = registrosError;
	}

	/**
	 * @return the edoCtaValidos
	 */
	public List<TblHistoricoEdocta> getEdoCtaValidos() {
		return edoCtaValidos;
	}

	/**
	 * @param edoCtaValidos
	 *            the edoCtaValidos to set
	 */
	public void setEdoCtaValidos(List<TblHistoricoEdocta> edoCtaValidos) {
		this.edoCtaValidos = edoCtaValidos;
	}
	
}