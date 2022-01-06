import java.util.*;
import java.io.*;

/**
 * 
 * @author dd3053
 * contains the details of the Node :
 * 1. Node Name
 * 2. Rewards of a Node
 * 3. EdgeList of the Node
 * 4. Probabilities of the Node
 * 5. Contains if it is a Terminal Node,a Chance Node or a Decision Node.
 *
 */
class Node{
	String name;
	int value;
	float prob;
	LinkedList<String> EdgeList;
	LinkedList<Float> probList;
	boolean isDecisionNode;
	boolean isTerminalNode;
	boolean isChanceNode;
	
	public Node(String name) {
		this.name = name;
		value = 0;//Defaults to 0 
		prob = 1f;
		EdgeList = new LinkedList<>();
		probList = new LinkedList<>();
		isTerminalNode = true;
		isDecisionNode = false;
		isChanceNode = false;
	}
	//Utility Functions for the Class Node
	public void updateValue(int num) {
		if(this.value!=0) {
			System.out.println("Duplicate Entry of a Value Node : "+name); //Returns Error in case more than one entry of the same Node is found in the Input.
			System.exit(-1);
		}
		this.value = num;
	}
	
	public void updateEdgeList(LinkedList<String> edgeList) {
		if(this.EdgeList.size()!=0) {
			System.out.println("Duplicate Entry of an Edge List. Node : "+name); //Returns Error in case more than one entry of the same Node is found in the Input.
			System.exit(-1);
		}
		this.EdgeList = edgeList;
	}
	
	public void updateProbabilityList(LinkedList<Float> probabilityList) {
			if(this.probList.size()!=0) {
				System.out.println("Duplicate Entry of an Probability List of Node : "+name); //Returns Error in case more than one entry of the same Node is found in the Input.
				System.exit(-1);
			}
			this.probList = probabilityList;
	}
	
	public void become_decisionNode() {
		isTerminalNode = false;
		isDecisionNode = true;
		isChanceNode = false;
	}
	
	public void become_chanceNode() {
		isTerminalNode = false;
		isDecisionNode = false;
		isChanceNode = true;
	}
	
	public void become_terminalNode() {
		isTerminalNode = true;
		isDecisionNode = false;
		isChanceNode = false;
	}
	
}

//Class NodeList will only communicate with Class Node.
/**
 * 
 * @author dd3053
 * Handles the NodeList.
 * @param entries: Contains the number of Nodes
 * @param integerToNodeMap : Maps a given Integer to a Node
 * @param nodeToIntegerMap : Maps a given Node to a unique Integer
 * @param isMin : Maintains if the min flag is provided.
 */
class NodeList{
	public int entries = 0;
	public HashMap<Integer,Node> integerToNodeMap;
	public HashMap<Node,Integer> nodeToIntegerMap;
	public HashMap<String,Node>  nodeDetails;
	public boolean isMin;
	
	/************************* UTILITY FUNCTIONS *******************/
	public NodeList() {
		entries = 0;
		integerToNodeMap = new HashMap<>();
		nodeDetails = new HashMap<>();
		nodeToIntegerMap = new HashMap<>();
		isMin = false;
	}
	
	public Node getNode(String nodeName) {
		if(!nodeDetails.containsKey(nodeName)) {
			Node newNode = new Node(nodeName);
			nodeDetails.put(nodeName,newNode);
			integerToNodeMap.put(entries, newNode);
			nodeToIntegerMap.put(newNode, entries);
			entries++;
		}
		return nodeDetails.get(nodeName);
	}
	
	public void updateNodeVal(String nodeName,int num) {
		Node tmpNode = getNode(nodeName);
		tmpNode.updateValue(num);
	}
	
	public void updateNodeEdgeList(String nodeName,LinkedList<String> edgeList) {
		Node tmpNode = getNode(nodeName);
		tmpNode.updateEdgeList(edgeList);
	}
	
