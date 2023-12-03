package com.painter.function_painter;
public class operation {// 编写一个类 Operation 可以返回一个运算符 对应的优先级
    private static int Log = 8;
    private static int Ln = 8;
    private static int Sin = 8;
    private static int Cos = 8;
    private static int Tan = 8;
    private static int Or = 2;
    private static int And = 2;
    private static int Left = 3;
    private static int Right = 3;
    private static int Add = 4;
    private static int Sub = 4;
    private static int Mul = 5;
    private static int Div = 5;
    private static int Mod = 5;
    private static int Pow = 6;
    private static int Neg = 7;

    public static int getValue(String operation) {// 写一个方法，返回对应的优先级数字
        int result = 0;
        switch (operation)
            {
                case "+":
                    result = Add;
                    break;
                case "-":
                    result = Sub;
                    break;
                case "*":
                    result = Mul;
                    break;
                case "/":
                    result = Div;
                    break;
                case "sin":
                    result = Sin;
                    break;
                case "cos":
                    result = Cos;
                    break;
                case "tan":
                    result = Tan;
                    break;
                case "log":
                    result = Log;
                    break;
                case "ln":
                    result = Ln;
                    break;
                case "|":
                    result = Or;
                    break;
                case "&":
                    result = And;
                    break;
                case "<<":
                    result = Left;
                    break;
                case ">>":
                    result = Right;
                    break;
                case "%":
                    result = Mod;
                    break;
                case "^":
                    result = Pow;
                    break;
                case "~":
                    result = Neg;
                    break;
                default:
                    break;
            }
        return result;
    }

}