package stud.cg.task5.view;

import stud.cg.task5.util.ScreenConverter;
import stud.cg.task5.world.DropRender;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class DrawPanel extends JPanel implements MouseMotionListener, KeyListener, MouseWheelListener {

    private ScreenConverter sc;
    private DropRender dropRender;
    private int[][][] gif;
    private int size;

    private int[][] buffer;

    private Timer timer;
    private List<Drop> list = new LinkedList<>();

    public DrawPanel(int size, long time, long dt, double impulse) {
        super();
        this.addMouseMotionListener(this);
        this.addKeyListener(this);
        this.addMouseWheelListener(this);

        this.size = size;

        sc = new ScreenConverter(-1.3, -1.3, 2.6, 2.6, size, size);
        dropRender = new DropRender(time, 1, (t, r) -> Math.sqrt(1-t) * r, impulse);
        gif = dropRender.render(120, sc, d -> (int) (d*1000));

        timer = new Timer((int) dt, (e) -> {
            buffer = new int[getWidth() + size * 2][getHeight() + size * 2];
            Random random = new Random();
            if (random.nextInt(1) == 0) {
                Drop drop = new Drop(gif, random.nextInt(getWidth()) + size/2, random.nextInt(getHeight()) + size/2);
                list.add(drop);
            }
            list.stream()
                    .peek(Drop::next)
                    .filter(Drop::isAlive)
                    .peek(d -> d.draw(buffer))
            .count();
            repaint();
        });
    }

    public void restart(int size, long time, long dt, double impulse) {
        sc = new ScreenConverter(-1.3, -1.3, 2.6, 2.6, size, size);
        dropRender = new DropRender(time, 1, (t, r) -> Math.sqrt(1-t) * r, impulse);
        gif = dropRender.render(120, sc, d -> (int) (d*1000));

        timer = new Timer((int) dt, (e) -> {
            Random random = new Random();
            if (random.nextInt(30) == 0) {
                Drop drop = new Drop(gif, random.nextInt(getWidth() + size * 2), random.nextInt(getHeight() + size * 2));
                list.add(drop);
            }
            List<Drop> temp = list.stream()
                    .peek(Drop::next)
                    .filter(Drop::isAlive)
                    .collect(Collectors.toList());
            list.clear();
            list.addAll(temp);
            repaint();
        });
        startDraw();
    }

    public void startDraw() {
        timer.start();
    }

    @Override
    public void paint(Graphics g) {
        BufferedImage bi = new BufferedImage(
                getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);

        if (buffer != null)
        for (int i = 0; i < bi.getWidth(); i++) {
            for (int j = 0; j < bi.getHeight(); j++) {
                double k = buffer[i + size][j + size];
                bi.setRGB(i, j, new Color(0, 0, (int) (k < 1000 ? ((k / 1000.0) * 255) : 255)).getRGB());
            }
        }
        g.drawImage(bi, 0, 0, null);
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
        switch (e.getKeyChar()) {
            case 'y':
                timer.start();
                break;
            case 'n' :
                timer.stop();
                break;
            case 't' :
                Drop drop = new Drop(gif, getWidth()/2, getHeight()/2);
                list.add(drop);
                break;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }


    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

    }
}
