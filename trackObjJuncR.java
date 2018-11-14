 
/*
 * for code puposes track orientation is:
 * 			SO2	  SO3
 * 			|   /
 *          | /
 * 			|   
 *          | 
 * 			SO1
 */


public class trackObjJuncR {
	
		
		public final int SIZE = 10;
		
		public String name; // Track identifier name ex: "trk.st.1"

		public String SO1; // S01 track orientation marker "R" or "L"
		public String SO2; // SO2 track orientation marker "R" or "L"
		public String SO3; // SO2 track orientation marker "R" or "L"
		
		public trackObjStr conecOnSO1; //what is connected to the right of this track
		public trackObjStr conecOnSO2; //what is connected to the left
		public trackObjStr conecOnSO3; //what is connected to the left
		
	public trackObjJuncR (String n, String SO1, String SO2, trackObjStr s1, trackObjStr s2, 
							trackObjStr s3) {

		this.name = n;
		this.SO1 = SO1;
		this.SO2 = SO2;
		this.conecOnSO1 = s1;
		this.conecOnSO2 = s2;
		this.conecOnSO3 = s3;
			
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

	public trackObjStr getConecOnBtSt() {
		return conecOnSO1;
	}

	public trackObjStr getConecOnTpSt() {
		return conecOnSO2;
	}
		
	public trackObjStr getConnecOnTpR() {
		return conecOnSO3;
	}

}
