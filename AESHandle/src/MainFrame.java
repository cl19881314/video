import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class MainFrame extends JFrame{
    private static JButton bt1;//登陆按钮
    private static JButton bt2;//退出按钮
    private static JButton cc; //复制
    private static JLabel jl_1;//登录的版面
    private static JFrame jf_1;//登陆的框架
    private static JTextField jtext1;//用户名
    private static JTextField jtext2;//密码
    private static JLabel jl_admin;
    private static JLabel jl_password;
    private static String userName;
    private static String mStoreKey =  "storeKey";
    public MainFrame(){//初始化登陆界面
        Font font =new Font("黑体", Font.PLAIN, 20);//设置字体
        jf_1 = new JFrame("加密功能");
        jf_1.setSize(450, 300);
        jl_1 = new JLabel();

        jl_admin = new JLabel("用户名");
        jl_admin.setBounds(20, 30, 60, 50);
        jl_admin.setFont(font);

        jl_password=new JLabel("秘钥");
        jl_password.setBounds(20, 100, 50, 50);
        jl_password.setFont(font);

        cc =new JButton("复制");
        cc.setBounds(350, 110, 55, 20);
        cc.setFont(new Font("黑体", Font.PLAIN, 10));
        jl_1.add(cc);

        bt1 = new JButton("加密");
        bt1.setBounds(90, 200, 100, 40);
        bt1.setFont(font);

        bt2=new JButton("退出");
        bt2.setBounds(250, 200, 100, 40);
        bt2.setFont(font);
        //加入文本框
        jtext1=new JTextField(userName);
        jtext1.setBounds(120, 30, 200, 50);
        jtext1.setFont(font);

        jtext2=new JTextField();//密码输入框
        jtext2.setBounds(120, 100, 200, 50);
        jtext2.setFont(font);

        jl_1.add(jtext1);
        jl_1.add(jtext2);

        jl_1.add(jl_admin);
        jl_1.add(jl_password);
        jl_1.add(bt1);
        jl_1.add(bt2);

        jf_1.add(jl_1);
        jf_1.setVisible(true);
        jf_1.setResizable(false);
        jf_1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf_1.setLocation(700,400);
    }

    public static void main(String[] args) {
        //初始化登陆界面
        MainFrame hl = new MainFrame();
        //登陆点击事件
        ActionListener bt1_ls = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                String admin = jtext1.getText();
                String encrypt = AESHandle.Encrypt(admin, "chen1234567890@@");
                jtext2.setText(encrypt);
            }
        };
        bt1.addActionListener(bt1_ls);
        //退出事件的处理
        ActionListener bt2_ls = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);//终止当前程序
            }
        };
        bt2.addActionListener(bt2_ls);
        cc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (jtext2.getText().equals("")){
                    return;
                }
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();//获取系统剪切板
                StringSelection selection = new StringSelection(jtext2.getText());//构建String数据类型
                clipboard.setContents(selection, selection);//添加文本到系统剪切板
                JOptionPane.showMessageDialog(null,"复制成功！");
            }
        });
        //70-85-C2-79-19-36

//        String encrypt = AESHandle.Encrypt("70-85-C2-79-19-36", "chen1234567890@@");
//        System.out.println(encrypt);
//        String decrypt = AESHandle.Decrypt("Apv+TGJLpX31Yxc1vUazuwgoykK3gmMoAXA4xUp+jms=", "chen1234567890@@");
//        System.out.println(decrypt);
        jf_1.addWindowListener(new WindowListener() {
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
}