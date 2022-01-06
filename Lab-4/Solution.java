import java.util.*;
import java.io.*;


class Node{
	int distance;
	String label;
	
	public Node(int a,String b) {
		distance = a;
		label = b;
	}
}

class kNNNode{
	String label;
	LinkedList<Integer> ll;
	int N;//To detect incorrect Input
	
	public kNNNode(String str) {
		String[] arr = str.split(",");
		N = arr.length;
		ll = new LinkedList<>();
		for(int i = 0;i<arr.length - 1;i++) {
			try {
				ll.add(Integer.parseInt(arr[i]));
			}catch(NumberFormatException e) {
				System.out.println("Invalid Entry Found : "+str);
				System.exit(-1);
			}
		}
		label = arr[arr.length - 1];
		N = arr.length;
	}
	
}



class kNN{
	public int k;
	public String d;
	public boolean unitw;
	String trainFile;
	String testFile;
	public int N_dimension;
	ArrayList<kNNNode> arr;
	HashSet<String> uniqueLabels;
	LinkedList<String> labelName;
	
	HashMap<String,Integer> commonMap;
	HashMap<String,Integer> precisionMap;
	HashMap<String,Integer> recallMap;
	
	public kNN(String[] args) {
		k = 3;
		d = "e2";
		unitw = false;
		trainFile = "";
		testFile = "";
		arr = new ArrayList<>();
		uniqueLabels = new HashSet<>();
		labelName = new LinkedList<>();
		commonMap = new HashMap<>();
		precisionMap = new HashMap<>();
		recallMap = new HashMap<>();
		
		
		
		for(int i = 0;i<args.length;i++) {
			switch(args[i]) {
			case "-mode" :  i++;
							break;
			case "-k": i++;	
						if(i>=args.length) {
							System.out.println("Value Missing for Flag -k");
							System.exit(-1);
						}
						try {
							k = Integer.parseInt(args[i]);
						}catch(NumberFormatException e) {
							System.out.println("Number Incorrect Format for -k flag");
							System.exit(-1);
						}
						break;
						
			case "-d":	i++;
						if(i>=args.length) {
							System.out.println("Value Missing for Flag -d flag");
							System.exit(-1);
						}
						d = args[i];
						if(!(d.equals("e2") || d.equals("manh"))) {
							System.out.println("Invalid Value for Argument : -d");
							System.out.println("Valid Values for d Flag = e2/manh");
							System.exit(-1);
						}
				
						break;
						
			case "-unitw": unitw = true;
						break;
						
			case "-train": i++;	
						if(i>=args.length) {
							System.out.println("File Name missing for Train");
							System.exit(-1);
						}
						trainFile = args[i];
						break;
						
			case "-test":i++;
						if(i>=args.length) {
							System.out.println("File Name missing for Test");
							System.exit(-1);
						}
						testFile = args[i];
						break;
						
			default:	System.out.println("Unknown Flag");
						System.exit(-1);
						break;
			}
		}
	}
	
	private void printLabelData() {
		
		Iterator itr = labelName.iterator();
		while(itr.hasNext()) {
			String label = (String)itr.next();
			System.out.print("Label="+label+" ");
			System.out.print("Precision="+commonMap.getOrDefault(label,0)+"/"+precisionMap.getOrDefault(label,0)+" ");
			System.out.print("Recall="+commonMap.getOrDefault(label,0)+"/"+recallMap.getOrDefault(label,0));
			System.out.println();
		}
		
	}
	
