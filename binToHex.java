import java.util.Scanner;

public class binToHex {
	@SuppressWarnings("resource")
	public static void main(String[] args) 
	{
		Scanner input = new Scanner(System.in);

		String hex = "";
		int remain;

		char hexChars[] = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
		
		System.out.print("Binary Number: ");
		
		String bin = input.next().trim();

		int dec = Integer.parseInt(bin, 2);
		
		while(dec > 0)
		{
			remain = dec % 16;
			hex = hexChars[remain] + hex;
			dec = dec / 16;
		}

		System.out.print("Hexadecimal value: 0x" + hex);
	}
}
