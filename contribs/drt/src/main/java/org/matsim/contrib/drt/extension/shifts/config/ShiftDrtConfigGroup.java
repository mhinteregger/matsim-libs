package org.matsim.contrib.drt.extension.shifts.config;

import java.net.URL;
import java.util.Map;

import org.apache.log4j.Logger;
import org.matsim.core.config.ConfigGroup;
import org.matsim.core.config.ReflectiveConfigGroup;
import org.matsim.core.config.groups.ControlerConfigGroup;

/**
 * @author nkuehnel / MOIA
 */
public class ShiftDrtConfigGroup extends ReflectiveConfigGroup {

    private static final Logger log = Logger.getLogger( ControlerConfigGroup.class );

    public static final String GROUP_NAME = "drtShifts";

    private static final String SHIFT_INPUT_FILE = "shiftInputFile";
    private static final String OPERATION_FACILITY_INPUT_FILE = "operationFacilityInputFile";

    private static final String CHANGEOVER_DURATION = "changeoverDuration";
    private static final String SHIFT_SCHEDULE_LOOK_AHEAD = "shiftScheduleLookAhead";
    private static final String SHIFT_END_LOOK_AHEAD = "shiftEndLookAhead";
    private static final String SHIFT_END_RESCHEDULE_LOOK_AHEAD = "shiftEndRescheduleLookAhead";
    private static final String ALLOW_IN_FIELD_CHANGEOVER = "allowInFieldChangeover";

    private static final String CHARGE_AT_HUB_THRESHOLD = "chargeAtHubThreshold";
    private static final String SHIFT_ASSIGNMENT_BATTERY_THRESHOLD = "shiftAssignmentBatteryThreshold";

    private String shiftInputFile;
    private String operationFacilityInputFile;

    private double changeoverDuration = 900;
    private double shiftScheduleLookAhead = 1800;
    private double shiftEndLookAhead = 3600;
    private double shiftEndRescheduleLookAhead = 1800;

    private double chargeAtHubThreshold = 0.5;
    private double shiftAssignmentBatteryThreshold = 0.6;

    private boolean allowInFieldChangeover = true;

    public ShiftDrtConfigGroup() {
        super(GROUP_NAME);
    }

    @Override
    public final Map<String, String> getComments() {
        Map<String,String> map = super.getComments();
        map.put(SHIFT_INPUT_FILE, "path to shift xml");
        map.put(OPERATION_FACILITY_INPUT_FILE, "path to operation facility xml");
        map.put(CHANGEOVER_DURATION, "changeover duration in [seconds]");
        map.put(SHIFT_SCHEDULE_LOOK_AHEAD, "Time of shift assignment (i.e. which vehicle carries out a specific shift) before start of shift in [seconds]");
        map.put(SHIFT_END_LOOK_AHEAD, "Time of shift end scheduling (i.e. plan shift end location) before end of shift in [seconds]");
        map.put(SHIFT_END_RESCHEDULE_LOOK_AHEAD, "Time of shift end rescheduling  (i.e. check whether shift should end" +
                " at a different facillity) before end of shift in [seconds]");
        map.put(ALLOW_IN_FIELD_CHANGEOVER, "set to true if shifts can start and end at in field operational facilities," +
                " false if changerover is only allowed at hubs");
        map.put(CHARGE_AT_HUB_THRESHOLD, "defines the battery state of charge threshold at which vehicles will start charging" +
                " at hubs when not in an active shift. values between [0,1)");
        map.put(SHIFT_ASSIGNMENT_BATTERY_THRESHOLD, "defines the minimum battery state of charge threshold at which vehicles are available " +
                " for shift assignment. values between [0,1)");
        return map;
    }

    @StringSetter( SHIFT_INPUT_FILE )
    public void setShiftInputFile(final String shiftInputFile) {
        this.shiftInputFile = shiftInputFile;
    }

    @StringSetter( OPERATION_FACILITY_INPUT_FILE )
    public void setOperationFacilityInputFile(final String operationFacilityInputFile) {
        this.operationFacilityInputFile = operationFacilityInputFile;
    }

    @StringSetter( CHANGEOVER_DURATION )
    public void setChangeoverDuration(final double changeoverDuration) {
        this.changeoverDuration = changeoverDuration;
    }

    @StringSetter( SHIFT_SCHEDULE_LOOK_AHEAD )
    public void setShiftScheduleLookAhead(final double lookAheadTime) {
        this.shiftScheduleLookAhead = lookAheadTime;
    }

    @StringSetter( SHIFT_END_LOOK_AHEAD )
    public void setShiftEndLookAhead(final double lookAheadTime) {
        this.shiftEndLookAhead = lookAheadTime;
    }

    @StringSetter( SHIFT_END_RESCHEDULE_LOOK_AHEAD )
    public void setShiftEndRescheduleLookAhead(final double lookAheadTime) {
        this.shiftEndRescheduleLookAhead = lookAheadTime;
    }

    @StringSetter( ALLOW_IN_FIELD_CHANGEOVER )
    public void setAllowInFieldChangeover(final boolean allowInFieldChangeover) {
        this.allowInFieldChangeover = allowInFieldChangeover;
    }

    @StringSetter( CHARGE_AT_HUB_THRESHOLD )
    public void setChargeAtHubThreshold(final double chargeAtHubThreshold) {
        this.chargeAtHubThreshold = chargeAtHubThreshold;
    }

    @StringSetter( SHIFT_ASSIGNMENT_BATTERY_THRESHOLD )
    public void setShiftAssignmentBatteryThreshold(final double shiftAssignmentBatteryThreshold) {
        this.shiftAssignmentBatteryThreshold = shiftAssignmentBatteryThreshold;
    }

    @StringGetter( SHIFT_INPUT_FILE )
    public String getShiftInputFile() {
        return this.shiftInputFile;
    }

	public URL getShiftInputUrl(URL context) {
		return shiftInputFile == null ? null : ConfigGroup.getInputFileURL(context, shiftInputFile);
	}

	@StringGetter( OPERATION_FACILITY_INPUT_FILE )
    public String getOperationFacilityInputFile() {
        return this.operationFacilityInputFile;
    }

	public URL getOperationFacilityInputUrl(URL context) {
		return operationFacilityInputFile == null ?
				null :
				ConfigGroup.getInputFileURL(context, operationFacilityInputFile);
	}

	@StringGetter( CHANGEOVER_DURATION )
    public double getChangeoverDuration() {
        return this.changeoverDuration;
    }

    @StringGetter( SHIFT_SCHEDULE_LOOK_AHEAD )
    public double getShiftScheduleLookAhead() {
        return this.shiftScheduleLookAhead;
    }

    @StringGetter( SHIFT_END_LOOK_AHEAD )
    public double getShiftEndLookAhead() {
        return this.shiftEndLookAhead;
    }

    @StringGetter( SHIFT_END_RESCHEDULE_LOOK_AHEAD )
    public double getShiftEndRescheduleLookAhead() {
        return this.shiftEndRescheduleLookAhead;
    }

    @StringGetter( ALLOW_IN_FIELD_CHANGEOVER )
    public boolean isAllowInFieldChangeover() {
        return this.allowInFieldChangeover;
    }

    @StringGetter( CHARGE_AT_HUB_THRESHOLD )
    public double getChargeAtHubThreshold() {
        return this.chargeAtHubThreshold;
    }

    @StringGetter( SHIFT_ASSIGNMENT_BATTERY_THRESHOLD )
    public double getShiftAssignmentBatteryThreshold() {
        return this.shiftAssignmentBatteryThreshold;
    }
}
