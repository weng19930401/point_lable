package frame;

import panel.MousePanel;
import tool.FileTool;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;


public class PhotoFrame extends JFrame {

    private static final long serialVersionUID = -2216276219179107707L;
    private Container con;

    private MousePanel zPanel;
    private JScrollPane imgSp;
    private JScrollPane listSp;
    private JList<String> dirList;
    private JPanel btnPanel;
    private String imageDir;
    private String currImg;//文件名，不包含目录
    private int currIndex;//当前图片索引
    ArrayList<String> imgList;//所有图片的绝对路径

    public void writeCropImg() throws IOException {

        BufferedImage sourceImage = (BufferedImage) zPanel.getImg();
        Image croppedImage;
        ImageFilter cropFilter;
        int x = zPanel.getPointX() - 25;
        int y = zPanel.getPointY() - 25;
        //四个参数分别为图像起点坐标和宽高，即CropImageFilter(int x,int y,int width,int height)，详细情况请参考API 
        //指定要裁剪的的文件的宽度和高度，以及起始坐标 
        cropFilter = new CropImageFilter(x, y, 50, 50);
        //生成图片
        if (sourceImage == null) {
            JOptionPane.showMessageDialog(null, "请选择图片文件或目录！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        croppedImage = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(sourceImage.getSource(), cropFilter));
        //获取创建后的图片的高度 
        int h1 = croppedImage.getHeight(null);
        int w1 = croppedImage.getWidth(null);
        BufferedImage bi = new BufferedImage(w1, h1, BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.getGraphics();
        //在画图的时候可以设置背景色 
        g.drawImage(croppedImage, 0, 0, Color.white, null);
        String dir = imageDir + "_crops";
        //创建文件输出流  
        FileOutputStream fos = new FileOutputStream(new File(dir + "/" + currImg));
        //将创建的图片写入到输出流 
        ImageIO.write(bi, "png", fos);
        fos.close();
    }

    private void showNext() {
        updateCurrName();
        if (currIndex >= imgList.size()) {
            return;
        }
        zPanel.setImagePath(imgList.get(currIndex));
        zPanel.clearPointList();
        zPanel.setClick_times(0);
        setList(imgList);
        // 设置默认选中项
        dirList.setSelectedIndex(currIndex);
    }

    private void updateCurrName() {
        if (imgList == null) {
            JOptionPane.showMessageDialog(null, "请选择图片文件或目录！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int size = imgList.size();
        if (currIndex >= size) {
            JOptionPane.showMessageDialog(null, "已是最后一张！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        } else {
            String path = imgList.get(currIndex);
            int index = path.lastIndexOf("\\");
            currImg = path.substring(index);
        }
    }

    private void setList(ArrayList<String> imgList) {
        String[] strings = new String[imgList.size()];
        imgList.toArray(strings);
        dirList.setListData(strings);
        listSp.setViewportView(dirList);
        listSp.repaint();
    }

    private class nextHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            showNext();
        }
    }

    private class deletePoint implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            ArrayList<Point> list = zPanel.getPointList();
            if (list.size()==0){
                JOptionPane.showMessageDialog(null, "图中未找到标注点！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            list.remove(list.size() - 1);
            zPanel.setPointList(list);
            zPanel.updateUI();
        }
    }

    private class openDirHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);//设置只能选择目录
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                imageDir = chooser.getSelectedFile().getPath();
                imgList = FileTool.refreshFileList(imageDir);
                currIndex = 0;
                showNext();
                //System.out.println ( "你选择的目录是：" + selectPath );
                //你选择的目录是：/home/linger/imdata/collar
            }
        }
    }

