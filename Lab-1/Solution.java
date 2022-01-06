import java.util.*;
import java.io.*;

/**
 * 
 * @author dd3053
 * @class Node
 * @paramsDescription : 
 * @param Name : AlphaNumeric Name as mentioned in the Input File.
 * @param x : The x coordinate.
 * @param y : The y-coordinate. 
 */
class Node{
	String name;
	int x;
	int y;
	public Node(String name,int x,int y) {
		this.name = name;
		this.x = x;
		this.y = y;
	}
}
/**
 * 
 * @author dd3053
 * @class AStarNode
 * @paramDescription : 
 * @path : Stores the complete path
 * @set : Stores the visited Nodes in the complete path
 * @g : Stores the g value
 * @h : Stores the Heuristic Function Value
 * @t : Stores the total value
 * t = g + h
 */
class AStarNode{
	ArrayList<String> path;
	HashSet<String> set;
	double g;
	double h;
	double t;
	public AStarNode() {
		path = new ArrayList<>();
		set = new HashSet<>();
	}
}
/**
 * 
 * @author dd3053
 * The complete execution of the program gets executed here.
 */
public class Solution {
	
	public static boolean verbose_flag = false;//Flag for Verbose. False : No Display of Verbose. True : Verbose information gets displayed.
	public static String startNode = "";//startNode : The starting Node as mentioned in the input parameter. Should be Alphanumeric.
	public static String endNode = "";	//endNode : The end Node as mentioned in the input parameter. Should be AlphaNumeric.
	public static String algorithm = "";//Algorithm as mentioned in the input parameter. Valid Inputs : "BFS","ASTAR","ID"
	public static int global_depth = 1;//The initial Depth as required. Optional parameter. Can only be provided in case of ID.
	public static HashMap<String,Node> nodeDetails;//HashMap to store the Coordinates of a given Node. The key is the Node Name and its value is the coordinated details (x,y)
	public static HashMap<String,LinkedList<String>> adjList;//HashMap to store the neighbours of a given Node. The key is a given Node Name. The value is the extracted LinkedList of all the Neighbour/Connected Nodes.
	
	/**
	 * 
	 * @param str : String to be printed.
	 * It prints the string on a new line only if [-v] has been passed in the Argument list.
	 */
	public static void logger(String str) {
		if(verbose_flag)System.out.println(str);
	}
	
	
	/**
	 * @description : Searches the graph for a the "endNode" starting from the "startNode" and incrementing the value of depth by 1
	 *                on each iteration.
	 * The iteration starts at the -depth parameter provided.
	 * If the depth parameter is not provided it starts with depth = 1.
	 * If a path is found from startNode to endNode, it prints the complete path.
	 * It call DFS() function on each iteration.
	 */
	public static void IterativeDFS(){
		for(int depth = global_depth;depth>0;depth++) {
			HashSet<String> isVisited = new HashSet<>(); // A new HashSet for every new Maximum Depth to keep track of visited Nodes.
			Stack<String> stack = new Stack<>();	//Stack to print the Solution in case Solution is found.
			if(DFS(startNode,0,depth,isVisited,stack)) { //If Path is found DFS() will return true. The Stack will contain the Solution and it gets printed.
				System.out.print("Solution: "+stack.pop());
				while(!stack.isEmpty()) {
					System.out.print(" -> "+stack.pop());
				}
				break;
			}
		}
	}
	
