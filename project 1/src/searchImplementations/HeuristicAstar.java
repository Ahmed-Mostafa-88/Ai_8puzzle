package searchImplementations;

import java.util.ArrayList;
import java.util.Comparator;

public class HeuristicAstar{

    private   ArrayList<TreeNode> open = new ArrayList<>();
    protected ArrayList<TreeNode> closed = new ArrayList<>();
    private TreeGenerator tree = new TreeGenerator();
    private String goalState;
    private TreeNode root;

    public HeuristicAstar(String startState, String goalState){
        root = new TreeNode(startState);
        root.depth = 0;
        this.goalState = goalState;
    }

    public void hSearch(int i){
        root.hValue =  heuristicValue(i,root.state);
        open.add(root);
        boolean foundState = false;
        closed.clear();
        while(!open.isEmpty() && foundState==false){
            TreeNode x = open.remove(0);
            if(x.state.equals(goalState)){
                foundState = true;
                closed.add(x);
            }
            else{
                tree.generateChildren(x);
                closed.add(x);

                while(!tree.children.isEmpty()){
                    TreeNode temp = tree.children.pop();
                    temp.hValue = heuristicValue(i,temp.state) + temp.depth;
                    boolean inOpen = false;
                    boolean inClosed = false;

                    /* Check if child is in open
                     * If child is on open and the path is shorter
                     * (Same state exists in open but this path is shorter)
                     */
                    int index = 0;
                    while(index<open.size() && inOpen==false){
                        if((open.get(index).state).equals(temp.state)){
                            inOpen=true;
                            if(temp.depth < open.get(index).depth){
                                open.set(index, temp);
                            }
                        }
                        index++;
                    }

                    //Check if child is in closed
                    index =0;
                    while(index<closed.size() && inClosed==false){
                        if((closed.get(index).state).equals(temp.state)){
                            inClosed = true;
                            if(temp.depth<closed.get(index).depth){
                                closed.remove(index);
                                index--;
                                open.add(temp);
                            }
                        }
                        index++;
                    }
                    /*
                     * Child not in open or closed
                     * Give it a heuristic value, add to open
                     */
                    if(inOpen==false && inClosed==false){
                        open.add(temp);
                    }
                }
            }
            open.sort(new NodeComparator()); //Sort list by heuristic value(low cost to high cost)
        }
    }
    public int heuristicValue(int i, String s){
        switch (i){
            case 0:
                return calcOutOfPlace(s);
            case 1:
                return manhattenDistance(s);
            default:
                return euclideanDistance(s);
        }
    }

    public int calcOutOfPlace(String s){
        int hValue = 0;
        // # of pieces out of place
        for(int i=0; i<goalState.length();i++)
            if(s.indexOf(i)!=goalState.indexOf(i))
                hValue++;
        return hValue;
    }
    public  int manhattenDistance(String s){
        int z = 0,h = 0;
        int skipIndex = goalState.indexOf(' ');
        int[][] goal = stringTo2DArray(goalState);
        int[][] state = stringTo2DArray(s);
        for(int i = 0; i < 3;i++){
            for(int j = 0; j < 3;j++){
                int[] xy = getXY(goal[i][j],state);
                h += Math.abs(i - xy[0]) + Math.abs(j - xy[1]);
            }
        }
        return h;
    }
    public  int euclideanDistance(String s){
        int z = 0,h = 0;
        int skipIndex = goalState.indexOf(' ');
        int[][] goal = stringTo2DArray(goalState);
        int[][] state = stringTo2DArray(s);
        for(int i = 0; i < 3;i++){
            for(int j = 0; j < 3;j++){
                int[] xy = getXY(goal[i][j],state);
                h += Math.sqrt((i - xy[0]) * (i - xy[0]) + Math.abs(j - xy[1]) * Math.abs(j - xy[1]));
            }
        }
        return h;
    }
    public int[][] stringTo2DArray(String s){
        int[][] arr = new int[3][3];
        int skipIndex = s.indexOf(' ');
        int z = 0;
        for(int i = 0; i < 3;i++){
            for(int j =0; j < 3;j++){
                if(z == skipIndex){
                    arr[i][j] = 0;
                }else{
                    arr[i][j] = Integer.parseInt(String.valueOf(s.charAt(z)));
                }
                z++;
            }
        }
        return arr;
    }
    public int[] getXY(int value,int[][] state) {
        int[] container = new int[2];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (state[i][j] == value) {
                    container[0] = i;
                    container[1] = j;
                    break;
                }
            }
        }
        return container;
    }

    public ArrayList<String> getSolutionPath(){
        ArrayList<String> path = new ArrayList<>();
        TreeNode currentNode = closed.get(closed.size()-1);
        path.add(currentNode.state);
        int i = 2;
        while(closed.size()-i>=1){

            if(closed.get(closed.size()-i).down.state.equals(currentNode.state)){
                currentNode = closed.get(closed.size()-i);
                path.add(currentNode.state); //parent of solution
            }
            else if(closed.get(closed.size()-i).up.state.equals(currentNode.state)){
                currentNode = closed.get(closed.size()-i);
                path.add(currentNode.state);
            }
            else if(closed.get(closed.size()-i).left.state.equals(currentNode.state)){
                currentNode = closed.get(closed.size()-i);
                path.add(currentNode.state);
            }
            else if(closed.get(closed.size()-i).right.state.equals(currentNode.state)){
                currentNode = closed.get(closed.size()-i);
                path.add(currentNode.state);
            }
            else
                i++;
        }
        path.add(root.state);
        return path;
    }

    class NodeComparator implements Comparator<TreeNode>, java.io.Serializable{

        private static final long serialVersionUID = 3408238623005743778L;

        @Override
        public int compare(TreeNode x, TreeNode y) {
            int xValue = x.hValue + x.depth;
            int yValue = y.hValue + y.depth;

//            return xValue-yValue;
            if(xValue<yValue)
                return -1;
            else if(xValue==yValue)
                return 0;
            else
                return 1;

        }
    }
    public static void main(String[] args){
        HeuristicAstar as = new HeuristicAstar("1 2345678"," 12345678");
    }
}