import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.*;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class client extends JFrame {
    Socket client;
    BufferedReader br;
    PrintWriter pw;

    JLabel l = new JLabel("CLIENT AREA");
    JTextArea ta = new JTextArea();
    JTextField tf = new JTextField();
    Font font = new Font("Roboto", Font.PLAIN, 20);

    public client() {
        try {
            client = new Socket("192.168.29.146", 7777);
            System.out.println("connection established");
            br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            pw = new PrintWriter(client.getOutputStream());

            createGUI();
            handleevents();
            startreading();
            // startwriting();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleevents() {
        tf.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
              if(e.getKeyCode()==10&&!client.isClosed()) {
                String contentfrmkey=tf.getText();
                pw.println(contentfrmkey);
                ta.append("Me:"+contentfrmkey+"\n");
                if(contentfrmkey=="exit"){
                    tf.setEnabled(false);
                }

                pw.flush();
                tf.setText("");
                tf.requestFocus();
              }

            }
        });

    }

    public void createGUI() {

        this.setTitle("client messenger");
        this.setSize(500, 500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        l.setFont(font);
        ta.setFont(font);
        tf.setFont(font);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        l.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        tf.setHorizontalAlignment(SwingConstants.CENTER);
        ta.setEditable(false);

        this.setLayout(new BorderLayout());
        this.add(l, BorderLayout.NORTH);
        JScrollPane sp=new JScrollPane(ta);
        this.add(sp, BorderLayout.CENTER);
        this.add(tf, BorderLayout.SOUTH);

        this.setVisible(true);

    }

    public void startreading() {
        Runnable r1 = () -> {
            System.out.println("reading started");
            try {
                while (!client.isClosed()) {

                    String msg = br.readLine();
                    if (msg.equals("exit")) {
                        System.out.println(("server terminated chat"));
                        JOptionPane.showMessageDialog(this,"server terminated the chat");
                        tf.setEnabled(false);
                        client.close();
                        break;
                    }
                    
                    ta.append("server: " + msg+"\n");
                }
            } catch (Exception e) {
                System.err.println("connection is closed");
            }
        };
        new Thread(r1).start();
    }

    public void startwriting() {
        Runnable r2 = () -> {
            System.out.println("writing started");
            try {
                while (!client.isClosed()) {

                    BufferedReader br2 = new BufferedReader(new InputStreamReader(System.in));
                    String content = br2.readLine();
                    pw.println(content);
                    pw.flush();

                    if (content.equals("exit")) {
                        client.close();
                        break;
                    }

                }
            } catch (Exception e) {
                System.out.println("connection is closed");
            }

        };
        new Thread(r2).start();

    }

    public static void main(String[] args) {
        new client();
    }

}
