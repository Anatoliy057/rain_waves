package stud.cg.task5.view;

import stud.cg.task5.util.ScreenConverter;
import stud.cg.task5.world.DropRender;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.security.Key;
import java.util.*;
import java.util.List;

import static java.awt.event.KeyEvent.*;

public class DrawPanel extends JPanel implements MouseListener, KeyListener, MouseWheelListener {

    private Set<Integer> keys;

    private ScreenConverter sc;
    private DropRender dropRender;
    private int[][][] gif;

    private int[][] buffer;

    private Timer updater;
    private Timer producer;

    private int dtUpdate = -1;
    private int dtProduce = -1;
    private int size = -1;

    private long timeLive;
    private double impulse;

    private boolean update;
    private boolean produce;

    private List<Drop> list = Collections.synchronizedList(new LinkedList<>());

    public DrawPanel(int size, long timeLive, int dtUpdate, int dtProduce, double impulse) {
        super();
        this.addMouseListener(this);
        this.addMouseWheelListener(this);

        this.timeLive = timeLive;
        this.impulse = impulse;

        keys = new HashSet<>();

        sc = new ScreenConverter(-4, -4, 8, 8, size, size);
        dropRender = new DropRender(timeLive, 3, (t, r) -> Math.sqrt(1-t) * r, impulse);
        gif = dropRender.render((int) (timeLive/1000) * 60, sc, d -> (int) (d*1000));

        producer = new Timer(dtProduce, (e) -> {
            Random random = new Random();
            Drop drop = new Drop(gif, random.nextInt(getWidth()) + gif[0].length/2, random.nextInt(getHeight()) + gif[0].length/2);
            list.add(drop);
        });

        updater = new Timer(dtUpdate, (e) -> {
            buffer = new int[getWidth() + gif[0].length * 2][getHeight() + gif[0].length * 2];
            list.stream()
                    .peek(Drop::next)
                    .filter(Drop::isAlive)
                    .peek(d -> d.draw(buffer))
            .count();
            repaint();
        });
    }


    @Override
    public void paint(Graphics g) {
        BufferedImage bi = new BufferedImage(
                getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);

        if (buffer != null)
        for (int i = 0; i < bi.getWidth(); i++) {
            for (int j = 0; j < bi.getHeight(); j++) {
                double k = buffer[i + gif[0].length][j + gif[0].length];
                bi.setRGB(i, j, new Color(0, 0, (int) (k < 1000 ? ((k / 1000.0) * 255) : 255)).getRGB());
            }
        }
        g.drawImage(bi, 0, 0, null);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        keys.add(e.getKeyCode());
        switch (e.getKeyCode()) {
            case VK_P : {
                if (keys.contains(VK_SHIFT)) {
                    if (keys.contains(VK_CONTROL)) {
                        if (producer.isRunning())
                            producer.stop();
                        else
                            producer.start();
                    } else {
                        produce = true;
                        update = false;
                    }
                }
                break;
            }
            case VK_U : {
                if (keys.contains(VK_SHIFT)) {
                    if (keys.contains(VK_CONTROL)) {
                        if (updater.isRunning())
                            updater.stop();
                        else
                            updater.start();
                    } else {
                        produce = false;
                        update = true;
                    }
                }
                break;
            }
            case VK_UP : {
                if (keys.contains(VK_SHIFT)) {
                    if (update) {
                        if (dtUpdate == -1) {
                            dtUpdate = updater.getDelay();
                        }
                        dtUpdate += 10;
                        System.out.println(dtUpdate);
                    } else if (produce) {
                        if (dtProduce == -1) {
                            dtProduce = producer.getDelay();
                        }
                        dtProduce += 10;
                        System.out.println(dtProduce);
                    }
                }
                break;
            }
            case VK_DOWN : {
                if (keys.contains(VK_SHIFT)) {
                    if (update) {
                        if (dtUpdate == -1) {
                            dtUpdate = updater.getDelay();
                        }
                        dtUpdate -= 10;
                        System.out.println(dtUpdate);
                    } else if (produce) {
                        if (dtProduce == -1) {
                            dtProduce = producer.getDelay();
                        }
                        dtProduce -= 10;
                        System.out.println(dtProduce);
                    }
                }
                break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys.remove(e.getKeyCode());
        switch (e.getKeyCode()) {
            case VK_SHIFT : {
                if (dtProduce != -1) {
                    producer.stop();
                    producer = new Timer(dtProduce, (ea) -> {
                        Random random = new Random();
                        Drop drop = new Drop(gif, random.nextInt(getWidth()) + gif[0].length/2, random.nextInt(getHeight()) + gif[0].length/2);
                        list.add(drop);
                    });
                    producer.start();
                    dtProduce = -1;
                }
                if (dtUpdate != -1) {
                    updater.stop();
                    updater = new Timer(dtUpdate, (ea) -> {
                        buffer = new int[getWidth() + gif[0].length * 2][getHeight() + gif[0].length * 2];
                        list.stream()
                                .peek(Drop::next)
                                .filter(Drop::isAlive)
                                .peek(d -> d.draw(buffer))
                                .count();
                        repaint();
                    });
                    updater.start();
                    dtUpdate = -1;
                }
                if (size != -1) {
                    updater.stop();
                    producer.stop();
                    list.clear();
                    sc = new ScreenConverter(-4, -4, 8, 8, size, size);
                    dropRender = new DropRender(timeLive, 3, (t, r) -> Math.sqrt(1-t) * r, impulse);
                    gif = dropRender.render((int) (timeLive/1000) * 60, sc, d -> (int) (d*1000));
                    updater.start();
                    producer.start();
                    size = -1;
                }
                break;
            }
        }
    }


    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (keys.contains(VK_SHIFT)) {
            if (size == -1) {
                size = gif[0].length;
            }
            size += e.getUnitsToScroll();
            System.out.println(size);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Drop drop = new Drop(gif, e.getLocationOnScreen().x + gif[0].length/2, e.getLocationOnScreen().y +  gif[0].length/2);
        list.add(drop);
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
