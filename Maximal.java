
// without efficiently processing small nodes 

package maximal;

import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

class TreeNode {
    String item;
    int count;
   
    Map<String, TreeNode> children;
    public TreeNode(String item, int count) {
        this.item = item;
        this.count = count;
        this.children = new LinkedHashMap<>();
    }
    
    protected void finalize()  
    {    
    } 
}

public class Maximal {
	static TreeNode x = new TreeNode(null,100);
	static int minSupport = 100;
	static int count=0;
	static int totalNodes = 0;
	static int maxNodesDPT = 0; // New variable to track maximum nodes in DPT
	
    public static void main(String[] args) {
    	List<String> F=new ArrayList<String>();
        Maximal preTree=new Maximal();
        long startTime = System.nanoTime();
        preTree.initialdata("C://Users//jahna//Downloads//Major Project//T10I4D100K.txt");
        
    	
    	long endTime = System.nanoTime();
        long executionTime = endTime - startTime;
        System.out.println("Execution Time: " + executionTime + " nanoseconds");

        startTime = System.nanoTime();
       while(x.children.size()>0) {
    	preTree.dpt(F,x,100,x);  	
    }
       endTime = System.nanoTime();
       executionTime = endTime - startTime;
       System.out.println("Execution Time (DPT): " + executionTime + " nanoseconds");

       Runtime runtime = Runtime.getRuntime();
       long memory = runtime.totalMemory() - runtime.freeMemory();
       System.out.println("Memory Allocated: " + memory + " bytes");
       System.out.println("Total number of nodes generated: " + count);
       System.out.println("Maximum number of nodes generated in DPT: " + maxNodesDPT);
       
	
    }  
    public void initialdata(String filePath) {
        List<List<String>> database = new ArrayList<>();
        try {
            BufferedReader brCount = new BufferedReader(new FileReader(filePath));
            String line;
            int totalLines = 0;
            while ((line = brCount.readLine()) != null) {
                totalLines++;
            }
            brCount.close();

            // Calculate 60% of the total lines
            int linesToRead = (int) (totalLines * 0.5);

            BufferedReader br = new BufferedReader(new FileReader(filePath)); // New BufferedReader
            int linesRead = 0;
            while (linesRead < linesToRead && (line = br.readLine()) != null) {
                List<String> transaction = Arrays.asList(line.trim().split("\\s+"));
                database.add(transaction);
                linesRead++;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        constructPrefixTree(database, minSupport);
    }

    public void dpt(List<String> F, TreeNode n, int s, TreeNode p) {
        List<String> F1 = new ArrayList<String>();

        Map<String, TreeNode> ch = new LinkedHashMap<>();
        TreeNode leftnode = null;
        ch = n.children;
        for (Map.Entry<String, TreeNode> ch1 : ch.entrySet()) {
            String key = ch1.getKey();
        }

        for (Map.Entry<String, TreeNode> ch1 : ch.entrySet()) {
            String key = ch1.getKey();
            leftnode = ch1.getValue();
            break;
        }

        TreeNode c = null;
        c = n;

        if (leftnode != null) {
            copySubtree(leftnode, c);
        }

        // Step 1: Check if the current node is a small node
        if (n.count < minSupport) {
            // This is a small node, so we don't need to generate any new branches
            // Instead, we merge the subtree rooted at this node with the subtrees
            // rooted at n's right sibling nodes
            mergeSubtree(n, p);
        } else {
            // This is a big node, so we proceed with the regular DPT process
            if (n.count >= minSupport) {
                if (!F1.contains(n.item)) {
                    F1.add(n.item);
                    F1.addAll(F);

                    System.out.println("Result:" + F1.toString() + ":" + n.count);
                    Map<String, TreeNode> a = new LinkedHashMap<>();
                    a = n.children;

                    Set<String> keys = new LinkedHashSet<>(a.keySet());

                    for (String k : keys) {
                        dpt(F1, a.get(k), minSupport, n);
                        if (n.item == null) {
                            break;
                        }
                    }

                    p.children.remove(n.item);
                    n.finalize();
                }
            }
        }

        totalNodes++;
        if (totalNodes > maxNodesDPT) {
            maxNodesDPT = totalNodes;
        }
    }

    public static void mergeSubtree(TreeNode smallNode, TreeNode parent) {
        Map<String, TreeNode> siblingChildren = parent.children;
        Map<String, TreeNode> smallNodeChildren = smallNode.children;

        for (Map.Entry<String, TreeNode> entry : smallNodeChildren.entrySet()) {
            String key = entry.getKey();
            TreeNode value = entry.getValue();

            if (siblingChildren.containsKey(key)) {
                siblingChildren.get(key).count += value.count;
                mergeSubtree(value, siblingChildren.get(key));
            } else {
                siblingChildren.put(key, value);
            }
        }

        parent.children.remove(smallNode.item);
        smallNode.finalize();
    }
    
public static void constructPrefixTree(List<List<String>> database, int minSupport) {
    Map<String, Integer> frequentItems = getFrequentItems(database, minSupport);
    
   
    for (List<String> transaction : database) {
        List<String> filteredTransaction = new ArrayList<>();
        for (String item : transaction) {
            if (frequentItems.containsKey(item)) {
                filteredTransaction.add(item);
            }
        }
        Collections.sort(filteredTransaction, Comparator.comparingInt(frequentItems::get).reversed());
        insertTransaction(x, filteredTransaction);
    }

    //return root;
}

public static Map<String, Integer> getFrequentItems(List<List<String>> database, int minSupport) {
    Map<String, Integer> itemCounts = new HashMap<>();

    for (List<String> transaction : database) {
        for (String item : transaction) {
            itemCounts.put(item, itemCounts.getOrDefault(item, 0) + 1);
        }
    }

    Map<String, Integer> frequentItems = new HashMap<>();
    for (Map.Entry<String, Integer> entry : itemCounts.entrySet()) {
        if (entry.getValue() >= minSupport) {
            frequentItems.put(entry.getKey(), entry.getValue());
        }
    }

    return frequentItems;
}

public static void insertTransaction(TreeNode node, List<String> items) {
    if (items.isEmpty()) {
        return;
    }
    count++;

    String item = items.get(0);
    if (node.children.containsKey(item)) {
        node.children.get(item).count++;
    } else {
        TreeNode newNode = new TreeNode(item, 1);
        node.children.put(item, newNode);
    }

    // Recursively insert the remaining items (skip infrequent items)
    if (node.children.containsKey(item)) {
        insertTransaction(node.children.get(item), items.subList(1, items.size()));
    }
}

public static void printPrefixTree(TreeNode node, int level) {
    if (node.item != null) {
        System.out.println(indent(level) + node.item + " (" + node.count + ")");
    }
    for (TreeNode child : node.children.values()) {
    	
        printPrefixTree(child, level + 1);
    }
}

private static String indent(int level) {
    StringBuilder indentation = new StringBuilder();
    for (int i = 0; i < level; i++) {
        indentation.append("  ");
    }
    return indentation.toString();
}


public static void copySubtree(TreeNode left,TreeNode right) {
//creating a treenode to access the children	
Map<String, TreeNode> ch = new LinkedHashMap<>();
   //checks if the left children is empty or not

if(!left.children.isEmpty() ) {
	//assigning the left children to ch
ch=left.children;

//declaring another treenode named tempright
TreeNode tempright;

//iterate to get the key and values
for (Map.Entry<String, TreeNode> ch1 : ch.entrySet()) {
	//assigning the key and value of ch to a variable key and value
    String key = ch1.getKey();
    TreeNode value = ch1.getValue();
    //displaying the key and values
  
    //checks if the right children contains the left children
    if(right.children.containsKey(key)) {
    	//if true then add both the counts
    	right.children.get(key).count+=value.count;
    	//assigning the right children's key to tempright
    	tempright=right.children.get(key);
    }
    else {
    	
    	TreeNode temp= new TreeNode(key,value.count); 
    	right.children.put(key, temp);
    	tempright=right;
    }
    if(!value.children.isEmpty()) {
    copySubtree(value,tempright);
    }
    
}
}
}
}

