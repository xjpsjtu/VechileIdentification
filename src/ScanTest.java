import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ScanTest {
	int width;// 图像宽
	int height;// 图像高
	int[] grayData, grayData2;// 图像灰度值
	int size; // 图像大小
	int gradientThreshold = -1;// 判断时用到的阈值
	BufferedImage outBinary, bufferedImage;// 输出的边缘图像

	public void readImage(String imageName) throws IOException {
		File imageFile = new File(imageName);
		bufferedImage = ImageIO.read(imageFile);
		width = bufferedImage.getWidth();
		height = bufferedImage.getHeight();
		size = width * height;
		int imageData[] = bufferedImage.getRGB(0, 0, width, height, null, 0,
				width);// 读进的图像的RGB值
		outBinary = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);// 生成边缘图像
		// outBinary=bufferedImage;
		grayData = new int[width * height];// 开辟内存空间
		for (int i = 0; i < imageData.length; i++) {
			grayData[i] = (imageData[i] & 0xff0000) >> 16;
		}
	}

	public void createEdgeImage(String desImageName) {

		for (int y = 1; y < height - 1; ++y)
			for (int x = 1; x < width - 1; ++x) {
				if (grayData[y * width + x] > 150 && y > height / 2) {
					outBinary.setRGB(x, y, 0xffffff);
				} else
					outBinary.setRGB(x, y, 0x000000);
			}

		writeImage(outBinary, desImageName);

	}

	public void createEdgeImage2(String desImageName) {
		BufferedImage bufferedImage2 = bufferedImage;
		int imageData2[] = outBinary
				.getRGB(0, 0, width, height, null, 0, width);// 读进的图像的RGB值
		grayData2 = new int[width * height];// 开辟内存空间
		for (int i = 0; i < imageData2.length; i++) {
			grayData[i] = (imageData2[i] & 0xff0000) >> 16;
		}
		for (int y = 1; y < height - 1; ++y)
			for (int x = 1; x < width - 1; ++x) {
				if (grayData[y * width + x] > 200 && y > height / 2) {
					bufferedImage2.setRGB(x, y, 0x000000);
				}
			}

		writeImage(bufferedImage2, desImageName);

	}

	public int getGrayPoint(int x, int y) {
		return grayData[y * width + x];
	}

	public void writeImage(BufferedImage bi, String imageName) {
		File skinImageOut = new File(imageName);
		try {
			ImageIO.write(bi, "jpg", skinImageOut);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ScanTest test = new ScanTest();// 100
		String imageName = "C:\\Users\\jp\\Desktop\\a.jpg";
		String desImageName1 = "C:\\Users\\jp\\Desktop\\b.jpg";
		String desImageName2 = "C:\\Users\\jp\\Desktop\\c.jpg";
		try {
			test.readImage(imageName);
		} catch (IOException e) {
			e.printStackTrace();
		}

		test.createEdgeImage(desImageName1);
		test.createEdgeImage2(desImageName2);
	}
}
