package jtreepresentation;

import java.util.List;
import java.io.File;
import java.util.ArrayList;
import javax.swing.tree.DefaultMutableTreeNode;

public class FileTreeNode {
    private final File file;
    private final DefaultMutableTreeNode node;
    private final List<FileTreeNode> children;
    
     public FileTreeNode(String name) {
        this.file = null;
        this.node = new DefaultMutableTreeNode(name);
        this.children = new ArrayList<>();
    } 
    
    public FileTreeNode(File file) {
        this.file = file;
        this.node = new DefaultMutableTreeNode(file.getName());
        this.children = new ArrayList<>();
    } 
    
    public void add(FileTreeNode node) {
        this.node.add(node.getNode());
        this.children.add(node);
    }
    
    public void removeAllChildren()
    {
        this.node.removeAllChildren();
        this.children.clear();
    }
    
    public List<FileTreeNode> children()
    {
        return this.children;
    }
    
    public File getFile() 
    {
        return this.file;
    }
    
    public DefaultMutableTreeNode getNode()
    {
        return this.node;
    }
}
