package com.painter.function_painter;

import java.util.ArrayList;
import java.util.List;
import java.text.*;
import java.util.Stack;

public class Calculate {

    public static String x;
    private static String Error = "Domain error";
    private static double halfPI = Math.PI/2;

    public Calculate(String num){//传入x
        x = num;
    }

    public String evaluate(String express) {
        String expression = express;
        double calculate = 0;
        
        try {
            List<String> infixExpression = depositList(expression);// 中缀表达式
            List<String> calculateExpression = parsExpression(infixExpression);// 后缀表达式
            calculate = calculate(calculateExpression);
        }
        catch (RuntimeException e) {
            return e.getMessage();
        }

        if (calculate == 0)
            return "0";
        else
            {
                String result = Double.toString(calculate);
                if(result.equals("NaN"))
                    return "0";
                if (result.indexOf(".") > 0)
                    {
                        result = result.replaceAll("0+?$", "");// 去掉后面无用的零
                        result = result.replaceAll("[.]$", "");// 如小数点后面全是零去掉小数点
                    }
                return result;
            }
    }

    public static List<String> depositList(String s) {//将表达式转换为中缀表达式
        List<String> list = new ArrayList<String>();// 定义一个List,存放中缀表达式 对应的内容
        int i = 0;
        int L = s.length();
        do {
            boolean isMinus = false;//判断当前数字是否为负数
            char c = s.charAt(i);
            if (c == '-')
                {
                    if (i == 0)
                        isMinus = true;
                    else if (s.charAt(i-1) == '(')
                        isMinus = true;
                    else if ((s.charAt(i-1)>57 || s.charAt(i-1)<48) && s.charAt(i-1)!='x' && s.charAt(i-1)!=')')
                        isMinus = true;
                }
            if(c == 'x')
                {
                    if(i-1>=0 && (s.charAt(i-1)=='x' || s.charAt(i-1)==')' || (s.charAt(i-1)>=48&&s.charAt(i-1)<=57)))
                        list.add("*");
                    list.add("" + x);
                    i++;
                }
            else if ((c<48 || c>57) && !isMinus)//不是数字且不是负号,则此时一定为算数运算符
                {
                    if(c == ' ')
                        {
                            i++;
                            continue;
                        }
                    if(i+2<L && s.substring(i, i+3).equals("sin"))
                        {
                            list.add("sin");
                            i += 3;
                        }
                    else if (i+2<L && s.substring(i, i+3).equals("tan"))
                        {
                            list.add("tan");
                            i += 3;
                        }
                    else if (i+2<L && s.substring(i, i+3).equals("cos"))
                        {
                            list.add("cos");
                            i += 3;
                        }
                    else if (i+1<L && c=='l' && s.charAt(i+1)=='n')
                        {
                            list.add("ln");
                            i += 2;
                        }
                    else if (i+2<L && s.substring(i, i+3).equals("log"))
                        {
                            list.add("log");
                            i += 3;
                        }
                    else if (i+1<L && c=='<' && s.charAt(i+1)=='<')
                        {
                            list.add("<<");
                            i += 2;
                        }
                    else if (i+1<L && c=='>' && s.charAt(i+1)=='>')
                        {
                            list.add(">>");
                            i += 2;
                        }
                    else if (i-1>=0 && c=='(' && s.charAt(i-1)==')')
                        {
                            list.add("*");
                            list.add("" + c);
                            i++;
                        }
                    else
                        {
                            list.add("" + c);
                            i++;
                        }
                }
            else
                {
                    String str = ""; // 先将str 置成"" '0'[48]->'9'[57]
                    if(isMinus)
                        {
                            str += '-';
                            ++i;
                            if(i<L && s.charAt(i)=='x')
                                {
                                    list.add("-1");
                                    list.add("*");
                                    list.add(x);
                                    ++i;
                                    continue;
                                }
                        }
                    for(;i<L;++i)
                        {
                            c = s.charAt(i);
                            if((c>=48 && c<=57) || c=='.')
                                {
                                    str += c;
                                }
                            else
                                break;
                        }
                    list.add(str);
                }
        } while (i < L);
        return list;
    }

