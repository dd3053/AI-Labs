import java.util.*;
import java.io.*;


/**
 * 
 * @author dd3053
 * @Class Node : This a Node which is used for the Parse Tree.
 * @param op: The operation(&, | , !, =>, <=>) or Atom Name
 * @sign : Contains whether there is an negation operation in front.
 * 			True : Indicates there is no Negation operation in front of it.
 * 			False: Indicates there is a Negation Operation in front of it.
 * @leftChild : The left Child of the given Node
 * @rightChild : The right Child of the Given Node.
 */
class Node{
	String op;
	boolean sign;
	Node leftChild;
	Node rightChild;
	Node(String str){
		op = str;
		sign = true;
	}
}
/**
 * 
 * @author dd3053
 * @Class DPLLNode : Contains the information related to the DPLLNodes.
 * @name : The Atom Name in a CNF
 * @value : Current Value of the Atom. Possible Value : true,false,UNBOUNDED
 * @isNegation : True if there is a Negation in front of it.
 * 
 */
class DPLLNode{
	String name;
	boolean isNegation;
	String value;
	DPLLNode(String name){
		this.name = name;
		this.isNegation = false;
		this.value = "UNBOUNDED";
	}
	DPLLNode(DPLLNode old){
		this.name = old.name;
		this.isNegation = old.isNegation;
		this.value = old.value;
	}
	
}

public class Solution{
	
	public static boolean verbose; //Flag which keeps the Verbose Mode On or Off.
	public static String mode; //The mode of operation. Possible values : dpll,solver,cnf
	public static LinkedList<String> listOfExpr;//Maintains the List of Expressions.
	
	/**
	 * An utility function to fetch the Value of a particular atom from the AtomList.
	 * @param node : The node whose value is to be fetched.
	 * @param atomList : The List that contains the updated values of all the Nodes.
	 * @return: The present value of the Atom(true,false or UNBOUNDED)
	 */
	public static String fetchValue(DPLLNode node,LinkedList<DPLLNode> atomList) {
		Iterator itr = atomList.iterator();
		String ans = "";
		while(itr.hasNext()) {
			DPLLNode tmpNode = (DPLLNode)itr.next();
			if(tmpNode.name.equals(node.name)) {
				return tmpNode.value;
			}
		}
		return ans;
	}
	
	
	/**
	 * Utility Function which returns whether the Given Atom occurs in the statement without any negation.
	 * @param node The Node whose value is to be fetched.
	 * @param statements : The CNF Statements.
	 * @return: True if there is a CNF Statement in which the Atom Node appears without Negation sign.
	 * 			False if it doesn't.
	 */
	public static boolean checkForPositiveSign(DPLLNode node,LinkedList<LinkedList<DPLLNode>> statements) {
		Iterator itr = statements.iterator();
		while(itr.hasNext()) {
			LinkedList<DPLLNode> ll = (LinkedList<DPLLNode>) itr.next();
			Iterator itr2 = ll.iterator();
			while(itr2.hasNext()) {
				DPLLNode tmpNode = (DPLLNode)itr2.next();
				if(!tmpNode.name.equals(node.name))continue;
				if(tmpNode.isNegation==false)return true;
			}
		}
		return false;
	}
	
	/**
	 * Utility Function which returns whether the Given Atom occurs in the statement with negation.
	 * @param node The Node whose value is to be fetched.
	 * @param statements : The CNF Statements.
	 * @return: True if there is a CNF Statement in which the Atom Node appears with Negation sign.
	 * 			False if it doesn't.
	 */
	public static boolean checkForNegativeSign(DPLLNode node,LinkedList<LinkedList<DPLLNode>> statements) {
		Iterator itr = statements.iterator();
		while(itr.hasNext()) {
			LinkedList<DPLLNode> ll = (LinkedList<DPLLNode>) itr.next();
			Iterator itr2 = ll.iterator();
			while(itr2.hasNext()) {
				DPLLNode tmpNode = (DPLLNode)itr2.next();
				if(!tmpNode.name.equals(node.name))continue;
				if(tmpNode.isNegation==true)return true;
			}
		}
		return false;
	}
	
	/**
	 * Utility Function which Alphabetically searches through the atomList and if any Atom with Unbounded value it checks
	 * whether its negation occurs or not.
	 * Returns The First Element from the Atom List whose Negation is not present.
	 * @param atomList : The list of Atoms
	 * @param statements : The CNF Statements
	 * @return: The First Element from the Atom List whose Negation is not present. Null if such element doesn't exist.
	 */
	public static DPLLNode negationDoesNotAppear(LinkedList<DPLLNode> atomList,LinkedList<LinkedList<DPLLNode>> statements) {
		Iterator itr = atomList.iterator();
		while(itr.hasNext()) {
			DPLLNode tmpNode = (DPLLNode)itr.next();
			if(!tmpNode.value.equals("UNBOUNDED"))continue;
			boolean hasPositive = checkForPositiveSign(tmpNode,statements);
			boolean hasNegative = checkForNegativeSign(tmpNode,statements);
			if(!hasPositive && hasNegative) {
				tmpNode.value = "false";
				return tmpNode;
			}
			if(hasPositive && !hasNegative) {
				tmpNode.value = "true";
				return tmpNode;
			}
		}
		return null;
	}
	
