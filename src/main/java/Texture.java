import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Texture {


    public int[] pixels; //Все пиксели в изображении текстуры
    private String loc; //Где находится текстура
    public final int SIZE; //Размер текстуры с одной стороны (64 соотвествует 64х64)



    public Texture(String location, int size){
        loc = location;
        SIZE = size;
        pixels = new int[SIZE * SIZE];
        load();

    }

    //Метод для загрузки нашей текстуры
    private void load(){
        try {
            //Загружаем изображение из файла
            BufferedImage image = ImageIO.read(new File(loc));

            //Получаем ширину и высоту
            int w = image.getWidth();
            int h = image.getHeight();

            //Строим изображение
            image.getRGB(0, 0, w, h, pixels, 0, w);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //Текстуры имеющиеся в игре
    public static Texture wood = new Texture("src/main/resources/Wall1.png", 64);
    public static Texture brick = new Texture("src/main/resources/Wall2.png", 64);
    public static Texture bluestone = new Texture("src/main/resources/Wall3.png", 64);
    public static Texture stone = new Texture("src/main/resources/Wall4.png", 64);


}
