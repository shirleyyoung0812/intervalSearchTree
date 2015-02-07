/**
 * Every node of Interval Tree stores following information.
a) i: An interval which is represented as a pair [low, high]
b) max: Maximum high value in subtree rooted with this node.

BST structure
compare rule: (see Interval class)
1. the interval with the lower low is always smaller
2. if the low is the same, the interval with the lower high is smaller
The insert and delete operations are same as insert and delete in 
self-balancing BST used.
@author shirleyyoung
 */
package intervalSearchTree;
import java.util.*;
public class IntervalSearchTree<V> {
	/**
	 * tree node class
	 *
	 */
	private class Node {
		Interval interval;
		V label;
		Node left, right;
		int N;//size of subtree (number of nodes) rooted at this node
		int max;
		Node (Interval interval, V label) {
			this.interval = interval;
			this.label = label;
			this.N = 1;
			this.max = (int) interval.high;
		}
	}
	private Node root;
	
	/***************************************
	 * search if an interval is in the tree
	 * @param interval
	 * @return
	 ***************************************/
	public boolean contains(Interval interval) {
		return get(interval) != null;
	}
	public V get(Interval interval) {
		return get(root, interval);
	}
	private V get(Node node, Interval interval) {
		if (node == null)
			return null;
		int cmp = interval.compareTo(node.interval);
		if (cmp < 0)
			return get(node.left, interval);
		else if (cmp > 0)
			return get(node.right, interval);
		else
			return node.label;
	}
	
	
	/*****************************
	 * insertion 
	 *****************************/
	public void put(Interval interval, V label) {
		if (contains(interval)) {
			System.out.println("Duplicate interval!");
			return;
		}
		root = insert(root, interval, label);
	}	
	/**
	 * insert a node based on BST rule
	 * @param node
	 * @param interval
	 * @param label
	 * @return
	 */
	private Node insert(Node node, Interval interval, V label) {
		if (node == null)
			return new Node(interval, label);
		int cmp = interval.compareTo(node.interval);
		if (cmp < 0) {
			node.left = insert(node.left, interval, label);
		}
		else {
			node.right = insert(node.right, interval, label);
		}
		fix(node);
		return node;
	}
	/**
	 * insert the new interval as the root of the tree
	 * @param interval
	 * @param label
	 */
	public void insertRoot(Interval interval, V label) {
		if (contains(interval)) {
			System.out.println("Duplicate interval!");
			return;
		}
		root = insertRoot(root, interval, label);	
	}
	/**
	 * insert the node at the correct position
	 * rotate the node so that the new node will be the root 
	 * while still maintaining the BST structure
	 * @param node
	 * @param interval
	 * @param label
	 * @return
	 */
	private Node insertRoot(Node node, Interval interval, V label) {
		if (node == null)
			return new Node (interval, label);
		int cmp = interval.compareTo(node.interval);
		if (cmp < 0) {
			node.left = insertRoot(node.left, interval, label);
			node = rotR(node);
		}
		else {
			node.right = insertRoot(node.right, interval, label);
			node = rotL(node);
		}
		return node;
	}
	
	/***************************************
	 * deletion
	 * *************************************/
	public V remove(Interval interval) {
		V value = get(interval);
		root = remove(root, interval);
		return value;
	}
	private Node remove(Node node, Interval interval) {
		if (node == null)
			return null;
		int cmp = interval.compareTo(node.interval);
		if (cmp < 0)
			node.left = remove(node.left, interval);
		else if (cmp > 0)
			node.right = remove(node.right, interval);
		else
			node = join(node.left, node.right);
		fix(node);
		return node;
	}
	/**
	 * join the left and right subtree of a node 
	 * once the node is deleted
	 * use a random number to determine whether the new node is the left child
	 * or the right child
	 * @param a
	 * @param b
	 * @return
	 */
	private Node join(Node a, Node b) {
		if (a == null)
			return b;
		if (b == null)
			return a;
		//generate a number between 0.0 to 1.0
		if (Math.random() * (double)(size(a) + size(b)) < (double)size(a)) {
			a.right = join(a.right, b);
			fix(a);
			return a;
		}
		else {
			b.left = join(a, b.left);
			fix(b);
			return b;
		}
	}
	
	
	