    public static List<String> parsExpression(List<String> list) {//将中缀表达式转化为后缀表达式对应的List
        Stack<String> s1 = new Stack<String>(); // 符号栈
        List<String> s2 = new ArrayList<String>();
        for (String item : list) {
            String re = "^\\d*\\.\\d*|^\\d*|^-\\d\\d*\\.\\d*|^-\\d\\d*";
            // "^[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|[1-9]\\d*|0|-[1-9]\\d*\\.\\d*|-0\\.\\d*|-[1-9]\\d*$";
            if (item.matches(re))// 如果是一个数加入s2
                s2.add(item);
            else if (item.equals("("))
                s1.push(item);
            else if (item.equals(")"))
                {
                    while (!s1.peek().equals("("))
                        {// 如果是右括号“)”，则依次弹出s1栈顶的运算符，并压入s2，直到遇到左括号为止，此时将这一对括号丢弃
                            s2.add(s1.pop());
                        }
                    s1.pop();// 将 ( 弹出 s1栈， 消除小括号
                }
            else
                {
                    while (s1.size()!=0 && operation.getValue(s1.peek())>=operation.getValue(item))
                        {
                            s2.add(s1.pop());
                        }
                    s1.push(item);
                }
        }

        while (s1.size() != 0)
            { // 将s1中剩余的运算符依次弹出并加入s2
                s2.add(s1.pop());
            }
        return s2;
    }

