package com.painter.function_painter;

import com.singularsys.jep.Jep;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

//import java.awt.Color;
import java.util.Random;

public class function extends ListCell<Itempro> {
    public Label tip,fx;
    public Button sx,cl;
    public ColorPicker sc;
    public HBox h1,h2;
    public VBox v;
    //默认颜色
    static public String[] mColors = {
            "#000000", //dark
            "#39add1", // light blue
            "#e15258", // red
            "#51b46d", // green
            "#7d669e", // purple
            "#3079ab", // dark blue
            "#c25975", // mauve
            "#f9845b", // orange
            "#838cc7", // lavender
            "#53bbb4", // aqua
            "#e0ab18", // mustard
            "#637a91", // dark gray
            "#f092b0", // pink
            "#b7c0c7"  // light gray
    };
    public void ini(){
        //初始化布局
        tip=new Label("f(x):");
        fx=new Label();
        sc=new ColorPicker();
        sx=new Button("修改");
        cl=new Button("删除");
        h1=new HBox(tip,fx);
        h2=new HBox(sc,sx,cl);
        v=new VBox(h1,h2);
        h1.setAlignment(Pos.CENTER);
        h2.setAlignment(Pos.CENTER);
        h2.setSpacing(20);
        v.setMargin(h2,new Insets(10,10,10,10));
        v.setAlignment(Pos.CENTER);
        tip.setFont(new Font("Arial",20));
        fx.setFont(new Font("Arial",20));

        sc.setStyle("-fx-focus-color: firebrick");
        sx.setStyle("-fx-focus-color: firebrick");
        cl.setStyle("-fx-focus-color: firebrick");

        //注册更新颜色事件
        sc.setOnAction(actionEvent -> {
            getListView().getItems().get(getIndex()).setColor(sc.getValue());
            updatecolor(sc.getValue());
            Controller.redraw();
        });

        //注册修改表达式事件
        sx.setOnMouseClicked(mouseEvent -> {
            adder.getinput(fx.getText().toString(),this);
            Controller.redraw();
        });

        //注册移除表达式事件
        cl.setOnMouseClicked(mouseEvent -> {
//            if (getItem() == getListView().getSelectionModel().getSelectedItem()) {
//                getListView().getSelectionModel().clearSelection();
//            }
              //getListView().getItems().
              getListView().getItems().remove(getIndex());
              Controller.redraw();
        });
    }
    //更新颜色
    public void updatecolor(Color color){
        tip.setTextFill(color);
        fx.setTextFill(color);
        sc.setValue(color);
    }
    //重载布局，属于javafx的规定语法
    @Override
    protected void updateItem(Itempro Item,boolean empty){
        super.updateItem(Item,empty);
        if(Item!=null&&!empty){
            ini();
            Controller.redraw();
            if(Item.getColor()==null)Item.setColor(Color.web(mColors[getIndex()%mColors.length]));
            updatecolor(Item.getColor());
            fx.setText(Item.getExpression());
            setGraphic(v);
        }
        else {
            setGraphic(null);
        }
    }
}
