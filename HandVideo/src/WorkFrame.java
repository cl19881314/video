import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import util.CheckKeyUtil;
import util.FFmpegUtil;

public class WorkFrame extends JFrame implements FFmpegUtil.CallBackListener {
    private HashMap<String, JLabel> mDownTable = new HashMap<>();
    private JPanel contentJP;
    private ArrayList<String> mFileList = new ArrayList<>();
    private JButton mStartButton;
    private String mSavePath = "D:\\handleVideo\\";
    public WorkFrame() {
        setLayout(null);
        addOpenButton();
        addStartButton();
        addStopButton();
        addOutPutPath();

        contentJP = new JPanel();
        contentJP.setLayout(new BoxLayout(contentJP,BoxLayout.Y_AXIS));

        JScrollPane jsp = new JScrollPane(contentJP);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jsp.setBounds(10, 90, 1000, 500);
        add(jsp);

        addWindowListener(new WindowAdapter() {
            public void WindowClosing(WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });
        setSize(1050, 650);
        setTitle("红哥--视频处理器");
        setLocation(250, 100);
        setResizable(false);
        show();
    }

    private void addOutPutPath() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        JLabel save = new JLabel("保存位置：");
        panel.add(save);
        final JLabel name = new JLabel("D:\\handleVideo\\");
        panel.add(name);
        JButton button = new JButton("修改路径");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("选择目录");
                chooser.setApproveButtonText("确定");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);  //设置只选择目录
                int resule = chooser.showOpenDialog(new JPanel());
                if (resule == chooser.APPROVE_OPTION) {
                    String dir = chooser.getSelectedFile().getAbsolutePath();
                    name.setText(dir);
                    mSavePath = dir + "\\";
                }
            }
        });
        panel.add(button);
        panel.setBounds(10,50,2000,30);
        add(panel);
    }

    private void addStartButton(){
        mStartButton = new JButton("开始处理");
        mStartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!CheckKeyUtil.CheckKey()){
                    return;
                }
                File file = new File(mSavePath);
                if (!file.exists()){
                    file.mkdirs();
                }
                mStartButton.setEnabled(false);
                if (!mFileList.isEmpty()){
                    FFmpegUtil.setCallBackListener(WorkFrame.this);
                    FFmpegUtil.doMain(mFileList, mSavePath);
                }
            }
        });
        mStartButton.setBounds(120, 10, 150, 30);
        add(mStartButton);
    }

    private void addStopButton(){
        JButton button = new JButton("停止处理");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                FFmpegUtil.stopRunningProcesses();
                mStartButton.setEnabled(true);
            }
        });
        button.setBounds(280, 10, 150, 30);
        add(button);
    }

    private void addOpenButton() {
        JButton button = new JButton("选择目录");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("选择目录");
                chooser.setApproveButtonText("确定");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);  //设置只选择目录
                int resule = chooser.showOpenDialog(new JPanel());
                if (resule == chooser.APPROVE_OPTION) {
                    mFileList.clear();
                    mStartButton.setEnabled(true);
                    contentJP.removeAll();
                    contentJP.updateUI();
//                    String fileName = chooser.getSelectedFile().getName();
                    String dir = chooser.getSelectedFile().getAbsolutePath();
                    File dirPath = new File(dir);
                    if (dirPath.isDirectory()){
                        FileFilter filter = new FileFilter() {
                            @Override
                            public boolean accept(File pathname) {
                                if (pathname.getName().endsWith(".mp4")){
                                    return true;
                                }
                                return false;
                            }
                        };
                        File[] files = dirPath.listFiles(filter);
                        for (File file : files){
                            mFileList.add(file.getAbsolutePath());
                            JPanel panel2 = new JPanel();
                            panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));
//                            panel2.setLayout(new GridLayout(2,2));
                            JLabel name = new JLabel(file.getName());
                            name.setBounds(0, 0,300,50);
                            panel2.add(name);
                            JLabel ing = new JLabel();
                            ing.setBounds(0, 0,300,50);
                            panel2.add(ing);
                            contentJP.add(panel2);
                            contentJP.updateUI();
                            mDownTable.put(file.getName(), ing);
                        }
                    }
                }
            }
        });
        button.setBounds(10, 10, 100, 30);
        add(button);
    }

    @Override
    public void updateDown(int count,String name) {
        JLabel jLabel = mDownTable.get(name);
        jLabel.setText("        处理时间"+count + "秒");
        contentJP.updateUI();
    }
    @Override
    public void updateSuccess(String name) {
        JLabel jLabel = mDownTable.get(name);
        jLabel.setText(jLabel.getText() + "         处理完成");
        contentJP.updateUI();
        String lastVideo = mFileList.get(mFileList.size() - 1);
        String videoName = lastVideo.substring(lastVideo.lastIndexOf("\\") + 1);
        if (videoName.equals(name)){
            mStartButton.setEnabled(true);
        }
    }
}