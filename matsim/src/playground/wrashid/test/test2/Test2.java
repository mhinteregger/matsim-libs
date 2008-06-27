package playground.wrashid.test.test2;

import java.util.ArrayList;

import org.matsim.testcases.MatsimTestCase;

import playground.wrashid.DES.EventLog;
import playground.wrashid.DES.SimulationParameters;
import playground.wrashid.deqsim.DEQSimStarter;
import playground.wrashid.test.CppEventFileParser;

public class Test2 extends MatsimTestCase {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
		// the config file comes as input
		
		String baseDir="src/playground/wrashid/test/test2/";
		args=new String[1];
			
		args[0]= baseDir + "config.xml";
		DEQSimStarter.main(args);
		
		args[0]= baseDir + "deq_events.txt";
		CppEventFileParser.main(args);
		
		ArrayList<EventLog> eventLog1= SimulationParameters.eventOutputLog;
		
		ArrayList<EventLog> eventLog2= CppEventFileParser.eventLog;
		System.out.println("here...");
		assertEquals(EventLog.compare(eventLog1,eventLog2),true);
	}
	
	
	public void testTest2() {
		//Test2.main(null);
		String baseDir="src/playground/wrashid/test/test2/";
		String[] args=new String[1];
			
		System.out.println("A");
		try {Thread.sleep(100);} catch (InterruptedException e) {}
		
		args[0]= baseDir + "config.xml";
		DEQSimStarter.main(args);
		
		System.out.println("B");
		try {Thread.sleep(100);} catch (InterruptedException e) {}
		
		args[0]= baseDir + "deq_events.txt";
		CppEventFileParser.main(args);
		
		System.out.println("C");
		try {Thread.sleep(100);} catch (InterruptedException e) {}
		
		ArrayList<EventLog> eventLog1= SimulationParameters.eventOutputLog;
		
		ArrayList<EventLog> eventLog2= CppEventFileParser.eventLog;
		
		assertEquals(EventLog.compare(eventLog1,eventLog2),true);
	}
	
	public void testTest1() {
		//Test2.main(null);
		
		assertEquals(true,true);
	}

}
