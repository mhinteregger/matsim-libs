package playground.wrashid.PDES2;

import java.util.concurrent.locks.Lock;


public abstract class Message implements Comparable {
	
	private static long messageCounter=0;
	public double messageArrivalTime=0;
	public SimUnit sendingUnit;
	public SimUnit receivingUnit;
	public long messageType;
	//private long messageId;
	public String queueKey=""; // only used because of implementation convenience (might be removed in future, if not needed)
	public Object firstLock=null;
	public Object secondLock=null;
	public Lock lock=null;
	private volatile boolean isAlive=true;
	public boolean isAcrossBorderMessage=false;
	public int priority=0;
	//public FairLock lock=null;
	
	// all inheriting or extending modules must
	// invoke this Constructer first
	public Message() {
		//messageId=getMessageCounterAndIncrement();
	}
	
	public double getMessageArrivalTime() {
		return messageArrivalTime;
	}
	
	public void setMessageArrivalTime(double messageArrivalTime) {
		this.messageArrivalTime = messageArrivalTime;
	}
	
	public abstract void printMessageLogString();

	// two messages can not be equal!!!
	public int compareTo(Object obj){
		Message otherMessage= (Message) obj;
		if (messageArrivalTime>otherMessage.messageArrivalTime){
			return 1;
		} else if (messageArrivalTime<otherMessage.messageArrivalTime) {
			return -1;
		} else if (priority>otherMessage.priority) {
			// messages with same arrival time
			// messages with higher priority should be allowed to advance in the queue
			// (inverse logic...)
			return -1;
		}else if (priority<otherMessage.priority) {
			// messages with same arrival time
			return 1;
		}else {
			// messages with same arrival time and priority
			return -1;
		}
	}

	synchronized public static long getMessageCounterAndIncrement() {
		messageCounter++;
		return messageCounter-1;
	}
	
	public void killMessage(){
		isAlive=false;
	}
	
	public void reviveMessage(){
		isAlive=true;
	}
	
	public boolean isAlive(){
		return isAlive;
	}
	
	public abstract void recycleMessage();

}