	/**
	 * 
	 * @param currentNode : The name of the current Node.
	 * @param currentDepth : The current Depth.
	 * @param depth. : Maximum allowed Depth. The DFS will not continue after this depth.
	 * @param isVisited : Keeps the information of the visited Nodes. The visited Nodes are not visited again.
	 * @param stack : Stores the solution in the stack once a path has been found from the startNdoe to the end Node.
	 * @return : Returns true when a path has been found from startNode and endNode going through currentNode.
	 * 			Otherwise, returns false
	 */
	public static boolean DFS(String currentNode,int currentDepth,int depth,HashSet<String> isVisited,Stack<String> stack) {
		if(isVisited.contains(currentNode))return false; //Return false as we are visiting a visited node.
		isVisited.add(currentNode);	//The Node has been visited. Adding it to the Visited List so that we don't visit it again.
		if(currentNode.equals(endNode)) {
			stack.push(currentNode);//EndNode found. Adding it to Stack.
			return true;//Returning True will let know the subprograms which called this function that a Solution exists.
		}
		if(currentDepth==depth) {// We hit the Maximum permissible Depth. Hence, can't explore any further.
			logger("hit depth="+depth+": "+currentNode);
			return false;
		}
		logger("Expand : "+currentNode);//Exploring a found Node.
		if(!adjList.containsKey(currentNode)) {
			return false;	//The Explored Node doesn't have any Neighbours. So returning.
		}
		LinkedList<String> ll = adjList.get(currentNode); //Fetching the Neighbour Node List.
		Collections.sort(ll); //Sorting the Nodes in the list according to their names.
		Iterator itr = ll.iterator();
		while(itr.hasNext()) {
			String tmpString = (String)itr.next();
			if(DFS(tmpString,currentDepth+1,depth,isVisited,stack)==true) { //Visiting the Neighbours. The currentDepth has been incremented in order to track the Depth Reached.
				stack.push(currentNode); //Incase, path is found, inserting this node to answer.
				return true;
			}
		}
		return false;//Returning False when no path is found.
	}
	
