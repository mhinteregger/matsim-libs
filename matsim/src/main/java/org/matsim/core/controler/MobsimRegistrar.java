package org.matsim.core.controler;

import org.matsim.core.mobsim.jdeqsim.JDEQSimulationFactory;
import org.matsim.core.mobsim.queuesim.QueueSimulationFactory;
import org.matsim.ptproject.qsim.QSimFactory;

public class MobsimRegistrar {

	private MobsimFactoryRegister register = new MobsimFactoryRegister();
	
	public MobsimRegistrar() {
		register.register("qsim", new QSimFactory());
		register.register("queueSimulation", new QueueSimulationFactory());
		register.register("jdeqsim", new JDEQSimulationFactory());
	}
	
	public MobsimFactoryRegister getFactoryRegister() {
		return register;
	}
	
}
