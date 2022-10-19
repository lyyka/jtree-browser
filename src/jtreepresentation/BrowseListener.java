package jtreepresentation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class BrowseListener implements ActionListener {
    private final javax.swing.JFrame parent;
    private final javax.swing.JFileChooser fileChooser;
    private final javax.swing.JTree jtree;
    
    public BrowseListener(javax.swing.JFrame parent, 
            javax.swing.JFileChooser fileChooser,
            javax.swing.JTree jtree) {
        this.parent = parent;
        this.fileChooser = fileChooser; 
        this.jtree = jtree; 
    }
    
    // Find `lookFor` in a tree of node which root is `rootNode`
    private FileTreeNode findRootFileTreeNode(FileTreeNode rootNode, DefaultMutableTreeNode lookFor) {
        if(rootNode.getNode() == lookFor) {
            return rootNode;
        }
        
        for(FileTreeNode inner : rootNode.children()) {
            FileTreeNode found = findRootFileTreeNode(inner, lookFor);
            if(found != null) {
                return found;
            }
        }
        
        return null;
    }
    
    // Loads list of file from rootFile into a rootNode (shallow)
    private void loadJTree(FileTreeNode rootNode) 
    {
        for(File f : rootNode.getFile().listFiles()) {
            FileTreeNode currentFileAsNode = new FileTreeNode(f);
            if(f.isDirectory()) {
                // Add temp item to mark the item as expandable
                currentFileAsNode.add(new FileTreeNode("..."));
                rootNode.add(currentFileAsNode);
            } else if (f.isFile()) {
                rootNode.add(currentFileAsNode);
            }
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        this.fileChooser.setFileSelectionMode(
                javax.swing.JFileChooser.DIRECTORIES_ONLY
        );
        
        int option = fileChooser.showOpenDialog(parent);
        
        if(option == javax.swing.JFileChooser.APPROVE_OPTION){
            File file = fileChooser.getSelectedFile();

            // Create root node based on the selected directory
            FileTreeNode rootNode = new FileTreeNode(file);

            // Load files into root
            loadJTree(rootNode);

            // Construct a tree model
            DefaultTreeModel treeModel = new DefaultTreeModel(rootNode.getNode());

            // Set model on the tree
            this.jtree.setModel(treeModel);

            this.jtree.addTreeExpansionListener(new TreeExpansionListener() {
                 @Override
                 public void treeExpanded(TreeExpansionEvent event) {
                    // Get expanded node
                    DefaultMutableTreeNode expanded = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();

                    // Find the expanded node inside our parallel FileTreeNode tree
                    FileTreeNode rootFileTreeNode = findRootFileTreeNode(rootNode, expanded);
                    
                    rootFileTreeNode.removeAllChildren();
 
                    // Load files into it
                    loadJTree(rootFileTreeNode);
                    
                    // Replace the model (TODO: rootNode does not recognize updates to rootFileTreeNode)
                    DefaultTreeModel treeModel = new DefaultTreeModel(rootNode.getNode());
                    jtree.setModel(treeModel);
                }

                @Override
                public void treeCollapsed(TreeExpansionEvent event) {
                    // Get collapsed node
                    DefaultMutableTreeNode collapsed = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();

                    // Find the collapsed node inside our parallel FileTreeNode tree
                    FileTreeNode rootFileTreeNode = findRootFileTreeNode(rootNode, collapsed);

                    // Cleanup the tree
                    rootFileTreeNode.removeAllChildren();

                    // Add a placeholder child
                    rootFileTreeNode.add(new FileTreeNode("..."));
                }
            });
        }
    }
}
