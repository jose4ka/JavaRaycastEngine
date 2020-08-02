import javax.swing.*;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import javax.swing.JFrame;

public class Game extends JFrame implements Runnable {

    private static final long serialVersionUID = 1L;
    public int mapWidth = 15; //Ширина карты
    public int mapHeight = 15; //Высота карты
    private Thread thread; //Поток для отрисовки
    private boolean running; //Работает ли игра
    private BufferedImage image; //Изображение выводимое на экран
    public int[] pixels; //Массив всех пикселей в изображении
    public ArrayList<Texture> textures;
    public static int[][] map =
            {
                    {1,1,1,1,1,1,1,1,2,2,2,2,2,2,2},
                    {1,0,0,0,0,0,0,0,2,0,0,0,0,0,2},
                    {1,0,3,3,3,3,3,0,0,0,0,0,0,0,2},
                    {1,0,3,0,0,0,3,0,2,0,0,0,0,0,2},
                    {1,0,3,0,0,0,3,0,2,2,2,0,2,2,2},
                    {1,0,3,0,0,0,3,0,2,0,0,0,0,0,2},
                    {1,0,3,3,0,3,3,0,2,0,0,0,0,0,2},
                    {1,0,0,0,0,0,0,0,2,0,0,0,0,0,2},
                    {1,1,1,1,1,1,1,1,4,4,4,0,4,4,4},
                    {1,0,0,0,0,0,1,4,0,0,0,0,0,0,4},
                    {1,0,0,0,0,0,1,4,0,0,0,0,0,0,4},
                    {1,0,0,2,0,0,1,4,0,3,3,3,3,0,4},
                    {1,0,0,0,0,0,1,4,0,3,3,3,3,0,4},
                    {1,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
                    {1,1,1,1,1,1,1,4,4,4,4,4,4,4,4}
            };

    public Camera camera;
    public Screen screen;

    public Game() {
        thread = new Thread(this); //Инициадизация потока
        image = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);//Размер изображения
        pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();//Сбор всех пикселей на изображении

        textures = new ArrayList<Texture>();
        textures.add(Texture.wood);
        textures.add(Texture.brick);
        textures.add(Texture.bluestone);
        textures.add(Texture.stone);

        camera = new Camera(4.5, 4.5, 1, 0, 0, -.66);
        screen = new Screen(map, mapHeight, mapWidth, textures,  640, 480);

        addKeyListener(camera);

        //Настройки окна
        setSize(640, 480);
        setResizable(false);
        setTitle("Raycast engine");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(Color.black);
        setLocationRelativeTo(null);
        setVisible(true);

        start();


    }

    //Стартуем игру
    private synchronized void start(){
        running = true;
        thread.start();
    }

    //Останавливаем игру
    public synchronized void stop() {
        running = false;
        try {
            thread.join();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Метод для отрисовки изображения
    public void render(){

        //Используется для того чтобы рендер был более плавным
        BufferStrategy bs = getBufferStrategy();

        if(bs == null){
            createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();
        //Отрисовываем полученное изображение
        g.drawImage(image, 0,0, image.getWidth(), image.getHeight(), null);
        //Выводим на экран
        bs.show();
    }

    //Определяет частоту обновления программы
    public void run(){
        long lastTime = System.nanoTime();
        final double ns = 1000000000.0 / 60.0; // 60 раз в секунду
        double delta = 0;

        requestFocus();

        while (running){
            long now = System.nanoTime();
            delta = delta + ((now-lastTime) / ns);
            lastTime = now;

            while (delta >= 1)//Убеждаемся в том что обновление происходит только 60 раз в секунду
            {
                //Обрабатывает всё логическое ограничение времени
                screen.update(camera, pixels);
                camera.update(map);
                delta--;
            }
            render(); //Происходит вывод на экран неограниченное кол-во времени

        }
    }



}
