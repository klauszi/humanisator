package com.human.view;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * @author human
 * Erzeugt einen Dateiauswähler, der nur Dateien 
 * mit der Endung "gka" lesen und schreiben kann.
 *
 */
public class GKAChooser extends JFileChooser {
	
	// Damit keine Warnung ausgegeben wird
	private static final long serialVersionUID = 1L;

	/**
	 * @author human
	 * 
	 * Erzeugt einen Dateifilter, der nur GKA-Dateien erkennt.
	 *
	 */
	private class GKAFilter extends FileFilter{

		@Override
		public boolean accept(File file) {
			String fileName = file.getName();
   			if (file.isDirectory()) {
   				return true;
   			} else {
   				return fileName.endsWith(".gka");
   			}
		}

		@Override
		public String getDescription() {
    		return "GKA Documents (*.gka)";
		}

	}
	
    public GKAChooser() {
    	// Schalte alle StandardFilter aus
    	this.setAcceptAllFileFilterUsed(false);
    	// Schalte GKA-Filter an
    	this.addChoosableFileFilter(new GKAFilter());
    }
}