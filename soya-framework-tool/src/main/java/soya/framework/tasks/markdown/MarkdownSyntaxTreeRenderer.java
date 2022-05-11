package soya.framework.tasks.markdown;

import soya.framework.core.Command;

import javax.swing.tree.*;

@Command(group = "markdown", name = "syntax-tree", httpMethod = Command.HttpMethod.POST)
public class MarkdownSyntaxTreeRenderer extends MarkdownTask<String> {


    @Override
    protected String process() throws Exception {

        TreeNode root = new DefaultMutableTreeNode();
        TreeModel treeModel = new DefaultTreeModel(root);

        TreePath treePath;



        return document.getFirstChild().toString();
    }

    @Override
    public void onEvent(MarkdownEvent event) {

        System.out.println("============== " + event.getSource().getClass().getName());
    }
}