	/**
	 * An utility function to find the value of a given Node. 
	 * If the Node occurs without negation, the value is true
	 * If the Node occurs with Negation the value is set to false.
	 * @param node
	 * @param statements
	 */
	public static void obviousAssignment(DPLLNode node,LinkedList<LinkedList<DPLLNode>> statements) {
		Iterator itr = statements.iterator();
		while(itr.hasNext()) {
			LinkedList<DPLLNode> ll = (LinkedList<DPLLNode>)itr.next();
			Iterator itr2 = ll.iterator();
			while(itr2.hasNext()) {
				DPLLNode tmpNode = (DPLLNode)itr2.next();
				if(tmpNode.name.equals(node.name)) {
					if(tmpNode.isNegation==true) {
						node.value = "false";
					}else {
						node.value = "true";
					}
					return;
				}
			}
		}
	}
	/**
	 * Takes the input Atom Node and assigns the Value.
	 * If the Node Value is true, it eliminates all the statements which have Node without negation and eliminates the Nodes where it occurs with Negation.
	 * If the Node Value is false, it eliminates all the statements which have Node with negation and eliminates the Nodes where it occurs withouts Negation.
	 * @param node : Takes the input Atom as Node whose Value needs to be assigned.
	 * @param statements : The List of CNF Statements
	 * @return : The new List of Statements where the Atom node has been assigned.
	 * 
	 */
	public static LinkedList<LinkedList<DPLLNode>> propogate(DPLLNode node,LinkedList<LinkedList<DPLLNode>> statements){
		Stack<Integer> row = new Stack<>();
		Iterator itr = statements.iterator();
		int index = 0;
		while(itr.hasNext()) {
			LinkedList<DPLLNode> ll = (LinkedList<DPLLNode>)itr.next();
			Iterator itr2 = ll.iterator();
			while(itr2.hasNext()) {
				DPLLNode tmpNode = (DPLLNode)itr2.next();
				if(tmpNode.name.equals(node.name)) {
					if(node.value.equals("false") && tmpNode.isNegation==true) {
						row.push(index);
						break;
					}else if(node.value.equals("true") && tmpNode.isNegation==false) {
						row.push(index);
						break;
					}
				}
			}
			index++;
		}
		//Delete the indexes.
		while(!row.isEmpty()) {
			int a = row.pop();
			statements.remove(a);
		}
		
		Stack<Integer> q_row = new Stack<>();
		Stack<Integer> q_col = new Stack<>();
		
		itr = statements.iterator();
	    index = 0;
		while(itr.hasNext()) {
			LinkedList<DPLLNode> ll = (LinkedList<DPLLNode>)itr.next();
			Iterator itr2 = ll.iterator();
			int column = 0;
			while(itr2.hasNext()) {
				DPLLNode tmpNode = (DPLLNode)itr2.next();
				if(tmpNode.name.equals(node.name)) {
				if(node.value.equals("false") && tmpNode.isNegation==false) {
					q_row.push(index);
					q_col.push(column);
				}else if(node.value.equals("true") && tmpNode.isNegation==true) {
					q_row.push(index);
					q_col.push(column);
				}
			}
				column++;
			}
			index++;
		}
		
		while(!q_row.isEmpty()) {
			int x = q_row.pop();
			int y = q_col.pop();
			LinkedList<DPLLNode> ll = statements.get(x);
			ll.remove(y);
		}
		
		return statements;
	}
	/**
	 * An utility function to copy the Statements.
	 * @param statements
	 * @returns : The copies statements.
	 */
	public static LinkedList<LinkedList<DPLLNode>> copyStatements(LinkedList<LinkedList<DPLLNode>>  statements){
		LinkedList<LinkedList<DPLLNode>> newStatement = new LinkedList<>();
		Iterator itr = statements.iterator();
		while(itr.hasNext()) {
			LinkedList<DPLLNode> ll = (LinkedList<DPLLNode>)itr.next();
			Iterator itr2 = ll.iterator();
			LinkedList<DPLLNode> new_ll = new LinkedList<>();
			while(itr2.hasNext()) {
				DPLLNode tmpNode = (DPLLNode)itr2.next();
				new_ll.add(new DPLLNode(tmpNode));
			}
			newStatement.add(new_ll);
		}
		return newStatement;
	}
	
