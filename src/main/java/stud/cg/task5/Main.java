package stud.cg.task5;

import stud.cg.task5.view.DrawPanel;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        JFrame jFrame = new JFrame();
        jFrame.setSize(500, 500);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        DrawPanel dp = new DrawPanel(400, 3000, 2, 0.3);
        jFrame.add(dp);
        jFrame.addKeyListener(dp);
        jFrame.setVisible(true);
    }
}
