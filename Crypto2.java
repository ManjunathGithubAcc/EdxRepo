import java.util.Scanner;
import java.util.regex.*;

public class Crypto2 {

	private static Scanner input;

	public static void main(String[] args) {
		// Part 1 - Normalize Text
		System.out.println("Part 1 - Normalize Text");
		String normalize = "This is some \"really\" great. (Text)!?";
		String cadena = normalizeText(normalize);// llamada NORMALIZE

		System.out.println("***********");

		// Part 2 - Caesar Cipher
		// System.out.println("Part 2 - Caesar Cipher");
		System.out.print("Enter the password, it can be negative");
		input = new Scanner(System.in);
		int key = input.nextInt();
		String alfabeto = caesarify(cadena, key);// LLAMADA CESARIFY
		System.out.println("alphabet:  " + alfabeto);
		String Cesar = Obify(cadena, alfabeto, key);// LLAMADA OBIFY
		System.out.println("encrypted string: " + Cesar);
		System.out.println("***********");

		/// Part 3 - Codegroups
		System.out.println("enter block size: ");
		int bloq = input.nextInt();
		String agrupados = groupify(Cesar, bloq);// LLAMADA GRUPIFY
		System.out.println(agrupados);
		System.out.println("***********");
		/// Part 4 - Putting it all together
		System.out.println("original string" + normalize);
		System.out.println("key: " + key);
		System.out.println("cant block letters: " + bloq);
		System.out.println("***********");
		String enderezando = encryptString(normalize, key, bloq);
		System.out.println("final encrypted message:   \n" + enderezando);

		// Part 5 - Hacker Problem - Decrypt
		String retorno = decryptString(enderezando, key, bloq);
		System.out.println("chain without x; " + retorno);
		int key1=-key;
		alfabeto = caesarify(cadena, key1);// LLAMADA CESARIFY
		Cesar = Obify(retorno, alfabeto, key1);// LLAMADA OBIFY
		System.out.println("what went out??? " + Cesar);
	}

	// Part 1 - Normalize Text
	
	public static String normalizeText(String text) {
		String texto = text.toUpperCase();
		texto = texto.replaceAll(" ", "");
		texto = texto.replaceAll("\\s+", "");
		texto = texto.replaceAll("\"", "");
		texto = texto.replaceAll("\'", "");
		texto = texto.replaceAll("[)-?+.^!:,(]", "");
		return texto;
}

	// Part 2 - Caesar Cipher
	public static String caesarify(String cadena, int llave) {

		String shiftA = shiftAlphabet(llave);
		return shiftA;
	}

	public static String shiftAlphabet(int shift) {
		int start = 0;
		if (shift < 0) {
			start = (int) 'Z' + shift + 1;
		} else {
			start = 'A' + shift;
		}
		String result = "";
		char currChar = (char) start;
		for (; currChar <= 'Z'; ++currChar) {
			result = result + currChar;
		}
		if (result.length() < 26) {
			for (currChar = 'A'; result.length() < 26; ++currChar) {
				result = result + currChar;
			}
		}
		return result;
	}