	public void trainData() {
		if(trainFile==null || trainFile.length()==0) {
			System.out.println("Train File Name not provided");
			System.exit(-1);
		}
		Scanner scanner = null;
		try {
			FileReader filereader = new FileReader(trainFile);
			scanner = new Scanner(filereader);
		}catch(Exception e) {
			System.out.println("Train File doesn't Exist/ Incorrect File Path");
			System.exit(-1);
		}
		boolean flag_firstData = true;
		while(scanner.hasNext()) {
			String str = scanner.nextLine();
			if(str.length()==0)continue;
			if(flag_firstData) {
				kNNNode newNode = new kNNNode(str);
				N_dimension = newNode.N;
				flag_firstData = false;
				arr.add(new kNNNode(str));
			}else {
				kNNNode newNode = new kNNNode(str);
				if(newNode.N!= N_dimension) {
					System.out.println("Dimensions Not Match : "+str);
					System.exit(-1);
				}
				arr.add(new kNNNode(str));
			}
			String l_name = arr.get(arr.size() - 1).label;
			if(!uniqueLabels.contains(l_name)) {
				uniqueLabels.add(l_name);
				labelName.add(l_name);
			}
			if(arr.size() > 1) {
				if(arr.get(arr.size() - 1).N != arr.get(arr.size() - 2).N) {
					System.out.println("Incorrect Input in Train File");
					System.exit(-1);
				}
			}
		}
		scanner.close();
		
	}
	
	public void testData() {
		if(testFile==null || testFile.length()==0) {
			System.out.println("Test File Name not provided");
			System.exit(-1);
		}
		Scanner scanner = null;
		try {
			FileReader filereader = new FileReader(testFile);
			scanner = new Scanner(filereader);
		}catch(Exception e) {
			System.out.println("Test File doesn't Exist/ Incorrect File Path");
			System.exit(-1);
		}
		while(scanner.hasNext()) {
			String str = scanner.nextLine();
			if(str.length()==0)continue;
			getResult(str);
		}
		scanner.close();
	}
	
	private void getResult(String str) {
		kNNNode tmpNode = new kNNNode(str);
		if(tmpNode.N != N_dimension) {
			System.out.println("Incorrect Dimension for : "+str);
			System.exit(-1);
		}
		PriorityQueue<Node> pq = new PriorityQueue<>((x,y)->{
			return y.distance - x.distance;
		});
		for(int i = 0;i<arr.size();i++) {
			int dist = calcDistance(tmpNode,arr.get(i));
			if(pq.size() < k) {
				pq.add(new Node(dist,arr.get(i).label));
			}else {
				if(dist<pq.peek().distance) {
					pq.poll();
					pq.add(new Node(dist,arr.get(i).label));
				}
			}
		}
		if(unitw == true) {
			HashMap<String,Integer> map = new HashMap<>();
			while(!pq.isEmpty()) {
				Node itrNode = pq.poll();
				map.put(itrNode.label, map.getOrDefault(itrNode.label, 0) + 1);
			}
			String ans = "";
			int max = Integer.MIN_VALUE;
			for(String s: map.keySet()) {
				int str_val = map.get(s);
				if(str_val  > max) {
					max = str_val;
					ans = s;
				}
			}
			if(tmpNode.label.equals(ans)){
				commonMap.put(ans, commonMap.getOrDefault(ans, 0) + 1);
			}
			precisionMap.put(ans, precisionMap.getOrDefault(ans, 0) + 1);
			recallMap.put(tmpNode.label, recallMap.getOrDefault(tmpNode.label, 0) + 1);
			System.out.println("want="+tmpNode.label +" got="+ans);
		}else {
			HashMap<String,Double> map = new HashMap<>();
			while(!pq.isEmpty()) {
				Node itrNode = pq.poll();
				double dist = itrNode.distance;
				if(dist>=10000) {
					dist = 0.0001;
				}else {
					dist = 1/dist;
				}
				map.put(itrNode.label, map.getOrDefault(itrNode.label, 0.0) + dist);
			}
			String ans = "";
			double max = 0;
			for(String s: map.keySet()) {
				double str_val = map.get(s);
				if(str_val  > max) {
					max = str_val;
					ans = s;
				}
			}
			if(tmpNode.label.equals(ans)){
				commonMap.put(ans, commonMap.getOrDefault(ans, 0) + 1);
			}
			precisionMap.put(ans, precisionMap.getOrDefault(ans, 0) + 1);
			recallMap.put(tmpNode.label, recallMap.getOrDefault(tmpNode.label, 0) + 1);
			System.out.println("want="+tmpNode.label +" got="+ans);
		}
	}

