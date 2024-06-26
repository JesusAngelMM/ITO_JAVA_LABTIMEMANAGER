package Windows;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class LoginWindow extends javax.swing.JFrame {

    private static String URL;
    private static String usuario;
    private static String contrasena;
    private static final String PROPERTIES_FILE_PATH = "config.properties";
    
    PreparedStatement ps;
    ResultSet rs;
    
    public LoginWindow() {
        initComponents();
        loadProperties();
    }
    
    private void loadProperties() {
        ConfigLoader configLoader = new ConfigLoader(PROPERTIES_FILE_PATH);
        URL = configLoader.getProperty("db.url");
        usuario = configLoader.getProperty("db.user");
        contrasena = configLoader.getProperty("db.password");
    }
    
    private String validateLogin(String username, String password) throws Exception {
        String role = null;
        try {
            Connection conn = DriverManager.getConnection(LoginWindow.URL, LoginWindow.usuario, LoginWindow.contrasena);
            String query = "SELECT role FROM USER WHERE username = ? AND password = ?";
            ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
            rs = ps.executeQuery();

            if (rs.next()) {
                role = rs.getString("role"); // Obtener el rol del usuario
            }

            conn.close();
        } catch (Exception e) {
            //e.printStackTrace(); // Imprimir el stack trace del error
            //System.err.println("Error de conexión: " + e.getMessage());
            throw new Exception("Error al conectar a la base de datos", e);
        }
        return role;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        AccesoAdmin = new javax.swing.JDialog();
        panelAccesoAdmin = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtSupUsuario = new javax.swing.JTextField();
        btnSupAcceder = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        txtSupContrasena = new javax.swing.JPasswordField();
        ModificarConexion = new javax.swing.JDialog();
        panelModificarConexion = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtConfURL = new javax.swing.JTextField();
        txtConfUsuario = new javax.swing.JTextField();
        txtConfContrasena = new javax.swing.JTextField();
        btnConfGuardar = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        lblContrasena = new javax.swing.JLabel();
        btnIniciarSesion = new javax.swing.JButton();
        lblUsuario = new javax.swing.JLabel();
        txtUsuario = new javax.swing.JTextField();
        btnCancelar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtContrasena = new javax.swing.JPasswordField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        btnModificarDB = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel7 = new javax.swing.JLabel();

        AccesoAdmin.setTitle("Acceder como super Administrador");

        jLabel8.setText("Usuario");

        jLabel9.setText("Contraseña:");

        btnSupAcceder.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/iniciar_sesion.png"))); // NOI18N
        btnSupAcceder.setText("Acceder");
        btnSupAcceder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSupAccederActionPerformed(evt);
            }
        });

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/seguridad-de-la-base-de-datos.png"))); // NOI18N

        javax.swing.GroupLayout panelAccesoAdminLayout = new javax.swing.GroupLayout(panelAccesoAdmin);
        panelAccesoAdmin.setLayout(panelAccesoAdminLayout);
        panelAccesoAdminLayout.setHorizontalGroup(
            panelAccesoAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAccesoAdminLayout.createSequentialGroup()
                .addGroup(panelAccesoAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelAccesoAdminLayout.createSequentialGroup()
                        .addGap(170, 170, 170)
                        .addComponent(jLabel10))
                    .addGroup(panelAccesoAdminLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(panelAccesoAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnSupAcceder)
                            .addGroup(panelAccesoAdminLayout.createSequentialGroup()
                                .addGroup(panelAccesoAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel9))
                                .addGap(46, 46, 46)
                                .addGroup(panelAccesoAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtSupUsuario)
                                    .addComponent(txtSupContrasena, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE))))))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        panelAccesoAdminLayout.setVerticalGroup(
            panelAccesoAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAccesoAdminLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel10)
                .addGap(18, 18, 18)
                .addGroup(panelAccesoAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtSupUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panelAccesoAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtSupContrasena, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnSupAcceder)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout AccesoAdminLayout = new javax.swing.GroupLayout(AccesoAdmin.getContentPane());
        AccesoAdmin.getContentPane().setLayout(AccesoAdminLayout);
        AccesoAdminLayout.setHorizontalGroup(
            AccesoAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelAccesoAdmin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        AccesoAdminLayout.setVerticalGroup(
            AccesoAdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelAccesoAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        ModificarConexion.setTitle("Modificar la conexión a la base de datos");

        panelModificarConexion.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel11.setText("URL:");
        panelModificarConexion.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, -1));

        jLabel12.setText("Usuario:");
        panelModificarConexion.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, -1, -1));

        jLabel13.setText("Contraseña:");
        panelModificarConexion.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, -1, -1));
        panelModificarConexion.add(txtConfURL, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 40, 265, -1));
        panelModificarConexion.add(txtConfUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 80, 265, -1));
        panelModificarConexion.add(txtConfContrasena, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 120, 265, -1));

        btnConfGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/guardar.png"))); // NOI18N
        btnConfGuardar.setText("Guardar");
        btnConfGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfGuardarActionPerformed(evt);
            }
        });
        panelModificarConexion.add(btnConfGuardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, -1, -1));
        panelModificarConexion.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(461, 97, -1, -1));

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/base-de-datos.png"))); // NOI18N
        panelModificarConexion.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 30, -1, -1));

        javax.swing.GroupLayout ModificarConexionLayout = new javax.swing.GroupLayout(ModificarConexion.getContentPane());
        ModificarConexion.getContentPane().setLayout(ModificarConexionLayout);
        ModificarConexionLayout.setHorizontalGroup(
            ModificarConexionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelModificarConexion, javax.swing.GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE)
        );
        ModificarConexionLayout.setVerticalGroup(
            ModificarConexionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelModificarConexion, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Inicio de Sesión");
        setResizable(false);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("LabTimeManager");

        lblContrasena.setText("Contraseña:");

        btnIniciarSesion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/iniciar_sesion.png"))); // NOI18N
        btnIniciarSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIniciarSesionActionPerformed(evt);
            }
        });

        lblUsuario.setText("Usuario:");

        txtUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsuarioActionPerformed(evt);
            }
        });

        btnCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/cerrar.png"))); // NOI18N
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/laboratory.png"))); // NOI18N

        jLabel3.setText("Iniciar Sesión");

        jLabel4.setText("Cancelar");

        btnModificarDB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/configuracion.png"))); // NOI18N
        btnModificarDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarDBActionPerformed(evt);
            }
        });

        jLabel5.setText("Conexión");

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/ayuda.png"))); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel6.setText("Ayuda");

        jLabel7.setText("¡Bienvenido a LabTimeManager!");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(143, 143, 143)
                .addComponent(jLabel1)
                .addContainerGap(146, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel7))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblContrasena)
                            .addComponent(lblUsuario))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtContrasena)
                            .addComponent(txtUsuario)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(59, 59, 59)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnIniciarSesion, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel3))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnModificarDB, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel5)))
                        .addGap(18, 102, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(38, 38, 38)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6))))
                .addGap(35, 35, 35))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblUsuario)
                            .addComponent(txtUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblContrasena)
                            .addComponent(txtContrasena, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel2))
                .addGap(33, 33, 33)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnIniciarSesion)
                    .addComponent(btnCancelar)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnModificarDB)
                    .addComponent(jLabel5)
                    .addComponent(jButton2)
                    .addComponent(jLabel6))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txtUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsuarioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUsuarioActionPerformed

    private void btnIniciarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIniciarSesionActionPerformed
        String username = txtUsuario.getText();
        String password = new String(txtContrasena.getPassword());

        try {
            String role = validateLogin(username, password);

            if (role != null) {
                JOptionPane.showMessageDialog(this, "Inicio de sesión exitoso");
                if (role.equals("administrador")) {
                    new AdminDashboard(username).setVisible(true); // Abrir ventana de administrador
                } else {
                    new UserDashboard(username).setVisible(true); // Abrir ventana de usuario normal
                }
                this.dispose(); // Cerrar la ventana de login
            } else {
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos", "Error de autenticación", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            // Mostrar mensaje de error si no se puede conectar a la base de datos
            JOptionPane.showMessageDialog(this, "Error al conectarse a la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
            //System.err.println("Error al conectar a la base de datos: " + e.getMessage());
        }
    }//GEN-LAST:event_btnIniciarSesionActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            try {
                Desktop.getDesktop().browse(new URI("https://jesusangelmm.github.io/Proyectos/LabTimeManager/Inicio"));
            } catch (URISyntaxException ex) {
                Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void btnModificarDBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarDBActionPerformed
        AccesoAdmin.setSize(400, 260);
        AccesoAdmin.setLocationRelativeTo(null);
        AccesoAdmin.setVisible(true);
    }//GEN-LAST:event_btnModificarDBActionPerformed

    private void btnConfGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfGuardarActionPerformed
        String nuevaURL = txtConfURL.getText();
        String nuevoUsuario = txtConfUsuario.getText();
        String nuevaContrasena = txtConfContrasena.getText();

        // Ruta al archivo de configuración
        String configFilePath = "config.properties";

        // Cargar las propiedades existentes
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(configFilePath)) {
            props.load(in);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar el archivo de configuración", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Actualizar las propiedades con los nuevos valores
        props.setProperty("db.url", nuevaURL);
        props.setProperty("db.user", nuevoUsuario);
        props.setProperty("db.password", nuevaContrasena);

        // Guardar las propiedades actualizadas de nuevo en el archivo con formato específico
        try (FileOutputStream out = new FileOutputStream(configFilePath)) {
            props.store(out, null);

            // Reescribir el archivo en el formato específico
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFilePath))) {
                writer.write("db.url=" + props.getProperty("db.url"));
                writer.newLine();
                writer.write("db.user=" + props.getProperty("db.user"));
                writer.newLine();
                writer.write("db.password=" + props.getProperty("db.password"));
                writer.newLine();
            }

            JOptionPane.showMessageDialog(this, "Configuración guardada exitosamente\nCierre y reinicie el programa", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            ModificarConexion.dispose();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar el archivo de configuración", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnConfGuardarActionPerformed

    private void btnSupAccederActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSupAccederActionPerformed
        String superUsuario = txtSupUsuario.getText();
        String superContrasena = new String(txtSupContrasena.getPassword());

        if (superUsuario.equals("superAdmin01") && superContrasena.equals("b4s3")) {
            ModificarConexion.setSize(575, 250);
            ModificarConexion.setLocationRelativeTo(null);
            ModificarConexion.setVisible(true);
            AccesoAdmin.dispose(); // Cierra el JDialog de AccesoAdmin

        } else {
            // Mostrar mensaje de error si las credenciales de superusuario son incorrectas
            JOptionPane.showMessageDialog(this, "Error, contraseña o usuario incorrecto", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnSupAccederActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        try {
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
                java.util.logging.Logger.getLogger(LoginWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                java.util.logging.Logger.getLogger(LoginWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                java.util.logging.Logger.getLogger(LoginWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            } catch (javax.swing.UnsupportedLookAndFeelException ex) {
                java.util.logging.Logger.getLogger(LoginWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
            //</editor-fold>
            UIManager.setLookAndFeel(new FlatLightLaf());
            /* Create and display the form */
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    new LoginWindow().setVisible(true);
                }
            });
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(LoginWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog AccesoAdmin;
    private javax.swing.JDialog ModificarConexion;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnConfGuardar;
    private javax.swing.JButton btnIniciarSesion;
    private javax.swing.JButton btnModificarDB;
    private javax.swing.JButton btnSupAcceder;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblContrasena;
    private javax.swing.JLabel lblUsuario;
    private javax.swing.JPanel panelAccesoAdmin;
    private javax.swing.JPanel panelModificarConexion;
    private javax.swing.JTextField txtConfContrasena;
    private javax.swing.JTextField txtConfURL;
    private javax.swing.JTextField txtConfUsuario;
    private javax.swing.JPasswordField txtContrasena;
    private javax.swing.JPasswordField txtSupContrasena;
    private javax.swing.JTextField txtSupUsuario;
    private javax.swing.JTextField txtUsuario;
    // End of variables declaration//GEN-END:variables
}
