
public class trackObjStr {
	
	public final int SIZE = 10;
	
	public String name; // Track identifier name ex: "trk.st.1"

	public String SO1; // S01 track orientation marker "R" or "L"
	public String SO2; // SO2 track orientation marker "R" or "L"
	
	public trackObjStr conecOnR; //what is connected to the right of this track
	public trackObjStr conecOnL; //what is connected to the left
	
	public String [][] location = new String [2][100]; // Location array [string][string] - [T or F] [1-100], 100 instances = 100 x .1 inch locatin markers
	
public trackObjStr (String n, String SO1, String SO2, trackObjStr R, trackObjStr L) {

	this.name = n;
	this.SO1 = SO1;
	this.SO2 = SO2;
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

public trackObjStr getConecOnR() {
	return conecOnR;
}

public trackObjStr getConecOnL() {
	return conecOnL;
}
	
}