	/**
	 * An utility function to copy the List of DPLLNodes.
	 * @param arr
	 * @return The copies List of DPLL Node.
	 */
	public static LinkedList<DPLLNode> copyDPLLNode(LinkedList<DPLLNode> arr){
		LinkedList<DPLLNode> res = new LinkedList<>();
		Iterator itr = arr.iterator();
		while(itr.hasNext()) {
			DPLLNode tmpNode = (DPLLNode)itr.next();
			res.add(new DPLLNode(tmpNode));
		}
		return res;
	}
	/**
	 * Utility Function to print all the statements.
	 * @param statement
	 */
	public static void printStatement(LinkedList<LinkedList<DPLLNode>> statement) {
		Iterator itr = statement.iterator();
		while(itr.hasNext()) {
			LinkedList<DPLLNode> ll = (LinkedList<DPLLNode>)itr.next();
			Iterator itr2 = ll.iterator();
			while(itr2.hasNext()) {
				DPLLNode tmpNode = (DPLLNode)itr2.next();
				if(!tmpNode.isNegation)System.out.print(tmpNode.name+" ");
				else System.out.print("!"+tmpNode.name+" ");
			}
			System.out.println();
		}
	}
	/**
	 * It executes the DPLL Algorithm
	 * @param atomList : The input List of Atoms.
	 * @param statements : The input List of Statements.
	 * @return The values of DPLLNode if such values exits. Null otherwise.
	 */
	public static LinkedList<DPLLNode> ExecuteDPLL(LinkedList<DPLLNode> atomList,LinkedList<LinkedList<DPLLNode>> statements){
		boolean easyCases = true;
		while(easyCases) {
		easyCases = false;
		if(statements.size()==0) {
			Iterator itr = atomList.iterator();
			while(itr.hasNext()) {
				DPLLNode tmpNode = (DPLLNode)itr.next();
				if(tmpNode.value.equals("UNBOUNDED")) {
					tmpNode.value = "false";
					if(verbose)System.out.println("unbound "+tmpNode.name+"="+tmpNode.value);
				}
			}
			
			return atomList;
		}
		//Check Statements and if all the values of a Statement is found.
		//Then if the sentence remains return nil : No solution available.
		Iterator itr = statements.iterator();
		while(itr.hasNext()) {
			LinkedList<DPLLNode> ll = (LinkedList<DPLLNode>) itr.next();
			Iterator itr2 = ll.iterator();
			if(ll.size()==0)continue;
			boolean flag = true;
			while(itr2.hasNext()) {
				DPLLNode tmpNode = (DPLLNode)itr2.next();
				String res = fetchValue(tmpNode,atomList);
				if(res.equals("UNBOUNDED")) {
					flag = false;
					break;
				}
			}
			
			if(flag) {
				return null;//Answer cannot exist.
			}
		}
			//Easy Cases:
		
			DPLLNode tmpNode = negationDoesNotAppear(atomList,statements);
			if(tmpNode!=null) {
				easyCases = true;
				if(verbose)System.out.println("easyCase "+tmpNode.name+" = "+tmpNode.value);
				obviousAssignment(tmpNode,statements);
				statements = propogate(tmpNode,statements);
				if(verbose)printStatement(statements);
			}
		}
		//Hard Cases:
		Iterator itr = atomList.iterator();
		while(itr.hasNext()) {
			DPLLNode tmpNode = (DPLLNode)itr.next();
			if(tmpNode.value.equals("UNBOUNDED")) {
				tmpNode.value = "true";
				if(verbose)System.out.println("hard case, guess: "+tmpNode.name+"="+tmpNode.value);
				LinkedList<LinkedList<DPLLNode>> newStatement = copyStatements(statements);
				newStatement = propogate(tmpNode,newStatement);
				if(verbose) {
					printStatement(newStatement);
				}
				LinkedList<DPLLNode> newAtomList = copyDPLLNode(atomList);
				newAtomList = ExecuteDPLL(newAtomList,newStatement);
				if(newAtomList==null) {
					tmpNode.value = "false";
					if(verbose)System.out.println("fail|hard case, try: "+tmpNode.name+"="+tmpNode.value);
					newStatement = copyStatements(statements);
					newStatement = propogate(tmpNode,newStatement);
					if(verbose) {
						printStatement(newStatement);
					}
					newAtomList = copyDPLLNode(atomList);
					newAtomList = ExecuteDPLL(newAtomList,newStatement);
				}
				if(newAtomList==null) {
					if(verbose) {
						System.out.println(tmpNode.name+" Contradiction");
					}
				}
				return newAtomList;
				
			}
		}
		return null;
	}
	/**
	 * Prints all the Value of Atoms.
	 * @param atomList
	 */
	public static void printValues(LinkedList<DPLLNode> atomList) {
		Iterator itr = atomList.iterator();
		while(itr.hasNext()) {
			DPLLNode tmpNode = (DPLLNode)itr.next();
			System.out.println(tmpNode.name+"="+tmpNode.value);
		}
	}
	/**
	 * The code for DPLL Solver.
	 * @args : null
	 * @returns : null
	 * Prints the Values of the Atoms if there exists a Solution.
	 * Prints "NO VALID ASSIGNMENT" in case there is no possible solution.
	 * 
	 */
	public static void DPLLSolver() {
		//Will Create a List of List of DPLLNodes.
		HashSet<String> set = new HashSet<>(); //The HashSet checks whether there is an already seen atom while going through the statements and checking for the Atoms.
		LinkedList<DPLLNode> atomList = new LinkedList<>();//The list of Atoms.
		LinkedList<LinkedList<DPLLNode>> statements = new LinkedList<>();//The list of statements
		Iterator itr = listOfExpr.iterator();
		while(itr.hasNext()) {
			String str = (String)itr.next();
			LinkedList<DPLLNode> ll = new LinkedList<>();
			str = str.trim();
			int tmp_startIndex = 0;
			// 
			//Parses the Expressions Provided and makes the list of available Atoms and Sentences.
			//
			while(tmp_startIndex<str.length()){
				if(str.charAt(tmp_startIndex) ==' ') {
					tmp_startIndex++;
					continue;
				}
				int i = tmp_startIndex;
				if(str.charAt(tmp_startIndex)=='!') {
					i++;
					while(str.charAt(i)==' ')i++;
				}
				for(;i<str.length();i++) {
					if(str.charAt(i)==' ')break;
				}
				
				if(str.charAt(tmp_startIndex)=='!') {
					String tmpString = str.substring(tmp_startIndex+1,i).trim();
					DPLLNode newNode = new DPLLNode(tmpString);
					newNode.isNegation = true;
					ll.add(newNode);
					if(!set.contains(tmpString)) {
						atomList.add(newNode);
						set.add(tmpString);
					}
				}else {
					String tmpString = str.substring(tmp_startIndex,i);
					DPLLNode newNode = new DPLLNode(tmpString);
					ll.add(newNode);
					if(!set.contains(tmpString)) {
						atomList.add(newNode);
						set.add(tmpString);
					}
				}
				tmp_startIndex = i+1;
			}
			statements.add(ll);
		}
		//Sorts the collection of atoms on Alphabatical Order
		Collections.sort(atomList,(x,y)->{
			return x.name.compareTo(y.name);
		});
		//Executes the DPLL Algorithm using the atomList and statements.
		if(verbose)printStatement(statements);
		LinkedList<DPLLNode> ans = ExecuteDPLL(atomList,statements);
		if(ans==null) {	//If answer is null there is no Valid Assignment.
			System.out.println("NO VALID ASSIGNMENT");
		}else {
			printValues(ans);//If answer exists the answer gets printed.
		}
	}
	/**
	 * An utility function to copy a Node.
	 * @param root
	 * @return new Copied Node
	 */
	public static Node copyNode(Node root) {
		if(root==null)return null;
		Node ans = new Node(root.op);
		ans.sign = root.sign;
		ans.leftChild = copyNode(root.leftChild);
		ans.rightChild = copyNode(root.rightChild);
		return ans;
	}
	/**
	 * Solves the If And Only If Expression "<=>"
	 * @param root
	 * @return The Parse Tree with solving the "<=>"
	 */
	public static Node solveIfAndOnlyIf(Node root) {
		if(root==null)return null;
		root.leftChild = solveIfAndOnlyIf(root.leftChild);
		root.rightChild = solveIfAndOnlyIf(root.rightChild);
		if(root.op.equals("<=>")) {
			root.op = "&";
			
			Node tmpLeft = root.leftChild;
			Node tmpRight = root.rightChild;
			
			root.leftChild = new Node("=>");
			root.leftChild.leftChild = copyNode(tmpLeft);
			root.leftChild.rightChild = copyNode(tmpRight);
			
			root.rightChild = new Node("=>");
			root.rightChild.leftChild = copyNode(tmpRight);
			root.rightChild.rightChild = copyNode(tmpLeft);
			
		}
		return root;
	}
	/**
	 * Solves the Implies Expression "=>"
	 * @param root
	 * @return The Parse Tree with solving the "=>"
	 */
	public static Node solveImplies(Node root) {
		if(root==null)return null;
		root.leftChild = solveImplies(root.leftChild);
		root.rightChild = solveImplies(root.rightChild);
		if(root.op.equals("=>")) {
			root.op = "|";
			root.leftChild.sign = !root.leftChild.sign;
		}
		return root;
	}
	/**
	 * Solves the DeMorgan Expression
	 * @param root
	 * @return The Parse Tree after Solving the DeMorgan Expression
	 */
	public static Node solveDeMorgan(Node root) {
		if(root==null)return null;
		if(root.op.equals("|") && root.sign==false) {
			root.op = "&";
			root.sign = true;
			root.leftChild.sign = !root.leftChild.sign;
			root.rightChild.sign = !root.rightChild.sign;
		}else if(root.op.equals("&") && root.sign==false) {
			root.op = "|";
			root.sign = true;
			root.leftChild.sign = !root.leftChild.sign;
			root.rightChild.sign = !root.rightChild.sign;
		}
		root.leftChild = solveDeMorgan(root.leftChild);
		root.rightChild = solveDeMorgan(root.rightChild);
		return root;
	}
	/**
	 * Breaks down a statement into 2 Statements if a "&" sign is at the root.
	 * @param Queue of all the parse trees
	 */
	public static void checkForAndCondition(Queue<Node> q) {
		int n = q.size();
		while(n>0) {
			n--;//Can be a source of potential bug.
			Node tmp = q.poll();
			if(tmp.op.equals("&")) {
				q.add(tmp.leftChild);
				q.add(tmp.rightChild);
				n = q.size();
			}else {
				q.add(tmp);
			}
		}
	}
	/**
	 * Function to Solve the Distribution
	 * @param root
	 * @return The root node after solving the Distribution
	 */
	public static Node Distribute(Node root) {
		if(root==null)return null;
		if(root.op.equals("&")) {
			return root;
		}
		root.leftChild = Distribute(root.leftChild);
		if(root.leftChild!=null && root.leftChild.op.equals("&") && root.op.equals("|")) {
			Node tmp_a = root.rightChild;
			Node tmp_b = root.leftChild.leftChild;
			Node tmp_c = root.leftChild.rightChild;
			root.op = "&";
			
			root.leftChild = new Node("|");
			root.rightChild = new Node("|");
			root.leftChild.leftChild = copyNode(tmp_a);
			root.leftChild.rightChild = copyNode(tmp_b);
			
			root.rightChild.leftChild = copyNode(tmp_a);
			root.rightChild.rightChild = copyNode(tmp_c);
			
		}else {
			root.rightChild = Distribute(root.rightChild);
			if(root.rightChild!=null && root.rightChild.op.equals("&") && root.op.equals("|")) {
				Node tmp_a = root.leftChild;
				Node tmp_b = root.rightChild.leftChild;
				Node tmp_c = root.rightChild.rightChild;
				
				root.op = "&";
				root.leftChild = new Node("|");
				root.rightChild = new Node("|");
				root.leftChild.leftChild = copyNode(tmp_a);
				root.leftChild.rightChild = copyNode(tmp_b);
				
				root.rightChild.leftChild = copyNode(tmp_a);
				root.rightChild.rightChild = copyNode(tmp_c);
			}
		}
		return root;
	}
	
