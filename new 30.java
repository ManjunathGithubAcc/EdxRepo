import java.util.*;

public class OddsAndEvensRefactored
{
	
	public static Scanner sc = new Scanner(System.in); 
	
	public static void main(String args[])
	{
		getInput();
		sc.close();
	}
	
	public static void getTheInput()
	{
		System.out.println("Let's play a game called \"Odds and Evens\"");
		System.out.println("What is your name?");
		String name = sc.next();
		System.out.println("Hi, "+name+", which do you choose? (O)dds or (E)vens?");
		Boolean validInpFlag = false;
		String inp;
		do
		{
			inp = sc.next();
			if(inp.equalsIgnoreCase("O"))
			{
				validInpFlag = true;
				System.out.println(name+" has chosen Odds. The computer will play Evens!");
			}
			
			else if(inp.equalsIgnoreCase("E"))
			{
				validInpFlag = true;
				System.out.println(name+" has chosen Evens. The computer will play Odds!");
			}
			else
			{
				inp = getTheInput();
			}
		}
		while(validInpFlag == false);
		
		playTheGame(name, inp);
		
	}
	
	public static void playTheGame(String name, String userChoice)
	{
		System.out.println("How many \"fingers\" do you put out?");
				
		Boolean validFlag = false;
		Integer humFing;
		do
		{
			humFing = sc.nextInt();
			if(humFing % 2 == 0 && userChoice.equalsIgnoreCase("o"))
			{
				System.out.println("You should select an ODD number");
			}
			
			else if(humFing % 2 != 0 && userChoice.equalsIgnoreCase("o"))
			{
				validFlag = true;
			}

			else if(humFing % 2 == 0 && userChoice.equalsIgnoreCase("o"))
			{
				System.out.println("You should select an EVEN number");
			}
			
			else if(humFing % 2 == 0 && userChoice.equalsIgnoreCase("e"))
			{
				validFlag = true;
			}
		}
		while(validFlag == false);

		Random rand = new Random();
		Integer computer = rand.nextInt(6);
		
		System.out.println("The computer plays "+computer+" \"fingers\"");
		
		Integer sum = humFing + computer;
		
		System.out.println(humFing+" + "+computer+" = "+sum);
		
		if(sum % 2 == 0)
		{
			System.out.println(sum+" is ....even");
			
			if(userChoice.equalsIgnoreCase("e"))
			{
				System.out.println("You win!");
			}
			else
			{
				System.out.println("You lose!");
			}
		}
		else
		{
			System.out.println(sum+" is ....odd");
			
			if(userChoice.equalsIgnoreCase("o"))
			{
				System.out.println("You win!");
			}
			else
			{
				System.out.println("You lose!");
			}
		}
	}
}
