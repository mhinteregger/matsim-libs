/*
 * *********************************************************************** *
 * project: org.matsim.*
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2020 by the members listed in the COPYING,        *
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
 * *********************************************************************** *
 */

package org.matsim.contrib.drt.optimizer.insertion;

import org.matsim.api.core.v01.network.Network;
import org.matsim.contrib.drt.optimizer.QSimScopeForkJoinPoolHolder;
import org.matsim.contrib.drt.optimizer.insertion.InsertionCostCalculator.InsertionCostCalculatorFactory;
import org.matsim.contrib.drt.run.DrtConfigGroup;
import org.matsim.contrib.dvrp.path.OneToManyPathSearch.PathData;
import org.matsim.contrib.dvrp.run.AbstractDvrpModeQSimModule;
import org.matsim.contrib.dvrp.run.DvrpModes;
import org.matsim.core.modal.ModalProviders;
import org.matsim.contrib.dvrp.trafficmonitoring.DvrpTravelTimeModule;
import org.matsim.contrib.zone.skims.DvrpTravelTimeMatrix;
import org.matsim.core.router.costcalculators.TravelDisutilityFactory;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;

import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;

/**
 * @author Michal Maciejewski (michalm)
 */
public class SelectiveInsertionSearchQSimModule extends AbstractDvrpModeQSimModule {
	private final DrtConfigGroup drtCfg;

	public SelectiveInsertionSearchQSimModule(DrtConfigGroup drtCfg) {
		super(drtCfg.getMode());
		this.drtCfg = drtCfg;
	}

	@Override
	protected void configureQSim() {
		bindModal(new TypeLiteral<DrtInsertionSearch<PathData>>() {
		}).toProvider(modalProvider(getter -> {
			var insertionCostCalculatorFactory = getter.getModal(InsertionCostCalculatorFactory.class);
			var provider = SelectiveInsertionProvider.create(drtCfg, insertionCostCalculatorFactory,
					getter.getModal(DvrpTravelTimeMatrix.class),
					getter.getModal(QSimScopeForkJoinPoolHolder.class).getPool());
			var insertionCostCalculator = insertionCostCalculatorFactory.create(PathData::getTravelTime, null);
			return new DefaultDrtInsertionSearch(provider, getter.getModal(DetourPathCalculator.class),
					insertionCostCalculator);
		})).asEagerSingleton();

		addModalComponent(SingleInsertionDetourPathCalculator.class,
				new ModalProviders.AbstractProvider<>(getMode(), DvrpModes::mode) {
					@Inject
					@Named(DvrpTravelTimeModule.DVRP_ESTIMATED)
					private TravelTime travelTime;

					@Override
					public SingleInsertionDetourPathCalculator get() {
						Network network = getModalInstance(Network.class);
						TravelDisutility travelDisutility = getModalInstance(
								TravelDisutilityFactory.class).createTravelDisutility(travelTime);
						return new SingleInsertionDetourPathCalculator(network, travelTime, travelDisutility, drtCfg);
					}
				});
		bindModal(DetourPathCalculator.class).to(modalKey(SingleInsertionDetourPathCalculator.class));
	}
}
