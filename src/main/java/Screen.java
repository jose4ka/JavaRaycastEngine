import java.util.ArrayList;
import java.awt.Color;

public class Screen {

    public int[][] map;
    public int mapWidth, mapHeight, width, height; //Высота и ширина экрана
    public ArrayList<Texture> textures; //Текстуры используемые на этой карте

    public Screen(int[][] m, int mapW, int mapH, ArrayList<Texture> tex, int w, int h) {
        map = m;
        mapWidth = mapW;
        mapHeight = mapH;
        textures = tex;
        width = w;
        height = h;
    }
    public int[] update(Camera camera, int[] pixels) {
        for(int n=0; n<pixels.length/2; n++) {
            if(pixels[n] != Color.DARK_GRAY.getRGB()) pixels[n] = Color.DARK_GRAY.getRGB();
        }
        for(int i=pixels.length/2; i<pixels.length; i++){
            if(pixels[i] != Color.gray.getRGB()) pixels[i] = Color.gray.getRGB();
        }

        for(int x=0; x<width; x=x+1) {
            double cameraX = 2 * x / (double)(width) -1;
            double rayDirX = camera.xDir + camera.xPlane * cameraX;
            double rayDirY = camera.yDir + camera.yPlane * cameraX;
            //Позиция на карте
            int mapX = (int)camera.xPos;
            int mapY = (int)camera.yPos;
            //Длина луча из текущей позиции до следующей x или y стороны
            double sideDistX;
            double sideDistY;
            //Длина луча с одной стороны до следующей
            double deltaDistX = Math.sqrt(1 + (rayDirY*rayDirY) / (rayDirX*rayDirX));
            double deltaDistY = Math.sqrt(1 + (rayDirX*rayDirX) / (rayDirY*rayDirY));
            double perpWallDist;
            //Направление движения по x и y
            int stepX, stepY;
            boolean hit = false;//Столкновение со стеной
            int side=0;//Стена вертикальная или горизонтальная

            //Выясняем направление шага и начальное расстояние в сторону
            if (rayDirX < 0)
            {
                stepX = -1;
                sideDistX = (camera.xPos - mapX) * deltaDistX;
            }
            else
            {
                stepX = 1;
                sideDistX = (mapX + 1.0 - camera.xPos) * deltaDistX;
            }
            if (rayDirY < 0)
            {
                stepY = -1;
                sideDistY = (camera.yPos - mapY) * deltaDistY;
            }
            else
            {
                stepY = 1;
                sideDistY = (mapY + 1.0 - camera.yPos) * deltaDistY;
            }
            //Цикл для нахождения удара луча о стену
            while(!hit) {
                //Перепрыгиваем на следующий квадрат
                if (sideDistX < sideDistY)
                {
                    sideDistX += deltaDistX;
                    mapX += stepX;
                    side = 0;
                }
                else
                {
                    sideDistY += deltaDistY;
                    mapY += stepY;
                    side = 1;
                }
                //Проверяем если луч ударился о стену
                //System.out.println(mapX + ", " + mapY + ", " + map[mapX][mapY]);
                if(map[mapX][mapY] > 0) hit = true;
            }
            //Высчитываем дистанцию до точки столкновения
            if(side==0)
                perpWallDist = Math.abs((mapX - camera.xPos + (1 - stepX) / 2) / rayDirX);
            else
                perpWallDist = Math.abs((mapY - camera.yPos + (1 - stepY) / 2) / rayDirY);

            //Теперь вычисляем высоту стены на основе расстояния от камеры
            int lineHeight;
            if(perpWallDist > 0) lineHeight = Math.abs((int)(height / perpWallDist));
            else lineHeight = height;
            //Вычисляем нижний и верхний пиксель для заполнения текущей полосы
            int drawStart = -lineHeight/2+ height/2;
            if(drawStart < 0)
                drawStart = 0;
            int drawEnd = lineHeight/2 + height/2;
            if(drawEnd >= height)
                drawEnd = height - 1;
            //Добавляем текстуру
            int texNum = map[mapX][mapY] - 1;
            double wallX;//Точная позиция места удара стены
            if(side==1) {//Если это стена оси Y
                wallX = (camera.xPos + ((mapY - camera.yPos + (1 - stepY) / 2) / rayDirY) * rayDirX);
            } else {//Если это стена оси X
                wallX = (camera.yPos + ((mapX - camera.xPos + (1 - stepX) / 2) / rayDirX) * rayDirY);
            }
            wallX-=Math.floor(wallX);
            //X координата на текстуре
            int texX = (int)(wallX * (textures.get(texNum).SIZE));
            if(side == 0 && rayDirX > 0) texX = textures.get(texNum).SIZE - texX - 1;
            if(side == 1 && rayDirY < 0) texX = textures.get(texNum).SIZE - texX - 1;
            //Y координата на текстуре
            for(int y=drawStart; y<drawEnd; y++) {
                int texY = (((y*2 - height + lineHeight) << 6) / lineHeight) / 2;
                int color;
                if(side==0) color = textures.get(texNum).pixels[texX + (texY * textures.get(texNum).SIZE)];
                else color = (textures.get(texNum).pixels[texX + (texY * textures.get(texNum).SIZE)]>>1) & 8355711;//Make y sides darker
                pixels[x + y*(width)] = color;
            }
        }
        return pixels;
    }

}
