package calculator.ast;

import calculator.errors.EvaluationError;
import calculator.interpreter.Environment;
import calculator.interpreter.Interpreter;
import datastructures.interfaces.IList;

/**
 * Note: this file is meant for the extra credit portion of this assignment
 * focused around adding a programming language to our calculator.
 *
 * If you choose to work on this extra credit, feel free to add additional
 * control flow handlers beyond the two listed here. Be sure to register
 * each new function inside the 'Calculator' class -- see line 59.
 */
public class ControlFlowManipulators {
    /**
     * Handles AST nodes corresponding to "randomlyPick(body1, body2)"
     *
     * Preconditions:
     *
     * - Receives an operation node with the name "randomlyPick" and two (arbitrary) children
     *
     * Postcondition:
     *
     * - This method will randomly decide to evaluate and return the result of either body1 or
     *   body2 with 50% probability. If body1 is interpreted, body2 is ignored completely and vice versa.
     */
    public static AstNode handleRandomlyPick(Environment env, AstNode wrapper) {
        AstNode body1 = wrapper.getChildren().get(0);
        AstNode body2 = wrapper.getChildren().get(1);

        Interpreter interp = env.getInterpreter();
        if (Math.random() < 0.5) {
            // Note: when implementing this method, we do NOT want to
            // manually recurse down either child: we instead want the calculator
            // to take back full control and evaluate whatever the body1 or body2
            // AST nodes might be. To do so, we use the 'Interpreter' object
            // available to us within the environment.
            return interp.evaluate(env, body1);
        } else {
            return interp.evaluate(env, body2);
        }
    }

    /**
     * Handles AST nodes corresponding to "if(cond, body, else)"
     *
     * Preconditions:
     *
     * - Receives an operation node with the name "if" and three children
     *
     * Postcondition:
     *
     * - If 'cond' evaluates to any non-zero number, interpret the "body" AST node and ignore the
     *   "else" node completely.
     * - Otherwise, evaluate the "else" node.
     * - In either case, return the result of whatever AST node you ended up interpreting.\
     * @Throws EvaluationError if eith condition/body/else cannot be evaluated to a number.
     */
    public static AstNode handleIf(Environment env, AstNode wrapper) {
        IList<AstNode> params = wrapper.getChildren();
        Interpreter interp = env.getInterpreter();
        AstNode cond = interp.evaluate(env, params.get(0));
        if (!cond.isNumber()) {
            throw new EvaluationError("Condition is invalid");
        }
        AstNode result = null;
        if (cond.getNumericValue() > 0) {
            result = interp.evaluate(env, params.get(1));
        } else {
            result = interp.evaluate(env, params.get(2));
        }
        if (result == null || !result.isNumber()) {
            throw new EvaluationError("body/else condtion is invalid");
        }
        return result;
    }

    /**
     * Handles AST nodes corresponding to "repeat(times, body)"
     *
     * Preconditions:
     *
     * - Receives an operation node with the name "repeat" and two children
     * - The 'times' AST node is assumed to be some arbitrary AST node that,
     *   when interpreted, will also produce an integer result.
     *
     * Postcondition:
     *
     * - Repeatedly evaluates the given body the specified number of times.
     * - Returns the result of interpreting 'body' for the final time.
     * @throws EvaluationError if the given parameters cannot be evaluated.
     */
    public static AstNode handleRepeat(Environment env, AstNode wrapper) {
        IList<AstNode> params = wrapper.getChildren();
        Interpreter interp = env.getInterpreter();
        double repeatTime = interp.evaluate(env, params.get(0)).getNumericValue();
        if (repeatTime < 0) {
            throw new EvaluationError("Repeat time cannot be negative!");
        }
        double accumulator = 0;
        while (repeatTime > 0) {
            AstNode node = interp.evaluate(env, params.get(1));
            if (!node.isNumber()) {
                throw new EvaluationError("Fail to evaluated");
            }
            accumulator += 
            repeatTime--;
        }
        return new AstNode(accumulator);
    }
    
    /**
     * Handles AST nodes corresponding to "for(lower value, max value, variable, step, body)"
     * Example usage: for(0, 100, i, 1, i) will return the sum of 0 + 1 + 2 + 3 +...+ 99
     * Note: this is same in java as for(0, i < 100; i++)
     *
     * Preconditions:
     *
     * - all parameters except variable name can be non-number AST nodes
     *
     * Postcondition:
     *  Evaluate the body a number of times
     *  Returns the result of interpreting 'body' for the final time.
     *  @throw EvaluationError if variable has already been defined.
     */
    public static AstNode handleFor(Environment env, AstNode wrapper) {
        IList<AstNode> params = wrapper.getChildren();
        Interpreter interp = env.getInterpreter();
        double start = interp.evaluate(env, params.get(0)).getNumericValue();
        double end = interp.evaluate(env, params.get(1)).getNumericValue();
        String var = params.get(2).getChildren().get(0).getName();
        double step = interp.evaluate(env, params.get(3)).getNumericValue();
        double count = start;
        double accumulator = 0;
        AstNode body = params.get(4);
        if (env.getVariables().containsKey(var)) {
            throw new EvaluationError("Variable has already been defined");
        }
        if (step < 0) {
            count += step;
        }
        env.getVariables().put(var, new AstNode(count));
        while (Math.abs(count - end) > 0) {
            env.getVariables().put(var, new AstNode(count));
            count += step;
            AstNode result = interp.evaluate(env, body);
            if (result.isNumber()) {
                accumulator += result.getNumericValue();
            }
        }
        env.getVariables().remove(var);
        return new AstNode(accumulator);
    }
}
