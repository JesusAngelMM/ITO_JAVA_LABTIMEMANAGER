package Windows;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Desktop;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.lang.ClassNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


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
        txtBievenida.setText("!Bienvenido " + nombre_usuario + "!");
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

        String query = "SELECT R.id_reservation, L.name as lab_name, S.date, S.start_time, S.end_time, R.purpose, R.status, R.type "
                + "FROM RESERVATION R "
                + "JOIN LABORATORY L ON R.id_lab = L.id_lab "
                + "JOIN SCHEDULE S ON R.id_schedule = S.id_schedule "
                + "WHERE R.id_user = (SELECT id_user FROM USER WHERE username = ?) "
                + "ORDER BY L.name, S.date";

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

    private boolean existeReservacion(String date, String startTime, String endTime, int idLab) throws SQLException, ClassNotFoundException {
        boolean existe = false;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, usuario, contrasena);
            String query = "SELECT COUNT(*) FROM RESERVATION R " +
                           "JOIN SCHEDULE S ON R.id_schedule = S.id_schedule " +
                           "WHERE S.date = ? AND S.start_time = ? AND S.end_time = ? AND R.id_lab = ?";
            ps = conn.prepareStatement(query);
            ps.setString(1, date);
            ps.setString(2, startTime);
            ps.setString(3, endTime);
            ps.setInt(4, idLab);
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
    
    private void actualizarTablaHorariosSemana(String selectedDate) {
        DefaultTableModel modelo = (DefaultTableModel) tablaHorariosSemana.getModel();
        modelo.setRowCount(0); // Limpiar la tabla antes de agregar nuevas filas

        String[] horas = { "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00" };
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, usuario, contrasena);
            for (int i = 0; i < horas.length - 1; i++) {
                String horaInicio = horas[i];
                String horaFin = horas[i + 1];
                String query = "SELECT COUNT(*) FROM RESERVATION R " +
                               "JOIN SCHEDULE S ON R.id_schedule = S.id_schedule " +
                               "WHERE S.date = ? AND S.start_time = ? AND S.end_time = ?";
                ps = conn.prepareStatement(query);
                ps.setString(1, selectedDate);
                ps.setString(2, horaInicio);
                ps.setString(3, horaFin);
                rs = ps.executeQuery();

                String estatus = "Libre";
                if (rs.next() && rs.getInt(1) > 0) {
                    estatus = "Ocupado";
                }

                modelo.addRow(new Object[]{horaInicio, horaFin, estatus});
            }
            conn.close();
        } catch (Exception e) {
            System.err.println("Error al actualizar los horarios: " + e.getMessage());
        }
    }
    
    private boolean validarHoras(String horaInicio, String horaFin) {
        String[] horas = {"07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00"};
        int indexInicio = -1;
        int indexFin = -1;

        for (int i = 0; i < horas.length; i++) {
            if (horaInicio.equals(horas[i])) {
                indexInicio = i;
            }
            if (horaFin.equals(horas[i])) {
                indexFin = i;
            }
        }

        if (indexInicio == -1 || indexFin == -1) {
            return false;
        }

        int diferencia = indexFin - indexInicio;
        return diferencia > 0 && diferencia <= 2;
    }

    private void initValidationListeners() {
        cboHorasI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                validarSeleccionHoras();
            }
        });

        cboHorasF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                validarSeleccionHoras();
            }
        });
    }

    private void validarSeleccionHoras() {
        String horaInicio = cboHorasI.getSelectedItem().toString();
        String horaFin = cboHorasF.getSelectedItem().toString();

        if (!validarHoras(horaInicio, horaFin)) {
            JOptionPane.showMessageDialog(this, "La hora de fin debe ser mayor que la hora de inicio y el rango máximo debe ser de 2 horas.", "Error", JOptionPane.ERROR_MESSAGE);
            cboHorasF.setSelectedIndex(cboHorasI.getSelectedIndex() + 1);
        }
    }
    
    


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        Reservation = new javax.swing.JDialog();
        panelReservation = new javax.swing.JPanel();
        btnAgregarMaterial = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        cboHorasF = new javax.swing.JComboBox<>();
        cboType = new javax.swing.JComboBox<>();
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
        lblSelectMateriales = new javax.swing.JLabel();
        btnEliminarElemento = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        cboMaterial = new javax.swing.JComboBox<>();
        calendario = new com.toedter.calendar.JCalendar();
        lblSelectHora = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtPurpose = new javax.swing.JTextArea();
        cboLaboratorios = new javax.swing.JComboBox<>();
        lblDate = new javax.swing.JLabel();
        Schedules = new javax.swing.JDialog();
        panelFecha = new javax.swing.JPanel();
        panelHorario = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tablaHorariosSemana = new javax.swing.JTable();
        calendarioHorarios = new com.toedter.calendar.JCalendar();
        grupoBotonesUser = new javax.swing.ButtonGroup();
        panelPadre = new javax.swing.JPanel();
        panelBienvenida = new javax.swing.JPanel();
        txtBievenida = new javax.swing.JLabel();
        panelEstatus = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        panelTabla = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaHorarios = new javax.swing.JTable();
        btnEliminarReservación = new javax.swing.JButton();
        menuBarraUsuario = new javax.swing.JMenuBar();
        menuSalir = new javax.swing.JMenu();
        opcionMenuCerrarSesion = new javax.swing.JMenuItem();
        opcionMenuSalir = new javax.swing.JMenuItem();
        menuPreferencias = new javax.swing.JMenu();
        subMenuApariencia = new javax.swing.JMenu();
        opcionClaro = new javax.swing.JRadioButtonMenuItem();
        opcionOscuro = new javax.swing.JRadioButtonMenuItem();
        menuOpciones = new javax.swing.JMenu();
        opcionAgregarReservacion = new javax.swing.JMenuItem();
        opcionVerReservaciones = new javax.swing.JMenuItem();
        menuAyuda = new javax.swing.JMenu();
        opcionAyuda = new javax.swing.JMenuItem();

        Reservation.setTitle("Crear una reservación");
        Reservation.setResizable(false);

        btnAgregarMaterial.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/aceptar.png"))); // NOI18N
        btnAgregarMaterial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarMaterialActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel19.setText("Tipo:");

        cboHorasF.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00" }));
        cboHorasF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboHorasFActionPerformed(evt);
            }
        });

        cboType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Práctica", "Clase", "Recorrido", "Mantenimiento" }));

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
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        tablaMateriales.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Material", "Cantidad"
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
        jLabel10.setText("Proposito:");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("Crear una nueva reservación");

        jLabel12.setText("Guardar");

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("a");

        jLabel14.setText("Exportar");

        lblSelectLaboratorio.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblSelectLaboratorio.setText("Seleccionar laboratorio:");

        lblSelectMateriales.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblSelectMateriales.setText("Seleccionar material:");

        btnEliminarElemento.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/papelera.png"))); // NOI18N
        btnEliminarElemento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarElementoActionPerformed(evt);
            }
        });

        jLabel9.setText("Cancelar");

        cboMaterial.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboMaterial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboMaterialActionPerformed(evt);
            }
        });

        calendario.setPreferredSize(new java.awt.Dimension(250, 185));
        calendario.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                calendarioPropertyChange(evt);
            }
        });

        lblSelectHora.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblSelectHora.setText("Seleccionar Fecha:");

        txtPurpose.setColumns(20);
        txtPurpose.setRows(5);
        jScrollPane3.setViewportView(txtPurpose);

        cboLaboratorios.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Laboratorio de Fisicoquímica", "Laboratorio de Ing. Civil", "Laboratorio de Ing. Eléctrica", "Laboratorio de Ing. Industrial", "Laboratorio de Ing. Química e Ing. Mecánica Pesada", "Laboratorio de Simulación" }));

        lblDate.setText("0000-00-00");

        javax.swing.GroupLayout panelReservationLayout = new javax.swing.GroupLayout(panelReservation);
        panelReservation.setLayout(panelReservationLayout);
        panelReservationLayout.setHorizontalGroup(
            panelReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelReservationLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(panelReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelReservationLayout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelReservationLayout.createSequentialGroup()
                        .addGroup(panelReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelReservationLayout.createSequentialGroup()
                                .addComponent(lblSelectLaboratorio)
                                .addGap(18, 18, 18)
                                .addComponent(cboLaboratorios, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelReservationLayout.createSequentialGroup()
                                .addGroup(panelReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel19)
                                    .addComponent(cboType, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(panelReservationLayout.createSequentialGroup()
                                        .addComponent(cboHorasI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(5, 5, 5)
                                        .addComponent(cboHorasF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(lblDate, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(calendario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblSelectHora)
                                    .addGroup(panelReservationLayout.createSequentialGroup()
                                        .addComponent(jLabel12)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(btnHacerReservacion, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel14)
                                        .addGap(12, 12, 12)
                                        .addComponent(btnExportarPDF, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(30, 30, 30)
                                        .addComponent(jLabel9)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 156, Short.MAX_VALUE)
                                .addGroup(panelReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                    .addGroup(panelReservationLayout.createSequentialGroup()
                                        .addComponent(cboMaterial, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(36, 36, 36)
                                        .addComponent(btnAgregarMaterial, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnEliminarElemento, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(lblSelectMateriales, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING))))
                        .addGap(27, 27, 27))))
        );
        panelReservationLayout.setVerticalGroup(
            panelReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelReservationLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addGap(29, 29, 29)
                .addGroup(panelReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSelectLaboratorio)
                    .addComponent(cboLaboratorios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(42, 42, 42)
                .addGroup(panelReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelReservationLayout.createSequentialGroup()
                        .addComponent(lblSelectHora)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(calendario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblDate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panelReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(cboHorasI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cboHorasF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel13)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel19)
                        .addGap(12, 12, 12)
                        .addComponent(cboType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(panelReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnExportarPDF)
                            .addComponent(jLabel9)
                            .addComponent(jLabel12)
                            .addComponent(btnHacerReservacion)
                            .addComponent(btnCancelar)
                            .addComponent(jLabel14))
                        .addGap(17, 17, 17))
                    .addGroup(panelReservationLayout.createSequentialGroup()
                        .addGroup(panelReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panelReservationLayout.createSequentialGroup()
                                .addComponent(lblSelectMateriales)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboMaterial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnEliminarElemento)
                            .addComponent(btnAgregarMaterial))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout ReservationLayout = new javax.swing.GroupLayout(Reservation.getContentPane());
        Reservation.getContentPane().setLayout(ReservationLayout);
        ReservationLayout.setHorizontalGroup(
            ReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelReservation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        ReservationLayout.setVerticalGroup(
            ReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ReservationLayout.createSequentialGroup()
                .addComponent(panelReservation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 6, Short.MAX_VALUE))
        );

        Schedules.setTitle("Ver horarios");
        Schedules.setResizable(false);

        panelFecha.setLayout(new javax.swing.BoxLayout(panelFecha, javax.swing.BoxLayout.LINE_AXIS));

        panelHorario.setLayout(new javax.swing.BoxLayout(panelHorario, javax.swing.BoxLayout.LINE_AXIS));

        tablaHorariosSemana.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Hora de inicio", "Hora de finalización", "Estatus"
            }
        ));
        jScrollPane4.setViewportView(tablaHorariosSemana);

        calendarioHorarios.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                calendarioHorariosPropertyChange(evt);
            }
        });

        javax.swing.GroupLayout SchedulesLayout = new javax.swing.GroupLayout(Schedules.getContentPane());
        Schedules.getContentPane().setLayout(SchedulesLayout);
        SchedulesLayout.setHorizontalGroup(
            SchedulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SchedulesLayout.createSequentialGroup()
                .addComponent(panelFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(calendarioHorarios, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelHorario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );
        SchedulesLayout.setVerticalGroup(
            SchedulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelFecha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelHorario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(SchedulesLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(SchedulesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(calendarioHorarios, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        panelPadre.setLayout(new javax.swing.BoxLayout(panelPadre, javax.swing.BoxLayout.Y_AXIS));

        txtBievenida.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtBievenida.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtBievenida.setText("¡Bienvenido");
        panelBienvenida.add(txtBievenida);

        panelPadre.add(panelBienvenida);

        panelEstatus.setLayout(new java.awt.GridBagLayout());

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/usuario_normal.png"))); // NOI18N
        panelEstatus.add(jLabel3, new java.awt.GridBagConstraints());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setText("Estatus del usuario");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        panelEstatus.add(jLabel1, gridBagConstraints);

        jLabel2.setText("Nombre: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        panelEstatus.add(jLabel2, gridBagConstraints);

        jLabel7.setText("Rol: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        panelEstatus.add(jLabel7, gridBagConstraints);

        jLabel6.setText("Correo: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        panelEstatus.add(jLabel6, gridBagConstraints);

        jLabel8.setText("Departamento: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        panelEstatus.add(jLabel8, gridBagConstraints);

        panelPadre.add(panelEstatus);

        panelTabla.setLayout(new java.awt.BorderLayout(1, 1));

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

        panelTabla.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        btnEliminarReservación.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/papelera.png"))); // NOI18N
        btnEliminarReservación.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarReservaciónActionPerformed(evt);
            }
        });
        panelTabla.add(btnEliminarReservación, java.awt.BorderLayout.PAGE_END);

        panelPadre.add(panelTabla);

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

        grupoBotonesUser.add(opcionClaro);
        opcionClaro.setSelected(true);
        opcionClaro.setText("Claro");
        opcionClaro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcionClaroActionPerformed(evt);
            }
        });
        subMenuApariencia.add(opcionClaro);

        grupoBotonesUser.add(opcionOscuro);
        opcionOscuro.setText("Oscuro");
        opcionOscuro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcionOscuroActionPerformed(evt);
            }
        });
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

        opcionVerReservaciones.setText("Ver Reservaciones");
        opcionVerReservaciones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcionVerReservacionesActionPerformed(evt);
            }
        });
        menuOpciones.add(opcionVerReservaciones);

        menuBarraUsuario.add(menuOpciones);

        menuAyuda.setText("Ayuda");
        menuAyuda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAyudaActionPerformed(evt);
            }
        });

        opcionAyuda.setText("Manual y Documentación");
        opcionAyuda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcionAyudaActionPerformed(evt);
            }
        });
        menuAyuda.add(opcionAyuda);

        menuBarraUsuario.add(menuAyuda);

        setJMenuBar(menuBarraUsuario);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelPadre, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelPadre, javax.swing.GroupLayout.DEFAULT_SIZE, 537, Short.MAX_VALUE)
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
            String date = lblDate.getText();
            String horaInicio = cboHorasI.getSelectedItem().toString();
            String horaFin = cboHorasF.getSelectedItem().toString();
            String proposito = txtPurpose.getText();
            String status = "confirmed";
            String tipo = cboType.getSelectedItem().toString();

            // Validar que la hora de fin sea mayor que la hora de inicio y que el rango sea máximo de 2 horas
            if (!validarHoras(horaInicio, horaFin)) {
                JOptionPane.showMessageDialog(this, "La hora de fin debe ser mayor que la hora de inicio y el rango máximo debe ser de 2 horas.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Obtener IDs de las tablas relacionadas
            int idLab = obtenerIdLaboratorio(laboratorio);

            // Validar si ya existe una reservación en esa fecha y hora
            if (existeReservacion(date, horaInicio, horaFin, idLab)) {
                JOptionPane.showMessageDialog(this, "Ya existe una reservación en esa fecha y hora para este laboratorio", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int idSchedule = insertarSchedule(date, horaInicio, horaFin); // Inserta y obtiene el ID del nuevo horario

            // Insertar la reservación en la base de datos
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, usuario, contrasena);
            String query = "INSERT INTO RESERVATION (id_user, id_lab, id_schedule, purpose, status, type) VALUES (?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, obtenerIdUsuario(nombre_usuario));
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
                String material = (String) materialModel.getValueAt(i, 0);
                String quantityStr = materialModel.getValueAt(i, 1) != null ? materialModel.getValueAt(i, 1).toString() : "0";
                int idMaterial = obtenerIdMaterial(material);
                int quantity = Integer.parseInt(quantityStr); // Leer la cantidad de materiales

                // Verificar si el material ya está asociado a la reservación para evitar duplicados
                if (!existeReservaMaterial(idReservation, idMaterial)) {
                    insertarReservationMaterial(idReservation, idMaterial, quantity); // Ajustar la cantidad según sea necesario
                }
            }

            JOptionPane.showMessageDialog(this, "Reservación guardada exitosamente");
            Reservation.setVisible(false);
            mostrarReservaciones(); // Actualizar la tabla de reservaciones
        } catch (Exception e) {
            System.err.println("Error al guardar la reservación: " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
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
        // Limpiar los datos del formulario
        cboLaboratorios.setSelectedIndex(0);
        cboHorasI.setSelectedIndex(0);
        cboHorasF.setSelectedIndex(0);
        cboType.setSelectedIndex(0);
        lblDate.setText("");
        txtPurpose.setText("");
        DefaultTableModel materialModel = (DefaultTableModel) tablaMateriales.getModel();
        materialModel.setRowCount(0);

        // Mostrar el diálogo de reservación
        Reservation.setVisible(true);
        Reservation.setSize(780, 600);
        Reservation.setLocationRelativeTo(this);
    }//GEN-LAST:event_opcionAgregarReservacionActionPerformed

    private void btnAgregarMaterialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarMaterialActionPerformed
        String material = cboMaterial.getSelectedItem().toString();
        DefaultTableModel model = (DefaultTableModel) tablaMateriales.getModel();

        // Verificar si el material ya está en la lista
        boolean materialExistente = false;
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).toString().equals(material)) {
                materialExistente = true;
                break;
            }
        }

        // Agregar el material solo si no está en la lista
        if (!materialExistente) {
            model.addRow(new Object[]{material});
        } else {
            JOptionPane.showMessageDialog(this, "El material ya está en la lista", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
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

    private void cboMaterialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboMaterialActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboMaterialActionPerformed

    private void btnEliminarReservaciónActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarReservaciónActionPerformed
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            int filaSeleccionada = tablaHorarios.getSelectedRow();
            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(this, "Selecciona una reservación para eliminar", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Obtener el ID de la reservación seleccionada
            String idReservacion = tablaHorarios.getValueAt(filaSeleccionada, 0).toString();

            // Confirmar eliminación
            int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que deseas eliminar esta reservación?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            // Conectar a la base de datos
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, usuario, contrasena);

            // Eliminar registros de RESERVATION_MATERIAL relacionados con la reservación
            String queryEliminarMateriales = "DELETE FROM RESERVATION_MATERIAL WHERE id_reservation = ?";
            ps = conn.prepareStatement(queryEliminarMateriales);
            ps.setInt(1, Integer.parseInt(idReservacion));
            ps.executeUpdate();
            ps.close();

            // Eliminar la reservación
            String queryEliminarReservacion = "DELETE FROM RESERVATION WHERE id_reservation = ?";
            ps = conn.prepareStatement(queryEliminarReservacion);
            ps.setInt(1, Integer.parseInt(idReservacion));
            ps.executeUpdate();
            ps.close();

            // Mostrar mensaje de éxito y actualizar la tabla de reservaciones
            JOptionPane.showMessageDialog(this, "Reservación eliminada exitosamente");
            mostrarReservaciones();
        } catch (Exception e) {
            System.err.println("Error al eliminar la reservación: " + e.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_btnEliminarReservaciónActionPerformed

    private void calendarioPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_calendarioPropertyChange
        calendario.getDayChooser().addPropertyChangeListener("day", new java.beans.PropertyChangeListener() {
            @Override
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                if(evt.getOldValue() != null){
                    SimpleDateFormat ff = new SimpleDateFormat("yyyy-MM-dd");
                    lblDate.setText(ff.format(calendario.getDate()));
                }
            }
        });
    }//GEN-LAST:event_calendarioPropertyChange

    private void opcionVerReservacionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcionVerReservacionesActionPerformed
        Schedules.setSize(860,260);
        Schedules.setVisible(true);
        Schedules.setLocationRelativeTo(null);
    }//GEN-LAST:event_opcionVerReservacionesActionPerformed

    private void calendarioHorariosPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_calendarioHorariosPropertyChange
        calendarioHorarios.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                calendarioHorariosPropertyChange(evt);
            }
        });

        if ("calendar".equals(evt.getPropertyName())) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String selectedDate = sdf.format(calendarioHorarios.getDate());
            actualizarTablaHorariosSemana(selectedDate);
        }
    }//GEN-LAST:event_calendarioHorariosPropertyChange

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        Reservation.dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void menuAyudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAyudaActionPerformed

    }//GEN-LAST:event_menuAyudaActionPerformed

    private void opcionAyudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcionAyudaActionPerformed
        try {
            try {
                Desktop.getDesktop().browse(new URI("https://github.com/JesusAngelMM/ITO_JAVA_LABTIMEMANAGER.git"));
            } catch (IOException ex) {
                Logger.getLogger(UserDashboard.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (URISyntaxException ex) {
            Logger.getLogger(UserDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_opcionAyudaActionPerformed

    private void opcionClaroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcionClaroActionPerformed
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            SwingUtilities.updateComponentTreeUI(this); // Actualiza el aspecto de todos los componentes
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_opcionClaroActionPerformed

    private void opcionOscuroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcionOscuroActionPerformed
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
            SwingUtilities.updateComponentTreeUI(this); // Actualiza el aspecto de todos los componentes
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_opcionOscuroActionPerformed
    
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
    private javax.swing.JDialog Schedules;
    private javax.swing.JButton btnAgregarMaterial;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnEliminarElemento;
    private javax.swing.JButton btnEliminarReservación;
    private javax.swing.JButton btnExportarPDF;
    private javax.swing.JButton btnHacerReservacion;
    private com.toedter.calendar.JCalendar calendario;
    private com.toedter.calendar.JCalendar calendarioHorarios;
    private javax.swing.JComboBox<String> cboHorasF;
    private javax.swing.JComboBox<String> cboHorasI;
    private javax.swing.JComboBox<String> cboLaboratorios;
    private javax.swing.JComboBox<String> cboMaterial;
    private javax.swing.JComboBox<String> cboType;
    private javax.swing.ButtonGroup grupoBotonesUser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblDate;
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
    private javax.swing.JRadioButtonMenuItem opcionOscuro;
    private javax.swing.JMenuItem opcionVerReservaciones;
    private javax.swing.JPanel panelBienvenida;
    private javax.swing.JPanel panelEstatus;
    private javax.swing.JPanel panelFecha;
    private javax.swing.JPanel panelHorario;
    private javax.swing.JPanel panelPadre;
    private javax.swing.JPanel panelReservation;
    private javax.swing.JPanel panelTabla;
    private javax.swing.JMenu subMenuApariencia;
    private javax.swing.JTable tablaHorarios;
    private javax.swing.JTable tablaHorariosSemana;
    private javax.swing.JTable tablaMateriales;
    private javax.swing.JLabel txtBievenida;
    private javax.swing.JTextArea txtPurpose;
    // End of variables declaration//GEN-END:variables
}