	public void updateProbEntry(String nodeName,LinkedList<Float> probabilityEntry) {
		Node tmpNode = getNode(nodeName);
		tmpNode.updateProbabilityList(probabilityEntry);
	}
	
	/**
	 * 
	 * @return a 2D Array(Just 1 column and n-rows for each Node) of Initial Values.
	 * The Array is filled with the Reward Costs of the given Nodes.
	 */
	public float[][] initializeValue() {
		float[][] res = new float[entries][1];
		for(int i=0;i<entries;i++) {
			res[i][0] = integerToNodeMap.get(i).value;
		}
		return res;
	}
	
	/**
	 * This would be only for Decision and Chance Node.
	 * Never for an Terminal Node
	 * @return A new Policy where each Decision Node is mapped to its next Step.
	 * In initial Policy the next Decision for each Node is the first Node in the EdgeList.
	 */
	public HashMap<Integer, Integer> initializePolicy() {
		// TODO Auto-generated method stub
		HashMap<Integer,Integer> res = new HashMap<>();
		
		for(int i = 0;i<entries;i++) {
			Node tmpNode = integerToNodeMap.get(i);
			if(tmpNode.isTerminalNode || tmpNode.isChanceNode)continue;
			Iterator itr = tmpNode.EdgeList.iterator();
			while(itr.hasNext()) {
				String str = (String)itr.next();
				Node itrNode = nodeDetails.get(str);
				res.put(i, nodeToIntegerMap.get(itrNode));
				break;
			}
			
		}
		return res;
	}
	
	
	/**
	 * Validates the Input.
	 * Throws and Reports any Error.
	 */
	public void validateInputs() {
		for(String str : nodeDetails.keySet()) {
			Node tmpNode = nodeDetails.get(str);
			//Lets check RuleWise : 
			//Single Probability Entry should be Decision Node
			if(tmpNode.EdgeList.size()==0) {//If no Edges are present then it is terminal Node
				if(tmpNode.probList.size()!=0) {
					System.out.println("Terminal Node cannot have Probability Entry. Exiting Program");//If a node has no edges it is terminal. A probability entry for such a node is an error.
					System.exit(-1);
				}
				tmpNode.become_terminalNode();
			}else {
				if(tmpNode.probList.size()==0) {
					tmpNode.probList.add(1f);//If a node has edges but no probability entry, it is assumed to be a decision node with p=1
					tmpNode.become_decisionNode();
				}else if(tmpNode.probList.size()==1) {
					tmpNode.become_decisionNode();//Single Probability Entry should be Decision Node
				}else if(tmpNode.probList.size() == tmpNode.EdgeList.size()) {// A Node with same number of probabilities : Chance Node
					tmpNode.become_chanceNode();
				}else {
					System.out.println("The Size of Probability List and Edge Lists are not Equal. Exiting Program ");
					System.exit(-1);
				}
			}
			// Sum of probabilities of Chance Node should be 1.0
			if(tmpNode.isChanceNode) {
				float ans = 0f;
				LinkedList<Float> ll = tmpNode.probList;
				Iterator itr = ll.iterator();
				while(itr.hasNext()) {
					float f = (float)itr.next();
					ans += f;
				}
				if(ans!=1f) {
					System.out.println("The Sum of probabilities of a Chance Node must be 1");
					System.exit(-1);
				}
			}
			//A node referenced as an edge must separately have one of the three entries to be valid
			LinkedList<String> edgeList = tmpNode.EdgeList;
			Iterator<String> itr = edgeList.iterator();
			while(itr.hasNext()) {
				String tmpStr = (String)itr.next();
				if(!nodeDetails.containsKey(tmpStr)) {
					System.out.println("Details Missing for Node : "+tmpStr);
					System.exit(-1);
				}
			}
		}
	}
	/**
	 * Takes a given policy and uses it to generate a Transition Matrix.
	 * @param policy
	 * @return a Transition Matrix having values for all the probabilities of the Transition generated using the Policy
	 */
	public float[][] getTransitionMatrix(HashMap<Integer, Integer> policy) {
		float[][] res = new float[entries][entries];
		for(int i = 0;i<entries;i++) {
			Node tmpNode = integerToNodeMap.get(i);
			if(tmpNode.isTerminalNode) {
				continue;
			}else if(tmpNode.isChanceNode) {
				Iterator<String> itr_edge = tmpNode.EdgeList.iterator();
				Iterator<Float> itr_prob = tmpNode.probList.iterator();
				while(itr_edge.hasNext()) {
					String edgeNode = (String)itr_edge.next();
					float floatValue = (float)itr_prob.next();
					Node getNode = nodeDetails.get(edgeNode);
					int index = nodeToIntegerMap.get(getNode);
					res[i][index] = floatValue;
				}
			}else if(tmpNode.isDecisionNode) {
				Iterator<Float> itr_prob = tmpNode.probList.iterator();
				float p = (float)itr_prob.next();
				int n = tmpNode.EdgeList.size();
				float def_val = (1-p)/(n - 1); 
				int thatNode = policy.get(i);
				Iterator<String> edge_itr = tmpNode.EdgeList.iterator();
				while(edge_itr.hasNext()) {
					String edgeName = (String)edge_itr.next();
					Node getNode = nodeDetails.get(edgeName);
					int index = nodeToIntegerMap.get(getNode);
					if(index == thatNode) {
						res[i][index] = p;
					}else {
						res[i][index] = def_val;
					}
				}
			}
		}
		return res;
	}
	/**
	 * 
	 * @param Takes Input as the New Values computed using the ValueIteration
	 * @return A new Greedy Policy based on the min flag.
	 * 
	 */
	public HashMap<Integer, Integer> GreedyPolicyComputation(float[][] newValues) {
		// TODO Auto-generated method stub
		//Only for Decision Nodes :
		HashMap<Integer,Integer> res = new HashMap<>();
		for(int i = 0;i<entries;i++) {
			Node tmpNode = integerToNodeMap.get(i);
			if(tmpNode.isTerminalNode || tmpNode.isChanceNode)continue;
			//Decision Node: Take a decision
			if(isMin) {
				float min_val = Float.MAX_VALUE;
				int mostOptimal = -1;
				Iterator<String> itr = tmpNode.EdgeList.iterator();
				while(itr.hasNext()) {
					String edgeName = (String)itr.next();
					Node itrNode = nodeDetails.get(edgeName);
					int index = nodeToIntegerMap.get(itrNode);
					if(Float.compare(newValues[index][0],min_val)<0) {
						mostOptimal = index;
						min_val = newValues[index][0];
					}
				}
				if(mostOptimal!=-1)res.put(i, mostOptimal);//Puts the most Optimal Value
			}else {
				float max_val = -1*Float.MAX_VALUE;
				int mostOptimal = -1;
				Iterator<String> itr = tmpNode.EdgeList.iterator();
				while(itr.hasNext()) {
					String edgeName = (String)itr.next();
					Node itrNode = nodeDetails.get(edgeName);
					int index = nodeToIntegerMap.get(itrNode);
					if(Float.compare(newValues[index][0],max_val)>0) {
						mostOptimal = index;
						max_val = newValues[index][0];
					}
				}
				if(mostOptimal!=-1)res.put(i, mostOptimal);
			}
			
		}
		return res;
	}
	
