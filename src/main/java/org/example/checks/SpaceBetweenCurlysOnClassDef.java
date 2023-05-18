package org.example.checks;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class SpaceBetweenCurlysOnClassDef extends CustomCheck {
    @Override
    public void visitToken(DetailAST ast) {
        // find the OBJBLOCK node below the CLASS_DEF/INTERFACE_DEF
        DetailAST objBlock = ast.findFirstToken(TokenTypes.OBJBLOCK);

        // Replacing node type with clear name
        String nodeType;
        if (objBlock.getParent().getText().equals("INTERFACE_DEF")) {
            nodeType = "interface definition";
        } else {
            nodeType = "class definition";
        }

        // Space after first curly
        DetailAST leftCurly = objBlock.findFirstToken(TokenTypes.LCURLY);
        DetailAST firstVariableDeclaration = leftCurly.getNextSibling();

        if (leftCurly.getLineNo() == firstVariableDeclaration.getLineNo()-1) {
            String message = "White space required after "+nodeType+".";
            log(firstVariableDeclaration.getLineNo(), message);
        }

        // No space after last curly
        DetailAST rightCurly = objBlock.getLastChild();
        System.out.println(rightCurly.getText());
        System.out.println(rightCurly.getPreviousSibling().getText());
        System.out.println(findLastRightCurly(rightCurly.getPreviousSibling()).getLineNo());
        if (rightCurly.getLineNo() - findLastRightCurly(rightCurly.getPreviousSibling()).getLineNo() > 1) {
            String message = "No white space required before final right curly on "+nodeType+".";
            log(firstVariableDeclaration.getLineNo(), message);
        }
    }

    private DetailAST findLastRightCurly(DetailAST node) {
        if (node == null) return null;
        DetailAST lastNode = node.getLastChild();
        while (lastNode != null ) {
            if (lastNode.getType() == TokenTypes.RCURLY) {
                return lastNode;
            }
            lastNode = lastNode.getLastChild();
        }
        return lastNode;
    }
}