	private int calcDistance(kNNNode tmpNode, kNNNode mykNNNode) {
		int ans = 0;
		Iterator<Integer> itr1 = tmpNode.ll.iterator();
		Iterator<Integer> itr2 = mykNNNode.ll.iterator();
		while(itr1.hasNext() && itr2.hasNext()) {
			int a = (Integer)itr1.next();
			int b = (Integer)itr2.next();
			if(d.equals("e2")) {
				ans += (a - b)*(a - b);
			}
			if(d.equals("manh")) {
				ans += Math.abs(a - b);
			}
		}
		return ans;
	}

	public void executekNN() {
		trainData();
		testData();
		printLabelData();
	}

}

class kMeansNode{
	public String name;
	LinkedList<Integer> list;
	public int N;
	
	public kMeansNode(String args) {
		list = new LinkedList<>();
		String[] arr = args.split(",");
		for(int i = 0;i<arr.length - 1;i++) {
			try {
				int num = Integer.parseInt(arr[i]);
				list.add(num);
			}catch(NumberFormatException e) {
				System.out.println("Incorrect Details provided for kMeans : "+args);
				System.exit(-1);
			}
		}
		name = arr[arr.length - 1];
		N = arr.length - 1;
	}
}

class kMeansCluster{
	public LinkedList<Double> coord_ll;
	public LinkedList<kMeansNode> kMeans_ll;
	
	public kMeansCluster(String str) {
		coord_ll = new LinkedList<>();
		kMeans_ll = new LinkedList<>();
		String[] arr = str.split(",");
		for(int i = 0;i<arr.length;i++) {
			try {
				coord_ll.add(Double.parseDouble(arr[i]));
			}catch(NumberFormatException e) {
				System.out.println("The provided Coordinates must be numbers");
				System.exit(-1);
			}
		}
	}
	
	public kMeansCluster(kMeansCluster old) {
		coord_ll = new LinkedList<>();
		kMeans_ll = new LinkedList<>();
		Iterator<Double> itr = old.coord_ll.iterator();
		while(itr.hasNext()) {
			coord_ll.add((Double)itr.next());
		}
	}
	
	public void addToCluster(kMeansNode newNode) {
		kMeans_ll.add(newNode);
	}

	public boolean compare(kMeansCluster kMeansCluster) {
		// TODO Auto-generated method stub
		if(kMeansCluster==null)return false;
		LinkedList<Double> ll = kMeansCluster.coord_ll;
		if(ll.size() != coord_ll.size())return false;
		Iterator<Double> itr = coord_ll.iterator();
		Iterator<Double> itr_new = ll.iterator();
		while(itr.hasNext()) {
			Double d1 = (Double)itr.next();
			Double d2 = (Double)itr_new.next();
			if(Math.abs(d1 - d2) > 0.0001)return false;
		}
		return true;
	}
	
	
	
}

class kMeans{
	public String d = "e2";
	public String fileName = "";
	public ArrayList<kMeansCluster> clusterList;
	public ArrayList<kMeansNode> nodeList;
	public int N_dimension;
	
