
public class trackObjSen {

	public final int SIZE = 5;
	
	public String name; // Track identifier name example: "trk.sen.1"

	public String SO1; // S01 track orientation marker "R" or "L"
	public String SO2; // SO2 track orientation marker "R" or "L"
	
	public String purpose; //What this sensor track is for. example. designate junction, home depot, a depot, a curve
	
	public trackObjSen conecOnR; //what is connected to the right of this track
	public trackObjSen conecOnL; //what is connected to the left
	
	public String [][] TrlocOnTRK = new String [2][50]; // Location array [string][string] - 	[T or F] 
																		// [1-100], 100 instances = 50 x .1 inch location markers bc sensor track is 5 inches

	
	public trackObjSen (String n, String SO1, String SO2, String p, trackObjSen R, trackObjSen L ) {
		this.name = name;
		this.SO1 = SO1;
		this.SO2 = SO2;
		this.purpose = p;
		this.conecOnR = R;
		this.conecOnL = L;
	}
	
	
	public int getSIZE() {
		return SIZE;
	}

	public String getName() {
		return name;
	}

	public String getSO1() {
		return SO1;
	}

	public String getSO2() {
		return SO2;
	}

	public String getPurpose() {
		return purpose;
	}

	public trackObjSen getConecOnR() {
		return conecOnR;
	}

	public trackObjSen getConecOnL() {
		return conecOnL;
	}                                                                                              
	
	
	
	void locUpdateFunc () {	//update train Location
		//TODO
	}

	public void switchTrkFunc () { //activate track switch
		//TODO
	}
	
}