	/**
	 * 
	 * @param newPolicy
	 * Prints the Given Policy
	 */
	public void printPolicy(HashMap<Integer, Integer> newPolicy) {
		for(int i: newPolicy.keySet()) {
			Node nodeA = integerToNodeMap.get(i);
			if(nodeA.EdgeList.size()<2)continue;
			Node nodeB = integerToNodeMap.get(newPolicy.get(i));
			System.out.println(nodeA.name + " -> "+nodeB.name);
			
		}
		
	}
	
	/**
	 * 
	 * @param newValues
	 * Prints the Given Values
	 */
	public void printValues(float[][] newValues) {
		for(int i=0;i<newValues.length;i++) {
			Node tmpNode = integerToNodeMap.get(i);
			System.out.print(tmpNode.name + "="+newValues[i][0]+" ");
		}
		
	}

}
//Class Solution Will only Communicate with Class NodeList


public class Solution{
	
	//Parameters : 
	public static float discount = 1F; //Default discount value
	public static boolean min = false;//Default Minimum Flag
	public static float tol = 0.01F;//Default tolerance Value
	public static int iter = 100;//Default Iteration Value
	
	//Global Parameters : 
	public static NodeList myNodeList;//NodeList object for all the Computations
	
	/**
	 * 
	 * @param args: The Command Line Parameters.
	 * Parses and checks the Provided Command Line Parameters.
	 */
	public static void parseParameters(String[] args) {
		for(int i=0;i<args.length - 1;i++) {
			switch(args[i]) {
			
			case "-df": try {
							discount = Float.parseFloat(args[i + 1]);
						}catch(NumberFormatException e) {
							System.out.println("-df should be a Float Value between 0 and 1");
							System.out.println("Exiting Program");
							System.exit(-1);
						}
						if(discount<0 || discount> 1) {
							System.out.println("-df should be a Float Value between 0 and 1");
							System.out.println("Exiting Program");
							System.exit(-1);
						}
						i = i + 1;
						break;
			
				case "-tol": try {
								tol = Float.parseFloat(args[i + 1]);
							}catch(NumberFormatException e) {
								System.out.println("-tol should be a Float Value ");
								System.out.println("Exiting Program");
								System.exit(-1);
							}
							if(tol<0) {
								System.out.println("-tol should be greater than 0");
								System.out.println("Exiting Program");
								System.exit(-1);
							}
							i = i + 1;
							break;
			
			case "-min": myNodeList.isMin = true;
				break;
			
			case "-iter":try {
							iter = Integer.parseInt(args[i + 1]);
						}catch(NumberFormatException e) {
							System.out.println("-iter should be an Integer Value ");
							System.out.println("Exiting Program");
							System.exit(-1);
						}
						if(iter<0) {
							System.out.println("-iter should be greater than 0");
							System.out.println("Exiting Program");
							System.exit(-1);
						}
						i = i + 1;
							break;
			
			default: System.out.println("Wrong Input Flag. Exiting the Program");
					System.exit(-1);
				break;
			}
		}
	}
	/**
	 * 
	 * @param str
	 * @return @true if the provided String consists of AlphaNumeric Characters @false otherwise
	 */
	public static boolean checkAlphaNumeric(String str) {
		for(int i = 0;i<str.length();i++) {
			char tmpChar = str.charAt(i);
			if(tmpChar>='a' && tmpChar<='z')continue;
			if(tmpChar>='A' && tmpChar<='Z')continue;
			if(tmpChar>='0' && tmpChar<='9')continue;
			return false;
		}
		return true;
	}
	
