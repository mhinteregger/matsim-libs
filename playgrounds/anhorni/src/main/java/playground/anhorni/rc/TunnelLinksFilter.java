/* *********************************************************************** *
 * project: org.matsim.*
 * LinkFilter.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2013 by the members listed in the COPYING,        *
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

package playground.anhorni.rc;

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.basic.v01.IdImpl;
import org.matsim.core.mobsim.framework.MobsimAgent;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.withinday.replanning.identifiers.interfaces.AgentFilter;

/**
 * Filter that removes agents which are not located on a link included
 * in a predefined set after 15:30 when the accident happened.
 * 
 * @author anhorni
 */
public class TunnelLinksFilter implements AgentFilter {

	private final Map<Id, MobsimAgent> agents;
	private final Set<Id> links;
	private static final Logger log = Logger.getLogger(TunnelLinksFilter.class);
	private Network network;
	
	Node middleNode;
	Node portalNodeSouth;
	Node portalNodeNorth;
	
	// use the factory
	/*package*/ TunnelLinksFilter(Map<Id, MobsimAgent> agents, Set<Id> links, Network network) {
		this.agents = agents;
		this.links = links;
		this.network = network;
		
		
		middleNode = this.network.getNodes().get(new IdImpl("17560200462218"));
		portalNodeSouth = this.network.getNodes().get(new IdImpl("17560200470368"));
		portalNodeNorth = this.network.getNodes().get(new IdImpl("17560200134734"));
		
	}
		
	@Override
	public void applyAgentFilter(Set<Id> set, double time) {
		log.info("this one is not used anymore ...");	
	}

	@Override
	public boolean applyAgentFilter(Id id, double time) {
		MobsimAgent agent = this.agents.get(id);
		Link currentLinkAgent = this.network.getLinks().get(agent.getCurrentLinkId());
//		if (!links.contains(agent.getCurrentLinkId())) return false;
//		else return true;
			
		if (time < 17.0 * 3600.0) {
			double dist0 = CoordUtils.calcDistance(currentLinkAgent.getCoord(), portalNodeSouth.getCoord());
			double dist1 = CoordUtils.calcDistance(currentLinkAgent.getCoord(), portalNodeNorth.getCoord());
			if (Math.min(dist0, dist1) < 1000.0) return true; // replan the agent
			else return false;	
		}
		else {
			double dist2 = CoordUtils.calcDistance(currentLinkAgent.getCoord(), middleNode.getCoord());
			if (dist2 < 5000) return true; // replan the agent
			else return false;
		}
	}
}