	/**
	 * Executes Breadth-First Search.
	 * If startNode is same as endNode. It returns the answer.
	 * Otherwise, the Node is pushed into a Queue.
	 * The Nodes are extracted from the queue and its unvisited neighbours are added to the the Queue again.
	 * In case the endNode is found, the method is returns that no path exists.
	 */
	public static void BFS() {
		if(startNode.equals(endNode)) {	//Handling the Edge case when StartNode is same as EndNode.
			System.out.println("Solution : "+startNode);
			return;
		}
		Queue<String> q = new LinkedList<>();//Maintain the Nodes under consideration.
		HashSet<String> isVisited = new HashSet<>();//T keep track of visited Nodes.
		HashMap<String,String> parentTrace = new HashMap<>();//To trace the complete path, we keep the data of parent Nodes.
		q.add(startNode);//Pushing the Source Node.
		isVisited.add(startNode);//Adding it to visited List.
		while(!q.isEmpty()) {
			int n = q.size();
			for(int i=0;i<n;i++) {
				String tmpString = q.poll();//Fetching the front part of Queue.
				logger("Expanding: "+tmpString);
				if(!adjList.containsKey(tmpString))continue;//If no children we can continue with the loop.
				LinkedList<String> tmp_ll = adjList.get(tmpString);//Fetching the Neighbours of the list.
				Collections.sort(tmp_ll);//Sorting the List in accordance with the Name of the Nodes.
				Iterator itr = tmp_ll.iterator();
				while(itr.hasNext()){
					String tmpString_ll = (String)itr.next();
					if(tmpString_ll.equals(endNode)) {	//Incase the endNode is found.
						parentTrace.put(tmpString_ll,tmpString); //Store the parent Path.
						String currNode = endNode;
						Stack<String> stack = new Stack<>();
						stack.push(endNode);  //Storing the path in a Stack.
						while(parentTrace.containsKey(currNode)) {	//We trace the path until the startNode. The parent of startNode will always be NULL. They will be added to the stack.
							currNode = parentTrace.get(currNode);
							stack.push(currNode);
						}
						System.out.print("Solution : "+stack.pop());
						while(!stack.isEmpty()) {
							System.out.print("->" + stack.pop());//Printing Paths
						}
						return; //We return after printing the Solution.
					}
					if(isVisited.contains(tmpString_ll))continue; //The Node is already visited. So, no need to add it to Queue.
					isVisited.add(tmpString_ll);//Adding it to Visited List
					parentTrace.put(tmpString_ll, tmpString); //Storing the parent Information.
					q.add(tmpString_ll);//Adding the Node to the Queue.
				}
			}
		}
		System.out.println("No Path Exists from Source to Goal");//The Queue is Empty and it can be concluded that no path exists.
		return;//Returning from the function.
	}
	/**
	 * Evaluates g,h & t values.
	 * Keeps a track of the visited Nodes to reach this position.
	 * Keeps the values of visited Nodes.
	 * @param mergeNode : The new Node that is now being added to the path.
	 * @param tmpNode : The AStarNode that has been selected with the least cose.
	 * @return : A new AStarNode which contains the g,h,t,the new path & the hashset.
	 * This will be used for the consideration of new path.
	 */
	public static AStarNode evaluateNode(String mergeNode,AStarNode tmpNode) {
		AStarNode newNode = new AStarNode();//Create a new AStarNode.
		newNode.path = new ArrayList<>(tmpNode.path);//Creating the same path as in previous Node.
		newNode.path.add(mergeNode);//Adding the new Node in the path.
		newNode.set = new HashSet<>(tmpNode.set);//Creating a new Visited List.
		newNode.set.add(mergeNode);//Adding the new Node to visited List.
		if(newNode.path.size()==1)return newNode;//No need to do the calculation when only the Source Node is added as the g value would be 0.
		Node oldNode = nodeDetails.get(tmpNode.path.get(tmpNode.path.size()-1));//Extracting the details of the last Node Visited in tmpNode.
		int x = Math.abs(oldNode.x-nodeDetails.get(mergeNode).x);//Finding x =  | oldNode.x - mergeNode.x |
		int y = Math.abs(oldNode.y - nodeDetails.get(mergeNode).y);//Finding y = |oldNode.y - mergeNode.y|
		newNode.g = tmpNode.g + Math.sqrt(x*x+y*y);//newNode.g = tmpNode.g + (x^2 + y^2)^(1/2)
		x = Math.abs(nodeDetails.get(endNode).x - nodeDetails.get(mergeNode).x);
		y = Math.abs(nodeDetails.get(endNode).y - nodeDetails.get(mergeNode).y);
		newNode.h = Math.sqrt(x*x+y*y);//Shorted possible distance between the newNode and endNode.
		return newNode;//Return the newNode.
	}
	/**
	 * Executes AStar Algorithm.
	 * Creates a PriorityQueue which return the Node with minimum t(=g+h) value.
	 * Initialised with the startNode.
	 * Prints the complete path incase a path has been found. Otherwise prints that the path is not found.
	 */
	public static void AStar() {
		PriorityQueue<AStarNode> pq = new PriorityQueue<>((x,y)->{//Creating a Min-PriorityQueue, which returns the Node with smallest t(=g+h)
			return x.g + x.h>y.g+y.h?1:-1;
		});
		pq.add(evaluateNode(startNode,new AStarNode()));//Add the starting Node.
		while(!pq.isEmpty()) {
			AStarNode tmpNode = pq.poll();//Fetching the Node with the smallest value.
			if(tmpNode.path.size()>1 && !endNode.equals(tmpNode.path.get(tmpNode.path.size()-1))) {
				//Not the Source : The Source shouldn't be displaying the Adding String output.
				//If the picked has solution then no need to add more to it.
				if(verbose_flag) {
					System.out.print("adding "+tmpNode.path.get(0));
					for(int i=1;i<tmpNode.path.size();i++) {
						System.out.print("->"+tmpNode.path.get(i));	//Display path if Verbose is Enabled.
					}
					System.out.println();
				}
			}
			
			String lastNode = tmpNode.path.get(tmpNode.path.size()-1);
			if(lastNode.equals(endNode)) {	//If the Extracted Node contains the endNode. THe Solution has been found.
				System.out.print("Solution : "+tmpNode.path.get(0));
				for(int i=1;i<tmpNode.path.size();i++) {
					System.out.print("->"+tmpNode.path.get(i));	//Print the Solution & Exit.
				}
				return;
			}
			if(!adjList.containsKey(lastNode))continue;//No neighbours for the given Node.
			LinkedList<String> ll = adjList.get(lastNode);
			Collections.sort(ll);//Sorting the Neighbour List in accordance with the Node Names.
			Iterator itr = ll.iterator();
			while(itr.hasNext()) {
				String str = (String)itr.next();
				if(tmpNode.set.contains(str))continue;
				AStarNode tmpNode_itr = evaluateNode(str,tmpNode); //Create a new AStarNode Object with Node named "str" and push it into Node.
				double total = tmpNode_itr.g+tmpNode_itr.h;
				if(verbose_flag) {
					System.out.print(tmpNode_itr.path.get(tmpNode_itr.path.size()-2)+"->"+tmpNode_itr.path.get(tmpNode_itr.path.size()-1));
					System.out.print(" ; "+"g=");
					System.out.printf("%.2f",tmpNode_itr.g);	//Displaying g
					System.out.print(" h=");
					System.out.printf("%.2f", tmpNode_itr.h);	//Displaying h
					System.out.print(" = ");
					System.out.printf("%.2f", total);			//Displaying total.
					System.out.println();
				}
				pq.add(tmpNode_itr);//Adding the new AStarNode to the Min-PriorityQueue.
			}
		}
		System.out.println("No Path Exists");
	}
	