	/**
	 * This functions read the Input File Line by Line.
	 * @param str
	 */
	public static void parseLine(String str) {
		if(str==null || str.length()==0)return;
		str = str.trim();
		if(str.charAt(0)=='#')return; //Comment Line
		
		//Check the Name "=" Value Pair :
		if(str.indexOf("=")>=0) {
			String[] tmpString = str.split("=");
			if(tmpString.length>2) {
				System.out.println("Invalid Input Provided in Input : "+str);
				System.exit(-1);
			}
			tmpString[0] = tmpString[0].trim();
			tmpString[1] = tmpString[1].trim();
			if(!checkAlphaNumeric(tmpString[0])) {
				System.out.println("The Node Name should just be AlphaNumerics : "+str);
				System.exit(-1);
			}
			int num = 0;
			try {
				num = Integer.parseInt(tmpString[1]);
			}catch(NumberFormatException e) {
				System.out.println("The Value of the Node should be an Integer. : "+str);
				System.exit(-1);
			}
			myNodeList.updateNodeVal(tmpString[0],num);
		}else if(str.indexOf(":")>=0){
			//The case of Edge List
			String[] tmpString = str.split(":");
			if(tmpString.length>2) {
				System.out.println("Node Name is invalid : "+ str);
				System.exit(-1);
			}
			tmpString[0] = tmpString[0].trim();
			tmpString[1] = tmpString[1].trim();
			if(!checkAlphaNumeric(tmpString[0])) {
				System.out.println("The Node Name should just be AlphaNumerics : "+str);
				System.exit(-1);
			}
			LinkedList<String> edgeList = new LinkedList<>();
			if(tmpString[1].charAt(0)!='[' || tmpString[1].charAt(tmpString[1].length() - 1)!=']') {
				System.out.println("Invalid EdgeList Provided in Input : "+str);
				System.exit(-1);
			}
			String[] edges = tmpString[1].substring(1,tmpString[1].length() - 1).split(",");
			for(int i = 0;i<edges.length;i++) {
				edgeList.add(edges[i].trim());
			}
			myNodeList.updateNodeEdgeList(tmpString[0],edgeList);
		}else if(str.indexOf("%")>=0){
			//The case of probabilities
			String[] tmpString = str.split("%");
			if(tmpString.length>2) {
				System.out.println("Invalid Probability List Provided in Input : "+str);
				System.exit(-1);
			}
			tmpString[0] = tmpString[0].trim();
			tmpString[1] = tmpString[1].trim();
			if(!checkAlphaNumeric(tmpString[0])) {
				System.out.println("The Node Name should just be AlphaNumerics : "+str);
				System.exit(-1);
			}
			String[] probabilities = tmpString[1].split(" ");
			LinkedList<Float> probabilityEntries = new LinkedList<>();
			for(int i = 0;i < probabilities.length;i++) {
				float f = 0f;
				probabilities[i] = probabilities[i].trim();
				try {
					f = Float.parseFloat(probabilities[i]);
				}catch(NumberFormatException e) {
					System.out.println("Invalid Input Provided in Input (Float Values are Allowed) : "+str);
					System.exit(-1);
				}
				probabilityEntries.add(f);
			}
			myNodeList.updateProbEntry(tmpString[0],probabilityEntries);
		}else {
			System.out.println("Invalid Input Line Provided in Input : "+str);
			System.exit(-1);
		}
		
	}
	
