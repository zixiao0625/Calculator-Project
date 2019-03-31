package calculator.ast;

import calculator.interpreter.Environment;
import calculator.errors.EvaluationError;
import calculator.gui.ImageDrawer;
import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IList;

/**
 * All of the public static methods in this class are given the exact same parameters for
 * consistency. You can often ignore some of these parameters when implementing your
 * methods.
 *
 * Some of these methods should be recursive. You may want to consider using public-private
 * pairs in some cases.
 */
public class ExpressionManipulators {
    /**
     * Checks to make sure that the given node is an operation AstNode with the expected
     * name and number of children. Throws an EvaluationError otherwise.
     */
    private static void assertNodeMatches(AstNode node, String expectedName, int expectedNumChildren) {
        if (!node.isOperation()
                && !node.getName().equals(expectedName)
                && node.getChildren().size() != expectedNumChildren) {
            throw new EvaluationError("Node is not valid " + expectedName + " node.");
        }
    }

    /**
     * Accepts an 'toDouble(inner)' AstNode and returns a new node containing the simplified version
     * of the 'inner' AstNode.
     *
     * Preconditions:
     *
     * - The 'node' parameter is an operation AstNode with the name 'toDouble'.
     * - The 'node' parameter has exactly one child: the AstNode to convert into a double.
     *
     * Postconditions:
     *
     * - Returns a number AstNode containing the computed double.
     *
     * For example, if this method receives the AstNode corresponding to
     * 'toDouble(3 + 4)', this method should return the AstNode corresponding
     * to '7'.
     * 
     * This method is required to handle the following binary operations
     *      +, -, *, /, ^
     *  (addition, subtraction, multiplication, division, and exponentiation, respectively) 
     * and the following unary operations
     *      negate, sin, cos
     *
     * @throws EvaluationError  if any of the expressions contains an undefined variable.
     * @throws EvaluationError  if any of the expressions uses an unknown operation.
     */
    
    public static AstNode handleToDouble(Environment env, AstNode node) {
        // To help you get started, we've implemented this method for you.
        // You should fill in the locations specified by "your code here"
        // in the 'toDoubleHelper' method.
        //
        // If you're not sure why we have a public method calling a private
        // recursive helper method, review your notes from CSE 143 (or the
        // equivalent class you took) about the 'public-private pair' pattern.

        assertNodeMatches(node, "toDouble", 1);
        AstNode exprToConvert = node.getChildren().get(0);
        return new AstNode(toDoubleHelper(env.getVariables(), exprToConvert));
    }

    private static double toDoubleHelper(IDictionary<String, AstNode> variables, AstNode node) {
        AstNode simpleNode = simplifyHelper(variables, node, true);
        if (simpleNode.isNumber()) {
            return simpleNode.getNumericValue();
        } else if (simpleNode.isVariable()) {
            throw new EvaluationError("Undefined variable " + simpleNode.getName());
        } else {
            String opName = simpleNode.getName();
            if (opName.equals("/")) {
                double numerator = simpleNode.getChildren().get(0).getNumericValue();
                double denum = simpleNode.getChildren().get(1).getNumericValue();
                return numerator / denum;
            }
            return unwrap(variables, node, true).getNumericValue();
        }
    }

    private static AstNode unwrap(IDictionary<String, AstNode> variables, AstNode node, 
            boolean precise) {
        if (node.isNumber()) {
            return node;
        } else if (node.isVariable()) {
            String varName = node.getName();
            if (!variables.containsKey(varName)) {
                throw new EvaluationError("Undefined variable " + varName);
            }
            return unwrap(variables, variables.get(varName), precise);
        } else {
            IList<AstNode> params = node.getChildren();
            params.set(0, new AstNode(toDoubleHelper(variables, params.get(0))));
            if (params.size() > 1) {
                params.set(1, new AstNode(toDoubleHelper(variables, params.get(1))));
            }
            return simplifyHelper(variables, new AstNode(node.getName(), params), precise);
        }
    }
    
    /**
     * Accepts a 'simplify(inner)' AstNode and returns a new node containing the simplified version
     * of the 'inner' AstNode.
     *
     * Preconditions:
     *
     * - The 'node' parameter is an operation AstNode with the name 'simplify'.
     * - The 'node' parameter has exactly one child: the AstNode to simplify
     *
     * Postconditions:
     *
     * - Returns an AstNode containing the simplified inner parameter.
     *
     * For example, if we received the AstNode corresponding to the expression
     * "simplify(3 + 4)", you would return the AstNode corresponding to the
     * number "7".
     *
     * Note: there are many possible simplifications we could implement here,
     * but you are only required to implement a single one: constant folding.
     *
     * That is, whenever you see expressions of the form "NUM + NUM", or
     * "NUM - NUM", or "NUM * NUM", simplify them.
     */
    public static AstNode handleSimplify(Environment env, AstNode node) {
        assertNodeMatches(node, "simplify", 1);
        AstNode param = node.getChildren().get(0);
        return simplifyHelper(env.getVariables(), param, false);
    }