    public static double calculate(List<String> list) {//通过后缀表达式计算表达式的值
        Stack<String> stack = new Stack<String>();// 创建栈
        for (String item : list)
            {
                String rg = "^\\d*\\.\\d*|^\\d*|^-\\d\\d*\\.\\d*|^-\\d\\d*";
                        // "^[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|[1-9]\\d*|0|-[1-9]\\d*\\.\\d*|-0\\.\\d*|-[1-9]\\d*$";
                if (item.matches(rg))// 使用正则表达式匹配多位数
                    stack.push(item);
                else
                    {
                        if (item.equals("+"))
                            {
                                double res = 0;
                                try{
                                    double num2 = Double.parseDouble(stack.pop());
                                    double num1 = Double.parseDouble(stack.pop());
                                    res = num1 + num2;
                                    DecimalFormat df = new DecimalFormat("0.0000000");
                                    res = Double.parseDouble(df.format(res));
                                }
                                catch(RuntimeException e){
                                    throw new RuntimeException("+左右两侧不可为空!");
                                }
                                
                                stack.push("" + res);
                            }
                        else if (item.equals("-"))
                            {
                                double res = 0;
                                try{
                                    double num2 = Double.parseDouble(stack.pop());
                                    double num1 = Double.parseDouble(stack.pop());
                                    res = num1 - num2;
                                    DecimalFormat df = new DecimalFormat("0.0000000");
                                    res = Double.parseDouble(df.format(res));
                                }
                                catch(RuntimeException e){
                                    throw new RuntimeException("-左右两侧不可为空!");
                                }
                                stack.push("" + res);
                            }
                        else if (item.equals("*"))
                            {
                                double res = 0;
                                try{
                                    double num2 = Double.parseDouble(stack.pop());
                                    double num1 = Double.parseDouble(stack.pop());
                                    res = num1 * num2;
                                    DecimalFormat df = new DecimalFormat("0.0000000");
                                    res = Double.parseDouble(df.format(res));
                                }
                                catch(RuntimeException e){
                                    throw new RuntimeException("*左右两侧不可为空!");
                                }
                                stack.push("" + res);
                            }
                        else if (item.equals("/"))
                            {
                                double res = 0;
                                try{
                                    double num2 = Double.parseDouble(stack.pop());
                                    double num1 = Double.parseDouble(stack.pop());
                                    if(num2 == 0)
                                        throw new RuntimeException(Error);
                                    res = num1 / num2;
                                    DecimalFormat df = new DecimalFormat("0.0000000");
                                    res = Double.parseDouble(df.format(res));
                                }
                                catch(RuntimeException e){
                                    if(e.getMessage().equals(Error))
                                        throw new RuntimeException(Error);
                                    throw new RuntimeException("/左右两侧不可为空!");
                                }
                                stack.push("" + res);
                            }
                        else if (item.equals("sin"))
                            {
                                double res = 0;
                                try{
                                    double num1 = Double.parseDouble(stack.pop());
                                    res = Math.sin(num1);
                                    DecimalFormat df = new DecimalFormat("0.0000000");
                                    res = Double.parseDouble(df.format(res));
                                }
                                catch(RuntimeException e){
                                    throw new RuntimeException("sin右侧不可为空!");
                                }
                                stack.push("" + res);
                            }
                        else if (item.equals("cos"))
                            {
                                double res = 0;
                                try{
                                    double num1 = Double.parseDouble(stack.pop());
                                    res = Math.cos(num1);
                                    DecimalFormat df = new DecimalFormat("0.0000000");
                                    res = Double.parseDouble(df.format(res));
                                }
                                catch(RuntimeException e){
                                    throw new RuntimeException("cos右侧不可为空!");
                                }
                                stack.push("" + res);
                            }
                        else if (item.equals("tan"))
                            {
                                double res = 0;
                                try{
                                    double num1 = Double.parseDouble(stack.pop());
                                    double flag = num1;
                                    while(flag <= 0)
                                        flag += halfPI;
                                    flag %= halfPI;
                                    if(halfPI-flag<=0.02 || halfPI-flag>=halfPI-0.02)
                                        throw new RuntimeException(Error);
                                    res = Math.tan(num1);
                                    DecimalFormat df = new DecimalFormat("0.0000000");
                                    res = Double.parseDouble(df.format(res));
                                }
                                catch(RuntimeException e){
                                    if(e.getMessage().equals(Error))
                                        throw new RuntimeException(Error);
                                    throw new RuntimeException("tan右侧不可为空!");
                                }
                                stack.push("" + res);
                            }
                        else if (item.equals("log"))
                            {
                                double res = 0;
                                try{
                                    double num1 = Double.parseDouble(stack.pop());
                                    if(num1 <= 0)
                                        throw new RuntimeException(Error);
                                    res = Math.log(num1)/Math.log(2);
                                    DecimalFormat df = new DecimalFormat("0.0000000");
                                    res = Double.parseDouble(df.format(res));
                                }
                                catch(RuntimeException e){
                                    if(e.getMessage().equals(Error))
                                        throw new RuntimeException(Error);
                                    throw new RuntimeException("log右侧不可为空!");
                                }
                                stack.push("" + res);
                            }
                        else if (item.equals("ln"))
                            {
                                double res = 0;
                                try{
                                    double num1 = Double.parseDouble(stack.pop());
                                    if(num1 <= 0)
                                        throw new RuntimeException(Error);
                                    res = Math.log(num1);
                                    DecimalFormat df = new DecimalFormat("0.0000000");
                                    res = Double.parseDouble(df.format(res));
                                }
                                catch(RuntimeException e){
                                    if(e.getMessage().equals(Error))
                                        throw new RuntimeException(Error);
                                    throw new RuntimeException("ln右侧不可为空!");
                                }
                                stack.push("" + res);
                            }
                        else if (item.equals("%"))
                            {
                                double res = 0;
                                try{
                                    long num2 = Long.parseLong(stack.pop());
                                    double num1 = Double.parseDouble(stack.pop());
                                    res = num1%num2;
                                    DecimalFormat df = new DecimalFormat("0.0000000");
                                    res = Double.parseDouble(df.format(res));
                                }
                                catch(RuntimeException e){
                                    throw new RuntimeException("%右侧必须为必须为正整数且不可为空!");
                                }
                                stack.push("" + res);
                            }
                        else if (item.equals("^"))
                            {
                                double res = 0;
                                try{
                                    double num2 = Double.parseDouble(stack.pop());
                                    double num1 = Double.parseDouble(stack.pop());
                                    res = Math.pow(num1, num2);
                                    DecimalFormat df = new DecimalFormat("0.0000000");
                                    res = Double.parseDouble(df.format(res));
                                }
                                catch(RuntimeException e){
                                    throw new RuntimeException("^两侧不可为空!");
                                }
                                stack.push("" + res); 
                            }
                        // else if (item.equals("~"))
                        //     {
                        //         long res = 0;
                        //         try{
                        //             long num1 = Integer.parseInt(stack.pop());
                        //             res = ~num1;
                        //         }
                        //         catch(RuntimeException e){
                        //             throw new RuntimeException("~右侧必须为整数且不可为空!");
                        //         }
                        //         stack.push("" + res);
                        //     }
                        else
                            throw new RuntimeException("该运算符有误:"+item);
                    }
            }
        if(stack.size() != 1)
            throw new RuntimeException("函数表达式有误");
        double top;
        try {
            top = Double.parseDouble(stack.peek());
        } catch (NumberFormatException e) {
            throw new RuntimeException("函数表达式有误");
        }
        return top;
    }
}