	/**
	 * Reads and parses the provided FileName.
	 * @param fileName
	 */
	public static void readFile(String fileName) {
		if(fileName==null || fileName.length()==0) {
			System.out.println("File Name not provided");
			System.exit(-1);
		}
		Scanner scanner = null;
    	try {
    		FileReader fr = new FileReader(fileName);
			scanner = new Scanner(fr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("File doesn't Exist/ Incorrect File Path");
			System.exit(-1);
		}
    	while(scanner.hasNext()) {
    		String str = scanner.nextLine();
    		parseLine(str);
    	}
    	scanner.close();
	}
	
	
	
	/**
	 * Takes an policy and does the ValueIteration steps until either it achieves the provided Tolerance or does a minimum number of Iterations.
	 * @param policy : A provided Policy
	 * @param oldValues : The Values provided.
	 * @return A new Set of Values.
	 */
	public static float[][] ValueIteration(HashMap<Integer,Integer> policy,float[][] oldValues) {
		float[][] newValues = copyMat(oldValues);//Make a deepCopy
		float[][] tMat = myNodeList.getTransitionMatrix(policy);//Generates the Transition Matrix
		float[][] rewards = myNodeList.initializeValue();//Initialize Values just return the Rewards.
		int itr_count = iter;
		while(itr_count>0) {//Checks Iteration Counts
			itr_count--;
			float[][] ans1 = matMult(tMat,oldValues);
			float[][] ans2 = scalarMult(discount,ans1);
			 newValues = addMult(ans2,rewards);
			 if(checkValuesForTolerance(oldValues,newValues)) { //Checks if a given tolerance have been achieved.
				 break;
			 }
			 oldValues = newValues;
		}
		return newValues;
	}
	/************* UTILITY FUNCTIONS FOR MATRIX COMPUTAIONS***********/
	
	private static boolean checkValuesForTolerance(float[][] oldValues, float[][] newValues) {
		// TODO Auto-generated method stub
		for(int i = 0;i<oldValues.length;i++) {
			float diff = Math.abs(oldValues[i][0] - newValues[i][0]);
			if(diff>tol) {
				return false;
			}
		}
		return true;
	}

	private static float[][] addMult(float[][] ans2, float[][] rewards) {
		float[][] res = new float[ans2.length][1];
		for(int i=0;i<ans2.length;i++) {
			res[i][0] = ans2[i][0] + rewards[i][0];
		}
		return res;
	}

	private static float[][] scalarMult(float discount2, float[][] ans1) {
		// TODO Auto-generated method stub
		float[][] res = new float[ans1.length][1];
		for(int i = 0;i<ans1.length;i++) {
			res[i][0] = discount2*ans1[i][0];
		}
		return res;
	}

	private static float[][] matMult(float[][] tMat, float[][] oldValues) {
		// TODO Auto-generated method stub
		float[][] res = new float[oldValues.length][1];
		for(int i = 0; i < tMat.length;i++) {
			for(int j = 0;j<tMat[0].length ; j++) {
				res[i][0] += tMat[i][j] * oldValues[j][0];
			}
		}
		return res;
	}

	private static float[][] copyMat(float[][] oldValues) {
		float[][] res = new float[oldValues.length][oldValues[0].length];
		for(int i = 0;i<oldValues.length;i++) {
			for(int j = 0;j<oldValues[0].length;j++) {
				res[i][j] = oldValues[i][j];
			}
		}
		return res;
	}

	/**
	 * Takes 2 policies and determine if they are equal.
	 * @param oldPolicy
	 * @param newPolicy
	 * @return @true if the policies are equal @false otherwise.
	 */
	public static boolean samePolicy(HashMap<Integer,Integer> oldPolicy, HashMap<Integer,Integer> newPolicy) {
		for(int i: oldPolicy.keySet()) {
			int val = oldPolicy.get(i);
			if(!newPolicy.containsKey(i)) {
				return false;
			}
			if(newPolicy.get(i)!=val)return false;
		}
		return true;
	}
	/**
	 * It computes the Policy and the Values and generates a new Policy and Values using the ValueIteration and Greedy Policy
	 */
	public static void doComputations() {
		HashMap<Integer,Integer> oldPolicy = myNodeList.initializePolicy();
		float[][] oldValues = myNodeList.initializeValue();
		HashMap<Integer,Integer> newPolicy = myNodeList.initializePolicy();
		float[][] newValues = myNodeList.initializeValue();
		
		while(true) {
			newValues = ValueIteration(oldPolicy,oldValues);
			newPolicy = myNodeList.GreedyPolicyComputation(newValues);
			if(samePolicy(oldPolicy,newPolicy)) {
				break;
			}
			oldValues = newValues;
			oldPolicy = newPolicy;
		}
		myNodeList.printPolicy(newPolicy);
		System.out.println();
		//Print the Values : 
		myNodeList.printValues(newValues);
	}
	
	/**
	 * The Main Function
	 * @param args : Command Line Arguments.
	 */
	public static void main(String[] args) {
		//Read the Inputs : 
		if(args.length == 0) {
			//No Arguments provided.
			System.out.println("No Arguments have been provided");
			System.exit(-1);
		}
		myNodeList = new NodeList();
		parseParameters(args);
		readFile(args[args.length - 1]);
		myNodeList.validateInputs();
		doComputations();

	}
}