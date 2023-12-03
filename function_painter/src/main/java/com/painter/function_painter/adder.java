package com.painter.function_painter;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

//工具类，用于修改函数表达式或者添加新函数
//所以大部分都是静态方法
public class adder {
    //文字提示框
    public Label tip;
    //
    public static function target;
    //输入框
    public static TextField input;
    //确定，取消按钮
    public static Button confirm,cancel;
    //布局，仅用作装饰
    public GridPane gr;
    //窗口，作为javafx的接口
    public static Stage stage;
    //舞台，作为javafx的接口
    public Scene scene;
    adder(){
        //构造函数中做布局初始化
        tip=new Label("f(x):");
        input=new TextField();
        confirm =new Button("确认");
        cancel =new Button("取消");
        gr = new GridPane();

        //细化布局和ui
        gr.add(tip,0,0);
        gr.add(input,1,0);
        gr.add(confirm,0,1);
        gr.add(cancel,1,1);
        gr.setAlignment(Pos.CENTER);
        //GridPane.setHalignment(tip,HPos.RIGHT);
        //GridPane.setMargin(confirm,new Insets(0,0,0,50));
        GridPane.setHalignment(tip, HPos.CENTER);
        GridPane.setHalignment(cancel,HPos.RIGHT);
        gr.setHgap(10);
        gr.setVgap(20);
        stage = new Stage();
        scene = new Scene(gr,250,120);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);

        //设置取消按钮的点击事件
        cancel.setOnMouseClicked(mouseEvent -> {
            stage.close();
        });
    }

    //参数为函数旧表达式和对应函数类的引用
    public static void getinput(String old_expression, function point){
        input.setText(old_expression);
        input.setUserData(old_expression);

        target=point;
        stage.show();

    }
    //因为java中没有默认参数的说法，所以重载getinput方法，放默认参数进去
    //用于新建一个函数类
    public static void getinput(){
        getinput(new String("x^2"),null);
    }
}
