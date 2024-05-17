package Windows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.lang.ClassNotFoundException;
import javax.swing.JOptionPane;

public class UserDashboard extends javax.swing.JFrame {

    String nombre_usuario;
    
    public static final String URL = "jdbc:mysql://localhost:3306/labtimemanager?useTimeZone=true&serverTimezone=UTC&autoReconnect=true&useSSL=false";
    public static final String usuario = "root";
    public static final String contrasena = "password";
    PreparedStatement ps;
    ResultSet rs;
    
    public UserDashboard(String nombre_usuario) {
        this.nombre_usuario = nombre_usuario;
        initComponents();
        txtBievenido.setText("!Bienvenido " + nombre_usuario + "!");
        mostrarEstatus();
        mostrarReservaciones();
        rellenarComboBoxMateriales();
    }

    private void mostrarEstatus() {
        try {
            // Registrar el driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, usuario, contrasena);
            String query = "SELECT username, email, role, department FROM USER WHERE username = ?";
            ps = conn.prepareStatement(query);
            ps.setString(1, nombre_usuario);
            rs = ps.executeQuery();

            if (rs.next()) {
                jLabel2.setText(jLabel2.getText() + rs.getString("username"));
                jLabel6.setText(jLabel6.getText() + rs.getString("email"));
                jLabel7.setText(jLabel7.getText() + rs.getString("role"));
                jLabel8.setText(jLabel8.getText() + rs.getString("department"));
            }

            conn.close();
        } catch (Exception e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }
    
    private void configurarTablaYAgregarListener() {
        // Configurar el modelo de la tabla
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Laboratorio");
        modelo.addColumn("Fecha");
        modelo.addColumn("Hora Inicio");
        modelo.addColumn("Hora Fin");
        modelo.addColumn("Propósito");
        modelo.addColumn("Estado");
        modelo.addColumn("Tipo");
        tablaHorarios.setModel(modelo);

        // Añadir ListSelectionListener a la tabla
        ListSelectionListener oyenteSeleccion = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int filaSeleccionada = tablaHorarios.getSelectedRow();
                    if (filaSeleccionada != -1) {
                        // Obtener los valores de la fila seleccionada
                        String idReservacion = tablaHorarios.getValueAt(filaSeleccionada, 0).toString();
                        String nombreLaboratorio = tablaHorarios.getValueAt(filaSeleccionada, 1).toString();
                        String fecha = tablaHorarios.getValueAt(filaSeleccionada, 2).toString();
                        String horaInicio = tablaHorarios.getValueAt(filaSeleccionada, 3).toString();
                        String horaFin = tablaHorarios.getValueAt(filaSeleccionada, 4).toString();
                        String proposito = tablaHorarios.getValueAt(filaSeleccionada, 5).toString();
                        String estado = tablaHorarios.getValueAt(filaSeleccionada, 6).toString();
                        String tipo = tablaHorarios.getValueAt(filaSeleccionada, 7).toString();

                        // Aquí puedes actualizar otros componentes de la interfaz gráfica
                        // con los datos obtenidos de la fila seleccionada.
                        // Por ejemplo:
                        // txtIdReservacion.setText(idReservacion);
                        // txtNombreLaboratorio.setText(nombreLaboratorio);
                        // txtFecha.setText(fecha);
                        // txtHoraInicio.setText(horaInicio);
                        // txtHoraFin.setText(horaFin);
                        // txtProposito.setText(proposito);
                        // txtEstado.setText(estado);
                        // txtTipo.setText(tipo);
                    }
                }
            }
        };

        // Añadir el listener a la tabla
        tablaHorarios.getSelectionModel().addListSelectionListener(oyenteSeleccion);
    }

    private void mostrarReservaciones() {
        DefaultTableModel modelo = (DefaultTableModel) tablaHorarios.getModel();
        modelo.setRowCount(0); // Limpiar el modelo de la tabla antes de agregar nuevas filas

        String query = "SELECT R.id_reservation, L.name as lab_name, S.date, S.start_time, S.end_time, R.purpose, R.status, R.type " +
                       "FROM RESERVATION R " +
                       "JOIN LABORATORY L ON R.id_lab = L.id_lab " +
                       "JOIN SCHEDULE S ON R.id_schedule = S.id_schedule " +
                       "WHERE R.id_user = (SELECT id_user FROM USER WHERE username = ?)";

        String[] data = new String[8];
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, usuario, contrasena);
            ps = conn.prepareStatement(query);
            ps.setString(1, nombre_usuario);
            rs = ps.executeQuery();

            while (rs.next()) {
                data[0] = rs.getString("id_reservation");
                data[1] = rs.getString("lab_name");
                data[2] = rs.getString("date");
                data[3] = rs.getString("start_time");
                data[4] = rs.getString("end_time");
                data[5] = rs.getString("purpose");
                data[6] = rs.getString("status");
                data[7] = rs.getString("type");
                modelo.addRow(data);
            }
            conn.close();
        } catch (Exception e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }


    private int obtenerIdLaboratorio(String nombreLaboratorio) throws SQLException, ClassNotFoundException {
        int idLab = -1;
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection(URL, usuario, contrasena);
        String query = "SELECT id_lab FROM LABORATORY WHERE name = ?";
        ps = conn.prepareStatement(query);
        ps.setString(1, nombreLaboratorio);
        rs = ps.executeQuery();
        if (rs.next()) {
            idLab = rs.getInt("id_lab");
        }
        conn.close();
        return idLab;
    }

    private int obtenerIdMaterial(String nombreMaterial) throws SQLException, ClassNotFoundException {
        int idMaterial = -1;
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection(URL, usuario, contrasena);
        String query = "SELECT id_material FROM MATERIAL WHERE name = ?";
        ps = conn.prepareStatement(query);
        ps.setString(1, nombreMaterial);
        rs = ps.executeQuery();
        if (rs.next()) {
            idMaterial = rs.getInt("id_material");
        }
        conn.close();
        return idMaterial;
    }

    private int obtenerIdUsuario(String username) throws SQLException, ClassNotFoundException {
        int idUser = -1;
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection(URL, usuario, contrasena);
        String query = "SELECT id_user FROM USER WHERE username = ?";
        ps = conn.prepareStatement(query);
        ps.setString(1, username);
        rs = ps.executeQuery();
        if (rs.next()) {
            idUser = rs.getInt("id_user");
        }
        conn.close();
        return idUser;
    }

    private int insertarSchedule(String date, String startTime, String endTime) throws SQLException, ClassNotFoundException {
        int idSchedule = -1;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, usuario, contrasena);
            String query = "INSERT INTO SCHEDULE (date, start_time, end_time) VALUES (?, ?, ?)";
            ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, date);
            ps.setString(2, startTime);
            ps.setString(3, endTime);
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                idSchedule = rs.getInt(1);
            }
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        }

        return idSchedule;
    }

    private void insertarReservationMaterial(int idReservation, int idMaterial, int quantity) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, usuario, contrasena);
            String query = "INSERT INTO RESERVATION_MATERIAL (id_reservation, id_material, quantity) VALUES (?, ?, ?)";
            ps = conn.prepareStatement(query);
            ps.setInt(1, idReservation);
            ps.setInt(2, idMaterial);
            ps.setInt(3, quantity);
            ps.executeUpdate();
        } finally {
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        }
    }

    
    private void rellenarComboBoxMateriales() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, usuario, contrasena);
            String query = "SELECT name FROM MATERIAL";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();

            cboMaterial.removeAllItems();
            while (rs.next()) {
                cboMaterial.addItem(rs.getString("name"));
            }

            conn.close();
        }catch (Exception e) {
            System.err.println("Error al rellenar ComboBox de materiales: " + e.getMessage());
        }
    }

    private boolean existeReservaMaterial(int idReservation, int idMaterial) throws SQLException, ClassNotFoundException {
        boolean existe = false;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, usuario, contrasena);
            String query = "SELECT COUNT(*) FROM RESERVATION_MATERIAL WHERE id_reservation = ? AND id_material = ?";
            ps = conn.prepareStatement(query);
            ps.setInt(1, idReservation);
            ps.setInt(2, idMaterial);
            rs = ps.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                existe = true;
            }
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        }

        return existe;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Reservation = new javax.swing.JDialog();
        jLabel9 = new javax.swing.JLabel();
        cboMaterial = new javax.swing.JComboBox<>();
        lblSelectHora = new javax.swing.JLabel();
        cboLaboratorios = new javax.swing.JComboBox<>();
        btnAgregarMaterial = new javax.swing.JButton();
        cboHorasF = new javax.swing.JComboBox<>();
        cboHorasI = new javax.swing.JComboBox<>();
        btnHacerReservacion = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaMateriales = new javax.swing.JTable();
        btnExportarPDF = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        lblSelectLaboratorio = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        lblSelectMateriales = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        btnEliminarElemento = new javax.swing.JButton();
        txtDate = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtBievenido = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaHorarios = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        btnEliminarReservación = new javax.swing.JButton();
        btnVerDetalles = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        menuBarraUsuario = new javax.swing.JMenuBar();
        menuSalir = new javax.swing.JMenu();
        opcionMenuCerrarSesion = new javax.swing.JMenuItem();
        opcionMenuSalir = new javax.swing.JMenuItem();
        menuPreferencias = new javax.swing.JMenu();
        subMenuApariencia = new javax.swing.JMenu();
        opcionClaro = new javax.swing.JRadioButtonMenuItem();
        opcionOscuro = new javax.swing.JCheckBoxMenuItem();
        menuOpciones = new javax.swing.JMenu();
        opcionAgregarReservacion = new javax.swing.JMenuItem();
        menuAyuda = new javax.swing.JMenu();
        opcionAyuda = new javax.swing.JMenuItem();

        jLabel9.setText("Cancelar Cambios");

        cboMaterial.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lblSelectHora.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblSelectHora.setText("Seleccionar Fecha:");

        cboLaboratorios.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Laboratorio de Fisicoquímica", "Laboratorio de Ing. Civil", "Laboratorio de Ing. Eléctrica", "Laboratorio de Ing. Industrial", "Laboratorio de Ing. Química e Ing. Mecánica Pesada", "Laboratorio de Simulación" }));

        btnAgregarMaterial.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/aceptar.png"))); // NOI18N
        btnAgregarMaterial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarMaterialActionPerformed(evt);
            }
        });

        cboHorasF.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00" }));
        cboHorasF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboHorasFActionPerformed(evt);
            }
        });

        cboHorasI.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00" }));
        cboHorasI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboHorasIActionPerformed(evt);
            }
        });

        btnHacerReservacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/guardar.png"))); // NOI18N
        btnHacerReservacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHacerReservacionActionPerformed(evt);
            }
        });

        btnCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/cerrar.png"))); // NOI18N

        tablaMateriales.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Material"
            }
        ));
        jScrollPane2.setViewportView(tablaMateriales);

        btnExportarPDF.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/exportar_pdf.png"))); // NOI18N
        btnExportarPDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportarPDFActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel10.setText("Opciones:");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("Crear una nueva reservación");

        jLabel12.setText("Guardar Reservación");

        jLabel13.setText("a");

        jLabel14.setText("Exportar Reporte");

        lblSelectLaboratorio.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblSelectLaboratorio.setText("Seleccionar laboratorio:");

        jLabel15.setText("Seleccionar");

        lblSelectMateriales.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblSelectMateriales.setText("Seleccionar material:");

        jLabel18.setText("Eliminar Elemento");

        btnEliminarElemento.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/papelera.png"))); // NOI18N
        btnEliminarElemento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarElementoActionPerformed(evt);
            }
        });

        jLabel4.setText("0000-00-00");

        javax.swing.GroupLayout ReservationLayout = new javax.swing.GroupLayout(Reservation.getContentPane());
        Reservation.getContentPane().setLayout(ReservationLayout);
        ReservationLayout.setHorizontalGroup(
            ReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ReservationLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(ReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ReservationLayout.createSequentialGroup()
                        .addGroup(ReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(ReservationLayout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(ReservationLayout.createSequentialGroup()
                                .addComponent(lblSelectHora)
                                .addGap(233, 233, 233)
                                .addGroup(ReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(ReservationLayout.createSequentialGroup()
                                        .addComponent(jLabel15)
                                        .addGap(33, 33, 33)
                                        .addComponent(cboMaterial, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(lblSelectMateriales))
                                .addGap(14, 14, 14)))
                        .addComponent(btnAgregarMaterial))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ReservationLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lblSelectLaboratorio)
                        .addGap(46, 46, 46)
                        .addComponent(cboLaboratorios, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(ReservationLayout.createSequentialGroup()
                        .addGroup(ReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(ReservationLayout.createSequentialGroup()
                                .addComponent(cboHorasI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel13))
                            .addGroup(ReservationLayout.createSequentialGroup()
                                .addGroup(ReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel10)
                                    .addGroup(ReservationLayout.createSequentialGroup()
                                        .addGroup(ReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING))
                                        .addGroup(ReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(ReservationLayout.createSequentialGroup()
                                                .addGap(136, 136, 136)
                                                .addGroup(ReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                    .addComponent(btnHacerReservacion, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                                                    .addComponent(btnExportarPDF, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                                                    .addComponent(btnCancelar, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)))
                                            .addGroup(ReservationLayout.createSequentialGroup()
                                                .addGap(23, 23, 23)
                                                .addComponent(cboHorasF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addGroup(ReservationLayout.createSequentialGroup()
                                        .addGap(1, 1, 1)
                                        .addGroup(ReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel4)
                                            .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(ReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ReservationLayout.createSequentialGroup()
                                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnEliminarElemento, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        ReservationLayout.setVerticalGroup(
            ReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ReservationLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel11)
                .addGap(18, 18, 18)
                .addGroup(ReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSelectLaboratorio)
                    .addComponent(cboLaboratorios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(39, 39, 39)
                .addGroup(ReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(ReservationLayout.createSequentialGroup()
                        .addGroup(ReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblSelectHora)
                            .addComponent(lblSelectMateriales))
                        .addGap(1, 1, 1)
                        .addComponent(jLabel4)
                        .addGap(1, 1, 1)
                        .addGroup(ReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(ReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel15)
                                .addComponent(cboMaterial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnAgregarMaterial)
                            .addComponent(txtDate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(ReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(ReservationLayout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addGroup(ReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cboHorasI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(ReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(cboHorasF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel13)))
                                .addGap(25, 25, 25)
                                .addGroup(ReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(ReservationLayout.createSequentialGroup()
                                        .addComponent(jLabel10)
                                        .addGap(18, 18, 18)
                                        .addComponent(btnHacerReservacion))
                                    .addComponent(jLabel12))
                                .addGap(18, 18, 18)
                                .addGroup(ReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnExportarPDF)
                                    .addComponent(jLabel14)))
                            .addGroup(ReservationLayout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(ReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(ReservationLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(ReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btnCancelar)
                                    .addComponent(jLabel9)))
                            .addGroup(ReservationLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel18))))
                    .addComponent(btnEliminarElemento))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        txtBievenido.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtBievenido.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtBievenido.setText("¡Bienvenido");

        jLabel1.setText("Estatus del usuario");

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/usuario_normal.png"))); // NOI18N

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setText("Reservaciones");

        tablaHorarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Laboratorio", "Fecha", "Hora inicio", "Hora Fin", "Propósito", "Estado", "Tipo"
            }
        ));
        // Código post-init
        configurarTablaYAgregarListener();
        jScrollPane1.setViewportView(tablaHorarios);

        jLabel2.setText("Nombre: ");

        jLabel6.setText("Correo: ");

        jLabel7.setText("Rol: ");

        jLabel8.setText("Departamento: ");

        btnEliminarReservación.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/papelera.png"))); // NOI18N

        btnVerDetalles.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/ojo_abierto.png"))); // NOI18N

        jLabel16.setText("Detalles");

        jLabel17.setText("Eliminar");

        menuSalir.setText("Salir");

        opcionMenuCerrarSesion.setText("Cerrar Sesión");
        opcionMenuCerrarSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcionMenuCerrarSesionActionPerformed(evt);
            }
        });
        menuSalir.add(opcionMenuCerrarSesion);

        opcionMenuSalir.setText("Salir");
        opcionMenuSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcionMenuSalirActionPerformed(evt);
            }
        });
        menuSalir.add(opcionMenuSalir);

        menuBarraUsuario.add(menuSalir);

        menuPreferencias.setText("Preferencias");

        subMenuApariencia.setText("Apariencia");

        opcionClaro.setSelected(true);
        opcionClaro.setText("Claro");
        subMenuApariencia.add(opcionClaro);

        opcionOscuro.setSelected(true);
        opcionOscuro.setText("Oscuro");
        subMenuApariencia.add(opcionOscuro);

        menuPreferencias.add(subMenuApariencia);

        menuBarraUsuario.add(menuPreferencias);

        menuOpciones.setText("Opciones");

        opcionAgregarReservacion.setText("Crear una reservación");
        opcionAgregarReservacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcionAgregarReservacionActionPerformed(evt);
            }
        });
        menuOpciones.add(opcionAgregarReservacion);

        menuBarraUsuario.add(menuOpciones);

        menuAyuda.setText("Ayuda");

        opcionAyuda.setText("Manual y Documentación");
        menuAyuda.add(opcionAyuda);

        menuBarraUsuario.add(menuAyuda);

        setJMenuBar(menuBarraUsuario);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(52, 52, 52)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 691, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel5)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtBievenido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(jLabel16)
                                        .addGap(18, 18, 18)
                                        .addComponent(btnVerDetalles))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel17)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnEliminarReservación)))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addGap(147, 147, 147)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)))
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 670, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(txtBievenido)
                .addGap(5, 5, 5)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel8)))
                .addGap(45, 45, 45)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnVerDetalles)
                            .addComponent(jLabel16))
                        .addGap(21, 21, 21)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17)
                            .addComponent(btnEliminarReservación)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void cboHorasFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboHorasFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboHorasFActionPerformed

    private void cboHorasIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboHorasIActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboHorasIActionPerformed

    private void btnHacerReservacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHacerReservacionActionPerformed
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Obtener los datos del formulario
            String laboratorio = cboLaboratorios.getSelectedItem().toString();
            DefaultTableModel materialModel = (DefaultTableModel) tablaMateriales.getModel();
            String date = txtDate.getText(); // Obtener la fecha desde el campo de texto
            String horaInicio = cboHorasI.getSelectedItem().toString();
            String horaFin = cboHorasF.getSelectedItem().toString();
            String proposito = "Propósito de la reservación"; // Puedes agregar un campo en el formulario para obtener este valor
            String status = "pending"; // Puedes establecer el estado inicial de la reservación
            String tipo = "tipo de uso"; // Puedes agregar un campo en el formulario para obtener este valor

            // Obtener IDs de las tablas relacionadas
            int idLab = obtenerIdLaboratorio(laboratorio);
            int idSchedule = insertarSchedule(date, horaInicio, horaFin); // Inserta y obtiene el ID del nuevo horario

            // Insertar la reservación en la base de datos
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, usuario, contrasena);
            String query = "INSERT INTO RESERVATION (id_user, id_lab, id_schedule, purpose, status, type) VALUES (?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, obtenerIdUsuario(nombre_usuario)); // Obtener el ID del usuario actual
            ps.setInt(2, idLab);
            ps.setInt(3, idSchedule);
            ps.setString(4, proposito);
            ps.setString(5, status);
            ps.setString(6, tipo);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            int idReservation = -1;
            if (rs.next()) {
                idReservation = rs.getInt(1);
            }

            // Insertar los materiales asociados a la reservación en la tabla RESERVATION_MATERIAL
            for (int i = 0; i < materialModel.getRowCount(); i++) {
                String material = materialModel.getValueAt(i, 0).toString();
                int idMaterial = obtenerIdMaterial(material);
                insertarReservationMaterial(idReservation, idMaterial, 1); // Puedes ajustar la cantidad según sea necesario
            }

            JOptionPane.showMessageDialog(this, "Reservación guardada exitosamente");
            Reservation.setVisible(false);
            mostrarReservaciones(); // Actualizar la tabla de reservaciones
        } catch (Exception e) {
            System.err.println("Error al guardar la reservación: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_btnHacerReservacionActionPerformed

    private void opcionMenuSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcionMenuSalirActionPerformed
        System.exit(0);
    }//GEN-LAST:event_opcionMenuSalirActionPerformed

    private void opcionMenuCerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcionMenuCerrarSesionActionPerformed
        LoginWindow login = new LoginWindow();
        login.setVisible(true);
        login.setLocationRelativeTo(null);
        dispose();
    }//GEN-LAST:event_opcionMenuCerrarSesionActionPerformed

    private void opcionAgregarReservacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcionAgregarReservacionActionPerformed
        Reservation.setVisible(true);
        Reservation.setSize(780, 475);
        Reservation.setLocationRelativeTo(this);
        
    }//GEN-LAST:event_opcionAgregarReservacionActionPerformed

    private void btnAgregarMaterialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarMaterialActionPerformed
        String material = cboMaterial.getSelectedItem().toString();
        DefaultTableModel model = (DefaultTableModel) tablaMateriales.getModel();
        model.addRow(new Object[]{material});
    }//GEN-LAST:event_btnAgregarMaterialActionPerformed

    private void btnEliminarElementoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarElementoActionPerformed
        int selectedRow = tablaMateriales.getSelectedRow();
        if (selectedRow != -1) {
            DefaultTableModel model = (DefaultTableModel) tablaMateriales.getModel();
            model.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Selecciona un material para eliminar", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnEliminarElementoActionPerformed

    private void btnExportarPDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportarPDFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnExportarPDFActionPerformed

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
            java.util.logging.Logger.getLogger(UserDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UserDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UserDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UserDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new UserDashboard(null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog Reservation;
    private javax.swing.JButton btnAgregarMaterial;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnEliminarElemento;
    private javax.swing.JButton btnEliminarReservación;
    private javax.swing.JButton btnExportarPDF;
    private javax.swing.JButton btnHacerReservacion;
    private javax.swing.JButton btnVerDetalles;
    private javax.swing.JComboBox<String> cboHorasF;
    private javax.swing.JComboBox<String> cboHorasI;
    private javax.swing.JComboBox<String> cboLaboratorios;
    private javax.swing.JComboBox<String> cboMaterial;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblSelectHora;
    private javax.swing.JLabel lblSelectLaboratorio;
    private javax.swing.JLabel lblSelectMateriales;
    private javax.swing.JMenu menuAyuda;
    private javax.swing.JMenuBar menuBarraUsuario;
    private javax.swing.JMenu menuOpciones;
    private javax.swing.JMenu menuPreferencias;
    private javax.swing.JMenu menuSalir;
    private javax.swing.JMenuItem opcionAgregarReservacion;
    private javax.swing.JMenuItem opcionAyuda;
    private javax.swing.JRadioButtonMenuItem opcionClaro;
    private javax.swing.JMenuItem opcionMenuCerrarSesion;
    private javax.swing.JMenuItem opcionMenuSalir;
    private javax.swing.JCheckBoxMenuItem opcionOscuro;
    private javax.swing.JMenu subMenuApariencia;
    private javax.swing.JTable tablaHorarios;
    private javax.swing.JTable tablaMateriales;
    private javax.swing.JLabel txtBievenido;
    private javax.swing.JTextField txtDate;
    // End of variables declaration//GEN-END:variables
}
