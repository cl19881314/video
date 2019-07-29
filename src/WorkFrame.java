import download.DownMain;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;

public class WorkFrame extends JFrame implements DownMain.VideoDownLister {
    private Hashtable<String, JProgressBar> mDownTable = new Hashtable<>();
    private JPanel contentJP;
    private JPanel failedJP;
    private JLabel mDownTip,mFailedDownTip,mLoadingTip;
    private Vector<String> mFailedUrlList = new Vector<>();
    public WorkFrame() {
        setLayout(null);
        File file = new File("D:\\video\\");
        if (!file.exists()){
            file.mkdirs();
        }
        addOpenButton();
        addReturyButton();

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
        mFailedDownTip.setBounds(10, 575, 100, 20);
        add(mFailedDownTip);

        JScrollPane failedJsp = new JScrollPane(failedJP);
        failedJsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        failedJsp.setBounds(10, 600, 1000, 200);
        add(failedJsp);

        addWindowListener(new WindowAdapter() {
            public void WindowClosing(WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });
        setSize(1200, 850);
        setTitle("红哥--视频下载器");
        setLocation(250, 100);
        show();
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
//                    String fileName = file.getSelectedFile().getName();
                    String dir = file.getSelectedFile().getAbsolutePath();
                    System.out.println(dir);
                    mLoadingTip.setText("准备下载...");
                    DownMain.addVideoNameListener(WorkFrame.this);
                    DownMain.main(dir);
//                        JOptionPane.showConfirmDialog(null, dir + "\\" + fileName, "选择的文件", JOptionPane.YES_OPTION);
//                        System.out.println(dir + fileName);
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
                }
            }
        });
        button.setBounds(120, 10, 150, 30);
        add(button);
    }

    @Override
    public void videoSuccessListener(int count) {
        mFailedUrlList.clear();
        mLoadingTip.setText("下载完成");
        JOptionPane.showMessageDialog(null,"成功下载完成" + count + "个视频", "下载完成", JOptionPane.YES_OPTION);
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