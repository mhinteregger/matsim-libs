/* *********************************************************************** *
 * project: org.matsim.*
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2007 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package org.matsim.run;

import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.ier.run.IERConfigGroup;
import org.matsim.contrib.newgreedo.GreedoConfigGroup;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.OutputDirectoryHierarchy;

import ch.sbb.matsim.config.SBBTransitConfigGroup;
import modalsharecalibrator.ModalShareCalibrationConfigGroup;
import models.utils.StockholmConfigGroup;


/**
 * This is currently only a substitute to the full Controler. 
 *
 * @author mrieser
 */
public class Controler {

	private final org.matsim.core.controler.Controler controler;
	
	public Controler(final String[] args) {
		this.controler = new org.matsim.core.controler.Controler(args);
	}
	
	public Controler(final String configFilename) {
		this.controler = new org.matsim.core.controler.Controler(configFilename);
	}
	
	public void setOverwriteFiles(final boolean overwriteFiles) {
		this.controler.getConfig().controler().setOverwriteFileSetting(
				overwriteFiles ?
						OutputDirectoryHierarchy.OverwriteFileSetting.overwriteExistingFiles :
						OutputDirectoryHierarchy.OverwriteFileSetting.failIfDirectoryExists );
	}
	
	public Scenario getScenario() {
		return this.controler.getScenario() ;
	}
	
	public void run() {
		this.controler.run();
	}
	
	public  static void main(String[] args) {
		new Controler(args).run();
		final Config config = ConfigUtils.loadConfig("\\home\\martin\\Desktop\\ptSimulation\\config_Vienna_v3.xml",
                new SBBTransitConfigGroup(), 
                new ModalShareCalibrationConfigGroup(),
                new IERConfigGroup(),
                new GreedoConfigGroup(),
                new StockholmConfigGroup());
	}
}