	public kMeans(String[] args) {
		nodeList = new ArrayList<>();
		boolean readClusters = false;
		int i=0;
		for(i = 0; i< args.length && !readClusters;i++) {
			switch(args[i]) {
			case "-mode" :	i++;
							break;
			case "-d" :		
							i++;
							if(i>=args.length) {
								System.out.println("Information not provided for -d Flag");
								System.exit(-1);
							}
							if(!(args[i].equals("e2") || args[i].equals("manh"))){
								System.out.println("Incorrect Parameter Provided for -d Flag.");
								System.out.println("Allowed Values for -d Flags = e2/manh");
								System.exit(-1);
							}
							d = args[i];
							break;
			case "-data" :  i++;
							if(i>=args.length) {
								System.out.println("FileName not provided");
								System.exit(-1);
							}
							fileName = args[i];
							readClusters = true;
							break;
			}
		}
		clusterList = new ArrayList<>();
		boolean flag_Cluster = false;
		for(;i<args.length;i++) {
			kMeansCluster newkMeansCluster = new kMeansCluster(args[i]);
			if(!flag_Cluster) {
				N_dimension = newkMeansCluster.coord_ll.size();
				flag_Cluster = true;
			}else {
				if(newkMeansCluster.coord_ll.size()!=N_dimension) {
					System.out.println("The Dimensions of CLusters mismatch");
					System.exit(-1);
				}
			}
			clusterList.add(newkMeansCluster);
			
		}
		if(clusterList.size() == 0) {
			System.out.println("Clusters not provided For the kMeans");
			System.exit(-1);
		}
		
	}
	
	public void executekMeans() {
		readFile();
		createClusters();
	}
	
	
	
	private void printValues(ArrayList<kMeansCluster> nClusterList) {
		for(int i = 0;i<nClusterList.size();i++) {
			System.out.print("C"+(i+1)+" = {");
			kMeansCluster tmpCluster = nClusterList.get(i);
			LinkedList<kMeansNode> tmpll = tmpCluster.kMeans_ll;
			Iterator<kMeansNode> itr = tmpll.iterator();
			if(itr.hasNext()) {
				kMeansNode tmpNode = (kMeansNode)itr.next();
				System.out.print(tmpNode.name);
			}
			while(itr.hasNext()) {
				kMeansNode tmpNode = (kMeansNode)itr.next();
				System.out.print(","+tmpNode.name);
			}
			System.out.print("}");
			System.out.println();
		}
		
		for(int i = 0; i < nClusterList.size();i++) {
			kMeansCluster tmpCluster = nClusterList.get(i);
			LinkedList<Double> tmp_ll = tmpCluster.coord_ll;
			Iterator<Double> itr = tmp_ll.iterator();
			System.out.print("([");
			if(itr.hasNext()) {
				Double d = (Double)itr.next();
				System.out.print(d);
			}
			while(itr.hasNext()) {
				Double d = (Double)itr.next();
				System.out.print(" "+d);
			}
			System.out.print("])");
			System.out.println();
		}
		
	}

	public void createClusters() {
		ArrayList<kMeansCluster> oClusterList = clusterList;
		ArrayList<kMeansCluster> nClusterList = clusterList;
		boolean firstTime = true;
		while(!isSame(oClusterList,nClusterList) || firstTime) {
			firstTime = false;
			oClusterList = getNewClusterList(nClusterList);
			nClusterList = getNewClusterList(oClusterList);
			for(int i = 0; i < nodeList.size(); i++) {
				Double distance = Double.MAX_VALUE;
				int index = -1;
				for(int j = 0;j<nClusterList.size(); j++) {
					double calc = calcDistance(nodeList.get(i),nClusterList.get(j));
					if(calc < distance) {
						distance = calc;
						index = j;
					}
				}
				nClusterList.get(index).addToCluster(nodeList.get(i));
			}
			//Calculate the NewCoordinates
			reCalculateCentroids(nClusterList);
		}
		printValues(nClusterList);
	}

	private void reCalculateCentroids(ArrayList<kMeansCluster> nClusterList) {
		for(int i = 0;i<nClusterList.size();i++) {
			LinkedList<Double> new_coord = calculateCentroid(nClusterList.get(i).kMeans_ll);
			nClusterList.get(i).coord_ll = new_coord;
		}
		
	}

