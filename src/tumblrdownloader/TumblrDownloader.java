/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tumblrdownloader;

import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Blog;
import com.tumblr.jumblr.types.Photo;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.VideoPost;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

/**
 *
 * @author anony
 */
public class TumblrDownloader extends javax.swing.JFrame {

    /**
     * Creates new form TumblrDownloader
     */
    public TumblrDownloader() {
        initComponents();
    }
    
    public String dir = "";
    public String text = "";
    
     private static boolean isMatch(String s, String pattern) {
        try {
            Pattern patt = Pattern.compile(pattern);
            Matcher matcher = patt.matcher(s);
            return matcher.matches();
        } catch (RuntimeException e) {
            return false;
        }       
    }
    
    public void downloadImg(String Url,String imgName, String imgType) throws MalformedURLException, IOException, InterruptedException {
        URL url = new URL(Url);
        InputStream in = new BufferedInputStream(url.openStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n = 0;
        while (-1!=(n=in.read(buf)))
        {
           out.write(buf, 0, n);       
        }    
        out.close();
        in.close();
        byte[] response = out.toByteArray();
        FileOutputStream fos = new FileOutputStream( dir + "//" + imgName + "." + imgType);
        fos.write(response);
        fos.close();
    }
    
    public void GetFullBlog(String url) throws IOException, MalformedURLException, InterruptedException {
        //API key
        JumblrClient client = new JumblrClient("IHWdEKf3toFTKqdbcUw4UQLFJnOJNp5HoZe0lgILfb9XGeFpWc", "pnvPWZA0CaJmYcPSKzfD6or8IcOSH1ED1ROa1otb0TD8hUQgDJ");
        client.setToken("kXxureXOGylhooGE1vTUfr9DIBrU4xyc1AgeuUrYyAgB5Q2rOm", "3LdkACWAaL8N5ciA4bhjUy81Z7DAB5WGApN2leXJTFc4FYY4ve");
        
        List<Post> post = client.blogPosts(url); 
        
        int post_size = post.size();
        int size = (int) 100 / post_size;
        
        JFrame f = new JFrame("Download Status");
        JProgressBar progressBar = new JProgressBar();
        f.setDefaultCloseOperation(f.EXIT_ON_CLOSE);
        Container content = f.getContentPane();
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setBackground(Color.blue);
        TitledBorder border = BorderFactory.createTitledBorder("Downloading...");
        progressBar.setBorder(border);
        content.add(progressBar, BorderLayout.NORTH);
        f.setSize(400, 100);
        f.setLocationRelativeTo(null); 
        f.setVisible(true);
        
        for (Post element : post) {
            if (element.getType().toString().contentEquals("photo")) {
                PhotoPost photo_post = (PhotoPost) client.blogPost(url,element.getId());
                List<Photo> image = photo_post.getPhotos();
                for (Photo elem : image) {
                    String Url = elem.getOriginalSize().getUrl();
                    String name = Url.substring(Url.lastIndexOf("/")+1,Url.lastIndexOf("."));
                    String type = Url.substring(Url.lastIndexOf(".")+1);
                    downloadImg(Url,name,type);
                    text += "Save " + name + "." + type + " success!\n";
                    jTextArea1.setText(text);
                    jTextArea1.update(jTextArea1.getGraphics());
                    jTextArea1.setCaretPosition(jTextArea1.getText().length() - 1);
                    
                }
            }
            progressBar.setValue(progressBar.getValue()+size);
            f.update(f.getGraphics());
        }
        progressBar.setValue(100);
        Thread.sleep(100);
        f.dispose();
        JOptionPane.showMessageDialog(null, "Download Complete!");
    }
    
    public void GetPost(String url, long postId) throws IOException, MalformedURLException, InterruptedException {
        //API key
        JumblrClient client = new JumblrClient("IHWdEKf3toFTKqdbcUw4UQLFJnOJNp5HoZe0lgILfb9XGeFpWc", "pnvPWZA0CaJmYcPSKzfD6or8IcOSH1ED1ROa1otb0TD8hUQgDJ");
        client.setToken("kXxureXOGylhooGE1vTUfr9DIBrU4xyc1AgeuUrYyAgB5Q2rOm", "3LdkACWAaL8N5ciA4bhjUy81Z7DAB5WGApN2leXJTFc4FYY4ve");
        
        Post post = client.blogPost(url, postId);
        
        JFrame f = new JFrame("Download Status");
        JProgressBar progressBar = new JProgressBar();
        f.setDefaultCloseOperation(f.EXIT_ON_CLOSE);
        Container content = f.getContentPane();
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setBackground(Color.blue);
        TitledBorder border = BorderFactory.createTitledBorder("Downloading...");
        progressBar.setBorder(border);
        content.add(progressBar, BorderLayout.NORTH);
        f.setSize(400, 100);
        f.setLocationRelativeTo(null); 
        f.setVisible(true);

        if (post.getType().toString().contentEquals("photo")) {
            PhotoPost photo_post = (PhotoPost) client.blogPost(url,post.getId());
            List<Photo> image = photo_post.getPhotos();
            int percent = 100/image.size();
            for (Photo elem : image) {
                String Url = elem.getOriginalSize().getUrl();
                String name = Url.substring(Url.lastIndexOf("/")+1,Url.lastIndexOf("."));
                String type = Url.substring(Url.lastIndexOf(".")+1);
                downloadImg(Url,name,type);
                text += "Save " + name + "." + type + " success!\n";
                jTextArea1.setText(text);
                jTextArea1.update(jTextArea1.getGraphics());
                jTextArea1.setCaretPosition(jTextArea1.getText().length() - 1);
                progressBar.setValue(progressBar.getValue() + percent);
                f.update(f.getGraphics());
            }
        }
        progressBar.setValue(100);
        Thread.sleep(100);
        f.dispose();
        JOptionPane.showMessageDialog(null, "Download Complete!");
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        tumblr_url = new javax.swing.JTextField();
        downloadButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setLocationByPlatform(true);
        setResizable(false);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setText("Tumblr Auto Downloader");

        jLabel2.setText("Link");

        tumblr_url.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                tumblr_urlCaretUpdate(evt);
            }
        });

        downloadButton.setText("Download");
        downloadButton.setEnabled(false);
        downloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downloadButtonActionPerformed(evt);
            }
        });

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jLabel3.setFont(new java.awt.Font("Tahoma", 3, 14)); // NOI18N
        jLabel3.setText("Made by Nhữ Đình Toàn (https://github.com/toannhu)");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tumblr_url, javax.swing.GroupLayout.PREFERRED_SIZE, 493, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(downloadButton, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(186, 186, 186))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(tumblr_url, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(downloadButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(jLabel3)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void downloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downloadButtonActionPerformed
        
        String regex = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";                
        if(isMatch(tumblr_url.getText(),regex) == true && tumblr_url.getText().contains(".tumblr.") == true) {
            try {
                jFileChooser1.setCurrentDirectory(new File(System.getProperty("user.dir")));
                jFileChooser1.setDialogTitle("choosertitle");
                jFileChooser1.setFileSelectionMode(jFileChooser1.DIRECTORIES_ONLY);
                jFileChooser1.setAcceptAllFileFilterUsed(false);
                jFileChooser1.showSaveDialog(null);
                dir = jFileChooser1.getSelectedFile().getAbsolutePath();
                text += dir +"\n";
                jTextArea1.setText(dir);
                String url = null;
                if (tumblr_url.getText().contains("/post/") == false) {
                    if (tumblr_url.getText().endsWith("/")) {
                        url = tumblr_url.getText().substring(8, tumblr_url.getText().length()-1);
                    }
                    else {
                        url = tumblr_url.getText().substring(8, tumblr_url.getText().length());
                    }
                    GetFullBlog(url);
                }
                else {
                    String[] temp = tumblr_url.getText().split("/post/");    
                    long postId;
                    if (tumblr_url.getText().endsWith("/")) {
                        url = temp[0].substring(8,temp[0].length());
                        postId = Long.parseLong(temp[1].substring(0,temp[1].length()-1),10);
                    }
                    else {
                        url = temp[0].substring(8,temp[0].length());
                        postId = Long.parseLong(temp[1],10);
                    }              
                    GetPost(url,postId);
                }
            } catch (IOException ex) {
                Logger.getLogger(TumblrDownloader.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(TumblrDownloader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else JOptionPane.showMessageDialog(null, "URL is wrong or mismatch! Please enter again!");
    }//GEN-LAST:event_downloadButtonActionPerformed

    private void tumblr_urlCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_tumblr_urlCaretUpdate
        // TODO add your handling code here:
        if (tumblr_url.getText().length() >= 1) {
            downloadButton.setEnabled(true);
        } else {
            downloadButton.setEnabled(false);
        }
    }//GEN-LAST:event_tumblr_urlCaretUpdate

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TumblrDownloader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TumblrDownloader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TumblrDownloader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TumblrDownloader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TumblrDownloader().setVisible(true);
            }
        });
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton downloadButton;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField tumblr_url;
    // End of variables declaration//GEN-END:variables
}
