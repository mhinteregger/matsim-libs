package playground.andreas.bvgScoringFunction;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.otfvis.OTFVis;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ActivityParams;
import org.matsim.core.config.groups.QSimConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.events.SynchronizedEventsManagerImpl;
import org.matsim.core.mobsim.framework.ObservableSimulation;
import org.matsim.core.mobsim.framework.listeners.SimulationListener;
import org.matsim.core.scenario.ScenarioImpl;
import org.matsim.core.scenario.ScenarioLoaderImpl;
import org.matsim.core.config.ConfigUtils;
import org.matsim.pt.PtConstants;
import org.matsim.pt.qsim.ComplexTransitStopHandlerFactory;
import org.matsim.pt.qsim.TransitQSimEngine;
import org.matsim.ptproject.qsim.QSim;
import org.matsim.ptproject.qsim.agents.AgentFactory;
import org.matsim.ptproject.qsim.agents.PopulationAgentSource;
import org.matsim.ptproject.qsim.agents.TransitAgentFactory;
import org.matsim.ptproject.qsim.qnetsimengine.DefaultQSimEngineFactory;
import org.matsim.ptproject.qsim.qnetsimengine.ParallelQNetsimEngineFactory;
import org.matsim.ptproject.qsim.qnetsimengine.QNetsimEngineFactory;
import org.matsim.vis.otfvis.OTFClientLive;
import org.matsim.vis.otfvis.OnTheFlyServer;

/**
 * @author aneumann
 */
public class TransitControler extends Controler {

	private final static Logger log = Logger.getLogger(TransitControler.class);

	private boolean useOTFVis = false;
	
	public TransitControler(Config config) {
		super(config);
	}
	
	public TransitControler(ScenarioImpl scenario) {
		super(scenario);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void runMobSim() {
		
		log.info("Overriding runMobSim()");

        Scenario sc = this.getScenario();EventsManager eventsManager = this.getEvents();

        QSimConfigGroup conf = sc.getConfig().getQSimConfigGroup();
        if (conf == null) {
            throw new NullPointerException("There is no configuration set for the QSim. Please add the module 'qsim' to your config file.");
        }

        // Get number of parallel Threads
        int numOfThreads = conf.getNumberOfThreads();
        QNetsimEngineFactory netsimEngFactory;
        if (numOfThreads > 1) {
            eventsManager = new SynchronizedEventsManagerImpl(eventsManager);
            netsimEngFactory = new ParallelQNetsimEngineFactory();
        } else {
            netsimEngFactory = new DefaultQSimEngineFactory();
        }
        QSim qSim = new QSim(sc, eventsManager, netsimEngFactory);
        AgentFactory agentFactory;
            agentFactory = new TransitAgentFactory(qSim);
            TransitQSimEngine transitEngine = new TransitQSimEngine(qSim);
            transitEngine.setUseUmlaeufe(true);
            transitEngine.setTransitStopHandlerFactory(new ComplexTransitStopHandlerFactory());
            qSim.addDepartureHandler(transitEngine);
            qSim.addAgentSource(transitEngine);
        PopulationAgentSource agentSource = new PopulationAgentSource(sc.getPopulation(), agentFactory, qSim);
        qSim.addAgentSource(agentSource);

        QSim simulation = (QSim) qSim;

		transitEngine.setTransitStopHandlerFactory(new ComplexTransitStopHandlerFactory());
//		this.events.addHandler(new LogOutputEventHandler());


		if (simulation instanceof ObservableSimulation){
			for (SimulationListener l : this.getQueueSimulationListener()) {
				((ObservableSimulation)simulation).addQueueSimulationListeners(l);
			}
		}
		if (this.useOTFVis) {
			OnTheFlyServer server = OTFVis.startServerAndRegisterWithQSim(config,getScenario(), events, simulation);
			OTFClientLive.run(config, server);
		}
		simulation.run();
	}	
	
	void setUseOTFVis(boolean useOTFVis) {
		this.useOTFVis = useOTFVis;
	}

	public static void main(final String[] args) {
		
		String configFile = args[0];		
		Config config;
		
		// reading the config file:
		config = ConfigUtils.loadConfig(configFile);
		
		// manipulate config
		// add "pt interaction" cause controler.init() is called too late and in a protected way
		ActivityParams transitActivityParams = new ActivityParams(PtConstants.TRANSIT_ACTIVITY_TYPE);
		transitActivityParams.setTypicalDuration(120.0);
		config.planCalcScore().addActivityParams(transitActivityParams);
		
		// reading the scenario (based on the config):
		ScenarioLoaderImpl scLoader = new ScenarioLoaderImpl(config) ;
		ScenarioImpl sc = (ScenarioImpl) scLoader.loadScenario() ;
		
		TransitControler tc = new TransitControler(sc);
		tc.setScoringFunctionFactory(new BvgScoringFunctionFactory(config.planCalcScore(), new BvgScoringFunctionConfigGroup(config), tc.getNetwork()));
		


		// Not needed to use own scoring function

		if(args.length > 1 && args[1].equalsIgnoreCase("true")){
			tc.setUseOTFVis(true);
		}
		tc.setOverwriteFiles(true);
//			tc.setCreateGraphs(false);
		tc.run();
		
	}
}
