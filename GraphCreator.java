import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class GraphCreator implements ActionListener, MouseListener {

	JFrame frame = new JFrame();
	GraphPanel panel = new GraphPanel();
	JButton nodeB = new JButton("Node");
	JButton edgeB = new JButton("Edge");
	JTextField labelsTF = new JTextField("A");
	JTextField firstNode = new JTextField("First");
	JTextField secondNode = new JTextField("Second");
	JButton connectedB = new JButton("Check Connected");
	JTextField salesmanStartTF = new JTextField("A");
	JButton salesmanB = new JButton("Find Shortest Path");
	
	Container west = new Container();
	Container east = new Container();
	Container south = new Container();
	
	final int NODE_CREATE = 0;
	final int EDGE_FIRST = 1;
	final int EDGE_SECOND = 2;
	int state = NODE_CREATE;
	Node first = null;
	
	ArrayList<Node> shortestPath = new ArrayList<Node>();
	int shortestPathWeight = Integer.MAX_VALUE;
	String shortestPathString = "";
	
	public GraphCreator() {
		frame.setSize(800, 600);
		frame.setLayout(new BorderLayout());
		frame.add(panel, BorderLayout.CENTER);
		
		west.setLayout(new GridLayout(3, 1));
		east.setLayout(new GridLayout(3, 1));
		south.setLayout(new GridLayout(1, 2));
		
		west.add(nodeB);
		nodeB.addActionListener(this);
		nodeB.setBackground(Color.GREEN);
		
		west.add(edgeB);
		edgeB.addActionListener(this);
		edgeB.setBackground(Color.LIGHT_GRAY);

		west.add(labelsTF);
		frame.add(west, BorderLayout.WEST);
		
		east.add(firstNode);
		east.add(secondNode);
		east.add(connectedB);
		connectedB.addActionListener(this);
		frame.add(east, BorderLayout.EAST);
		
		south.add(salesmanStartTF);
		south.add(salesmanB);
		salesmanB.addActionListener(this);
		frame.add(south, BorderLayout.SOUTH);
		
		panel.addMouseListener(this);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		new GraphCreator();
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (state == NODE_CREATE) {
			panel.addNode(e.getX(), e.getY(), labelsTF.getText());
		}
		else if (state == EDGE_FIRST) {
			Node n = panel.getNode(e.getX(), e.getY());
			if (n != null) {
				first = n;
				state = EDGE_SECOND;
				n.setHighlighted(true);
			}
		}
		else if (state == EDGE_SECOND) {
			Node n = panel.getNode(e.getX(), e.getY());
			if (n != null && !first.equals(n)) {
				//TODO remove highlighting of first if clicked again
				String s = labelsTF.getText();
				boolean valid = true;
				for (int i = 0; i < s.length(); i++) {
					if (!Character.isDigit(s.charAt(i))) {
						valid = false;
					}
				}
				if (valid) {
					first.setHighlighted(false);
					panel.addEdge(first, n, labelsTF.getText());
					first = null;
					state = EDGE_FIRST;
				}
				else {
					JOptionPane.showMessageDialog(frame, "Edge labels must be integers.");
				}
			}
		}
		frame.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(nodeB)) {
			nodeB.setBackground(Color.GREEN);
			edgeB.setBackground(Color.LIGHT_GRAY);
			state = NODE_CREATE;
		}
		if (e.getSource().equals(edgeB)) {
			edgeB.setBackground(Color.GREEN);
			nodeB.setBackground(Color.LIGHT_GRAY);
			state = EDGE_FIRST;
			panel.stopHighlighting();
			frame.repaint();
		}
		if (e.getSource().equals(connectedB)) {
			if (!panel.nodeExists(firstNode.getText())) {
				JOptionPane.showMessageDialog(frame, "First node does not exist.");
			}
			else if (!panel.nodeExists(secondNode.getText())) {
				JOptionPane.showMessageDialog(frame, "Second node does not exist.");
			}
			else if (firstNode.getText().equals(secondNode.getText())) {
				JOptionPane.showMessageDialog(frame, "Those are the same node!");
			}
			else {
				Queue queue = new Queue();
				ArrayList<String> connectedList = new ArrayList<String>();
				connectedList.add(panel.getNode(firstNode.getText()).getLabel());
				ArrayList<String> edges = panel.getConnectedLabels(firstNode.getText());
				for (int i = 0; i < edges.size(); i++) {
					queue.enqueue(edges.get(i));
				}
				while (!queue.isEmpty()) {
					String currentNode = queue.dequeue();
					if (!connectedList.contains(currentNode)) {
						connectedList.add(currentNode);
					}
					edges = panel.getConnectedLabels(currentNode);
					for (int i = 0; i < edges.size(); i++) {
						if (!connectedList.contains(edges.get(i))) {
							queue.enqueue(edges.get(i));
						}
					}
				}
				if (connectedList.contains(secondNode.getText())) {
					JOptionPane.showMessageDialog(frame, "Connected!");
				}
				else {
					JOptionPane.showMessageDialog(frame, "Not connected!");
				}
			}
		}
		if (e.getSource().equals(salesmanB)) {
			if (panel.getNode(salesmanStartTF.getText()) != null) {
				ArrayList<Node> path = new ArrayList<Node>();
				path.add(panel.getNode(salesmanStartTF.getText()));
				travelling(panel.getNode(salesmanStartTF.getText()), path, 0);
				//make sure completed has a path (otherwise means no possible path)
				if (shortestPath.isEmpty()) {
					JOptionPane.showMessageDialog(frame, "There is no path to all nodes!");
				}
				//print out cheapest route and weight
				else {
					for (int i = 0; i < shortestPath.size(); i++) {
						shortestPathString += shortestPath.get(i).getLabel();
					}
					JOptionPane.showMessageDialog(frame, "The cheapest route is " + shortestPathString + ", with a weight of " + shortestPathWeight);
					shortestPath = new ArrayList<Node>();
					shortestPathWeight = Integer.MAX_VALUE;
					shortestPathString = "";
				}
			}
			else {
				JOptionPane.showMessageDialog(frame, "Invalid starting node.");
			}
		}
	}

	public void travelling(Node n, ArrayList<Node> path, int weight) {
		//if the number of nodes in the path is equal to the number of nodes
		//  add this path to the completed list
		//  remove the last thing in the path
		//  return
		//else
		//  for each edge
		//     see if they are connected to the current node
		//     if they are not already in the path
		//       add node to path
		//       travelling(connectedNode, path, weight + edge cost);
		//remove the last thing in the path
		if (path.size() == panel.nodeList.size()) {
			if (weight < shortestPathWeight) {
				shortestPathWeight = weight;
				for (int i = 0; i < path.size(); i++) {
					shortestPath.add(path.get(i));
				}
			}
			path.remove(n);
			return;
		}
		else {
			for (int i = 0; i < panel.edgeList.size(); i++) {
				Edge e = panel.edgeList.get(i);
				if (e.getOtherEnd(n) != null) {
					if (!path.contains(e.getOtherEnd(n))) {
						path.add(e.getOtherEnd(n));
						travelling(e.getOtherEnd(n), path, weight + Integer.parseInt(e.getLabel()));
						path.remove(e.getOtherEnd(n));
					}
				}
			}
		}
	}
	
	/*
	 * Adjacency Matrix
	 * 
	 *     A   B   C
	 * A   1   1   1
	 * B   1   1   0
	 * C   1   0   1
	 */

}
