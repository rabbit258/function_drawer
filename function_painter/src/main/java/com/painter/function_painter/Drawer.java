package com.painter.function_painter;

import com.singularsys.jep.JepException;
import javafx.beans.binding.ObjectExpression;
import javafx.collections.ObservableList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.math.BigDecimal;

public class Drawer {
    private GraphicsContext gc;
    //当前图标的x范围，y范围，当前线段之间间距，预先设置的宽高，以及转化比例
    private double left,right,up,down,gap,preH,preW,ratio;
    private int state=0;
    private double sx,sy;
    //屏幕最少显示的线段数和最多显示的线段数
    private int Minlimit=10,Maxlimit=30;
    public Drawer(GraphicsContext GC){
        //初始化赋值
        gc=GC;
        gap=0.5;
        preH=gc.getCanvas().getHeight();
        preW=gc.getCanvas().getWidth();
        ratio=preH/preW;
        right=6;
        up=right*ratio;
        down=-up;
        left=-right;
        //gc.strokeLine(0,0,100,100);
        //System.out.println(gc.getCanvas().getWidth()+" "+gc.getCanvas().getHeight());
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(new Font("Microsoft YaHei", 20));
        //gc.fill

        //同时注册鼠标拖拽事件
        //当鼠标按下时记录鼠标位置，供随后使用
        gc.getCanvas().setOnMousePressed(mouseEvent -> {
            sx=mouseEvent.getX();
            sy=mouseEvent.getY();
        });

        //当鼠标开始拖拽时，记录当前位置，从而得到一个位移向量
        //利用位移向量在整个屏幕中移动的距离来进行等比例移动
        //这样就能做到实现鼠标拖拽移动函数图像，本质上是每移动一次然后重新绘制一次
        gc.getCanvas().setOnMouseDragged(mouseEvent -> {
            //redraw();
            double tx=mouseEvent.getX();
            double ty=mouseEvent.getY();

            double gapx=(tx-sx)/preW*(right-left);
            left-=gapx;
            right-=gapx;

            double gapy=(sy-ty)/preH*(up-down);
            up-=gapy;
            down-=gapy;

            redraw();

            sx=tx;
            sy=ty;
        });

        //鼠标缩放事件
        //每次减少或增大3倍当前的gap，利用当前鼠标位置进行比例缩放
        gc.getCanvas().addEventFilter(ScrollEvent.SCROLL,scrollEvent -> {
            //System.out.println(scrollEvent.getDeltaX()+" "+scrollEvent.getDeltaY());
            //首先计算是变大还是缩放
             double ta=-scrollEvent.getDeltaY()/Math.abs(scrollEvent.getDeltaY());
             double tx=scrollEvent.getX(),ty=scrollEvent.getY();
             double movegap=3*gap;
             left-=ta*movegap*tx/preW;
             right+=ta*movegap*(1-tx/preW);

             down-=ta*movegap*ratio*ty/preH;
             up+=ta*movegap*ratio*(1-ty/preH);

             redraw();
        });
    }
    //计算当前gap下会显示多少根x轴上辅助线
    public int getlinecntx(){
        return (int)Math.floor((right-left)/gap);
    }
    //计算当前gap下会显示多少根y轴上辅助线
    public int getlinecnty(){
        return (int)Math.floor((up-down)/gap);
    }
    //分别在x轴和y轴上画线，这里是为了方便调用，减少代码量
    public void strokexline(double ratiox){
        //System.out.println(ratiox);
        gc.strokeLine(ratiox*preW,0,ratiox*preW,gc.getCanvas().getHeight());
    }
    public void strokeyline(double ratioy){
        gc.strokeLine(0,ratioy*preH,gc.getCanvas().getWidth(),ratioy*preH);
    }
    //下面两个方法用于减少或增加gap，由于要适配1 2 5的间距，所以利用state作为当前状态的标记
    //进行特殊的缩放或扩大
    public void increase_gap(){
        if(state<2){
            ++state;
            gap*=2;
        }
        else {
            state=0;
            gap*=2.5;
        }
    }
    public void decrease_gap(){
        if(state==0){
            state=2;
            gap/=2.5;
        }
        else {
            --state;
            gap/=2;
        }
    }

