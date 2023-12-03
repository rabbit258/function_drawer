package com.painter.function_painter;

import com.singularsys.jep.EvaluationException;
import com.singularsys.jep.Jep;
import com.singularsys.jep.JepException;
import javafx.scene.paint.Color;

//信息类，携带函数表达式的所有信息
public class Itempro {
    private String expression;
    private Color color;
    private Jep jep;
    public Itempro(String str){
        setExpression(str);
        color=null;
    }
    //检测是否初始化过，防止重复初始化
    public boolean isini(){
        return color==null;
    }
    //设置颜色
    public void setColor(Color c){
        color=c;
    }
    //设置函数表达式
    public void setExpression(String str){
        expression=str;
        jep=new Jep();
        jep.setAllowUndeclared(true);
        jep.addVariable("x");
        try {
            jep.parse(str);
        }
        catch (JepException e){}
    }
    //接口，返回当前表达式的颜色
    public Color getColor(){
        return color;
    }
    //接口，返回表达式
    public String getExpression(){
        return expression;
    }
    //输入一个x的值，返回表达式的值
    public Object getval(double x) throws EvaluationException {
        jep.setVariable("x",x);
        return jep.evaluate();
    }
}