	/**
	 * Checks all the statements and breaks them down if "&" symbol is present on root.
	 * @param q
	 */
	public static void solveDistribution(Queue<Node> q) {
		int n = q.size();
		while(n>0) {
			n--;
			Node tmp = q.poll();
			tmp = Distribute(tmp);
			if(tmp.op.equals("&")) {
				q.add(tmp.leftChild);
				q.add(tmp.rightChild);
				n = q.size();
			}else {
				//Already CNF.
				q.add(tmp);
			}
		}
	}
	
	
	/**
	 * Takes a BNF Tree ans and converts it into one or multiple CNF Statements.
	 * 
	 * @param root
	 * @param ans
	 */
	public static void solveTree(Node root,LinkedList<Node> ans){
		Queue<Node> q = new LinkedList<>();
		q.add(root);
		int n = q.size();
		for(int i=0;i<n;i++) {
			Node res = solveIfAndOnlyIf(q.poll());
			if(res!=null) {
				q.add(res);
			}
		}
		checkForAndCondition(q);
		n = q.size();
		
		for(int i=0;i<n;i++) {
			Node res = solveImplies(q.poll());
			if(res!=null) {
				q.add(res);
			}
		}
		checkForAndCondition(q);
		
		n = q.size();
		for(int i=0;i<n;i++) {
			Node res = solveDeMorgan(q.poll());
			if(res!=null) {
				q.add(res);
			}
		}
		checkForAndCondition(q);
		
		solveDistribution(q);
		
		//After Solution : 
		//Append all elements of q to ans
		while(!q.isEmpty()) {
			ans.add(q.poll());
		}
		
	}
	/**
	 * Starts to process the given List Of Expressions in BNF Form and converts to CNF Form.
	 */
	public static void convertBNFToCNF() {
		LinkedList<Node> ll = new LinkedList<>();
		Iterator itr = listOfExpr.iterator();
		while(itr.hasNext()) {
			ll.add(constructTree((String)itr.next()));
		}
		LinkedList<Node> ans = new LinkedList<>();
		itr = ll.iterator();
		while(itr.hasNext()) {
			solveTree((Node)itr.next(),ans);
		}
		listOfExpr = new LinkedList<>();
		itr = ans.iterator();
		while(itr.hasNext()) {
			listOfExpr.add(inOrder((Node)itr.next()));
		}
		
		itr = listOfExpr.iterator();
		while(itr.hasNext()) {
			String printString = (String)itr.next();
		if(!mode.equals("solver"))	System.out.println(printString);
		}
	}
	/**
	 * 
	 * @param root
	 * @return The String in the inOrder Traversal of a given tree rooted at Node Root.
	 */
	public static String inOrder(Node root) {
		if(root==null) {
			return "";
		}
		String str = root.sign==true?"":"!";
		if(!root.op.equals("|"))str += root.op;
		return inOrder(root.leftChild) + " "+str+" " + inOrder(root.rightChild);
	}
//############## UTILITY FUNCTIONS FOR CONSTRUCTING THE TREENODE ##############################
// The following functions are used for constructing a Parse Tree from the statements.	
	/**
	 * Constructs a tree from a  given string
	 * @param str
	 * @return The Parse Tree rooted at Node ans
	 */
	/*public static Node constructTree(String str) {
		str = str.trim();
		Node ans = constructTree(str,0,str.length()-1);
		return ans;
	}*/
	/**
	 * Checks if it is an atom.
	 * @param str
	 * @return true if it is atom, false otherwise.
	 */
	public static boolean isAtom(String str) {
		int count_neg = 0;
		for(int i=0;i<str.length();i++) {
			char a = str.charAt(i);
			if(a=='<'||a=='='||a=='&'||a=='|')return false;
			if(a=='!')count_neg++;
			if(count_neg>1)return false;
		}
		return true;
	}
	
