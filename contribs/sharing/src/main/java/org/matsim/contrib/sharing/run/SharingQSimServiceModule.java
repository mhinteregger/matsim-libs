package org.matsim.contrib.sharing.run;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.contrib.sharing.io.SharingServiceSpecification;
import org.matsim.contrib.sharing.logic.SharingEngine;
import org.matsim.contrib.sharing.logic.SharingLogic;
import org.matsim.contrib.sharing.service.FreefloatingService;
import org.matsim.contrib.sharing.service.SharingService;
import org.matsim.contrib.sharing.service.SharingUtils;
import org.matsim.contrib.sharing.service.StationBasedService;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.modal.AbstractModalQSimModule;
import org.matsim.core.router.RoutingModule;
import org.matsim.core.utils.timing.TimeInterpretation;

import com.google.inject.Singleton;

public class SharingQSimServiceModule extends AbstractModalQSimModule<SharingMode> {
	private final SharingServiceConfigGroup serviceConfig;

	protected SharingQSimServiceModule(SharingServiceConfigGroup serviceConfig) {
		super(SharingUtils.getServiceMode(serviceConfig), SharingModes::mode);
		this.serviceConfig = serviceConfig;
	}

	@Override
	protected void configureQSim() {
		addModalComponent(SharingEngine.class, modalProvider(getter -> {
			EventsManager eventsManager = getter.get(EventsManager.class);
			SharingLogic logic = getter.getModal(SharingLogic.class);
			SharingService service = getter.getModal(SharingService.class);

			return new SharingEngine(service, logic, eventsManager);
		}));

		bindModal(SharingLogic.class).toProvider(modalProvider(getter -> {
			EventsManager eventsManager = getter.get(EventsManager.class);
			Scenario scenario = getter.get(Scenario.class);
			TimeInterpretation timeInterpretation = getter.get(TimeInterpretation.class);

			SharingService service = getter.getModal(SharingService.class);

			RoutingModule accessEgressRoutingModule = getter.getNamed(RoutingModule.class, TransportMode.walk);
			RoutingModule mainModeRoutingModule = getter.getNamed(RoutingModule.class, serviceConfig.getMode());

			return new SharingLogic(service, accessEgressRoutingModule, mainModeRoutingModule, scenario, eventsManager,
					timeInterpretation);
		})).in(Singleton.class);

		bindModal(FreefloatingService.class).toProvider(modalProvider(getter -> {
			Network network = getter.get(Network.class);
			SharingServiceSpecification specification = getter.getModal(SharingServiceSpecification.class);

			return new FreefloatingService(Id.create(serviceConfig.getId(), SharingService.class),
					specification.getVehicles(), network, serviceConfig.getMaximumAccessEgressDistance());
		})).in(Singleton.class);

		bindModal(StationBasedService.class).toProvider(modalProvider(getter -> {
			Network network = getter.get(Network.class);
			SharingServiceSpecification specification = getter.getModal(SharingServiceSpecification.class);

			return new StationBasedService(Id.create(serviceConfig.getId(), SharingService.class), specification,
					network, serviceConfig.getMaximumAccessEgressDistance());
		})).in(Singleton.class);

		switch (serviceConfig.getServiceScheme()) {
		case Freefloating:
			bindModal(SharingService.class).to(modalKey(FreefloatingService.class));
			break;
		case StationBased:
			bindModal(SharingService.class).to(modalKey(StationBasedService.class));
			break;
		default:
			throw new IllegalStateException();
		}
	}
}
