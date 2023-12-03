package com.painter.function_painter;

import com.singularsys.jep.Jep;
import com.singularsys.jep.JepException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

//工具类，作为builder和javafx的通信接口
public class Controller {
    //javafx内置控件的声明
    private Canvas canvas;
    @FXML
    private Pane pane;
    @FXML
    private BorderPane borderpane;
    @FXML
    private ListView<Itempro> listview;
    static private ObservableList strList;
    static private Drawer drawer;
    //重新绘制函数图像
    public static void redraw(){
        drawer.redraw();
    }
    //返回画布的画笔
    private GraphicsContext getgc(){
        return canvas.getGraphicsContext2D();
    }
    //返回当前函数列表
    public static ObservableList<Itempro> getObList(){
        return (ObservableList<Itempro>)strList;
    }
    public void initialize(){
        //初始化布局
        canvas=new Canvas(1020,720);
        pane.getChildren().add(canvas);
        pane.setStyle("-fx-background-color: white");

        MenuBar mb=new MenuBar();
        Menu me=new Menu("功能");
        MenuItem add=new MenuItem("添加");
        MenuItem allc=new MenuItem("清空");

        me.getItems().addAll(add,allc);
        mb.getMenus().add(me);

        borderpane.setTop(mb);

        //添加清空事件，用于清空当前函数列表
        allc.setOnAction(actionEvent -> {
            listview.getItems().clear();
            redraw();
        });

        //初始化函数列表
        strList = FXCollections.observableArrayList();
        strList.add(new Itempro("1/x"));
        strList.add(new Itempro("sin(x)"));
        listview.setItems(strList);
        listview.setCellFactory((ListView<Itempro> l)->new function());
        //listview.setMouseTransparent( true );

        //优化布局和ui
        listview.setFocusTraversable( false );
        listview.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //System.out.println(">> Mouse Clicked");
                event.consume();//消费掉事件
            }
        });
        listview.getStylesheets().add(getClass().getResource("listview.css").toExternalForm());

        drawer =new Drawer(getgc());
        drawer.redraw();

        adder z=new adder();
        //注册添加器的添加按钮的点击事件
        adder.confirm.setOnMouseClicked(mouseEvent -> {
            try{
                //利用当前输入框中的内容做语法检查
                Jep jep=new Jep();
                String temp=adder.input.getText();
                jep.setAllowUndeclared(true);
                jep.parse(temp);

                //不合法情况
                if(jep.getVariableTable().size()>5&&(jep.getVariableTable().size()>=7||!jep.getVariableTable().containsKey("x"))){
                    throw new JepException("请以小写的x为自变量,且不要带上其他不为x自变量");
                }
                //target是传进来的function类，如果为空代表这是要添加一个新的函数
                if(adder.target==null){
                    listview.getItems().add(new Itempro(temp));
                    adder.stage.close();
                }
                //否则修改原来的函数m
                else {
                    listview.getItems().get(adder.target.getIndex()).setExpression(temp);
                    adder.target.updateItem(listview.getItems().get(adder.target.getIndex()),false);
                    adder.stage.close();
                }
                redraw();
            }catch (JepException e){
                //显示异常类的提示文本
                Alert alert=new Alert(Alert.AlertType.ERROR);
                alert.setContentText(e.getMessage());
                alert.show();
            }
        });
        //给菜单的添加按钮注册添加函数事件
        add.setOnAction(actionEvent -> {
            adder.getinput();
        });
    }
}