	private LinkedList<Double> calculateCentroid(LinkedList<kMeansNode> kMeans_ll) {
		int n = kMeans_ll.size();
		double[] arr = null;
		LinkedList<Double> ans = new LinkedList<>();
		if(n==0) {
			for(int i= 0 ;i < N_dimension;i++)ans.add(0.0);
			return ans;
		}
		Iterator<kMeansNode> itr = kMeans_ll.iterator();
		while(itr.hasNext()) {
			kMeansNode tmpNode = (kMeansNode)itr.next();
			if(arr==null) {
				arr = new double[tmpNode.N];
			}
			Iterator<Integer> itr_kNode = tmpNode.list.iterator();
			int index = 0;
			while(itr_kNode.hasNext()) {
				int val = (Integer)itr_kNode.next();
				arr[index] += val;
				index++;
			}
		}
		
		
		for(int i = 0; i < arr.length;i++) {
			ans.add(arr[i]/n);
		}
		return ans;
	}

	private double calcDistance(kMeansNode kMeansNode, kMeansCluster kMeansCluster) {
		// TODO Auto-generated method stub
		double distance = 0;
		Iterator<Integer> ll_node = kMeansNode.list.iterator();
		Iterator<Double> ll_centroid = kMeansCluster.coord_ll.iterator();
		while(ll_node.hasNext()) {
			int val1 = (int)ll_node.next();
			double val2 = (double)ll_centroid.next();
			if(d.equals("e2")) {
				distance += Math.abs((val1 - val2)*(val1 - val2));
			}else if(d.equals("manh")) {
				distance += Math.abs(val2 - val1);
			}
		}
		return distance;
	}

	private ArrayList<kMeansCluster> getNewClusterList(ArrayList<kMeansCluster> clusterList2) {
		ArrayList<kMeansCluster> ans = new ArrayList<>();
		for(int i = 0;i<clusterList2.size();i++) {
			ans.add(new kMeansCluster(clusterList2.get(i)));
		}
		return ans;
	}

	public boolean isSame(ArrayList<kMeansCluster> oClusterList, ArrayList<kMeansCluster> nClusterList) {
		if(oClusterList == null || nClusterList == null)return false;
		if(oClusterList.size() != nClusterList.size())return false;
		for(int i = 0;i<oClusterList.size();i++) {
			if(!oClusterList.get(i).compare(nClusterList.get(i))) {
				return false;
			}
		}
		return true;
		
	}

	private void readFile() {
		if(fileName==null || fileName.length()==0) {
			System.out.println("File Name not provided");
			System.exit(-1);
		}
		Scanner scanner = null;
		try {
			FileReader filereader = new FileReader(fileName);
			scanner = new Scanner(filereader);
		}catch(Exception e) {
			System.out.println("File "+ fileName +"doesn't Exist/ Incorrect File Path");
			System.exit(-1);
		}
		while(scanner.hasNext()) {
			String str = scanner.nextLine();
			if(str.length()==0)continue;
			kMeansNode newkMeansNode = new kMeansNode(str);
			if(N_dimension != newkMeansNode.N) {
				System.out.println("Dimension of the Node doesn't match : "+str);
				System.exit(-1);
			}
			nodeList.add(newkMeansNode);
			if(nodeList.size() > 1) {
				if(nodeList.get(nodeList.size() - 1).N != nodeList.get(nodeList.size() - 2).N) {
					System.out.println("Incorrect Input File Format. Dimensions Mismatch : "+str);
					System.exit(-1);
				}
			}
			
		}
		scanner.close();
		
	}
}

public class Solution{
	
	public static void main(String[] args) {
		boolean flag_mode = false;
		for(int i = 0;i<args.length;i++) {
			if(args[i].equals("-mode")) {
				flag_mode = true;
				if(i>=args.length) {
					System.out.println("Mode not mentioned. Must mention : knn/kmeans");
					System.exit(-1);
				}
				if(args[i + 1].equals("knn")) {
					kNN mykNN = new kNN(args);
					mykNN.executekNN();
				}else if(args[i + 1].equals("kmeans")) {
					kMeans mykMeans = new kMeans(args);
					mykMeans.executekMeans();
				}else {
					System.out.println("Mode not Valid. Valid Flags : knn/kmeans");
					System.exit(-1);
				}
			}
		}
		if(!flag_mode) {
			System.out.println("Mode not mentioned. Must mention : knn/kmeans");
			System.exit(-1);
		}
		
	}
}