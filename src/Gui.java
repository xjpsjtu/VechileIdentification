import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;

import javax.swing.*;
import javax.swing.filechooser.*;

import java.io.*;
import java.applet.*;
import java.awt.Dimension;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifDirectory;

public class Gui extends Applet implements ActionListener {
	private static final long serialVersionUID = 5685733549421617658L;
	JPanel PathPanel, PicPanel, MainPanel;
	DrawPanel ShowPanel;
	JScrollPane ScroPanel;
	JLabel PathInfo, PicInfo, PicSize, PicTime, PicRes, PicOper, memory,
			dateTime;
	JButton PicLarger, PicSmaller, PicPasserReco, PicCarReco, OpenButton,
			LastButton, NextButton;
	JTextField SizeText, TimeText, memoryText, ResText, dateTimeText;
	long Start_Time, End_Time;
	Image pic, temppic;
	BufferedImage BufImage, outBinary;
	BufferedImage OriBufImage;
	Graphics2D BufImageG;
	int PicWidth, PicHeight, w, h;
	int index;
	float s;
	double scaleX;
	double scaleY;
	int[] grayData, grayData2;// 图像灰度值
	String AllFilePath, filename, dir;
	String[] Pics;
	boolean HasPic;
	final String FILE_TYPE = ".jpg";
	Filter filterJpg;

	@Override
	public void init() {
		PathPanel = new JPanel();
		PathPanel.setLayout(new BorderLayout());
		PathInfo = new JLabel();
		PathInfo.setText("请打开一张图片");
		PathPanel.add(PathInfo);
		// 图片显示区域布局
		JScrollPane ScroPanel = new JScrollPane();
		ShowPanel = new DrawPanel();
		ShowPanel.setBackground(Color.gray);
		ScroPanel.setViewportView(ShowPanel);
		// 图片操作区域布局
		PicInfo = new JLabel();
		PicInfo.setText("图片信息");
		PicPanel = new JPanel();
		PicPanel.add(PicInfo);
		PicSize = new JLabel("分辨率");
		PicPanel.add(PicSize);
		SizeText = new JTextField(20);
		PicPanel.add(SizeText);
		PicTime = new JLabel("加载时间");
		PicPanel.add(PicTime);
		TimeText = new JTextField(20);
		TimeText.setText("");
		PicPanel.add(TimeText);
		memory = new JLabel("占用内存");
		PicPanel.add(memory);
		memoryText = new JTextField(20);
		memoryText.setText("");
		PicPanel.add(memoryText);
		dateTime = new JLabel("拍摄时间");
		PicPanel.add(dateTime);
		dateTimeText = new JTextField(20);
		dateTimeText.setText("NULL");
		PicPanel.add(dateTimeText);
		PicOper = new JLabel("图片操作");
		PicLarger = new JButton("放大");
		PicLarger.addActionListener(this);
		PicPanel.add(PicLarger);
		PicSmaller = new JButton("缩小");
		PicSmaller.addActionListener(this);
		PicPanel.add(PicSmaller);
		scaleX = 1;
		scaleY = 1;
		PicCarReco = new JButton("汽车识别");
		PicCarReco.addActionListener(this);
		PicPanel.add(PicCarReco);
		PicPanel.setLayout(new GridBagLayout());
		GridLayout g1 = new GridLayout(12, 1, 5, 5);
		PicPanel.setLayout(g1);
		// 主操作区域布局
		MainPanel = new JPanel();
		OpenButton = new JButton("打开");
		OpenButton.addActionListener(this);
		LastButton = new JButton("上一张");
		LastButton.addActionListener(this);
		NextButton = new JButton("下一张");
		NextButton.addActionListener(this);
		MainPanel.add(OpenButton);
		MainPanel.add(LastButton);
		MainPanel.add(NextButton);
		// 总布局
		setLayout(new BorderLayout());
		add(PathPanel, BorderLayout.NORTH);
		add(ScroPanel, BorderLayout.CENTER);
		add(PicPanel, BorderLayout.EAST);
		add(MainPanel, BorderLayout.SOUTH);
		resize(2048, 768);
		HasPic = false;
		dir = "";
		PicLarger.setEnabled(false);
		PicSmaller.setEnabled(false);
		PicCarReco.setEnabled(false);
		LastButton.setEnabled(false);
		NextButton.setEnabled(false);
		this.setVisible(true);

	}

