package unl.cse; 

import java.util.ArrayList;

public class Broker extends Person {

	private String secID;
	private BrokerType type;
	
	public Broker(String code, String lastName, String firstName, Address address, ArrayList<String> emails,
			String secID, BrokerType type) {
		super(code, lastName, firstName, address, emails);
		this.secID = secID;
		this.type = type;
	}
	
	//Calculates the commissions based on the BrokerType
	public double calculateCommission(BrokerType type, double expectedReturn){
		double commission = 0;
		if (type == BrokerType.EXPERT){
			commission = .05 * expectedReturn;
		} else {
			commission = .02 * expectedReturn;
		}
		return commission;
	}
	//Calculates the fees based on the BrokerType
	public double calculateFees(BrokerType type, double assetCount){
		double fees = 0;
		if (type == BrokerType.EXPERT){
			fees = 10 * assetCount;
		} else {
			fees = 50 * assetCount;
		}

		return fees;
		
	}
	
	public String getSecID() {
		return secID;
	}

	public BrokerType getType() {
		return type;
	}
	
}