	public static String Obify(String stri, String alfa, int llave) {
		
		StringBuilder str = new StringBuilder(stri);
		for (int i = 0; i < str.length(); i++) {
			switch (str.charAt(i)) {
			case 'A':

				str.deleteCharAt(i);
				str.insert(i, alfa.charAt(0));
				break;
			case 'B':
				str.deleteCharAt(i);
				str.insert(i, alfa.charAt(1));
				break;
			case 'C':
				str.deleteCharAt(i);
				str.insert(i, alfa.charAt(2));
				break;
			case 'D':
				str.deleteCharAt(i);
				str.insert(i, alfa.charAt(3));
				break;
			case 'E':
				str.deleteCharAt(i);
				str.insert(i, alfa.charAt(4));
				break;
			case 'F':
				str.deleteCharAt(i);
				str.insert(i, alfa.charAt(5));
				break;
			case 'G':
				str.deleteCharAt(i);
				str.insert(i, alfa.charAt(6));
				break;
			case 'H':
				str.deleteCharAt(i);
				str.insert(i, alfa.charAt(7));
				break;
			case 'I':
				str.deleteCharAt(i);
				str.insert(i, alfa.charAt(8));
				break;
			case 'J':
				str.deleteCharAt(i);
				str.insert(i, alfa.charAt(9));
				break;
			case 'K':
				str.deleteCharAt(i);
				str.insert(i, alfa.charAt(10));
				break;
			case 'L':
				str.deleteCharAt(i);
				str.insert(i, alfa.charAt(11));
				break;
			case 'M':
				str.deleteCharAt(i);
				str.insert(i, alfa.charAt(12));
				break;
			case 'N':
				str.deleteCharAt(i);
				str.insert(i, alfa.charAt(13));
				break;
			case 'O':
				str.deleteCharAt(i);
				str.insert(i, alfa.charAt(14));
				break;
			case 'P':
				str.deleteCharAt(i);
				str.insert(i, alfa.charAt(15));
				break;
			case 'Q':
				str.deleteCharAt(i);

				str.insert(i, alfa.charAt(16));
				break;
			case 'R':
				str.deleteCharAt(i);
				str.insert(i, alfa.charAt(17));
				break;
			case 'S':
				str.deleteCharAt(i);
				str.insert(i, alfa.charAt(18));
				break;
			case 'T':
				str.deleteCharAt(i);

				str.insert(i, alfa.charAt(19));
				break;
			case 'U':
				str.deleteCharAt(i);
				str.insert(i, alfa.charAt(20));
				break;
			case 'V':
				str.deleteCharAt(i);
				str.insert(i, alfa.charAt(21));
				break;
			case 'W':
				str.deleteCharAt(i);
				str.insert(i, alfa.charAt(22));
				break;
			case 'X':
				str.deleteCharAt(i);
				str.insert(i, alfa.charAt(23));
				break;
			case 'Y':
				str.deleteCharAt(i);
				str.insert(i, alfa.charAt(24));
				break;
			case 'Z':
				str.deleteCharAt(i);
				str.insert(i, alfa.charAt(25));
				break;
			default:
				break;

			}

		}
		String str1 = str.toString();
		return str1;

	}

	/// Part 3 - Codegroups
	
	public static String groupify(String cadena, int bloque) {
		int largo = cadena.length();

		int equis = (largo) % bloque;
		int bloq = bloque - equis;
		StringBuilder cadena2 = new StringBuilder(cadena);
		int contador = 0;
		if (equis != 0) {
			for (int i = 0; i < bloq; i++)
				cadena2.append('x');
		}
		for (int i = bloque; i < cadena2.length(); i = i + bloque + 1) {

			cadena2.insert(i, " ");
		}

		cadena = cadena2.toString();
		// System.out.println(cadena2.length());
		return cadena;

	}

	// Part 4 - Putting it all together
	public static String encryptString(String caden, int shift, int size) {

		String caden1 = normalizeText(caden);
		String alfa = caesarify(caden1, shift);

		String caden3 = Obify(caden1, alfa, shift);
		String caden4 = groupify(caden3, size);
		return caden4;
	}

	// Part 5 - Hacker Problem - Decrypt
	

	public static String decryptString(String cadena, int key, int bloq) {
		String desenvuelto = ungroupify(cadena);
		String cadenaSinX = SinEquis(desenvuelto);
		
		return cadenaSinX;}

	public static String ungroupify(String str) {
		String str1=str.replaceAll("\\s","");
		return str1;}

	public  static String SinEquis(String cadena){
		StringBuilder str2=new StringBuilder(cadena);
		for(int i=0;i<str2.length();i++) {
			if(str2.charAt(i)=='x') {
				str2.delete(i, str2.length());

			}
		}

		String str1 = str2.toString();
		return str1;

	}
	
}