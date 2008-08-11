/* *********************************************************************** *
 * project: org.matsim.*
 * PlanFilter.java
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2008 by the members listed in the COPYING,        *
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

package playground.marcel.ectm.planfilter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.matsim.basic.v01.Id;
import org.matsim.config.Config;
import org.matsim.gbl.Gbl;
import org.matsim.network.Link;
import org.matsim.network.MatsimNetworkReader;
import org.matsim.network.NetworkLayer;
import org.matsim.network.NetworkWriter;
import org.matsim.network.Node;
import org.matsim.population.MatsimPopulationReader;
import org.matsim.population.Population;
import org.matsim.population.PopulationReader;
import org.matsim.population.PopulationWriter;
import org.matsim.population.algorithms.PersonIdRecorder;
import org.matsim.population.filters.PersonIntersectAreaFilter;
import org.matsim.utils.geometry.CoordImpl;
import org.matsim.utils.misc.Time;
import org.matsim.world.World;

public class PlanFilter {

	/** Generates a list of different combinations of inner and outer radii to figure out
	 * what are the best numbers for the scenario.
	 *
	 * @param args
	 */
	public static void subPopulation(final String[] args) {
		System.out.println("RUN: subPopulation");

		final CoordImpl center = new CoordImpl(683518.0, 246836.0); // Bellevue, Zrh
		double[] smallRadiuses = {5000, 7000, 9000};
		double[] bigRadiuses = {10000, 11000, 12000, 13000, 14000, 15000, 16000, 17000, 18000, 19000, 20000, 25000, 30000, 40000, 50000};

		final Config config = Gbl.createConfig(args);
		final World world = Gbl.createWorld();

		System.out.println("  reading the network... " + (new Date()));
		NetworkLayer network = null;
		network = (NetworkLayer)world.createLayer(NetworkLayer.LAYER_TYPE, null);
		new MatsimNetworkReader(network).readFile(config.network().getInputFile());
		System.out.println("  done.");

		System.out.println("  reading population... " + (new Date()));
		final Population population = new Population(Population.NO_STREAMING);
		PopulationReader plansReader = new MatsimPopulationReader(population);
		plansReader.readFile(config.plans().getInputFile());
		population.printPlansCount();

		System.out.println("  finding sub-networks... " + (new Date()));
		System.out.println("smallRadius\tbigRadius\t#linksSmall\t#linksBig\t#peopleSmall\t#peopleLeavingBig");
		for (double smallRadius : smallRadiuses) {
			for (double bigRadius : bigRadiuses) {

				final Map<Id, Link> smallAOI = new HashMap<Id, Link>();
				final Map<Id, Link> bigAOI = new HashMap<Id, Link>();

				for (Link link : network.getLinks().values()) {
					final Node from = link.getFromNode();
					final Node to = link.getToNode();
					if ((from.getCoord().calcDistance(center) <= smallRadius) || (to.getCoord().calcDistance(center) <= smallRadius)) {
						smallAOI.put(link.getId(),link);
					}
				}
//				System.out.println("  aoi with radius=" + smallRadius + " contains " + smallAOI.size() + " links.");

				for (Link link : network.getLinks().values()) {
					final Node from = link.getFromNode();
					final Node to = link.getToNode();
					if ((from.getCoord().calcDistance(center) <= bigRadius) || (to.getCoord().calcDistance(center) <= bigRadius)) {
						bigAOI.put(link.getId(),link);
					}
				}
//				System.out.println("  aoi with radius=" + bigRadius + " contains " + bigAOI.size() + " links.");

				final PersonIdRecorder recorder = new PersonIdRecorder();
				final PersonLeavesAreaFilter outsideFilter = new PersonLeavesAreaFilter(recorder, bigAOI);
				final PersonIntersectAreaFilter insideFilter = new PersonIntersectAreaFilter(outsideFilter, smallAOI);
				insideFilter.run(population);
//				System.out.println("  persons travelling in small area: " + insideFilter.getCount());
//				System.out.println("  persons leaving big area: " + outsideFilter.getCount());
				System.out.println(smallRadius + "\t" + bigRadius + "\t" + smallAOI.size() + "\t" + bigAOI.size() + "\t" + insideFilter.getCount() + "\t" + outsideFilter.getCount());

			}
		}

		System.out.println("RUN: subPopulation finished");
	}

	/** Generates the subset of all nodes and links within the bigger radius and creates a network from it.
	 * Generates the subset of all persons traveling through the inner circle. Trips that leave the outer
	 * circle are cut on the border. All Trips following a cut trip from a person are removed.
	 *
	 * @param args
	 */
	public static void generateSubsets(final String[] args) {
		System.out.println("RUN: generateSubset");

		final CoordImpl center = new CoordImpl(683518.0, 246836.0); // Bellevue, Zrh
		double smallRadius = 7000;
		double bigRadius = 14000;

		final Config config = Gbl.createConfig(args);
		final World world = Gbl.createWorld();

		System.out.println("  reading the network... " + (new Date()));
		NetworkLayer network = null;
		network = (NetworkLayer)world.createLayer(NetworkLayer.LAYER_TYPE, null);
		new MatsimNetworkReader(network).readFile(config.network().getInputFile());
		System.out.println("  done.");

		System.out.println("  reading population... " + (new Date()));
		final Population population = new Population(Population.NO_STREAMING);
		PopulationReader plansReader = new MatsimPopulationReader(population);
		plansReader.readFile(config.plans().getInputFile());
		population.printPlansCount();
		System.out.println("  finding AOI links");

		final Map<Id, Link> smallAOI = new HashMap<Id, Link>();
		final Map<Id, Link> bigAOI = new HashMap<Id, Link>();

		for (Link link : network.getLinks().values()) {
			final Node from = link.getFromNode();
			final Node to = link.getToNode();
			if ((from.getCoord().calcDistance(center) <= smallRadius) || (to.getCoord().calcDistance(center) <= smallRadius)) {
				smallAOI.put(link.getId(),link);
			}
		}

		// generate sub-net
		NetworkLayer subnet = new NetworkLayer();
		subnet.setName("based on " + config.network().getInputFile());
		subnet.setCapacityPeriod(network.getCapacityPeriod());
		for (Link link : network.getLinks().values()) {
			final Node from = link.getFromNode();
			final Node to = link.getToNode();
			if ((from.getCoord().calcDistance(center) <= bigRadius) || (to.getCoord().calcDistance(center) <= bigRadius)) {
				bigAOI.put(link.getId(),link);
				Node fromNode = link.getFromNode();
				if (!subnet.getNodes().containsKey(fromNode.getId())) {
					subnet.createNode(fromNode.getId().toString(), Double.toString(fromNode.getCoord().getX()), Double.toString(fromNode.getCoord().getY()), fromNode.getType());
				}
				Node toNode = link.getToNode();
				if (!subnet.getNodes().containsKey(toNode.getId())) {
					subnet.createNode(toNode.getId().toString(), Double.toString(toNode.getCoord().getX()), Double.toString(toNode.getCoord().getY()), toNode.getType());
				}
				subnet.createLink(link.getId().toString(), fromNode.getId().toString(), toNode.getId().toString(),
						Double.toString(link.getLength()), Double.toString(link.getFreespeed(Time.UNDEFINED_TIME)),
						Double.toString(link.getCapacity(Time.UNDEFINED_TIME)), Double.toString(link.getLanes(Time.UNDEFINED_TIME)),
						link.getOrigId(), link.getType());
			}
		}
		new NetworkWriter(subnet, "ivtch-osm_zrh14km.xml").write();

		final PopulationWriter plansWriter = new PopulationWriter(population, "plans_miv_zrh7km_cut14km_transitincl_10pct.xml", "v4");
		plansWriter.writeStartPlans();
		final CutTrips cutAlgo = new CutTrips(plansWriter, bigAOI);
		final PersonIntersectAreaFilter insideFilter = new PersonIntersectAreaFilter(cutAlgo, smallAOI);
		insideFilter.run(population);
		plansWriter.writeEndPlans();

		System.out.println("smallR \t bigR \t #linksSmall \t #linksBig \t #personsSmall");
		System.out.println(smallRadius + "\t" + bigRadius + "\t" + smallAOI.size() + "\t" + bigAOI.size() + "\t" + insideFilter.getCount());

		System.out.println("RUN: generateSubset finished");
	}

	public static void main(final String[] args) {
//		subPopulation(args);
		generateSubsets(args);
	}
}
