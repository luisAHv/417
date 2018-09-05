package com.afbanamex.GestionCorrespondencia;

import javax.swing.JRadioButtonMenuItem;

import com.jonima.utils.guiclient.IJonStartableFrame;

public class HistoricosRetiroParcial implements IJonStartableFrame{

	@Override
	public void closeJonWindow() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public JRadioButtonMenuItem getMenuItem() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void start(int arg0, int arg1) {
		frmHistoricosRetiroParcial hist = new frmHistoricosRetiroParcial();
		hist.startInternal("Historicos de Retiros Parcial");
		
	}

}