	/**
	 * An utility function to check the format of String
	 */
	public static int isFirstIndexNot(String str,int sIndex,int lIndex) {
		for(int i=sIndex;i<=lIndex;i++) {
			if(str.charAt(i)=='!')return i;
			if(str.charAt(i)==' ')continue;
			return -1;
		}
		return -1;
	}
	
	/**
	 * An utility function to check the format of String
	 */
	public static int isFirstCharacterBracket(String str,int sIndex,int lIndex) {
		for(int i=sIndex;i<=lIndex;i++) {
			if(str.charAt(i)=='(')return i;
			if(str.charAt(i)==' ')continue;
			return -1;
		}
		return -1;
	}
	
	public static Node constructTree(String str) {
		str = str.trim();
		HashMap<String,Stack<Integer>> map = new HashMap<>();
		String[] precedence = new String[] {"<=>","=>","|","&"};
		for(int i=0;i<precedence.length;i++) {
			map.put(precedence[i], new Stack<>());
		}
		Stack<Integer> stack = new Stack<>();
		for(int i=0;i<str.length();i++) {
			if(str.charAt(i)=='(') {
				stack.push(i);
				continue;
			}
			if(str.charAt(i)==')') {
				if(stack.size()==0) {
					System.out.println("Unbalanced Paraenthesis. Exiting Program....");
					System.exit(-1);
				}else {
					stack.pop();
				}
				continue;
			}
			if(stack.size()>0)continue;//Nothing to check.
			if(str.charAt(i)==' ')continue;
			if(str.charAt(i)=='&') {
				map.get("&").push(i);
			}
			if(str.charAt(i)=='|') {
				map.get("|").push(i);
			}
			if(str.charAt(i)=='=') {
				if(i+1<str.length() && str.charAt(i+1)=='>') {
					map.get("=>").push(i);
					i++;
				}
			}
			if(str.charAt(i)=='<') {
				if(i+1<str.length() && i+2<str.length()) {
					if(str.charAt(i+1)=='=' && str.charAt(i+2)=='>') {
						map.get("<=>").push(i);
					}
				}
			}
		}
		if(stack.size()>0) {
			System.out.println("Unbalanced Paraenthesis");
			System.exit(-1);
		}
		Node ans = null;
		for(int i=0;i<precedence.length;i++) {
			if(map.get(precedence[i]).size()>0) {
				int index = map.get(precedence[i]).pop();
				switch(i) {
				case 0:	ans = new Node("<=>");
						ans.leftChild = constructTree(str.substring(0,index));
						ans.rightChild = constructTree(str.substring(index + 3,str.length()));
					break;
				case 1: ans = new Node("=>");
						ans.leftChild = constructTree(str.substring(0,index));
						ans.rightChild = constructTree(str.substring(index + 2,str.length()));
					break;
				case 2: ans = new Node("|");
						ans.leftChild = constructTree(str.substring(0,index));
						ans.rightChild = constructTree(str.substring(index + 1,str.length()));
					break;
				case 3:	ans = new Node("&");
						ans.leftChild = constructTree(str.substring(0,index));
						ans.rightChild = constructTree(str.substring(index + 1,str.length()));
					break;
				}
				return ans;
			}
		}
		//Either Atom or Brackets
		String str2 = "";
		if(str.charAt(0)=='!') {
			str2 = str.substring(1,str.length()).trim();
			if(str2.charAt(0)=='(' && str2.charAt(str2.length()-1)==')') {
				ans = constructTree(str2.substring(1,str2.length() - 1));
			}else {
				ans = new Node(str2.trim());
			}
			ans.sign = false;
		}else {
			str2 = str.substring(0,str.length()).trim();
			if(str2.charAt(0)=='(' && str2.charAt(str2.length()-1)==')') {
				ans = constructTree(str2.substring(1,str2.length() - 1));
			}else {
				ans = new Node(str2.trim());
			}
		}
		return ans;
	}
	
	
	/**
	 * Constructs a Tree with String str.
	 * @param str
	 * @param startIndex : The starting Index of the String str from which we need to construct a Tree.
	 * @param lastIndex : The last Index of the String str from which we need to construct a tree.
	 * @return : The Parse Tree rooted at ans constructed from the string taking only the indexes from the startIndex to lastIndex.
	 */
	public static Node constructTree(String str,int startIndex,int lastIndex) {
		//Base Case 1 : Atom
		str = str.trim();
		boolean isNegation = false;
		if(isFirstIndexNot(str,startIndex,lastIndex)!=-1) {
			//startIndex = isFirstIndexNot(str,startIndex,lastIndex);
			isNegation = true;
			startIndex++;
		}
		if(isAtom(str.substring(startIndex,lastIndex + 1).trim())) {
			Node ans;
			ans = new Node(str.substring(startIndex,lastIndex+1).trim());
			if(isNegation)ans.sign = false;
			return ans;
		}
		if(isNegation) {
			startIndex--;
		}
		//Base Case 2: Parenthesis Handling
		if(isNegation==true) {
			startIndex++;
		}
		if(isFirstCharacterBracket(str,startIndex,lastIndex)!=-1) {
			//Need to return here.
			//Check if the braces are balanced
				Node ans;
				startIndex = isFirstCharacterBracket(str,startIndex,lastIndex);
				ans = constructTree(str,startIndex + 1,lastIndex - 1);
				if(isNegation) {
					ans.sign = false;
				}
				return ans;
			
		}
		if(isNegation) {
			startIndex--;
		}
		//Now search for the symbols and construct the Parse Tree.
		HashMap<String,Stack<Integer>> map = new HashMap<>();
		String[] precedence = new String[] {"<=>","=>","|","&"};
		for(int i=0;i<precedence.length;i++) {
			map.put(precedence[i], new Stack<>());
		}
		Stack<Integer> stack = new Stack<>();
		for(int i=startIndex;i<=lastIndex;i++) {
			if(str.charAt(i)=='(') {
				stack.push(i);
				continue;
			}
			if(str.charAt(i)==')') {
				if(stack.size()==0) {
					System.out.println("Unbalanced Paraenthesis. Exiting Program....");
					System.exit(-1);
				}else {
					stack.pop();
				}
				continue;
			}
			if(stack.size()>0)continue;//Nothing to check.
			if(str.charAt(i)==' ')continue;
			if(str.charAt(i)=='&') {
				map.get("&").push(i);
			}
			if(str.charAt(i)=='|') {
				map.get("|").push(i);
			}
			if(str.charAt(i)=='=') {
				if(i+1<=lastIndex && str.charAt(i+1)=='>') {
					map.get("=>").push(i);
					i++;
				}
			}
			if(str.charAt(i)=='<') {
				if(i+1<=lastIndex && i+2<=lastIndex) {
					if(str.charAt(i+1)=='=' && str.charAt(i+2)=='>') {
						map.get("<=>").push(i);
					}
				}
			}
		}
		if(stack.size()>0) {
			System.out.println("Unbalanced Paraenthesis");
			System.exit(-1);
		}
		Node ans = null;
		for(int i=0;i<precedence.length;i++) {
			if(map.get(precedence[i]).size()>0) {
				int index = map.get(precedence[i]).pop();
				switch(i) {
				case 0:	ans = new Node("<=>");
						ans.leftChild = constructTree(str,startIndex,index - 1);
						ans.rightChild = constructTree(str,index + 3,lastIndex);
					break;
				case 1: ans = new Node("=>");
						ans.leftChild = constructTree(str,startIndex,index - 1);
						ans.rightChild = constructTree(str,index + 2,lastIndex);
					break;
				case 2: ans = new Node("|");
						ans.leftChild = constructTree(str,startIndex,index - 1);
						ans.rightChild = constructTree(str,index + 1,lastIndex);
					break;
				case 3:	ans = new Node("&");
						ans.leftChild = constructTree(str,startIndex,index - 1);
						ans.rightChild = constructTree(str,index + 1,lastIndex);
					break;
				}
				return ans;
			}
		}
		return null;
		
		
	}
	/**
	 * Verfies whether the provided Character is Valid or Not.
	 * @param a: The input Characters
	 * @return : @true if the Character is a valid Character. Otherwise @false
	 */
	public static boolean isValidCharacter(char a) {
		if(a>='a' && a<='z')return true;
		if(a>='A' && a<='Z')return true;
		if(a=='_')return true;
		if(a=='!')return true;
		if(a==' ')return true;
		if(a=='=')return true;
		if(a=='<')return true;
		if(a=='>')return true;
		if(a=='&')return true;
		if(a=='|')return true;
		if(a=='(')return true;
		if(a==')')return true;
		return false;
	}
	
