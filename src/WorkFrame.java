import download.DownMain;
import util.CheckKeyUtil;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Hashtable;
import java.util.Vector;

public class WorkFrame extends JFrame implements DownMain.VideoDownLister {
    private Hashtable<String, JProgressBar> mDownTable = new Hashtable<>();
    private JPanel contentJP;
    private JPanel failedJP;
    private JLabel mDownTip,mFailedDownTip,mLoadingTip,mSavePathTip;
    private Vector<String> mFailedUrlList = new Vector<>();
    private String mSavePath = "D:\\video\\";
    private String mDownPath = "";
    private String mStoreKey =  "storeKey";

    public WorkFrame() {
        setLayout(null);
        addOpenButton();
        addStartButton();
        addReturyButton();
        addSavePathButton();

        contentJP = new JPanel();
        contentJP.setLayout(new BoxLayout(contentJP,BoxLayout.Y_AXIS));
        failedJP = new JPanel();
        failedJP.setLayout(new BoxLayout(failedJP,BoxLayout.Y_AXIS));

        mDownTip = new JLabel("下载");
        mDownTip.setBounds(10, 45, 100, 20);
        add(mDownTip);

        mLoadingTip = new JLabel("下载状态");
        mLoadingTip.setBounds(800, 45, 100, 20);
        add(mLoadingTip);

        JScrollPane jsp = new JScrollPane(contentJP);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jsp.setBounds(10, 70, 1000, 500);
        add(jsp);

        mFailedDownTip = new JLabel("下载失败");
        mFailedDownTip.setBounds(10, 585, 100, 20);
        add(mFailedDownTip);

        JScrollPane failedJsp = new JScrollPane(failedJP);
        failedJsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        failedJsp.setBounds(10, 620, 1000, 180);
        add(failedJsp);

        setSize(1200, 850);
        setTitle("红哥--视频下载器");
        setLocation(250, 100);
        setResizable(false);
        show();

        addWindowListener(new WindowListener() {

            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
    }

    private void addOpenButton() {
        JButton button = new JButton("打开文件");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser file = new JFileChooser();
                file.setDialogTitle("选择文件");
                TxtFileFilter excelFilter = new TxtFileFilter(); //txt过滤器
                file.addChoosableFileFilter(excelFilter);
                file.setFileFilter(excelFilter);
                int resule = file.showOpenDialog(new JPanel());
                if (resule == file.APPROVE_OPTION) {
                    contentJP.removeAll();
                    failedJP.removeAll();
                    contentJP.updateUI();
                    failedJP.updateUI();
                    String dir = file.getSelectedFile().getAbsolutePath();
                    mDownPath = dir;
                    System.out.println(dir);
                }
            }
        });
        button.setBounds(10, 10, 100, 30);
        add(button);
    }

    private void addReturyButton() {
        JButton button = new JButton("重试下载失败链接");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (mFailedUrlList.size() > 0){
                    contentJP.removeAll();
                    failedJP.removeAll();
                    contentJP.updateUI();
                    failedJP.updateUI();
                    mFailedDownTip.setText("下载失败");
                    DownMain.main(mFailedUrlList);
                } else {
                    JOptionPane.showMessageDialog(null,"没有下载失败的链接");
                }
            }
        });
        button.setBounds(100, 580, 150, 30);
        add(button);
    }
    private void addStartButton() {
        JButton button = new JButton("开始下载");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!CheckKeyUtil.CheckKey()){
                    return;
                }
                if (!mDownPath.equals("")) {
                    File file = new File(mSavePath);
                    if (!file.exists()){
                        file.mkdirs();
                    }
                    mLoadingTip.setText("准备下载...");
                    DownMain.addVideoNameListener(WorkFrame.this);
                    DownMain.main(mDownPath,mSavePath,true);
                } else {
                    JOptionPane.showMessageDialog(null,"请选择下载文件");
                }
            }
        });
        button.setBounds(120, 10, 150, 30);
        add(button);
    }

    private void addSavePathButton() {
        JLabel path = new JLabel("保存目录");
        path.setBounds(300,10,80,30);
        add(path);
        mSavePathTip= new JLabel(mSavePath);
        mSavePathTip.setBounds(380,10,200,30);
        add(mSavePathTip);
        JButton button = new JButton("修改保存目录");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("选择目录");
                chooser.setApproveButtonText("确定");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);  //设置只选择目录
                FileFilter filter = new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.isDirectory();
                    }

                    @Override
                    public String getDescription() {
                        return null;
                    }
                };
                chooser.addChoosableFileFilter(filter);
                chooser.setFileFilter(filter);
                int resule = chooser.showSaveDialog(new JPanel());
                if (resule == chooser.APPROVE_OPTION) {
                    mSavePath = chooser.getSelectedFile().getAbsolutePath() + "\\";
                    mSavePathTip.setText(mSavePath);
                }
            }
        });
        button.setBounds(620, 10, 150, 30);
        add(button);
    }

    @Override
    public void videoSuccessListener(int count) {
        mFailedUrlList.clear();
        mLoadingTip.setText("下载完成");
        JOptionPane.showMessageDialog(null,"成功下载完成" + count + "个视频");
    }

    @Override
    public void videoFailedListener(Vector<String> failedUrl) {
        mFailedUrlList.clear();
        mFailedUrlList.addAll(failedUrl);
        mLoadingTip.setText("下载完成，失败" + failedUrl.size() + "个");
        mFailedDownTip.setText("下载失败共计"+ failedUrl.size() + "个");
        for (String url : failedUrl){
            failedJP.add(new JLabel(url));
            failedJP.updateUI();
        }
        JOptionPane.showMessageDialog(null, "下载失败" + failedUrl.size() + "个", "失败提示", JOptionPane.OK_OPTION);
    }

    @Override
    public void videoUpdateNameListener(String videoName) {
        mLoadingTip.setText("下载进行中...");
        JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));
        panel2.add(new JLabel(videoName));
        JProgressBar pb = new JProgressBar();
        pb.setMinimum(0);
        pb.setMaximum(100);
        pb.setStringPainted(true);
        pb.setPreferredSize(new Dimension(80, 20));
        pb.setBackground(Color.white);
        pb.setForeground(Color.GRAY);
        panel2.add(pb);
        contentJP.add(panel2);
        contentJP.updateUI();
        mDownTable.put(videoName,pb);
    }

    @Override
    public void videoProgressListener(String title,double downCount) {
        JProgressBar jProgressBar = mDownTable.get(title);
        jProgressBar.setValue((int)downCount);
    }

    @Override
    public void videoOriginalListener(int count) {
        mDownTip.setText("共下载视频" + count + "个");
    }


    class TxtFileFilter extends FileFilter {
        public String getDescription() {
            return "*.txt";
        }

        public boolean accept(File file) {
            String name = file.getName();
            return file.isDirectory() || name.toLowerCase().endsWith(".txt");
        }
    }
}