    private static AstNode simplifyHelper(IDictionary<String, AstNode> variables, AstNode node, 
            boolean precise) {
        if (node.isNumber()) {
            return node;
        } else if (node.isVariable()) {
            String name = node.getName();
            if (variables.containsKey(name)) {
                return simplifyHelper(variables, variables.get(name), precise);
            } else {
                return node;
            }
        }
        String opName = node.getName();
        IList<AstNode> params = node.getChildren();
        AstNode param1 = null;
        AstNode param2 = null;
        param1 = simplifyHelper(variables, params.get(0), precise);
        if (opName.equals("negate")) {
            if (param1.isNumber()) {
                return new AstNode(-1 * param1.getNumericValue());
            }
            params.set(0, param1);
        }else if (opName.equals("sin")) {
            if (precise) {
                param1 = new AstNode(Math.sin(param1.getNumericValue()));
                return param1;
            }
            params.set(0, param1);
        } else if (opName.equals("cos")) {
            if (precise) {
                param1 = new AstNode(Math.cos(param1.getNumericValue()));
                return param1;
            }
            params.set(0, param1);
        } else if (params.size() >= 2) {
            param2 = simplifyHelper(variables, params.get(1), precise);
            if (opName.equals("/")) {
                if (param1.isNumber() && param2.isNumber() && param1.getNumericValue() == param2.getNumericValue()) {
                    return new AstNode(1);
                }
                if (precise) {
                    return new AstNode(param1.getNumericValue() / param2.getNumericValue());
                }
            }
            AstNode numNode = getNumberAnswer(opName, param1, param2);
            if (numNode != null) {
                return numNode;
            }
            params = new DoubleLinkedList<>();
            params.add(param1);
            params.add(param2);
        }
        return new AstNode(opName, params);
    }
    
    private static AstNode getNumberAnswer(String opName, AstNode param1, AstNode param2) {
        if (opName.equals("+")) {
            if (param1.isNumber() && param2.isNumber()) {
                return new AstNode(param1.getNumericValue() + param2.getNumericValue());
            }
        } else if (opName.equals("-")) {
            if (param1.isNumber() && param2.isNumber()) {
                return new AstNode(param1.getNumericValue() - param2.getNumericValue());
            }
        } else if (opName.equals("*")) {
            if (param1.isNumber() && param2.isNumber()) {
                return new AstNode(param1.getNumericValue() * param2.getNumericValue());
            }
        } else if (opName.equals("^")) {
            if (param1.isNumber() && param2.isNumber()) {
                return new AstNode(Math.pow(param1.getNumericValue(), param2.getNumericValue()));
            }
        }
        return null;
    }
    /**
     * Accepts an Environment variable and a 'plot(exprToPlot, var, varMin, varMax, step)'
     * AstNode and generates the corresponding plot on the ImageDrawer attached to the
     * environment. Returns some arbitrary AstNode.
     *
     * Example 1:
     *
     * >>> plot(3 * x, x, 2, 5, 0.5)
     *
     * This method will receive the AstNode corresponding to 'plot(3 * x, x, 2, 5, 0.5)'.
     * Your 'handlePlot' method is then responsible for plotting the equation
     * "3 * x", varying "x" from 2 to 5 in increments of 0.5.
     *
     * In this case, this means you'll be plotting the following points:
     *
     * [(2, 6), (2.5, 7.5), (3, 9), (3.5, 10.5), (4, 12), (4.5, 13.5), (5, 15)]
     *
     * ---
     *
     * Another example: now, we're plotting the quadratic equation "a^2 + 4a + 4"
     * from -10 to 10 in 0.01 increments. In this case, "a" is our "x" variable.
     *
     * >>> c := 4
     * 4
     * >>> step := 0.01
     * 0.01
     * >>> plot(a^2 + c*a + a, a, -10, 10, step)
     *
     * ---
     *
     * @throws EvaluationError  if any of the expressions contains an undefined variable.
     * @throws EvaluationError  if varMin > varMax
     * @throws EvaluationError  if 'var' was already defined
     * @throws EvaluationError  if 'step' is zero or negative
     */
    public static AstNode plot(Environment env, AstNode node) {
        assertNodeMatches(node, "plot", 5);
        IList<AstNode> params = node.getChildren();
        AstNode exprToPlot = params.get(0);
        String variable = params.get(1).getName();
        double varMin = toDoubleHelper(env.getVariables(), params.get(2));
        double varMax = toDoubleHelper(env.getVariables(), params.get(3));
        double step = toDoubleHelper(env.getVariables(), params.get(4));
        double cur = varMin - step;
        AstNode result = null;
        if (varMin > varMax || env.getVariables().containsKey(variable) || step <= 0) {
            throw new EvaluationError("Plot error due to one/more violations");
        }
        ImageDrawer graphic = env.getImageDrawer();
        IList<Double> xPoints = new DoubleLinkedList<>();
        IList<Double> yPoints = new DoubleLinkedList<>();
        env.getVariables().put(variable, new AstNode(varMin - step));
       
        while (cur < varMax) {
            cur += step;
            env.getVariables().put(variable, new AstNode(cur));
            result = new AstNode(toDoubleHelper(env.getVariables(), exprToPlot));
            xPoints.add(cur);
            yPoints.add(result.getNumericValue());
        }
        env.getVariables().remove(variable);
        graphic.drawScatterPlot("Plot", variable, "f("+variable+")", xPoints, yPoints);
        return new AstNode(1);
    }
}