    private void savePointAndImg() {
        try {
            ArrayList<Point> pointList = zPanel.getPointList();
            if(pointList.size()==0){
                //返回的是按钮的index  i=0或者1 YES:0  NO:1
                int n = JOptionPane.showConfirmDialog(null, "未选择标注点，是否继续?", "提示",JOptionPane.YES_NO_OPTION);
                if (n == 1){
                    return;
                }
            }
            String preName = currImg.substring(0, currImg.lastIndexOf("."));
            File writeName = new File(imageDir + "\\" + preName + ".txt"); // 相对路径，如果没有则要建立一个新的output.txt文件
            writeName.createNewFile(); // 创建新文件,有同名的文件的话直接覆盖
            try (FileWriter writer = new FileWriter(writeName);
                 BufferedWriter out = new BufferedWriter(writer)
            ) {
                out.write(pointList.size() + "\r\n"); // \r\n即为换行
                for (Point point : pointList) {
                    float x_pro = (float) point.x / zPanel.getImgWidth();
                    float y_pro = (float) point.y / zPanel.getImgHeight();
                    int scale = 3;//设置位数
                    int roundingMode = 4;//表示四舍五入，可以选择其他舍值方式，例如去尾，等等.
                    BigDecimal xbd = new BigDecimal((double) x_pro);
                    xbd = xbd.setScale(scale, roundingMode);
                    x_pro = xbd.floatValue();
                    BigDecimal ybd = new BigDecimal((double) y_pro);
                    ybd = ybd.setScale(scale, roundingMode);
                    y_pro = ybd.floatValue();
                    if (pointList.indexOf(point) == pointList.size() - 1) {
                        out.write(x_pro + "," + y_pro);
                    } else {
                        out.write(x_pro + "," + y_pro + "|");
                    }
                }
                out.flush(); // 把缓存区内容压入文件
            }
            JOptionPane.showMessageDialog(null, "保存成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    private class savePointHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            savePointAndImg();
        }
    }

    private class openImageHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & PNG Images", "jpg", "png");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                imageDir = file.getParent();
                imgList = new ArrayList<String>();
                imgList.add(file.getAbsolutePath());
                currImg = file.getName();//文件名,不包含路径
                // System.out.println();
                String path = file.getAbsolutePath();
                currIndex = imgList.indexOf(path);
                zPanel.setImagePath(path);
                zPanel.clearPointList();
                zPanel.setClick_times(0);
                setList(imgList);
                // 设置默认选中项
                dirList.setSelectedIndex(currIndex);
            }
        }
    }

    public class selectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            // 获取所有被选中的选项索引
            int[] indices = dirList.getSelectedIndices();
            // 获取选项数据的 ListModel
            ListModel<String> listModel = dirList.getModel();
            // 输出选中的选项
            for (int index : indices) {
                // 打开对应图片
                String path = listModel.getElementAt(index);
                currIndex = imgList.indexOf(path);
                zPanel.setImagePath(path);
                zPanel.clearPointList();
                zPanel.setClick_times(0);
                currIndex++;
            }
        }
    }

    private PhotoFrame() {
        con = getContentPane();
        con.setLayout(new GridLayout(2, 2));
        zPanel = new MousePanel();
        //zPanel.setImagePath("/home/linger/17820d01");//绑定图片
        javax.swing.GroupLayout zpanelLayout = new javax.swing.GroupLayout(zPanel);
        zPanel.setLayout(zpanelLayout);
        zpanelLayout.setHorizontalGroup(
                zpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 800, Short.MAX_VALUE)
        );
        zpanelLayout.setVerticalGroup(
                zpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 505, Short.MAX_VALUE)
        );
        imgSp = new JScrollPane();
        imgSp.setPreferredSize(new Dimension(zPanel.getImgWidth(), zPanel.getImgHeight()));
        //imgSp.setSize(400,400);
        imgSp.setViewportView(zPanel);
        imgSp.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent evt) {
                Dimension dim = new Dimension(imgSp.getSize().width
                        - imgSp.getVerticalScrollBar().getWidth(),    // 尺寸改变后可能会出现垂直滚动条，需减去其宽度
                        (int) (imgSp.getSize().width));
                zPanel.setPreferredSize(dim);     // 不设置此属性，ui会自动计算并设置
                zPanel.updateUI();
            }
        });
        btnPanel = new JPanel();
        btnPanel.setLayout(new FlowLayout());
        btnPanel.setSize(800, 100);
        JButton button1 = new JButton("打开图片");
        button1.setSize(100, 50);
        btnPanel.add(button1);
        button1.addActionListener(new openImageHandler());
        JButton button2 = new JButton("保存坐标");
        button2.setSize(100, 50);
        btnPanel.add(button2);
        button2.addActionListener(new savePointHandler());
        JButton button3 = new JButton("打开目录");
        button3.setSize(100, 50);
        btnPanel.add(button3);
        button3.addActionListener(new openDirHandler());
        JButton button4 = new JButton("下一张");
        button4.setSize(100, 50);
        btnPanel.add(button4);
        button4.addActionListener(new nextHandler());
        button3.addActionListener(new openDirHandler());
        JButton button5 = new JButton("删除标注点");
        button5.setSize(100, 50);
        btnPanel.add(button5);
        button5.addActionListener(new deletePoint());
        javax.swing.GroupLayout btnPanelLayout = new javax.swing.GroupLayout(btnPanel);
        btnPanel.setLayout(btnPanelLayout);
        btnPanelLayout.setHorizontalGroup(
                btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(btnPanelLayout.createSequentialGroup()
                                .addGap(225, 225, 225)
                                .addComponent(button1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(button3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(button4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(button5)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        btnPanelLayout.setVerticalGroup(
                btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(btnPanelLayout.createSequentialGroup()
                                .addGroup(btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(button1)
                                        .addComponent(button2)
                                        .addComponent(button3)
                                        .addComponent(button4)
                                        .addComponent(button5))
                                .addGap(0, 42, Short.MAX_VALUE))
        );
        dirList = new JList<String>();
        // 设置一下首选大小
        dirList.setLocation(100, 100);
        dirList.setSize(20, 20);
        //dirList.setPreferredSize(new Dimension(100, 100));
        // 允许可间断的多选
        dirList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        // 设置选项数据（内部将自动封装成 ListModel ）
        dirList.setListData(new String[]{});
        // 添加选项选中状态被改变的监听器
        dirList.addListSelectionListener(new selectionListener());
        // 设置默认选中项
        dirList.setSelectedIndex(1);
        listSp = new JScrollPane();
        listSp.setViewportView(dirList);
        //con.add(imgSp, BorderLayout.CENTER);
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(imgSp, javax.swing.GroupLayout.PREFERRED_SIZE, 1200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(listSp, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE))
                        .addComponent(btnPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(imgSp, javax.swing.GroupLayout.DEFAULT_SIZE, 507, Short.MAX_VALUE)
                                        .addComponent(listSp))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        zPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    savePointAndImg();
                    showNext();
                }
            }
        });
        finalSetting();
    }

    private void finalSetting() {
        setTitle("标注工具");

        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;
        int frameH = getHeight();
        int frameW = getWidth();
        setLocation((screenWidth - frameW) / 2 - 700,
                (screenHeight - frameH) / 2 - 500);

        setSize(1400, 1000);
        //setSize(zPanel.getImgWidth()+10, zPanel.getImgHeight()+10);
        //setPreferredSize(new Dimension(zPanel.getImgWidth()+100, zPanel.getImgHeight()+100));
        //pack();
        setVisible(true);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        new PhotoFrame();
    }

}