	/**
	 *  This checks whether the string is contains the valid characters or not.
	 *  If there is an invalid character it exits from the program.
	 * @param str
	 */
	public static void checkForCorrectCharacters(String str) {
		for(int i=0;i<str.length();i++) {
			if(!isValidCharacter(str.charAt(i))) {
				System.out.println("Incorrect Character : "+str.charAt(i));
				System.exit(-1);
			}
		}
	}
	
	/**
	 * This functions check whether each opening bracket is matched with corresponding closing bracket.
	 * @param str : The input String.
	 */
	public static void checkForMatchedBrackets(String str) {
		Stack<Character> stack = new Stack<>();
		for(int i=0;i<str.length();i++) {
			if(str.charAt(i)=='(') {
				stack.push('(');
			}
			if(str.charAt(i)==')') {
				if(stack.isEmpty()) {
					System.out.println("The closing paranthesis doesn't match Sentence : " + str);
					System.exit(-1);
				}else {
					stack.pop();
				}
			}
		}
		if(!stack.isEmpty()) {
			System.out.println("The paranthesis are not properly closed Sentence : " + str);
			System.exit(-1);
		}
	}
	
	/**
	 * Read and fetches the CNF/DPLL Statements.
	 * Validates that the provided name and symbols are valid.
	 * Verifies that the brackets are correctly placed if present.
	 * @param fileName : The name of the Input File.
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
    	listOfExpr = new LinkedList<>();
    	while(scanner.hasNext()) {
    		String str = scanner.nextLine();
    		if(str.length()==0)continue;//Allow Empty Lines
    		checkForCorrectCharacters(str);
    		checkForMatchedBrackets(str);
    		listOfExpr.add(str);
    	}
    	scanner.close();
    	//Print Test for Input.
    	//Iterator<String> itr = listOfExpr.iterator();
    	//while(itr.hasNext()) {
    	//	System.out.println((String)itr.next());
    	//}
	}
	
	public static boolean isCorrectMode(String str) {
		if(str.equals("cnf")||str.equals("dpll")||str.equals("solver"))return true;
		System.out.println("Allowed input values for mode : cnf,dpll,solver");
		return false;
	}
	/**
	 * Verifies the input Command Line Parameters and sets the required values for the file.
	 * @param args : The Command Line Arguments passed.
	 */
	public static void readAndValidateInputs(String[] args) {
		verbose = false;
		if(args.length<3 || args.length>4) {	
			System.out.println("Incorrect Parameters");
			System.exit(-1);
		}
		for(int i=0;i<args.length-1;i++) {
			switch(args[i]) {
			case "-v" : verbose = true;
						break;
			case "-mode" : mode = args[i+1];
						   if(!isCorrectMode(mode)) {
							   System.out.println("Incorrect Mode Value.... Exiting from Program");
							   System.exit(-1);
						   }
						   i++;
						   break;
			default : System.out.println("Incorrect Parameters");
					 System.exit(-1);
					 break;
						 
			}
		}
	}
	
	/**
	 * The main Function where the execution starts.
	 * @param args : The command Line Arguments.
	 * -v : Flag for enabling Verbose
	 * -mode : Input mode. Allowed options : dpll,cnf,solver
	 * Last parameter should be the file name.
	 */
	public static void main(String[] args) {
		readAndValidateInputs(args);
		readFile(args[args.length-1]);
		if(mode.equals("dpll")) {
			DPLLSolver();
		}
		if(mode.equals("cnf")) {
			convertBNFToCNF();
		}
		if(mode.equals("solver")) {
			convertBNFToCNF();
			DPLLSolver();
		}
		
	}
}