	/**
	 * 
	 * @param str
	 * @return Returns @true if the @str is not a number. Otherwise returns @false.
	 */
	public static boolean isValidAlphaNumeric(String str) {
		if(isValidNumber(str))return false;
		return true;
	}
	
	/**
	 * 
	 * @param str
	 * @return Returns @true if the @str is a number. Otherwise returns @false.
 	 */
	public static boolean isValidNumber(String str) {
		if(str==null || str.length()==0)return false;
		int i=0;
		if(str.charAt(0)=='-')i++;
		for(;i<str.length();i++) {
			if(!(str.charAt(i)>='0' && str.charAt(i)<='9'))return false;
		}
		return true;
	}
	/**
	 * 
	 * @param str
	 * @return Returns @true if the input is valid Algorithm. Returns @false if the algorithm is not valid.
	 * Valid Algorithms : "BFS","ID","ASTAR"
	 */
	public static boolean isValidAlgorithm(String str) {
		if(str.equals("BFS")||str.equals("ASTAR")||str.equals("ID"))return true;
		return false;
	}
	/**
	 * 
	 * @param str
	 * @return Returns @true if the provided @str is a comment. Otherwise returns @false.
	 */
	public static boolean isComment(String str) {
		for(int i=0;i<str.length();i++) {
			if(str.charAt(i)==' ')continue;
			if(str.charAt(i)=='#')return true;
			return false;
		}
		return true;
	}
	
	/**
	 * A helper function to read Input from the command Line and assign the correct values.
	 * @param args
	 * Takes args as input and parses the input.
	 * Detects Errors in input if any and exists if it encounters any error.
	 */
	public static void readAndValidateInputs(String[] args) {
		if(args.length==0) {
			System.out.println("No Input provided");
			System.exit(-1);
		}
		boolean hasStartNode = false;
		boolean hasEndNode = false;
		boolean hasAlgorithm = false;
		boolean hasDepth = false;
		
		for(int i=0;i<args.length;i++) {
    		switch(args[i]) {
    		case "-v" : verbose_flag = true;
    					break;
    		case "-start": startNode = args[i+1];
    				hasStartNode = true;
    				i++;
    				break;
    		case "-goal" : endNode = args[i+1];
    				hasEndNode = true;
    				i++;
    				break;
    		case "-alg" : algorithm = args[i+1];
    				hasAlgorithm = true;
    				i++;
    				break;
    		case "-depth" : if(!isValidNumber(args[i+1])) {
    							System.out.println("Depth should be number");
    							System.exit(-1);
    						}
    							hasDepth = true;
    							global_depth = Integer.parseInt(args[i+1]);
    							i++;
    				break;
    		default : if(i!=args.length-1) {
    						System.out.println("Invalid parameter provided");
    						System.exit(-1);
    					}
    		
    		}
    	}
		//Validate if all the inputs have been received.
		
		if(!hasStartNode || !isValidAlphaNumeric(startNode)) {
			System.out.println("startNode is not a valid alphaNumeric String.");
			System.exit(-1);
		}
		if(!hasEndNode && !isValidAlphaNumeric(endNode)) {
			System.out.println("EndNode is not a valid alphaNumeric String.");
			System.exit(-1);
		}
		if(!isValidAlgorithm(algorithm)) {
			System.out.println("Algorithm Mentioned is not correct");
			System.out.println("Valid Inputs : BFS | ASTAR | ID");
			System.exit(-1);
		}
		if(hasDepth) {
			if(!algorithm.equals("ID")) {
				System.out.println("-depth should be input only in case of Iterative DFS.");
				System.exit(-1);
			}
		}
		
	}
	
