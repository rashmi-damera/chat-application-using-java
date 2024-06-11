import java.net.*;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;

class server extends JFrame{
    ServerSocket server;
    Socket socket;

    BufferedReader br;
    PrintWriter out;
    JLabel show=new JLabel("SERVER AREA");
    JTextArea messagearea=new JTextArea();
    JTextField messages=new JTextField();
    Font font = new Font("Roboto", Font.PLAIN, 20);



    public server() {
        try {

            server = new ServerSocket(7777);
            System.out.println("waiting for connection.....");
            socket = server.accept();

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            


            creategui();
            handleevents();
            startreading();
            // startwriting();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void creategui() {
        this.setTitle("server messenger");
        this.setSize(500, 500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        show.setFont(font);
        messagearea.setFont(font);
        messages.setFont(font);
        
        show.setHorizontalAlignment(SwingConstants.CENTER);
        messages.setHorizontalAlignment(SwingConstants.CENTER);
        show.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        
        this.setLayout(new BorderLayout());
        this.add(show,BorderLayout.NORTH);
        JScrollPane sp=new JScrollPane(messagearea);
        this.add(sp,BorderLayout.CENTER);
        this.add(messages,BorderLayout.SOUTH);
        messagearea.setEditable(false);
            
        this.setVisible(true);  
      }

    private void handleevents() {
        KeyListener e;
        messages.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                
            }

            @Override
            public void keyPressed(KeyEvent e) {
              
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode()==10&&!socket.isClosed()){
                    String contentfrmkey=messages.getText();
                    out.println(contentfrmkey);
                    messagearea.append("Me:"+contentfrmkey+"\n");
                    if(contentfrmkey=="exit"){
                        messages.setEnabled(false);
                    }
    
                    out.flush();
                    messages.setText("");
                    messages.requestFocus();

                }
            }  
        });
    }

    public void startreading() {
        Runnable r1 = () -> {
            System.out.println("reader started");
            try {
                while (!socket.isClosed()) {

                    String msg = br.readLine();
                    if (msg.equals("exit")) {
                        System.out.println("client terminated the chat");
                        JOptionPane.showMessageDialog(this,"server terminated the chat");
                        messages.setEnabled(false);

                        socket.close();
                        break;
                    }
                    messagearea.append("client: " + msg+"\n");

                }
            } catch (Exception e) {
                System.out.println("connection is closed");
            }
        };
        new Thread(r1).start();
    }

    public void startwriting() {
        Runnable r2 = () -> {
            System.out.println("writing started");
            try{
            while (!socket.isClosed()) {
                
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                    String content = br1.readLine();
                    out.println(content);
                    out.flush();

                    if(content.equals("exit")){
                        socket.close();
                        break;
                    }
                } 
            }catch (Exception e) {
                    System.out.println("connection is closed");
                }
            

        };
        new Thread(r2).start();
    }

    public static void main(String args[]) {
        new server();
    }
}