    //格式化显示，防止因为过多的缩放而出现小数位过多的情况
    private String convertDoubleToString(double val) {
        val=Double.parseDouble(String.format("%.8f",val));
        BigDecimal bd = new BigDecimal(String.valueOf(val));
        return bd.stripTrailingZeros().toPlainString();
    }
    //绘制辅助线，辅助坐标值
    public void drawsup(){
        //把x轴和y轴上辅助线的数量控制在范围之类
        while(getlinecntx()<Minlimit)decrease_gap();
        while(getlinecntx()>Maxlimit)increase_gap();
        while(getlinecnty()<Minlimit)decrease_gap();
        while(getlinecnty()>Maxlimit)increase_gap();

        gc.setLineWidth(1);

        //计算屏幕内第一个x轴上辅助线的位置
        //然后不断增加这个位置，直到超出当前x值的上限
        double stx =(int)(left/gap) *gap;
        int xcnt=(int)(stx/gap);
        while(stx<=right){

            //当gap为1 2时，每5根线显示一次坐标;当gap为5时，每4次显示一次坐标
            //同时将该辅助线标粗一点，这样能优化视觉效果
            if(xcnt!=0&&((state!=0&&xcnt%5==0)||(state==0&&xcnt%4==0))){
                gc.setStroke(Color.rgb(150,150,150));
                strokexline((stx-left)/(right-left));

                gc.setStroke(Color.BLACK);
                double x=(stx-left)/(right-left)*preW;
                double y=up/(up-down)*preH;

                //辅助数字由当前辅助线的绝对下标*gap得到
                String str=convertDoubleToString(xcnt*gap);

                //辅助数字的显示位置由当前x，y的显示范围决定
                //可以显示在x轴附近，屏幕最上面或者最下面
                if(down<=0&&up>=0&&y<=preH-55){
                    gc.fillText(str,x,y+20);
                }
                else if(up<=0){
                    gc.fillText(str,x,20);
                }
                else {
                    gc.fillText(str,x,695);
                }
            }
            else {
                gc.setStroke(Color.web("#DCDCDCFF"));
                strokexline((stx-left)/(right-left));
            }

            stx+=gap;
            xcnt++;
        }

        double sty=(int)(down/gap)*gap;
        double ycnt=(int)(sty/gap);
        while(sty<=up){
//            gc.setStroke(Color.web("#DCDCDCFF"));
//            strokeyline((up-sty)/(up-down));
//            sty+=gap;


            if(ycnt!=0&&((state!=0&&ycnt%5==0)||(state==0&&ycnt%4==0))){
                gc.setStroke(Color.rgb(150,150,150));
                strokeyline((up-sty)/(up-down));

                gc.setStroke(Color.BLACK);
                double x=Math.abs(left/(right-left))*preW;
                double y=(up-sty)/(up-down)*preH;

                String str=convertDoubleToString(ycnt*gap);

                //y轴辅助线计算同x轴，唯一不同的是y轴辅助数字需要考虑数字长度
                //所以这里计算了数字长度，防止超出屏幕的问题
                if(left<=0&&right>=0&&x>=10+3*str.length()){
                    gc.fillText(str,x-10-3*str.length(),y+5);
                }
                else if(right<=0){
                    gc.fillText(str,preW-10-3*str.length(),y+5);
                }
                else {
                    gc.fillText(str,10+3*str.length(),y+5);
                }
            }
            else {
                gc.setStroke(Color.web("#DCDCDCFF"));
                strokeyline((up-sty)/(up-down));
            }

            sty+=gap;
            ycnt++;
        }
    }
    //画x，y轴
    public void drawaxis(){
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.2);

        int cnt=0;
        double x=0,y=0;
        if(left<=0&&right>=0){
            //计算出x轴相对位置
            x=Math.abs(left/(right-left));
            ++cnt;
            strokexline(x);
        }
        if(down<=0&&up>=0){
            //计算出y轴相对位置
            y=Math.abs(up/(up-down));
            ++cnt;
            strokeyline(y);
        }
        //只有同时画了x和y轴才显示坐标原点
        if(cnt==2){
            //System.out.println(x+" "+y);
            gc.fillText("0",x*preW-10,y*preH+20);
        }
    }
    //绘制函数
    public void drawfuncion(){
        //首先得到当前函数列表
        ObservableList<Itempro> strList=Controller.getObList();
        for(Itempro item:strList){
            gc.setStroke(item.getColor());
            double s=left;
            double lx=0,ly=0;
            boolean isok=false;
            //因为已经知道x和y值的显示范围，所以只要规定一个最小间距
            //然后在范围内暴力的去计算每个点的坐标值，就能做到描点的效果
            //然后把每个点连接起来，以线画曲线
            while(s<=right){
                try{
                    Object res=item.getval(s);
                    //System.out.println(res.toString());
                    //System.out.println(res.toString().charAt(0));

                    //当前不在定义域内则不画
                    if(res.toString()!="Nan"&&res.toString().charAt(0)!='('){

                        double y=Double.parseDouble(res.toString());
                        if(down<=y&&y<=up){
                            if(isok) {
                                gc.strokeLine((lx-left)/(right-left)*preW,(up-ly)/(up-down)*preH,
                                        (s-left)/(right-left)*preW,(up-y)/(up-down)*preH);
                            }
                            lx=s;
                            ly=y;
                            isok=true;
                        }
                        else {
                            isok=false;
                        }
                    }
                    else {
                        isok=false;
                    }
                }catch (JepException e){
                    isok=false;
                }
                s+=gap/200;
            }
        }
    }
    public void redraw(){
        //System.out.println(up+" "+down);
        //System.out.println(right+" "+left);
        gc.clearRect(0,0,preW,preH);
        drawsup();
        drawaxis();
        drawfuncion();
    }
}
