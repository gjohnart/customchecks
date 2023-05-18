package org.example.checks;

import com.puppycrawl.tools.checkstyle.api.*;

import java.util.Objects;

public class SpaceBetweenVariableBlockCheck extends CustomCheck {
    @Override
    public void visitToken(DetailAST ast) {
        // find the OBJBLOCK node below the CLASS_DEF/INTERFACE_DEF
        DetailAST objBlock = ast.findFirstToken(TokenTypes.OBJBLOCK);

        // Find first VARIABLE_DEF
        int varDefs = objBlock.getChildCount(TokenTypes.VARIABLE_DEF);
        DetailAST previous = objBlock.findFirstToken(TokenTypes.VARIABLE_DEF);

        // Iterate over each varDef
        for (int i = 1; i < varDefs; i++) {
            DetailAST current = previous.getNextSibling();
            DetailAST prevMod = previous.findFirstToken(TokenTypes.MODIFIERS);
            DetailAST currentMod = current.findFirstToken(TokenTypes.MODIFIERS);
            System.out.println("-------------------------------------------------------");
            System.out.println(prevMod);
            System.out.println(currentMod);
            DetailAST prevAnno = prevMod.findFirstToken(TokenTypes.ANNOTATION);
            DetailAST currentAnno = currentMod.findFirstToken(TokenTypes.ANNOTATION);

            // Both have no annotation
            if (prevAnno == null && currentAnno == null) {
                // But not the same public/private modifier
                if (!Objects.equals(prevMod.getFirstChild().getText(), currentMod.getFirstChild().getText())) {
                    // Then there should be whitespace
                    if (getWhiteSpaceDifference(current, previous) == 1) {
                        String message = "White space required between the different variable scopes.";
                        log(currentMod.getLineNo(), message);
                    }
                // And the same public/private modifier
                } else {
                    // Then there should be no whitespace
                    if (getWhiteSpaceDifference(current, previous) > 1) {
                        String message = "No white space between the same variable scopes.";
                        log(currentMod.getLineNo() -1, message);
                    }
                }
            }

            // Both have annotation
            else if (prevAnno != null && currentAnno != null) {
                System.out.println("both have anno");
                // And the same annotation
                if (Objects.equals(joinChildren(prevAnno), joinChildren(currentAnno))) {
                    System.out.println("same anno");
                    // Should be no whitespace
                    if (getWhiteSpaceDifference(current, previous) > 1 ) {
                        String message = "No white space between the same variable scopes.";
                        log(currentMod.getLineNo(), message);
                    }
                // Different annotation
                } else {
                    System.out.println("different anno");
                    // Should be whitespace
                    if (getWhiteSpaceDifference(current, previous) < 2) {
                        String message = "White space required between the different variable scopes.";
                        log(currentMod.getLineNo(), message);
                    }
                }
            // One has annotation and the other doesn't
            } else {
                int requiredDifference = prevAnno != null ? 3 : 2;
                // There should be whitespace
                if (getWhiteSpaceDifference(current, previous) < requiredDifference) {
                    String message = "White space required between the different variable scopes.";
                    log(currentMod.getLineNo(), message);
                }
            }
            previous = current;
        }
    }
    private int getWhiteSpaceDifference(DetailAST firstNode, DetailAST secondNode) {
        return firstNode.getLineNo() - secondNode.getLastChild().getLineNo();
    }

    private String joinChildren(DetailAST node) {
        if (node == null) throw new RuntimeException("Node is null");
        int childrenCount = node.getChildCount();
        StringBuilder output = new StringBuilder();
        if (childrenCount == 0) return output.toString();
        DetailAST nextChild = node.getFirstChild();
        if (childrenCount == 1) return nextChild.getText();

        for (int i = 0; i < childrenCount; i++) {
            output.append(nextChild.getText());
            nextChild = nextChild.getNextSibling();
        }
        return output.toString();
    }
}