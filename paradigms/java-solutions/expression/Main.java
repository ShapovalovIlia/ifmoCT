package expression;

import java.util.Scanner;
import java.util.Stack;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter an expression: ");
        String expression = scanner.nextLine();
        int result = evaluateExpression(expression);
        System.out.println("Result: " + result);
    }

    private static int evaluateExpression(String expression) {
        Stack<Integer> values = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (c == ' ') {
                continue;
            }

            if (Character.isDigit(c)) {
                int num = 0;
                while (i < expression.length() && Character.isDigit(expression.charAt(i))) {
                    num = num * 10 + (expression.charAt(i) - '0');
                    i++;
                }
                i--;
                values.push(num);
            } else if (c == '(') {
                operators.push(c);
            } else if (c == ')') {
                while (operators.peek() != '(') {
                    values.push(performOperation(operators.pop(), values.pop(), values.pop()));
                }
                operators.pop();
            } else if (c == '+' || c == '-' || c == '*' || c == '/') {
                while (!operators.empty() && hasPrecedence(c, operators.peek())) {
                    values.push(performOperation(operators.pop(), values.pop(), values.pop()));
                }
                operators.push(c);
            }
        }

        while (!operators.empty()) {
            values.push(performOperation(operators.pop(), values.pop(), values.pop()));
        }

        return values.pop();
    }

    private static boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') {
            return false;
        }
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) {
            return false;
        }
        return true;
    }

    private static int performOperation(char operator, int b, int a) {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) {
                    throw new UnsupportedOperationException("Cannot divide by zero");
                }
                return a / b;
        }
        return 0;
    }
}
