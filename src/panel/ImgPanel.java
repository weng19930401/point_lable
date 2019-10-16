package panel;

import tool.ImageUtil;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImgPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;
    private Image image;
    private int imgWidth;
    private int imgHeight;


    public Image getImg() {
        return image;
    }


    public int getImgWidth() {
        return imgWidth;
    }

    public void setImgWidth(int imgWidth) {
        this.imgWidth = imgWidth;
    }

    public int getImgHeight() {
        return imgHeight;
    }

    public void setImgHeight(int imgHeight) {
        this.imgHeight = imgHeight;
    }

    public ImgPanel() {
    }

    public void setImagePath(String imgPath) {
        // 该方法不推荐使用，该方法是懒加载，图像并不加载到内存，当拿图像的宽和高时会返回-1；
        // image = Toolkit.getDefaultToolkit().getImage(imgPath);
        try {
            // 该方法会将图像加载到内存，从而拿到图像的详细信息。
            // 自适应宽高 1180*860 w:h = 1180:860
            BufferedImage bufferedImage = ImageIO.read(new FileInputStream(imgPath));
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();
            int w,h;
            if (width > 1180) {
                w = 1180;
                h = w * height / width;
                width = w;
                height = h;
            }
            if (height> 860){
                h = 860;
                w = h * width / height;
                width = w;
                height = h;
            }
            image = ImageUtil.resizeImage(ImageIO.read(new FileInputStream(imgPath)), width, height);
            //读取图片的大小
            setImgWidth(image.getWidth(this));
            setImgHeight(image.getHeight(this));
            this.repaint();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void paintComponent(Graphics g1) {
        super.paintComponent(g1);
        int x = 0;
        int y = 0;
        Graphics g = (Graphics) g1;
        if (null == image) {
            return;
        }

        g.drawImage(image, x, y, image.getWidth(this), image.getHeight(this),
                this);
        g = null;


    }
}