	/******************************
	 * Search the Interval tree
	 ******************************/
	public Interval search(Interval interval) {
		return search(root, interval);
	}
	public Interval search(Node node, Interval interval) {
		while (node != null) {
			if (interval.intersects(node.interval))
				return node.interval;
			else if (node.left == null)
				node = node.right;
			else if (node.left.max < interval.low)
				node = node.right;
			else
				node = node.left;
		}
		return null;
	}
	/**
	 * return all intervals that intersect the given interval
	 * running time is proportional to RlogN, where R 
	 * is the number of intersections
	 * @param interval
	 * @return
	 */
	public Iterable<Interval> searchAll (Interval interval) {
		LinkedList<Interval> list = new LinkedList<Interval> ();
		searchAll(root, interval, list);
		return list;
	}
	public boolean searchAll(Node node, Interval interval, LinkedList<Interval> list) {
		boolean found_root = false;
		boolean found_left = false;
		boolean found_right = false;
		if (node == null)
			return false;
		if (interval.intersects(node.interval)) {
			list.add(node.interval);
			found_root = true;
		}
		if (node.left != null && node.left.max >= interval.low)
			found_left = searchAll(node.left, interval, list);
		if (node.right != null && node.right.max >= interval.high)
			found_right = searchAll(node.right, interval, list);
		return found_root || found_left || found_right;
	}
	
	
	
	/**********************************
	 * useful methods
	 **********************************/
	public int size() {
		return size(root);
	}
	private int size(Node node) {
		if (node == null)
			return 0;
		else
			return node.N;
	}
	public int height() {
		return height(root);
	}
	private int height(Node node) {
		if (node == null)
			return 0;
		return 1 + Math.max(height(node.left), height(node.right));
	}
	/**
	 * fix auxilliar information 
	 * subtree count and max fields
	 * @param node
	 */
	private void fix(Node node) {
		if (node == null)
			return;
		node.N = 1 + size(node.left) + size(node.right);
		node.max = max3(node.interval.high, max(node.left), max(node.right));
	}
	private int max(Node node) {
		if (node == null)
			return Integer.MIN_VALUE;
		return node.max;
	}
	
	private int max3(int a, int b, int c) {
		return Math.max(a, Math.max(b, c));
	}
	/**
	 * right rotate
	 *     1             2
	 *    / \           / \
	 *   2   3    ->   4   1
	 *  / \               / \
	 * 4   5             5   3
	 * @param h
	 * @return
	 */
	private Node rotR(Node h) {
		Node l = h.left;
		h.left = l.right;
		l.right = h;
		fix(h);
		fix(l);
		return l;
	}
	/**
	 * left rotate
	 *     1             3
	 *    / \           / \
	 *   2   3    ->   1   5
	 *      / \       / \
	 *     4   5     2   4
	 *     
	 * @param h
	 * @return
	 */
	private Node rotL(Node h) {
		Node r = h.right;
		h.right = r.left;
		r.left = h;
		fix(h);
		fix(r);
		return r;
	}
	/********************************
	 * Debugging 
	 ********************************/
	public boolean check() {
		return checkCount() && checkMax();
	}
	
	private boolean checkCount() {
		return checkCount(root);
	}
	private boolean checkCount(Node node) {
		if (node == null)
			return true;
		return checkCount(node.left) && checkCount(node.right) 
				&& (node.N == 1 + size(node.left) + size(node.right));
	}
	private boolean checkMax() {
		return checkMax(root);
	}
	private boolean checkMax(Node node) {
		if (node == null)
			return true;
		return node.max == max3(node.interval.high, max(node.left), max(node.right));
	}
}