	public static boolean hasEntry(LinkedList<String> ll,String str) {
		if(ll==null)return false;
		Iterator itr = ll.iterator();
		while(itr.hasNext()) {
			String tmp = (String)itr.next();
			if(tmp.equals(str))return true;
		}
		return false;
	}
	
	
	/**
	 * 
	 * @param str
	 * Reads the file as provided in the @str.
	 * Reads the file and creates the required Graph.
	 * nodeDetails : Maintains the coordinates of the Node.
	 * adjList : Maintains the Adjacency List.
	 * Exits the program in case it encounters any error.
	 */
	public static void readFile(String str) {
		if(str.length()==0) {
			System.out.println("FileName has not been provided");
			System.exit(-1);
		}
    	Scanner scanner = null;
    	try {
    		String fileName = str;
    		FileReader fr = new FileReader(fileName);
			scanner = new Scanner(fr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("File doesn't Exist/ Incorrect File Path");
			System.exit(-1);
		}
    	nodeDetails = new HashMap<>();
    	adjList = new HashMap<>();
    	while(scanner.hasNext()) {
    		String st = scanner.nextLine();
    		if(st.length()==0)continue;
    		if(isComment(st))continue;
    		String[] sample = st.split(" ");
    		if(sample.length == 2) {
    			if(!isValidAlphaNumeric(sample[0])) {
    				System.out.println("Not a valid AlphaNumericValue : "+sample[0]);
    				scanner.close();
    				System.exit(-1);
    			}
    			if(!isValidAlphaNumeric(sample[1])) {
    				System.out.println("Not a valid AlphaNumericValue : "+sample[1]);
    				scanner.close();
    				System.exit(-1);
    			}
    			
    			if(!adjList.containsKey(sample[0])) {
    				adjList.put(sample[0], new LinkedList<>());
    			}
    			if(!adjList.containsKey(sample[1])) {
    				adjList.put(sample[1], new LinkedList<>());
    			}
    			if(sample[0].equals(sample[1]))continue;
    			LinkedList<String> ll = adjList.get(sample[0]);
    			if(!hasEntry(ll,sample[1])) {
    				ll.add(sample[1]);
    			}
    			ll = adjList.get(sample[1]);
    			if(!hasEntry(ll,sample[0])) {
    				ll.add(sample[0]);
    			}
    		}else if(sample.length==3) {
    			if(!isValidAlphaNumeric(sample[0])) {
    				System.out.println("Not a valid AlphaNumericValue : "+sample[0]);
    				scanner.close();
    				System.exit(-1);
    			}
    			if(nodeDetails.containsKey(sample[0])) {
    				System.out.println("Duplicate Entry");
    				scanner.close();
    				System.exit(-1);
    			}
    			//Error : The sample coordinate be not be numeric.
    			if(!isValidNumber(sample[1])||!isValidNumber(sample[2])) {
    				System.out.println("Incorrect Coordinates");
    				System.exit(-1);
    			}
    			nodeDetails.put(sample[0], new Node(sample[0],Integer.parseInt(sample[1]),Integer.parseInt(sample[2])));
    		}else {
    			System.out.println("Incorrect Input");
    			scanner.close();
    			System.exit(-1);
    		}
    	}
    	scanner.close();
	}
	/**
	 * 1. Checks if the startNode is a valid Node.
	 * 2. Check if the endNode is a valid Node.
	 * 3. Once, the graph details have been received, this function checks if the coordinates of all nodes have been entered.
	 */
	public static void validateGraph() {
		//First check if the start Node & End Node are valid.
		if(!nodeDetails.containsKey(startNode)) {
			System.out.println("Details for StartNode have not been provided.");
			System.out.println("Exiting program .... ");
			System.exit(-1);
		}
		
		if(!nodeDetails.containsKey(endNode)) {
			System.out.println("Details for EndNode have not been provided.");
			System.out.println("Exiting program .... ");
			System.exit(-1);
		}
		
		
		for(String str:adjList.keySet()) {
    		LinkedList<String> ll = adjList.get(str);
    		Iterator itr = ll.iterator();
    		while(itr.hasNext()) {
    			String tmpString = (String)itr.next();
    			if(!nodeDetails.containsKey(tmpString)) {
    				System.out.println("Details have not been provided for Node : "+tmpString);
    				System.exit(-1);
    			}
    		}
    	}
	}
	
	
	/**
	 * Selects the Algorithms in accordance with the variable @algorithm.
	 */
	public static void getPath() {
		switch(algorithm) {
    	case "BFS":BFS();
    				break;
    	case "ID" : IterativeDFS();
    				break;
    	case "ASTAR" : AStar();
    				break;
    	}
    	
	}
	
	/**
	 * 
	 * @param args : Input command Line parameters.
	 * The main program executes here.
	 */
    public static void main(String[] args) {
    	
    	readAndValidateInputs(args);
    	readFile(args[args.length-1]);
    	validateGraph();
    	getPath();
    }
}