	public void actionPerformed(ActionEvent e) {
		// TODO 自动生成的方法存根
		JButton click = (JButton) e.getSource();
		if (click == OpenButton) {

			open();

		}
		if (click == LastButton) {
			Last();
		}
		if (click == NextButton) {
			Next();
		}

		if (click == PicLarger) {
			Larger();
		}
		if (click == PicSmaller) {
			Smaller();
		}
		if (click == PicCarReco) {
			Reco();
		}
	}

	public void open() {
		try {
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
		}
		final JFileChooser PicChooser = new JFileChooser();
		PicChooser.setDialogTitle("打开图片");
		FileNameExtensionFilter fiter = new FileNameExtensionFilter("JPG",
				"jpg");
		PicChooser.setFileFilter(fiter);
		PicChooser.setCurrentDirectory(new File(""));
		int ReturnVal = PicChooser.showOpenDialog(null);
		if (ReturnVal == JFileChooser.APPROVE_OPTION) {
			filename = PicChooser.getSelectedFile().getName();
			AllFilePath = PicChooser.getCurrentDirectory().getPath();
			filterJpg = new Filter(FILE_TYPE);
			File file = new File(AllFilePath);
			Pics = file.list(filterJpg);
			dir = AllFilePath + "\\" + filename;
			HasPic = true;
			try {
				Start_Time = System.currentTimeMillis();
				loadImage(dir);
				End_Time = System.currentTimeMillis();
				if (HasPic) {
					TimeText.setText(Long.toString(End_Time - Start_Time)
							+ "ms");
				}
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			PathInfo.setText(AllFilePath + "\\" + filename);
			LastButton.setEnabled(true);
			NextButton.setEnabled(true);
			PicLarger.setEnabled(true);
			PicSmaller.setEnabled(true);
			PicCarReco.setEnabled(true);
			for (int j = 0; j < Pics.length; j++) {
				if (Pics[j].equalsIgnoreCase(filename)) {
					index = j;
					break;
				}
			}
		} else {
			HasPic = false;
			PathInfo.setText("未选择文件");
		}
	}

	public void Last() {
		Start_Time = System.currentTimeMillis();
		if (index == 0) {
			index = Pics.length - 1;
		} else {
			index--;
		}
		dir = AllFilePath + "\\" + Pics[index];
		HasPic = true;
		try {
			loadImage(dir);
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		End_Time = System.currentTimeMillis();
		TimeText.setText(Long.toString(End_Time - Start_Time));
	}

	public void Next() {
		Start_Time = System.currentTimeMillis();
		if (index == Pics.length - 1) {
			index = 0;
		} else {
			index++;
		}
		dir = AllFilePath + "\\" + Pics[index];
		HasPic = true;
		try {
			loadImage(dir);
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		End_Time = System.currentTimeMillis();
		TimeText.setText(Long.toString(End_Time - Start_Time));
	}

	public void Larger() {
		if (BufImage != null) {
			if (scaleX < 1.7) {
				scaleX += 0.15;
				scaleY += 0.15;
			} else {
				scaleX = 1.8;
				scaleY = 1.8;
			}
		}
		picTrans();
		repaint();
	}

	public void Smaller() {
		if (BufImage != null) {
			if (scaleX > 0.6) {
				scaleX -= 0.15;
				scaleY -= 0.15;
			} else {
				scaleX = 0.4;
				scaleY = 0.4;
			}
		}
		picTrans();
		repaint();
	}

	public void picTrans() {
		if (BufImage == null)
			return; // 如果bufImage为空则直接返回
		int w = 1024;
		float s = (float) PicWidth / 1024.0f;
		int h = (int) ((float) PicHeight / s);
		BufferedImage filteredBufImage = new BufferedImage((int) (w * scaleX),
				(int) (h * scaleY), BufferedImage.TYPE_INT_RGB);
		AffineTransform transform = new AffineTransform();
		transform.setToScale(scaleX, scaleY);
		AffineTransformOp imageOp = new AffineTransformOp(transform, null);
		imageOp.filter(OriBufImage, filteredBufImage);
		BufImage = filteredBufImage;

		ShowPanel.setPreferredSize(new Dimension((int) (w * scaleX),
				(int) (h * scaleY)));
		ShowPanel.updateUI();
	}

	public void loadImage(String filename) throws Exception {
		pic = this.getToolkit().getImage(filename);
		MediaTracker mt = new MediaTracker(this);
		mt.addImage(pic, 0);
		try {
			mt.waitForAll();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		PicWidth = pic.getWidth(this);
		PicHeight = pic.getHeight(this);
		int w = 1024;
		float s = (float) PicWidth / 1024.0f;
		int h = (int) ((float) PicHeight / s);

		OriBufImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		BufImage = OriBufImage;
		BufImageG = BufImage.createGraphics();
		BufImageG.drawImage(pic, 0, 0, w, h, this);
		SizeText.setText(pic.getWidth(this) + "*" + pic.getHeight(this));
		Runtime currRuntime = Runtime.getRuntime();
		int nFreeMemory = (int) (currRuntime.freeMemory() / 1024 / 1024);
		int nTotalMemory = (int) (currRuntime.totalMemory() / 1024 / 1024);
		memoryText.setText(nTotalMemory - nFreeMemory + "M");
		File f = new File(dir);
		Metadata metadata = JpegMetadataReader.readMetadata(f);
		Directory exif = metadata.getDirectory(ExifDirectory.class);
		String dateTime = exif.getString(ExifDirectory.TAG_DATETIME_ORIGINAL);
		dateTimeText.setText(dateTime);

		repaint();

		ShowPanel.setPreferredSize(new Dimension(w, h));
		ShowPanel.updateUI();

	}

	public void Reco() {
		int PicWidth1 = BufImage.getWidth();
		int PicHeight1 = BufImage.getHeight();
		int imageData[] = BufImage.getRGB(0, 0, PicWidth1, PicHeight1, null, 0,
				PicWidth1);// 读进的图像的RGB值
		outBinary = new BufferedImage(PicWidth1, PicHeight1,
				BufferedImage.TYPE_INT_RGB);// 生成边缘图像
		grayData = new int[PicWidth1 * PicHeight1];// 开辟内存空间
		for (int i = 0; i < imageData.length; i++) {
			grayData[i] = (imageData[i] & 0xff0000) >> 16;// 由于读的是灰度图，故只考虑一个分量（三分量值相同）
		}
		for (int y = 1; y < PicHeight1 - 1; ++y)
			for (int x = 1; x < PicWidth1 - 1; ++x) {
				if (grayData[y * PicWidth1 + x] > 150 && y > PicHeight1 / 2) {
					outBinary.setRGB(x, y, 0xffffff);
				} else
					outBinary.setRGB(x, y, 0x000000);
			}
		int imageData2[] = outBinary.getRGB(0, 0, PicWidth1, PicHeight1, null,
				0, PicWidth1);
		grayData2 = new int[PicWidth1 * PicHeight1];// 开辟内存空间
		for (int i = 0; i < imageData2.length; i++) {
			grayData2[i] = (imageData2[i] & 0xff0000) >> 16;// 由于读的是灰度图，故只考虑一个分量（三分量值相同）
		}
		for (int y = 1; y < PicHeight1 - 1; ++y)
			for (int x = 1; x < PicWidth1 - 1; ++x) {
				if (grayData2[y * PicWidth1 + x] > 200 && y > PicHeight1 / 2) {
					BufImage.setRGB(x, y, 0xff0000);
				}
			}

		repaint();
		ShowPanel.updateUI();

	}

	class DrawPanel extends JPanel {
		public void update(Graphics g) {
			this.paint(g);
		}

		@Override
		public void paint(Graphics g) {
			int Width = this.getWidth();
			int Height = this.getHeight();
			super.paint(g);
			if (BufImage != null) {
				g.drawImage(BufImage, (Width - BufImage.getWidth()) / 2,
						(Height - BufImage.getHeight()) / 2, this);
			}

		}
	}

	class Filter implements FilenameFilter {
		String s;

		Filter(String s) {
			this.s = s;
		}

		@Override
		public boolean accept(File directory, String filename) {
			return filename.endsWith(s);
		}
	}

}