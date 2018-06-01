import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Main {

	static ArrayList<Item> LoR = new ArrayList<Item>();	//List of Rules
	static int NoOR=0;	//Number of Original Rules
	static HashMap<String, String> C0 = new HashMap<String, String>();	//<String of Closure, Group num>

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		readFile();
		addAugmentedProduction();
		
		findC0();
		//printC0();
	}

	static void readFile(){
		try {
			File input = new File("rule.txt");
			BufferedReader in = new BufferedReader(new FileReader(input));
			String line=null;
			int numOfRules = 0;
			while((line = in.readLine())!= null) {	//R#
				line = in.readLine();				//actual rule
				LoR.add(new Item(line));
				numOfRules++;
			}
			NoOR = numOfRules++;	//for augmented rule
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	static void addAugmentedProduction() {
		Item r1 = LoR.get(0);
		String r0 = "S`>"+r1.left;
		LoR.add(0,new Item(r0));
	}
	static void findC0() {
		ArrayList<Item> gTemp = new ArrayList<Item>();
		Integer count=0;
		Item iTemp = LoR.get(0);
		//calculate closure of this item
		iTemp.closure.add(iTemp);
		if(iTemp.nextChar != '\0'  && iTemp.isNonTerminal(iTemp.nextChar)) 
			iTemp.closure.addAll(iTemp.getItemsStartWith(iTemp.nextChar));
		gTemp.addAll(iTemp.closure);
		C0.put(iTemp.toStringClosure(), "i"+ count.toString());
		count++;
		System.out.println(iTemp.toStringClosure());
//		Iterator<Character> gtIt = start.gotos.iterator();
//		while(gtIt.hasNext()){
//			gtIt.next()
//		}

		
	}
	static void printC0() {

		try {
			File output = new File("output.txt");
			BufferedWriter bw = new BufferedWriter(new FileWriter(output));
			if(output.isFile() && output.canWrite()) {
				Iterator<String> it = C0.keySet().iterator();
				while(it.hasNext()) {
					String key = it.next();
					String closure = C0.get(key);
				}
			}


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

/**
 * 커널 아이템과 리듀스 아이템 모두를 의미. 
 * 텍스트 자체를 저장하진 않음. 
 * 출력시에만 텍스트화.
 */
class Item{	
	String left;
	String right;
	int dotPos;
	char nextChar;
	ArrayList<Item> closure;
	Set<Character> gotos;
	
	Item(String line){
		int divide = line.indexOf('>');
		this.left = line.substring(0, divide);
		this.right = line.substring(divide+1);
		this.dotPos=0;
		this.nextChar = charAfterDot();
		char l = this.getLeft();
		closure = new ArrayList<Item>();
		//calculate goto list
//		Iterator<Item> iterator = this.closure.iterator();
//		while(iterator.hasNext()){
//			gotos.add(iterator.next().nextChar);
//		}
	}
	
	char charAfterDot() {
		if(dotPos == right.length())	//reduce item일 경우 null문자 
			return '\0';
		else
			return right.charAt(dotPos);	//kernel item일 경우 해당 문자
	}
	char getLeft(){
		if(this.left.length() != 1){
			return '\0';
		}else
			return this.left.charAt(0);
	}
	public ArrayList<Item> getItemsStartWith(char c){	//nonterminal 만났을 때 호출
		ArrayList<Item> result = new ArrayList<Item>();
		Iterator<Item> iterator = Main.LoR.iterator();
		while(iterator.hasNext()) {
			Item temp = iterator.next();
			if(temp.getLeft() == c && temp.dotPos == 0){
				result.add(temp);
				if(temp.nextChar != temp.getLeft() )
					result.addAll(getItemsStartWith(temp.nextChar));
			}
		}
		return result;
	}
	public String toString() {
		StringBuilder sb = new StringBuilder(right);
		sb.insert(dotPos, '.');
		String result="[" + left + "->" + sb.toString() + "]";
		return result;
	}
	public boolean equals(Item that) {
		if((this.left == that.left) && 
				(this.right == that.right) &&
				(this.dotPos == that.dotPos))
			return true;
		return false;
	}

	static Item moveDot(Item item) {
		if(item.dotPos == item.right.length()) {	//reduce item
			return null;
		}else {	//kernel item
			Item newItem = item;
			newItem.dotPos++;
			return newItem;
		}
	}
	public String toStringClosure() {
		String result = "";
		Iterator<Item> iterator = closure.iterator();
		while(iterator.hasNext()) {
			Item i = iterator.next();
			result += i.toString();
		}
		return result;
	}
	
	boolean isNonTerminal(char c) {
		if (c >= 'A' && c <= 'Z')
			return true;
		else
			return false;
	}

}

