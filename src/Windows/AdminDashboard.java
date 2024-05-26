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
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


public class AdminDashboard extends javax.swing.JFrame {

    String nombre_usuario;
    
    private static String URL;
    private static String usuario;
    private static String contrasena;
    private static final String PROPERTIES_FILE_PATH = "config.properties";
    
    PreparedStatement ps;
    ResultSet rs;
    
    public AdminDashboard(String nombre_usuario) {
        this.nombre_usuario = nombre_usuario;
        initComponents();
        loadProperties();
        txtBievenida.setText("!Bienvenido " + nombre_usuario + "!");
        mostrarEstatus();
        rellenarComboBoxMateriales();
        agregarListenerTablaU();  // Listener para la tabla de usuarios
        agregarListenerTablaM();  // Listener para la tabla de materiales
        agregarListenerTablaL(); // Añadir el listener a la tabla de laboratorios
        agregarHorasCombo();
    }
    
    private void loadProperties() {
        ConfigLoader configLoader = new ConfigLoader(PROPERTIES_FILE_PATH);
        URL = configLoader.getProperty("db.url");
        usuario = configLoader.getProperty("db.user");
        contrasena = configLoader.getProperty("db.password");
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

        String[] horas = {"07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00"};
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, usuario, contrasena);
            for (int i = 0; i < horas.length - 1; i++) {
                String horaInicio = horas[i];
                String horaFin = horas[i + 1];
                String query = "SELECT L.name AS lab_name "
                        + "FROM RESERVATION R "
                        + "JOIN SCHEDULE S ON R.id_schedule = S.id_schedule "
                        + "JOIN LABORATORY L ON R.id_lab = L.id_lab "
                        + "WHERE S.date = ? AND S.start_time = ? AND S.end_time = ?";
                ps = conn.prepareStatement(query);
                ps.setString(1, selectedDate);
                ps.setString(2, horaInicio);
                ps.setString(3, horaFin);
                rs = ps.executeQuery();

                boolean isOcupado = false;

                while (rs.next()) {
                    String labName = rs.getString("lab_name");
                    modelo.addRow(new Object[]{horaInicio, horaFin, "Ocupado", labName});
                    isOcupado = true;
                }

                if (!isOcupado) {
                    modelo.addRow(new Object[]{horaInicio, horaFin, "Libre", ""});
                }
            }
            conn.close();
        } catch (Exception e) {
            System.err.println("Error al actualizar los horarios: " + e.getMessage());
        }
    }
    
    //Cargar usuarios en tabla U
    private void cargarUsuariosEnTabla() {
        DefaultTableModel modelo = (DefaultTableModel) tablaU.getModel();
        modelo.setRowCount(0); // Limpiar la tabla antes de agregar nuevas filas

        String query = "SELECT id_user, username, email, password, role, department FROM USER";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, usuario, contrasena);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id_user");
                String username = rs.getString("username");
                String email = rs.getString("email");
                String password = rs.getString("password");
                String role = rs.getString("role");
                String department = rs.getString("department");

                modelo.addRow(new Object[]{id, username, email, password, role, department});
            }
            conn.close();
        } catch (Exception e) {
            System.err.println("Error al cargar los usuarios: " + e.getMessage());
        }
    }
    
    //Listener a tabla U
    private void agregarListenerTablaU() {
        tablaU.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int selectedRow = tablaU.getSelectedRow();
                if (selectedRow != -1) {
                    DefaultTableModel model = (DefaultTableModel) tablaU.getModel();

                    int id = (int) model.getValueAt(selectedRow, 0);
                    String username = model.getValueAt(selectedRow, 1).toString();
                    String email = model.getValueAt(selectedRow, 2).toString();
                    String password = model.getValueAt(selectedRow, 3).toString();
                    String role = model.getValueAt(selectedRow, 4).toString();
                    String department = model.getValueAt(selectedRow, 5).toString();

                    txtUId.setText(String.valueOf(id));
                    txtUName.setText(username);
                    txtUMail.setText(email);
                    txtUPassword.setText(password);
                    cboURole.setSelectedItem(role);
                    cboUDepartment.setSelectedItem(department);
                }
            }
        });
    }
    
    private void limpiarCamposUsuario() {
        txtUId.setText("");
        txtUName.setText("");
        txtUPassword.setText("");
        txtUMail.setText("");
        cboURole.setSelectedIndex(0);
        cboUDepartment.setSelectedIndex(0);
    }

    //DIALOGO MATERIALES
    private void cargarMaterialesEnTabla() {
        DefaultTableModel modelo = (DefaultTableModel) tablaM.getModel();
        modelo.setRowCount(0); // Limpiar la tabla antes de agregar nuevas filas

        String query = "SELECT id_material, name, quantity, id_lab FROM MATERIAL";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, usuario, contrasena);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id_material");
                String name = rs.getString("name");
                int quantity = rs.getInt("quantity");
                int idLab = rs.getInt("id_lab");

                modelo.addRow(new Object[]{id, name, quantity, idLab});
            }
            conn.close();
        } catch (Exception e) {
            System.err.println("Error al cargar los materiales: " + e.getMessage());
        }
    }

    private void agregarListenerTablaM() {
        tablaM.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int selectedRow = tablaM.getSelectedRow();
                if (selectedRow != -1) {
                    DefaultTableModel model = (DefaultTableModel) tablaM.getModel();

                    int id = (int) model.getValueAt(selectedRow, 0);
                    String name = model.getValueAt(selectedRow, 1).toString();
                    int quantity = (int) model.getValueAt(selectedRow, 2);
                    int idLab = (int) model.getValueAt(selectedRow, 3);

                    txtMId.setText(String.valueOf(id));
                    txtMName.setText(name);
                    txtMQuantity.setText(String.valueOf(quantity));
                    txtMIdLab.setText(String.valueOf(idLab));
                }
            }
        });
    }

    private void limpiarCamposMaterial() {
        txtMId.setText("");
        txtMName.setText("");
        txtMQuantity.setText("");
        txtMIdLab.setText("");
    }
    
    // Método para verificar si un id_lab existe en la tabla LABORATORY
    private boolean idLabExiste(int idLab) {
        boolean existe = false;
        String query = "SELECT COUNT(*) FROM LABORATORY WHERE id_lab = ?";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, usuario, contrasena);
            ps = conn.prepareStatement(query);
            ps.setInt(1, idLab);
            rs = ps.executeQuery();
            if (rs.next()) {
                existe = rs.getInt(1) > 0;
            }
            conn.close();
        } catch (Exception e) {
            System.err.println("Error al verificar el id_lab: " + e.getMessage());
        }
        return existe;
    }
    
    //DIALOGO LABORATORIOS
    private void cargarLaboratoriosEnTabla() {
        DefaultTableModel modelo = (DefaultTableModel) tablaL.getModel();
        modelo.setRowCount(0); // Limpiar la tabla antes de agregar nuevas filas

        String query = "SELECT id_lab, name, location, capacity, type FROM LABORATORY";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, usuario, contrasena);
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id_lab");
                String name = rs.getString("name");
                String location = rs.getString("location");
                int capacity = rs.getInt("capacity");
                String type = rs.getString("type");

                modelo.addRow(new Object[]{id, name, location, capacity, type});
            }
            conn.close();
        } catch (Exception e) {
            System.err.println("Error al cargar los laboratorios: " + e.getMessage());
        }
    }

    private void agregarListenerTablaL() {
        tablaL.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            int selectedRow = tablaL.getSelectedRow();
            if (selectedRow != -1) {
                DefaultTableModel model = (DefaultTableModel) tablaL.getModel();

                int id = (int) model.getValueAt(selectedRow, 0);
                String name = model.getValueAt(selectedRow, 1).toString();
                String location = model.getValueAt(selectedRow, 2).toString();
                int capacity = (int) model.getValueAt(selectedRow, 3);
                String type = model.getValueAt(selectedRow, 4).toString();

                txtLId.setText(String.valueOf(id));
                txtLName.setText(name);
                txtLLocation.setText(location);
                txtLCapacity.setText(String.valueOf(capacity));
                cboLType.setSelectedItem(type);
            }
        }
    });
    }

    private void limpiarCamposLaboratorio() {
        txtLId.setText("");
        txtLName.setText("");
        txtLLocation.setText("");
        txtLCapacity.setText("");
        cboLType.setSelectedIndex(0);
    }
    
    private void rellenarComboBoxTiposLaboratorios() {
        cboLType.removeAllItems();
        cboLType.addItem("Química");
        cboLType.addItem("Física");
        cboLType.addItem("Biología");
        cboLType.addItem("Informática");
        cboLType.addItem("Electrónica");
        // Añadir más tipos según sea necesario
    }
    
    //DIALOGO RESERVACION
    private String obtenerNombreLaboratorio(int idLab) throws SQLException, ClassNotFoundException {
        String nombreLaboratorio = "";
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection(URL, usuario, contrasena);
        String query = "SELECT name FROM LABORATORY WHERE id_lab = ?";
        ps = conn.prepareStatement(query);
        ps.setInt(1, idLab);
        rs = ps.executeQuery();
        if (rs.next()) {
            nombreLaboratorio = rs.getString("name");
        }
        conn.close();
        return nombreLaboratorio;
    }
    
    private void agregarHorasCombo(){
        // Añadir opciones a los combo boxes
        String[] horas = { "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00" };
        for (String hora : horas) {
            cboHorasI.addItem(hora);
            cboHorasF.addItem(hora);
        }
    }
      
    private boolean existeOtraReservacion(String date, String startTime, String endTime, int idLab, int idReservacion) throws SQLException, ClassNotFoundException {
        boolean existe = false;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, usuario, contrasena);
            String query = "SELECT COUNT(*) FROM RESERVATION R "
                    + "JOIN SCHEDULE S ON R.id_schedule = S.id_schedule "
                    + "WHERE S.date = ? AND S.start_time = ? AND S.end_time = ? AND R.id_lab = ? AND R.id_reservation != ?";
            ps = conn.prepareStatement(query);
            ps.setString(1, date);
            ps.setString(2, startTime);
            ps.setString(3, endTime);
            ps.setInt(4, idLab);
            ps.setInt(5, idReservacion);
            rs = ps.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                existe = true;
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }

        return existe;
    }

    private int obtenerIdSchedule(int idReservacion) throws SQLException, ClassNotFoundException {
        int idSchedule = -1;
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection(URL, usuario, contrasena);
        String query = "SELECT id_schedule FROM RESERVATION WHERE id_reservation = ?";
        ps = conn.prepareStatement(query);
        ps.setInt(1, idReservacion);
        rs = ps.executeQuery();
        if (rs.next()) {
            idSchedule = rs.getInt("id_schedule");
        }
        conn.close();
        return idSchedule;
    }
    
    private void actualizarSchedule(int idSchedule, String date, String startTime, String endTime) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection(URL, usuario, contrasena);
        String query = "UPDATE SCHEDULE SET date = ?, start_time = ?, end_time = ? WHERE id_schedule = ?";
        ps = conn.prepareStatement(query);
        ps.setString(1, date);
        ps.setString(2, startTime);
        ps.setString(3, endTime);
        ps.setInt(4, idSchedule);
        ps.executeUpdate();
        conn.close();
    }

    private void eliminarMaterialesReservacion(int idReservacion) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection(URL, usuario, contrasena);
        String query = "DELETE FROM RESERVATION_MATERIAL WHERE id_reservation = ?";
        ps = conn.prepareStatement(query);
        ps.setInt(1, idReservacion);
        ps.executeUpdate();
        conn.close();
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
        jLabel13 = new javax.swing.JLabel();
        lblSelectLaboratorio = new javax.swing.JLabel();
        lblSelectMateriales = new javax.swing.JLabel();
        btnEliminarElemento = new javax.swing.JButton();
        cboMaterial = new javax.swing.JComboBox<>();
        calendario = new com.toedter.calendar.JCalendar();
        lblSelectHora = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtPurpose = new javax.swing.JTextArea();
        lblDate = new javax.swing.JLabel();
        cboLaboratorios = new javax.swing.JComboBox<>();
        txtIdReservacion = new javax.swing.JTextField();
        btnBuscarReservacion = new javax.swing.JButton();
        btnActualizarReservacion = new javax.swing.JButton();
        btnBorrarReservacion = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        ModifyUsers = new javax.swing.JDialog();
        panelModifyUsers = new javax.swing.JPanel();
        txtUMail = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        cboUDepartment = new javax.swing.JComboBox<>();
        btnUInsertar = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        btnUModificar = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        btnUEliminar = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        btnULimpiar = new javax.swing.JButton();
        txtUId = new javax.swing.JTextField();
        txtUName = new javax.swing.JTextField();
        btnUBuscar = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txtUPassword = new javax.swing.JPasswordField();
        cboURole = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaU = new javax.swing.JTable();
        cboOpcionesBusquedaU = new javax.swing.JComboBox<>();
        jLabel35 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        ModifyLabs = new javax.swing.JDialog();
        panelModifyLabs = new javax.swing.JPanel();
        txtLCapacity = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        btnLInsertar = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();
        btnLModificar = new javax.swing.JButton();
        jLabel25 = new javax.swing.JLabel();
        btnLEliminar = new javax.swing.JButton();
        btnLLimpiar = new javax.swing.JButton();
        txtLId = new javax.swing.JTextField();
        txtLName = new javax.swing.JTextField();
        btnLBuscar = new javax.swing.JButton();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        cboLType = new javax.swing.JComboBox<>();
        jScrollPane7 = new javax.swing.JScrollPane();
        tablaL = new javax.swing.JTable();
        txtLLocation = new javax.swing.JTextField();
        cboOpcionesBusquedaL = new javax.swing.JComboBox<>();
        jLabel36 = new javax.swing.JLabel();
        ModifyMaterials = new javax.swing.JDialog();
        panelModifyMaterials = new javax.swing.JPanel();
        txtMIdLab = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        btnMInsertar = new javax.swing.JButton();
        jLabel30 = new javax.swing.JLabel();
        btnMModificar = new javax.swing.JButton();
        txtMName = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        txtMId = new javax.swing.JTextField();
        btnMBuscar = new javax.swing.JButton();
        btnMEliminar = new javax.swing.JButton();
        btnMLimpiar = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        tablaM = new javax.swing.JTable();
        txtMQuantity = new javax.swing.JTextField();
        cboOpcionesBusquedaM = new javax.swing.JComboBox<>();
        jLabel37 = new javax.swing.JLabel();
        Statitics = new javax.swing.JDialog();
        grupoBotonesAdmin = new javax.swing.ButtonGroup();
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
        jScrollPane4 = new javax.swing.JScrollPane();
        tablaHorariosSemana = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        btnVerDetalles = new javax.swing.JButton();
        panelOpciones = new javax.swing.JPanel();
        calendarioHorarios = new com.toedter.calendar.JCalendar();
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
        opcionModificarUsuarios = new javax.swing.JMenuItem();
        opcionModificarLaboratorios = new javax.swing.JMenuItem();
        opcionModificarMaterial = new javax.swing.JMenuItem();
        opcionEstadisticas = new javax.swing.JMenuItem();
        menuAyuda = new javax.swing.JMenu();
        opcionAyuda = new javax.swing.JMenuItem();

        Reservation.setTitle("Modificar Reservaciones");
        Reservation.setResizable(false);

        panelReservation.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnAgregarMaterial.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/aceptar.png"))); // NOI18N
        btnAgregarMaterial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarMaterialActionPerformed(evt);
            }
        });
        panelReservation.add(btnAgregarMaterial, new org.netbeans.lib.awtextra.AbsoluteConstraints(642, 145, 30, -1));

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel19.setText("Tipo:");
        panelReservation.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 360, -1, -1));

        cboHorasF.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00" }));
        cboHorasF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboHorasFActionPerformed(evt);
            }
        });
        panelReservation.add(cboHorasF, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 370, -1, -1));

        cboType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Práctica", "Clase", "Recorrido", "Mantenimiento" }));
        panelReservation.add(cboType, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 360, 250, -1));

        cboHorasI.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00" }));
        cboHorasI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboHorasIActionPerformed(evt);
            }
        });
        panelReservation.add(cboHorasI, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 370, -1, -1));

        btnHacerReservacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/guardarRegistro.png"))); // NOI18N
        btnHacerReservacion.setText("Guardar");
        btnHacerReservacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHacerReservacionActionPerformed(evt);
            }
        });
        panelReservation.add(btnHacerReservacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 430, 102, -1));

        btnCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/cerrar.png"))); // NOI18N
        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });
        panelReservation.add(btnCancelar, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 480, -1, -1));

        tablaMateriales.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Material", "Cantidad"
            }
        ));
        jScrollPane2.setViewportView(tablaMateriales);

        panelReservation.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(386, 180, 322, 143));

        btnExportarPDF.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/exportar_pdf.png"))); // NOI18N
        btnExportarPDF.setText("Exportar");
        btnExportarPDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportarPDFActionPerformed(evt);
            }
        });
        panelReservation.add(btnExportarPDF, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 430, 96, -1));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel10.setText("Proposito:");
        panelReservation.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 410, -1, -1));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/registro.png"))); // NOI18N
        panelReservation.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 10, -1, -1));

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("a");
        panelReservation.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 370, 26, -1));

        lblSelectLaboratorio.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblSelectLaboratorio.setText("Seleccionar laboratorio:");
        panelReservation.add(lblSelectLaboratorio, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, -1, -1));

        lblSelectMateriales.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblSelectMateriales.setText("Seleccionar material:");
        panelReservation.add(lblSelectMateriales, new org.netbeans.lib.awtextra.AbsoluteConstraints(386, 124, -1, -1));

        btnEliminarElemento.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/papelera.png"))); // NOI18N
        btnEliminarElemento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarElementoActionPerformed(evt);
            }
        });
        panelReservation.add(btnEliminarElemento, new org.netbeans.lib.awtextra.AbsoluteConstraints(678, 145, 30, -1));

        cboMaterial.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboMaterial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboMaterialActionPerformed(evt);
            }
        });
        panelReservation.add(cboMaterial, new org.netbeans.lib.awtextra.AbsoluteConstraints(386, 146, 240, -1));

        calendario.setMinimumSize(new java.awt.Dimension(250, 185));
        calendario.setPreferredSize(new java.awt.Dimension(300, 222));
        calendario.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                calendarioPropertyChange(evt);
            }
        });
        panelReservation.add(calendario, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 146, -1, -1));

        lblSelectHora.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblSelectHora.setText("Seleccionar Fecha:");
        panelReservation.add(lblSelectHora, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 124, -1, -1));

        txtPurpose.setColumns(20);
        txtPurpose.setRows(5);
        jScrollPane3.setViewportView(txtPurpose);

        panelReservation.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 430, 310, 70));

        lblDate.setText("0000-00-00");
        panelReservation.add(lblDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 370, 72, 19));

        cboLaboratorios.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccionar", "Laboratorio de Fïsica", "Laboratorio de Ing. Química", "Laboratorio de Ing. Civil", "Laboratorio de Ing. Eléctrica", "Laboratorio de Ing. Industrial", "Laboratorio de Ing. en Sistemas Computacionales", "Laboratorio de Ing. Mecatrónica", "Laboratorio de Ing. Electrónica" }));
        panelReservation.add(cboLaboratorios, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 80, 532, -1));
        panelReservation.add(txtIdReservacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(395, 19, 80, -1));

        btnBuscarReservacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/buscar.png"))); // NOI18N
        btnBuscarReservacion.setText("Buscar");
        btnBuscarReservacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarReservacionActionPerformed(evt);
            }
        });
        panelReservation.add(btnBuscarReservacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 20, -1, -1));

        btnActualizarReservacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/guardarRegistro.png"))); // NOI18N
        btnActualizarReservacion.setText("Actualizar");
        btnActualizarReservacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarReservacionActionPerformed(evt);
            }
        });
        panelReservation.add(btnActualizarReservacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 480, -1, -1));

        btnBorrarReservacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/eliminarRegistro.png"))); // NOI18N
        btnBorrarReservacion.setText("Borrar");
        btnBorrarReservacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBorrarReservacionActionPerformed(evt);
            }
        });
        panelReservation.add(btnBorrarReservacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 430, -1, -1));

        jLabel9.setText("ID");
        jLabel9.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        panelReservation.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 20, 80, 20));

        javax.swing.GroupLayout ReservationLayout = new javax.swing.GroupLayout(Reservation.getContentPane());
        Reservation.getContentPane().setLayout(ReservationLayout);
        ReservationLayout.setHorizontalGroup(
            ReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelReservation, javax.swing.GroupLayout.DEFAULT_SIZE, 730, Short.MAX_VALUE)
        );
        ReservationLayout.setVerticalGroup(
            ReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ReservationLayout.createSequentialGroup()
                .addComponent(panelReservation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        ModifyUsers.setTitle("Modificar Usuarios");
        ModifyUsers.setResizable(false);

        jLabel5.setText("Nombre:");

        jLabel15.setText("Contraseña");

        cboUDepartment.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccionar", "Ingeniería en Sistemas Computacionales", "Ingeniería Industrial", "Ingeniería Química", "Ingeniería Eléctrica", "Ingeniería Electrónica", "Ingeniería en Gestión Empresarial", "Ciencias Básicas", "Ciencias Económico-Administrativas", "Ingeniería Mecánica", "Ingeniería Civil" }));

        btnUInsertar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/guardarUsuario.png"))); // NOI18N
        btnUInsertar.setText("Insertar");
        btnUInsertar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUInsertarActionPerformed(evt);
            }
        });

        jLabel16.setText("Correo eléctronico:");

        btnUModificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/actualizarUsuario.png"))); // NOI18N
        btnUModificar.setText("Modificar");
        btnUModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUModificarActionPerformed(evt);
            }
        });

        jLabel17.setText("Role:");

        btnUEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/eliminarUsuario.png"))); // NOI18N
        btnUEliminar.setText("Eliminar");
        btnUEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUEliminarActionPerformed(evt);
            }
        });

        jLabel18.setText("Departamento:");

        btnULimpiar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/limpiarUsuario.png"))); // NOI18N
        btnULimpiar.setText("Limpiar");
        btnULimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnULimpiarActionPerformed(evt);
            }
        });

        txtUId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUIdActionPerformed(evt);
            }
        });

        btnUBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/buscarUsuarios.png"))); // NOI18N
        btnUBuscar.setText("Buscar");
        btnUBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUBuscarActionPerformed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel20.setText("Información");

        cboURole.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "usuario", "administrador" }));

        tablaU.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Id", "Nombre", "Correo", "Email", "Rol", "Departamento"
            }
        ));
        jScrollPane1.setViewportView(tablaU);

        cboOpcionesBusquedaU.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ID", "Nombre", "Correo" }));

        jLabel35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/editarUsuarios.png"))); // NOI18N

        javax.swing.GroupLayout panelModifyUsersLayout = new javax.swing.GroupLayout(panelModifyUsers);
        panelModifyUsers.setLayout(panelModifyUsersLayout);
        panelModifyUsersLayout.setHorizontalGroup(
            panelModifyUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelModifyUsersLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(panelModifyUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelModifyUsersLayout.createSequentialGroup()
                        .addGap(133, 133, 133)
                        .addComponent(jLabel35)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel21))
                    .addGroup(panelModifyUsersLayout.createSequentialGroup()
                        .addGap(130, 130, 130)
                        .addComponent(jLabel20))
                    .addGroup(panelModifyUsersLayout.createSequentialGroup()
                        .addGroup(panelModifyUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(panelModifyUsersLayout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtUPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelModifyUsersLayout.createSequentialGroup()
                                .addGroup(panelModifyUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel16)
                                    .addComponent(jLabel17)
                                    .addComponent(jLabel18)
                                    .addComponent(btnUInsertar))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(panelModifyUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtUMail, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(cboURole, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(cboUDepartment, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnUModificar)))
                            .addGroup(panelModifyUsersLayout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtUName, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(panelModifyUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelModifyUsersLayout.createSequentialGroup()
                                .addComponent(txtUId, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cboOpcionesBusquedaU, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnUBuscar))
                            .addGroup(panelModifyUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(panelModifyUsersLayout.createSequentialGroup()
                                    .addComponent(btnUEliminar)
                                    .addGap(54, 54, 54)
                                    .addComponent(btnULimpiar))
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 435, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        panelModifyUsersLayout.setVerticalGroup(
            panelModifyUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelModifyUsersLayout.createSequentialGroup()
                .addGroup(panelModifyUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelModifyUsersLayout.createSequentialGroup()
                        .addGroup(panelModifyUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelModifyUsersLayout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addComponent(jLabel21))
                            .addGroup(panelModifyUsersLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(panelModifyUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtUId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnUBuscar)
                                    .addComponent(cboOpcionesBusquedaU, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(59, 59, 59))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelModifyUsersLayout.createSequentialGroup()
                        .addComponent(jLabel35)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel20)
                        .addGap(7, 7, 7)))
                .addGroup(panelModifyUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelModifyUsersLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelModifyUsersLayout.createSequentialGroup()
                        .addGroup(panelModifyUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5)
                            .addComponent(txtUName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(panelModifyUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(txtUPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(panelModifyUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtUMail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16))
                        .addGap(18, 18, 18)
                        .addGroup(panelModifyUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboURole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17))
                        .addGap(18, 18, 18)
                        .addGroup(panelModifyUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18)
                            .addComponent(cboUDepartment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(panelModifyUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnUInsertar)
                            .addComponent(btnUModificar)
                            .addComponent(btnUEliminar)
                            .addComponent(btnULimpiar))))
                .addGap(38, 38, 38))
        );

        javax.swing.GroupLayout ModifyUsersLayout = new javax.swing.GroupLayout(ModifyUsers.getContentPane());
        ModifyUsers.getContentPane().setLayout(ModifyUsersLayout);
        ModifyUsersLayout.setHorizontalGroup(
            ModifyUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelModifyUsers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        ModifyUsersLayout.setVerticalGroup(
            ModifyUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelModifyUsers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        ModifyLabs.setTitle("Modificar Laboratorios");
        ModifyLabs.setResizable(false);

        jLabel22.setText("Nombre:");

        jLabel23.setText("Ubcación:");

        btnLInsertar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/guardarRegistro.png"))); // NOI18N
        btnLInsertar.setText("Insertar");
        btnLInsertar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLInsertarActionPerformed(evt);
            }
        });

        jLabel24.setText("Capacidad:");

        btnLModificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/editar.png"))); // NOI18N
        btnLModificar.setText("Modificar");
        btnLModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLModificarActionPerformed(evt);
            }
        });

        jLabel25.setText("Tipo:");

        btnLEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/eliminarRegistro.png"))); // NOI18N
        btnLEliminar.setText("Eliminar");
        btnLEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLEliminarActionPerformed(evt);
            }
        });

        btnLLimpiar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/limpiarRegistro.png"))); // NOI18N
        btnLLimpiar.setText("Limpiar");
        btnLLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLLimpiarActionPerformed(evt);
            }
        });

        btnLBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/buscar.png"))); // NOI18N
        btnLBuscar.setText("Buscar");
        btnLBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLBuscarActionPerformed(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel26.setText("Información");

        cboLType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Física", "Química", "Biología", "Computación", "Electrónica", "Eléctrica", "Mecánica", "Mecatrónica", "Energías Renovables", "Materiales", "Análisis de Alimentos", "Ingeniería de Software", "Robótica" }));
        cboLType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboLTypeActionPerformed(evt);
            }
        });

        tablaL.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "id", "Nombre", "Ubicación", "Capacidad", "Tipo"
            }
        ));
        jScrollPane7.setViewportView(tablaL);

        cboOpcionesBusquedaL.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ID", "Nombre" }));

        jLabel36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Laboratorio.png"))); // NOI18N

        javax.swing.GroupLayout panelModifyLabsLayout = new javax.swing.GroupLayout(panelModifyLabs);
        panelModifyLabs.setLayout(panelModifyLabsLayout);
        panelModifyLabsLayout.setHorizontalGroup(
            panelModifyLabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelModifyLabsLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(panelModifyLabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelModifyLabsLayout.createSequentialGroup()
                        .addComponent(btnLEliminar)
                        .addGap(57, 57, 57)
                        .addComponent(btnLLimpiar))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelModifyLabsLayout.createSequentialGroup()
                        .addGroup(panelModifyLabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelModifyLabsLayout.createSequentialGroup()
                                .addGroup(panelModifyLabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel24)
                                    .addComponent(jLabel25))
                                .addGap(72, 72, 72)
                                .addGroup(panelModifyLabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtLCapacity)
                                    .addComponent(cboLType, 0, 236, Short.MAX_VALUE)))
                            .addGroup(panelModifyLabsLayout.createSequentialGroup()
                                .addComponent(jLabel22)
                                .addGap(25, 25, 25)
                                .addComponent(txtLName, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelModifyLabsLayout.createSequentialGroup()
                                .addComponent(jLabel23)
                                .addGap(18, 18, 18)
                                .addComponent(txtLLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panelModifyLabsLayout.createSequentialGroup()
                                .addComponent(btnLInsertar)
                                .addGap(48, 48, 48)
                                .addComponent(btnLModificar))
                            .addGroup(panelModifyLabsLayout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addGroup(panelModifyLabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel26)
                                    .addGroup(panelModifyLabsLayout.createSequentialGroup()
                                        .addComponent(jLabel27)
                                        .addGap(102, 102, 102)
                                        .addComponent(jLabel36)))))
                        .addGap(18, 18, 18)
                        .addGroup(panelModifyLabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelModifyLabsLayout.createSequentialGroup()
                                .addComponent(txtLId)
                                .addGap(18, 18, 18)
                                .addComponent(cboOpcionesBusquedaL, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnLBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE))))
                .addContainerGap())
        );
        panelModifyLabsLayout.setVerticalGroup(
            panelModifyLabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelModifyLabsLayout.createSequentialGroup()
                .addGroup(panelModifyLabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelModifyLabsLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(panelModifyLabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel36)
                            .addGroup(panelModifyLabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnLBuscar)
                                .addComponent(txtLId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cboOpcionesBusquedaL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(panelModifyLabsLayout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(jLabel27)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelModifyLabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelModifyLabsLayout.createSequentialGroup()
                        .addComponent(jLabel26)
                        .addGap(18, 18, 18)
                        .addGroup(panelModifyLabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel22)
                            .addComponent(txtLName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(panelModifyLabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel23)
                            .addComponent(txtLLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(21, 21, 21)
                        .addGroup(panelModifyLabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtLCapacity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel24))
                        .addGap(18, 18, 18)
                        .addGroup(panelModifyLabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel25)
                            .addComponent(cboLType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(panelModifyLabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelModifyLabsLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(panelModifyLabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnLEliminar)
                            .addComponent(btnLLimpiar)))
                    .addGroup(panelModifyLabsLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(panelModifyLabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnLInsertar)
                            .addComponent(btnLModificar))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout ModifyLabsLayout = new javax.swing.GroupLayout(ModifyLabs.getContentPane());
        ModifyLabs.getContentPane().setLayout(ModifyLabsLayout);
        ModifyLabsLayout.setHorizontalGroup(
            ModifyLabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ModifyLabsLayout.createSequentialGroup()
                .addComponent(panelModifyLabs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 13, Short.MAX_VALUE))
        );
        ModifyLabsLayout.setVerticalGroup(
            ModifyLabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ModifyLabsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelModifyLabs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        ModifyMaterials.setTitle("Modificar Materiales");
        ModifyMaterials.setResizable(false);

        panelModifyMaterials.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        panelModifyMaterials.add(txtMIdLab, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 194, 290, -1));

        jLabel28.setText("Nombre:");
        panelModifyMaterials.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 110, -1, -1));

        jLabel29.setText("Cantidad");
        panelModifyMaterials.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 150, -1, -1));

        btnMInsertar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/guardarRegistro.png"))); // NOI18N
        btnMInsertar.setText("Insertar");
        btnMInsertar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMInsertarActionPerformed(evt);
            }
        });
        panelModifyMaterials.add(btnMInsertar, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 300, -1, -1));

        jLabel30.setText("id Lab");
        panelModifyMaterials.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 190, -1, -1));

        btnMModificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/editar.png"))); // NOI18N
        btnMModificar.setText("Modificar");
        btnMModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMModificarActionPerformed(evt);
            }
        });
        panelModifyMaterials.add(btnMModificar, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 300, -1, -1));
        panelModifyMaterials.add(txtMName, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 110, 290, -1));

        jLabel31.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel31.setText("Información");
        panelModifyMaterials.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 90, -1, -1));
        panelModifyMaterials.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(391, 37, -1, -1));
        panelModifyMaterials.add(txtMId, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 20, 120, -1));

        btnMBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/buscar.png"))); // NOI18N
        btnMBuscar.setText("Buscar");
        btnMBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMBuscarActionPerformed(evt);
            }
        });
        panelModifyMaterials.add(btnMBuscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 20, -1, -1));

        btnMEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/eliminarRegistro.png"))); // NOI18N
        btnMEliminar.setText("Eliminar");
        btnMEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMEliminarActionPerformed(evt);
            }
        });
        panelModifyMaterials.add(btnMEliminar, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 300, -1, -1));

        btnMLimpiar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/limpiarRegistro.png"))); // NOI18N
        btnMLimpiar.setText("Limpiar");
        btnMLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMLimpiarActionPerformed(evt);
            }
        });
        panelModifyMaterials.add(btnMLimpiar, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 300, -1, -1));

        tablaM.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Id", "Nombre", "Cantidad", "Laboratorio"
            }
        ));
        jScrollPane6.setViewportView(tablaM);

        panelModifyMaterials.add(jScrollPane6, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 100, 328, 180));
        panelModifyMaterials.add(txtMQuantity, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 150, 290, -1));

        cboOpcionesBusquedaM.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ID", "Nombre" }));
        panelModifyMaterials.add(cboOpcionesBusquedaM, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 20, 100, -1));

        jLabel37.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/microscopio.png"))); // NOI18N
        panelModifyMaterials.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 20, -1, -1));

        javax.swing.GroupLayout ModifyMaterialsLayout = new javax.swing.GroupLayout(ModifyMaterials.getContentPane());
        ModifyMaterials.getContentPane().setLayout(ModifyMaterialsLayout);
        ModifyMaterialsLayout.setHorizontalGroup(
            ModifyMaterialsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ModifyMaterialsLayout.createSequentialGroup()
                .addComponent(panelModifyMaterials, javax.swing.GroupLayout.PREFERRED_SIZE, 758, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        ModifyMaterialsLayout.setVerticalGroup(
            ModifyMaterialsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelModifyMaterials, javax.swing.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)
        );

        Statitics.setResizable(false);

        javax.swing.GroupLayout StatiticsLayout = new javax.swing.GroupLayout(Statitics.getContentPane());
        Statitics.getContentPane().setLayout(StatiticsLayout);
        StatiticsLayout.setHorizontalGroup(
            StatiticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        StatiticsLayout.setVerticalGroup(
            StatiticsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Panel de Administrador");

        txtBievenida.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtBievenida.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        txtBievenida.setText("¡Bienvenido");
        panelBienvenida.add(txtBievenida);

        panelEstatus.setLayout(new java.awt.GridBagLayout());

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/usuario_admin.png"))); // NOI18N
        panelEstatus.add(jLabel3, new java.awt.GridBagConstraints());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setText("Estatus del Administrador");
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

        panelTabla.setLayout(new java.awt.BorderLayout(1, 1));

        tablaHorariosSemana.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Hora de inicio", "Hora de finalización", "Estatus", "Laboratorio"
            }
        ));
        jScrollPane4.setViewportView(tablaHorariosSemana);

        panelTabla.add(jScrollPane4, java.awt.BorderLayout.CENTER);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("Reservaciones");
        panelTabla.add(jLabel4, java.awt.BorderLayout.PAGE_START);

        btnVerDetalles.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/ojo_abierto.png"))); // NOI18N
        btnVerDetalles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerDetallesActionPerformed(evt);
            }
        });
        panelTabla.add(btnVerDetalles, java.awt.BorderLayout.PAGE_END);

        calendarioHorarios.setPreferredSize(new java.awt.Dimension(300, 221));
        calendarioHorarios.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                calendarioHorariosPropertyChange(evt);
            }
        });
        panelOpciones.add(calendarioHorarios);

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

        grupoBotonesAdmin.add(opcionClaro);
        opcionClaro.setSelected(true);
        opcionClaro.setText("Claro");
        opcionClaro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcionClaroActionPerformed(evt);
            }
        });
        subMenuApariencia.add(opcionClaro);

        grupoBotonesAdmin.add(opcionOscuro);
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

        opcionAgregarReservacion.setText("Modificar Reservación");
        opcionAgregarReservacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcionAgregarReservacionActionPerformed(evt);
            }
        });
        menuOpciones.add(opcionAgregarReservacion);

        opcionModificarUsuarios.setText("Modificar Usuarios");
        opcionModificarUsuarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcionModificarUsuariosActionPerformed(evt);
            }
        });
        menuOpciones.add(opcionModificarUsuarios);

        opcionModificarLaboratorios.setText("Modificar Laboratorios");
        opcionModificarLaboratorios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcionModificarLaboratoriosActionPerformed(evt);
            }
        });
        menuOpciones.add(opcionModificarLaboratorios);

        opcionModificarMaterial.setText("Modificar Material");
        opcionModificarMaterial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcionModificarMaterialActionPerformed(evt);
            }
        });
        menuOpciones.add(opcionModificarMaterial);

        opcionEstadisticas.setText("Ver estádisticas");
        opcionEstadisticas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcionEstadisticasActionPerformed(evt);
            }
        });
        menuOpciones.add(opcionEstadisticas);

        menuBarraUsuario.add(menuOpciones);

        menuAyuda.setText("Ayuda");

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
            .addComponent(panelBienvenida, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(panelOpciones, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelTabla, javax.swing.GroupLayout.DEFAULT_SIZE, 621, Short.MAX_VALUE))
            .addComponent(panelEstatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelBienvenida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelEstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelOpciones, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                    .addComponent(panelTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
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
        txtIdReservacion.setText("");
        txtPurpose.setText("");
        DefaultTableModel materialModel = (DefaultTableModel) tablaMateriales.getModel();
        materialModel.setRowCount(0);

        // Mostrar el diálogo de reservación
        Reservation.setVisible(true);
        Reservation.setSize(740, 560);
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

    private void btnVerDetallesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerDetallesActionPerformed
        int selectedRow = tablaHorariosSemana.getSelectedRow();
        if (selectedRow != -1) {
            DefaultTableModel model = (DefaultTableModel) tablaHorariosSemana.getModel();

            // Obtener los datos de la fila seleccionada
            String horaInicio = model.getValueAt(selectedRow, 0).toString();
            String horaFin = model.getValueAt(selectedRow, 1).toString();
            String estatus = model.getValueAt(selectedRow, 2).toString();

            // Consulta para obtener información adicional de la reservación
            String query = "SELECT R.id_reservation, L.name as lab_name, S.date, S.start_time, S.end_time, R.purpose, R.status, R.type, U.username " +
                           "FROM RESERVATION R " +
                           "JOIN LABORATORY L ON R.id_lab = L.id_lab " +
                           "JOIN SCHEDULE S ON R.id_schedule = S.id_schedule " +
                           "JOIN USER U ON R.id_user = U.id_user " +
                           "WHERE S.start_time = ? AND S.end_time = ? AND S.date = ?";

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection conn = DriverManager.getConnection(URL, usuario, contrasena);
                ps = conn.prepareStatement(query);
                ps.setString(1, horaInicio);
                ps.setString(2, horaFin);
                ps.setString(3, new SimpleDateFormat("yyyy-MM-dd").format(calendarioHorarios.getDate()));
                rs = ps.executeQuery();

                if (rs.next()) {
                    String idReservation = rs.getString("id_reservation");
                    String labName = rs.getString("lab_name");
                    String date = rs.getString("date");
                    String purpose = rs.getString("purpose");
                    String status = rs.getString("status");
                    String type = rs.getString("type");
                    String username = rs.getString("username");

                    // Crear el mensaje con la información
                    String message = "ID de Reservación: " + idReservation + "\n"
                                   + "Laboratorio: " + labName + "\n"
                                   + "Fecha: " + date + "\n"
                                   + "Hora de Inicio: " + horaInicio + "\n"
                                   + "Hora de Finalización: " + horaFin + "\n"
                                   + "Propósito: " + purpose + "\n"
                                   + "Estado: " + status + "\n"
                                   + "Tipo: " + type + "\n"
                                   + "Usuario: " + username + "\n"
                                   + "Estatus: " + estatus;

                    // Mostrar la información en un JOptionPane
                    JOptionPane.showMessageDialog(this, message, "Detalles de la Reservación", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontraron detalles para la reservación seleccionada.", "Detalles de la Reservación", JOptionPane.ERROR_MESSAGE);
                }

                conn.close();
            } catch (Exception e) {
                System.err.println("Error al obtener los detalles de la reservación: " + e.getMessage());
                JOptionPane.showMessageDialog(this, "Error al obtener los detalles de la reservación.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecciona una fila para ver los detalles.", "Detalles de la Reservación", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnVerDetallesActionPerformed

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

    private void btnUInsertarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUInsertarActionPerformed
        String username = txtUName.getText();
        String password = new String(txtUPassword.getPassword());
        String email = txtUMail.getText();
        String role = cboURole.getSelectedItem().toString();
        String department = cboUDepartment.getSelectedItem().toString();

        String query = "INSERT INTO USER (username, password, email, role, department) VALUES (?, ?, ?, ?, ?)";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, usuario, contrasena);
            ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, email);
            ps.setString(4, role);
            ps.setString(5, department);
            ps.executeUpdate();

            conn.close();
            cargarUsuariosEnTabla();
            limpiarCamposUsuario();
            JOptionPane.showMessageDialog(this, "Usuario insertado exitosamente");
        } catch (Exception e) {
            System.err.println("Error al insertar el usuario: " + e.getMessage());
        }
    }//GEN-LAST:event_btnUInsertarActionPerformed

    private void btnUModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUModificarActionPerformed
        if(txtUId.getText().equals("")){
            JOptionPane.showMessageDialog(null, "Debe seleccionar o buscar un usuario a modificar");
        }
        else{
            int id = Integer.parseInt(txtUId.getText());
            String username = txtUName.getText();
            String password = new String(txtUPassword.getPassword());
            String email = txtUMail.getText();
            String role = cboURole.getSelectedItem().toString();
            String department = cboUDepartment.getSelectedItem().toString();

            String query = "UPDATE USER SET username = ?, password = ?, email = ?, role = ?, department = ? WHERE id_user = ?";

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection conn = DriverManager.getConnection(URL, usuario, contrasena);
                ps = conn.prepareStatement(query);
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, email);
                ps.setString(4, role);
                ps.setString(5, department);
                ps.setInt(6, id);
                ps.executeUpdate();

                conn.close();
                cargarUsuariosEnTabla();
                limpiarCamposUsuario();
                JOptionPane.showMessageDialog(this, "Usuario modificado exitosamente");
            } catch (Exception e) {
                System.err.println("Error al modificar el usuario: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_btnUModificarActionPerformed

    private void btnULimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnULimpiarActionPerformed
        limpiarCamposUsuario();
    }//GEN-LAST:event_btnULimpiarActionPerformed

    private void btnUBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUBuscarActionPerformed
        String searchValue = txtUId.getText().trim();
        String searchOption = cboOpcionesBusquedaU.getSelectedItem().toString();

        String query = "";

        switch (searchOption) {
            case "ID":
                query = "SELECT id_user, username, password, email, role, department FROM USER WHERE id_user = ?";
                break;
            case "Nombre":
                query = "SELECT id_user, username, password, email, role, department FROM USER WHERE username = ?";
                break;
            case "Correo":
                query = "SELECT id_user, username, password, email, role, department FROM USER WHERE email = ?";
                break;
            default:
                JOptionPane.showMessageDialog(this, "Opción de búsqueda no válida", "Error", JOptionPane.ERROR_MESSAGE);
                return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, usuario, contrasena);
            ps = conn.prepareStatement(query);

            if (searchOption.equals("ID")) {
                ps.setInt(1, Integer.parseInt(searchValue));
            } else {
                ps.setString(1, searchValue);
            }

            rs = ps.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id_user");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String email = rs.getString("email");
                String role = rs.getString("role");
                String department = rs.getString("department");

                txtUId.setText(String.valueOf(id));
                txtUName.setText(username);
                txtUPassword.setText(password); // Cargar la contraseña en txtUPassword
                txtUMail.setText(email);
                cboURole.setSelectedItem(role);
                cboUDepartment.setSelectedItem(department);
            } else {
                JOptionPane.showMessageDialog(this, "Usuario no encontrado", "Advertencia", JOptionPane.WARNING_MESSAGE);
                limpiarCamposUsuario();
            }
            conn.close();
        } catch (Exception e) {
            System.err.println("Error al buscar el usuario: " + e.getMessage());
        }
    }//GEN-LAST:event_btnUBuscarActionPerformed

    private void btnLInsertarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLInsertarActionPerformed
        String name = txtLName.getText();
        String location = txtLLocation.getText();
        int capacity = Integer.parseInt(txtLCapacity.getText());
        String type = cboLType.getSelectedItem().toString();

        String query = "INSERT INTO LABORATORY (name, location, capacity, type) VALUES (?, ?, ?, ?)";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, usuario, contrasena);
            ps = conn.prepareStatement(query);
            ps.setString(1, name);
            ps.setString(2, location);
            ps.setInt(3, capacity);
            ps.setString(4, type);
            ps.executeUpdate();

            conn.close();
            cargarLaboratoriosEnTabla();
            limpiarCamposLaboratorio();
            JOptionPane.showMessageDialog(this, "Laboratorio insertado exitosamente");
        } catch (Exception e) {
            System.err.println("Error al insertar el laboratorio: " + e.getMessage());
        }
    }//GEN-LAST:event_btnLInsertarActionPerformed

    private void btnLModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLModificarActionPerformed
        int id = Integer.parseInt(txtLId.getText());
        String name = txtLName.getText();
        String location = txtLLocation.getText();
        int capacity = Integer.parseInt(txtLCapacity.getText());
        String type = cboLType.getSelectedItem().toString();

        String query = "UPDATE LABORATORY SET name = ?, location = ?, capacity = ?, type = ? WHERE id_lab = ?";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, usuario, contrasena);
            ps = conn.prepareStatement(query);
            ps.setString(1, name);
            ps.setString(2, location);
            ps.setInt(3, capacity);
            ps.setString(4, type);
            ps.setInt(5, id);
            ps.executeUpdate();

            conn.close();
            cargarLaboratoriosEnTabla();
            limpiarCamposLaboratorio();
            JOptionPane.showMessageDialog(this, "Laboratorio modificado exitosamente");
        } catch (Exception e) {
            System.err.println("Error al modificar el laboratorio: " + e.getMessage());
        }
    }//GEN-LAST:event_btnLModificarActionPerformed

    private void btnLLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLLimpiarActionPerformed
        limpiarCamposLaboratorio();
    }//GEN-LAST:event_btnLLimpiarActionPerformed

    private void btnLBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLBuscarActionPerformed
        String searchValue = txtLId.getText().trim();
        String searchOption = cboOpcionesBusquedaL.getSelectedItem().toString();

        String query = "";

        switch (searchOption) {
            case "ID":
                query = "SELECT id_lab, name, location, capacity, type FROM LABORATORY WHERE id_lab = ?";
                break;
            case "Nombre":
                query = "SELECT id_lab, name, location, capacity, type FROM LABORATORY WHERE name LIKE ?";
                searchValue = "%" + searchValue + "%"; // Añadir comodines para permitir búsqueda parcial
                break;
            default:
                JOptionPane.showMessageDialog(this, "Opción de búsqueda no válida", "Error", JOptionPane.ERROR_MESSAGE);
                return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, usuario, contrasena);
            ps = conn.prepareStatement(query);

            if (searchOption.equals("ID")) {
                ps.setInt(1, Integer.parseInt(searchValue));
            } else {
                ps.setString(1, searchValue);
            }

            rs = ps.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id_lab");
                String name = rs.getString("name");
                String location = rs.getString("location");
                int capacity = rs.getInt("capacity");
                String type = rs.getString("type");

                txtLId.setText(String.valueOf(id));
                txtLName.setText(name);
                txtLLocation.setText(location);
                txtLCapacity.setText(String.valueOf(capacity));
                cboLType.setSelectedItem(type);
            } else {
                JOptionPane.showMessageDialog(this, "Laboratorio no encontrado", "Advertencia", JOptionPane.WARNING_MESSAGE);
                limpiarCamposLaboratorio();
            }
            conn.close();
        } catch (Exception e) {
            System.err.println("Error al buscar el laboratorio: " + e.getMessage());
        }
    }//GEN-LAST:event_btnLBuscarActionPerformed

    private void cboLTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLTypeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboLTypeActionPerformed

    private void btnMInsertarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMInsertarActionPerformed
        String name = txtMName.getText();
        int quantity = Integer.parseInt(txtMQuantity.getText());
        int idLab = Integer.parseInt(txtMIdLab.getText());

        // Verificar que el laboratorio exista
        if (!idLabExiste(idLab)) {
            JOptionPane.showMessageDialog(this, "El laboratorio especificado no existe.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "INSERT INTO MATERIAL (name, quantity, id_lab) VALUES (?, ?, ?)";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, usuario, contrasena);
            ps = conn.prepareStatement(query);
            ps.setString(1, name);
            ps.setInt(2, quantity);
            ps.setInt(3, idLab);
            ps.executeUpdate();

            conn.close();
            cargarMaterialesEnTabla();
            limpiarCamposMaterial();
            JOptionPane.showMessageDialog(this, "Material insertado exitosamente");
        } catch (Exception e) {
            System.err.println("Error al insertar el material: " + e.getMessage());
        }
    }//GEN-LAST:event_btnMInsertarActionPerformed

    private void btnMModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMModificarActionPerformed
        int id = Integer.parseInt(txtMId.getText());
        String name = txtMName.getText();
        int quantity = Integer.parseInt(txtMQuantity.getText());
        int idLab = Integer.parseInt(txtMIdLab.getText());

        // Validar que el id_lab exista en la tabla LABORATORY
        if (!idLabExiste(idLab)) {
            JOptionPane.showMessageDialog(this, "El laboratorio especificado no existe.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "UPDATE MATERIAL SET name = ?, quantity = ?, id_lab = ? WHERE id_material = ?";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, usuario, contrasena);
            ps = conn.prepareStatement(query);
            ps.setString(1, name);
            ps.setInt(2, quantity);
            ps.setInt(3, idLab);
            ps.setInt(4, id);
            ps.executeUpdate();

            conn.close();
            cargarMaterialesEnTabla();
            limpiarCamposMaterial();
            JOptionPane.showMessageDialog(this, "Material modificado exitosamente");
        } catch (Exception e) {
            System.err.println("Error al modificar el material: " + e.getMessage());
        }
    }//GEN-LAST:event_btnMModificarActionPerformed

    private void btnMLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMLimpiarActionPerformed
        limpiarCamposMaterial();
    }//GEN-LAST:event_btnMLimpiarActionPerformed

    private void btnMBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMBuscarActionPerformed
        String searchValue = txtMId.getText().trim();
        String searchOption = cboOpcionesBusquedaM.getSelectedItem().toString();

        String query = "";

        switch (searchOption) {
            case "ID":
                query = "SELECT id_material, name, quantity, id_lab FROM MATERIAL WHERE id_material = ?";
                break;
            case "Nombre":
                query = "SELECT id_material, name, quantity, id_lab FROM MATERIAL WHERE name LIKE ?";
                searchValue = "%" + searchValue + "%"; // Añadir comodines para permitir búsqueda parcial
                break;
            default:
                JOptionPane.showMessageDialog(this, "Opción de búsqueda no válida", "Error", JOptionPane.ERROR_MESSAGE);
                return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, usuario, contrasena);
            ps = conn.prepareStatement(query);

            if (searchOption.equals("ID")) {
                ps.setInt(1, Integer.parseInt(searchValue));
            } else {
                ps.setString(1, searchValue);
            }

            rs = ps.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id_material");
                String name = rs.getString("name");
                int quantity = rs.getInt("quantity");
                int idLab = rs.getInt("id_lab");

                txtMId.setText(String.valueOf(id));
                txtMName.setText(name);
                txtMQuantity.setText(String.valueOf(quantity));
                txtMIdLab.setText(String.valueOf(idLab));
            } else {
                JOptionPane.showMessageDialog(this, "Material no encontrado", "Advertencia", JOptionPane.WARNING_MESSAGE);
                limpiarCamposMaterial();
            }
            conn.close();
        } catch (Exception e) {
            System.err.println("Error al buscar el material: " + e.getMessage());
        }
    }//GEN-LAST:event_btnMBuscarActionPerformed

    private void opcionModificarUsuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcionModificarUsuariosActionPerformed
        txtUId.setText("");
        txtUName.setText("");
        txtUPassword.setText("");
        txtUMail.setText("");
        cboURole.setSelectedIndex(0);
        cboUDepartment.setSelectedIndex(0);
        
        cargarUsuariosEnTabla();
        ModifyUsers.setSize(890, 430);
        ModifyUsers.setLocationRelativeTo(null);
        ModifyUsers.setVisible(true);
    }//GEN-LAST:event_opcionModificarUsuariosActionPerformed

    private void txtUIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUIdActionPerformed

    private void btnUEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUEliminarActionPerformed
        //Se reasignan al superUsuario 1 para no perder el resto de información
        int id = Integer.parseInt(txtUId.getText());

        int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que deseas eliminar este usuario y reasignar todas sus reservas a un usuario genérico?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int idUsuarioGenerico = 1; // ID del usuario genérico (superusuario).
            String updateReservationsQuery = "UPDATE RESERVATION SET id_user = ? WHERE id_user = ?";
            String deleteUserQuery = "DELETE FROM USER WHERE id_user = ?";

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection conn = DriverManager.getConnection(URL, usuario, contrasena);

                // Reasignar las reservas al usuario genérico
                ps = conn.prepareStatement(updateReservationsQuery);
                ps.setInt(1, idUsuarioGenerico);
                ps.setInt(2, id);
                ps.executeUpdate();

                // Eliminar el usuario
                ps = conn.prepareStatement(deleteUserQuery);
                ps.setInt(1, id);
                ps.executeUpdate();

                conn.close();
                cargarUsuariosEnTabla();
                limpiarCamposUsuario();
                JOptionPane.showMessageDialog(this, "Usuario eliminado exitosamente y sus reservas han sido reasignadas.");
            } catch (Exception e) {
                System.err.println("Error al eliminar el usuario: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_btnUEliminarActionPerformed

    private void btnMEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMEliminarActionPerformed
        int id = Integer.parseInt(txtMId.getText());

        int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que deseas eliminar este material y reasignar todas sus relaciones a un material genérico?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int idMaterialGenerico = 1; // ID del material genérico.
            String updateReservationMaterialQuery = "UPDATE RESERVATION_MATERIAL SET id_material = ? WHERE id_material = ?";
            String deleteMaterialQuery = "DELETE FROM MATERIAL WHERE id_material = ?";

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection conn = DriverManager.getConnection(URL, usuario, contrasena);

                // Reasignar las relaciones en RESERVATION_MATERIAL al material genérico
                ps = conn.prepareStatement(updateReservationMaterialQuery);
                ps.setInt(1, idMaterialGenerico);
                ps.setInt(2, id);
                ps.executeUpdate();

                // Eliminar el material
                ps = conn.prepareStatement(deleteMaterialQuery);
                ps.setInt(1, id);
                ps.executeUpdate();

                conn.close();
                cargarMaterialesEnTabla();
                limpiarCamposMaterial();
                JOptionPane.showMessageDialog(this, "Material eliminado exitosamente y sus relaciones han sido reasignadas.");
            } catch (Exception e) {
                System.err.println("Error al eliminar el material: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_btnMEliminarActionPerformed

    private void opcionModificarMaterialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcionModificarMaterialActionPerformed
        txtMId.setText("");
        txtMName.setText("");
        txtMQuantity.setText("");
        txtMId.setText("");
        
        cargarMaterialesEnTabla();
        ModifyMaterials.setSize(780, 380);
        ModifyMaterials.setLocationRelativeTo(null);
        ModifyMaterials.setVisible(true);
    }//GEN-LAST:event_opcionModificarMaterialActionPerformed

    private void opcionModificarLaboratoriosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcionModificarLaboratoriosActionPerformed
        txtLId.setText("");
        txtLCapacity.setText("");
        txtLLocation.setText("");
        txtLName.setText("");
        cboLType.setSelectedIndex(0);
        
        cargarLaboratoriosEnTabla();
        ModifyLabs.setSize(880, 370);
        ModifyLabs.setLocationRelativeTo(null);
        ModifyLabs.setVisible(true);
    }//GEN-LAST:event_opcionModificarLaboratoriosActionPerformed

    private void btnLEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLEliminarActionPerformed
        int id = Integer.parseInt(txtLId.getText());

        int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que deseas eliminar este laboratorio y todas sus relaciones?\n NO RECOMENDADO", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String deleteReservationMaterialQuery = "DELETE FROM RESERVATION_MATERIAL WHERE id_material IN (SELECT id_material FROM MATERIAL WHERE id_lab = ?)";
            String deleteReservationsQuery = "DELETE FROM RESERVATION WHERE id_lab = ?";
            String deleteMaterialsQuery = "DELETE FROM MATERIAL WHERE id_lab = ?";
            String deleteLabQuery = "DELETE FROM LABORATORY WHERE id_lab = ?";

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection conn = DriverManager.getConnection(URL, usuario, contrasena);

                // Eliminar todas las relaciones de materiales en reservaciones relacionadas con el laboratorio
                ps = conn.prepareStatement(deleteReservationMaterialQuery);
                ps.setInt(1, id);
                ps.executeUpdate();

                // Eliminar todas las reservas relacionadas con el laboratorio
                ps = conn.prepareStatement(deleteReservationsQuery);
                ps.setInt(1, id);
                ps.executeUpdate();

                // Eliminar todos los materiales relacionados con el laboratorio
                ps = conn.prepareStatement(deleteMaterialsQuery);
                ps.setInt(1, id);
                ps.executeUpdate();

                // Eliminar el laboratorio
                ps = conn.prepareStatement(deleteLabQuery);
                ps.setInt(1, id);
                ps.executeUpdate();

                conn.close();
                cargarLaboratoriosEnTabla();
                limpiarCamposLaboratorio();
                JOptionPane.showMessageDialog(this, "Laboratorio eliminado exitosamente.");
            } catch (Exception e) {
                System.err.println("Error al eliminar el laboratorio: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_btnLEliminarActionPerformed

    private void btnBuscarReservacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarReservacionActionPerformed
        int reservationId = Integer.parseInt(txtIdReservacion.getText());

        String queryReservation = "SELECT id_lab, id_schedule, purpose, status, type FROM RESERVATION WHERE id_reservation = ?";
        String querySchedule = "SELECT date, start_time, end_time FROM SCHEDULE WHERE id_schedule = ?";
        String queryMaterials = "SELECT M.name, RM.quantity FROM MATERIAL M JOIN RESERVATION_MATERIAL RM ON M.id_material = RM.id_material WHERE RM.id_reservation = ?";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, usuario, contrasena);

            // Cargar datos de la reservación
            ps = conn.prepareStatement(queryReservation);
            ps.setInt(1, reservationId);
            rs = ps.executeQuery();

            if (rs.next()) {
                int labId = rs.getInt("id_lab");
                int scheduleId = rs.getInt("id_schedule");
                String purpose = rs.getString("purpose");
                String status = rs.getString("status");
                String type = rs.getString("type");

                // Asignar datos a los componentes
                txtPurpose.setText(purpose);
                cboType.setSelectedItem(type);

                // Seleccionar el laboratorio correspondiente
                for (int i = 0; i < cboLaboratorios.getItemCount(); i++) {
                    if (cboLaboratorios.getItemAt(i).equals(obtenerNombreLaboratorio(labId))) {
                        cboLaboratorios.setSelectedIndex(i);
                        break;
                    }
                }

                // Cargar datos del horario
                ps = conn.prepareStatement(querySchedule);
                ps.setInt(1, scheduleId);
                rs = ps.executeQuery();

                if (rs.next()) {
                    java.util.Date date = rs.getDate("date");
                    String startTime = rs.getString("start_time");
                    String endTime = rs.getString("end_time");

                    // Formatear las horas para coincidir con las opciones del combo box
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    String formattedStartTime = sdf.format(rs.getTime("start_time"));
                    String formattedEndTime = sdf.format(rs.getTime("end_time"));

                    calendario.setDate(date);
                    cboHorasI.setSelectedItem(formattedStartTime);
                    cboHorasF.setSelectedItem(formattedEndTime);
                    lblDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(date));
                }

                // Cargar materiales asociados
                DefaultTableModel materialModel = (DefaultTableModel) tablaMateriales.getModel();
                materialModel.setRowCount(0); // Limpiar la tabla antes de agregar nuevos datos

                ps = conn.prepareStatement(queryMaterials);
                ps.setInt(1, reservationId);
                rs = ps.executeQuery();

                while (rs.next()) {
                    String materialName = rs.getString("name");
                    int quantity = rs.getInt("quantity");
                    materialModel.addRow(new Object[]{materialName, quantity});
                }

                conn.close();
            } else {
                JOptionPane.showMessageDialog(this, "Reservación no encontrada", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            System.err.println("Error al buscar la reservación: " + e.getMessage());
        }
    }//GEN-LAST:event_btnBuscarReservacionActionPerformed

    private void btnActualizarReservacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarReservacionActionPerformed
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Obtener los datos del formulario
            int idReservacion = Integer.parseInt(txtIdReservacion.getText());
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

            // Verificar si ya existe una reservación en esa fecha y hora para este laboratorio y no es la misma reservación
            if (existeOtraReservacion(date, horaInicio, horaFin, idLab, idReservacion)) {
                JOptionPane.showMessageDialog(this, "Ya existe una reservación en esa fecha y hora para este laboratorio", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Obtener el id_schedule de la reservación actual
            int idSchedule = obtenerIdSchedule(idReservacion);

            // Actualizar el horario
            actualizarSchedule(idSchedule, date, horaInicio, horaFin);

            // Actualizar la reservación en la base de datos
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, usuario, contrasena);
            String query = "UPDATE RESERVATION SET id_lab = ?, purpose = ?, status = ?, type = ? WHERE id_reservation = ?";
            ps = conn.prepareStatement(query);
            ps.setInt(1, idLab);
            ps.setString(2, proposito);
            ps.setString(3, status);
            ps.setString(4, tipo);
            ps.setInt(5, idReservacion);
            ps.executeUpdate();

            // Actualizar los materiales asociados a la reservación
            eliminarMaterialesReservacion(idReservacion);
            for (int i = 0; i < materialModel.getRowCount(); i++) {
                String material = materialModel.getValueAt(i, 0).toString();
                int idMaterial = obtenerIdMaterial(material);
                int quantity = Integer.parseInt(materialModel.getValueAt(i, 1).toString()); // Leer la cantidad de materiales

                insertarReservationMaterial(idReservacion, idMaterial, quantity); // Ajustar la cantidad según sea necesario
            }

            JOptionPane.showMessageDialog(this, "Reservación actualizada exitosamente");
            Reservation.setVisible(false);
        } catch (Exception e) {
            System.err.println("Error al actualizar la reservación: " + e.getMessage());
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
    }//GEN-LAST:event_btnActualizarReservacionActionPerformed

    private void btnBorrarReservacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBorrarReservacionActionPerformed
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            int idReservacion = Integer.parseInt(txtIdReservacion.getText().trim());

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
            ps.setInt(1, idReservacion);
            ps.executeUpdate();
            ps.close();

            // Eliminar la reservación
            String queryEliminarReservacion = "DELETE FROM RESERVATION WHERE id_reservation = ?";
            ps = conn.prepareStatement(queryEliminarReservacion);
            ps.setInt(1, idReservacion);
            ps.executeUpdate();
            ps.close();

            // Mostrar mensaje de éxito y actualizar la tabla de reservaciones
            JOptionPane.showMessageDialog(this, "Reservación eliminada exitosamente");
        } catch (Exception e) {
            System.err.println("Error al eliminar la reservación: " + e.getMessage());
        } finally {
            try {
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
    }//GEN-LAST:event_btnBorrarReservacionActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        Reservation.dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void opcionAyudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcionAyudaActionPerformed
        try {
            try {
                Desktop.getDesktop().browse(new URI("https://github.com/JesusAngelMM/ITO_JAVA_LABTIMEMANAGER.git"));
            } catch (URISyntaxException ex) {
                Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
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

    private void opcionEstadisticasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcionEstadisticasActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_opcionEstadisticasActionPerformed
    
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
            java.util.logging.Logger.getLogger(AdminDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AdminDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AdminDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AdminDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AdminDashboard(null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog ModifyLabs;
    private javax.swing.JDialog ModifyMaterials;
    private javax.swing.JDialog ModifyUsers;
    private javax.swing.JDialog Reservation;
    private javax.swing.JDialog Statitics;
    private javax.swing.JButton btnActualizarReservacion;
    private javax.swing.JButton btnAgregarMaterial;
    private javax.swing.JButton btnBorrarReservacion;
    private javax.swing.JButton btnBuscarReservacion;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnEliminarElemento;
    private javax.swing.JButton btnExportarPDF;
    private javax.swing.JButton btnHacerReservacion;
    private javax.swing.JButton btnLBuscar;
    private javax.swing.JButton btnLEliminar;
    private javax.swing.JButton btnLInsertar;
    private javax.swing.JButton btnLLimpiar;
    private javax.swing.JButton btnLModificar;
    private javax.swing.JButton btnMBuscar;
    private javax.swing.JButton btnMEliminar;
    private javax.swing.JButton btnMInsertar;
    private javax.swing.JButton btnMLimpiar;
    private javax.swing.JButton btnMModificar;
    private javax.swing.JButton btnUBuscar;
    private javax.swing.JButton btnUEliminar;
    private javax.swing.JButton btnUInsertar;
    private javax.swing.JButton btnULimpiar;
    private javax.swing.JButton btnUModificar;
    private javax.swing.JButton btnVerDetalles;
    private com.toedter.calendar.JCalendar calendario;
    private com.toedter.calendar.JCalendar calendarioHorarios;
    private javax.swing.JComboBox<String> cboHorasF;
    private javax.swing.JComboBox<String> cboHorasI;
    private javax.swing.JComboBox<String> cboLType;
    private javax.swing.JComboBox<String> cboLaboratorios;
    private javax.swing.JComboBox<String> cboMaterial;
    private javax.swing.JComboBox<String> cboOpcionesBusquedaL;
    private javax.swing.JComboBox<String> cboOpcionesBusquedaM;
    private javax.swing.JComboBox<String> cboOpcionesBusquedaU;
    private javax.swing.JComboBox<String> cboType;
    private javax.swing.JComboBox<String> cboUDepartment;
    private javax.swing.JComboBox<String> cboURole;
    private javax.swing.ButtonGroup grupoBotonesAdmin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JSeparator jSeparator1;
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
    private javax.swing.JMenuItem opcionEstadisticas;
    private javax.swing.JMenuItem opcionMenuCerrarSesion;
    private javax.swing.JMenuItem opcionMenuSalir;
    private javax.swing.JMenuItem opcionModificarLaboratorios;
    private javax.swing.JMenuItem opcionModificarMaterial;
    private javax.swing.JMenuItem opcionModificarUsuarios;
    private javax.swing.JRadioButtonMenuItem opcionOscuro;
    private javax.swing.JPanel panelBienvenida;
    private javax.swing.JPanel panelEstatus;
    private javax.swing.JPanel panelModifyLabs;
    private javax.swing.JPanel panelModifyMaterials;
    private javax.swing.JPanel panelModifyUsers;
    private javax.swing.JPanel panelOpciones;
    private javax.swing.JPanel panelReservation;
    private javax.swing.JPanel panelTabla;
    private javax.swing.JMenu subMenuApariencia;
    private javax.swing.JTable tablaHorariosSemana;
    private javax.swing.JTable tablaL;
    private javax.swing.JTable tablaM;
    private javax.swing.JTable tablaMateriales;
    private javax.swing.JTable tablaU;
    private javax.swing.JLabel txtBievenida;
    private javax.swing.JTextField txtIdReservacion;
    private javax.swing.JTextField txtLCapacity;
    private javax.swing.JTextField txtLId;
    private javax.swing.JTextField txtLLocation;
    private javax.swing.JTextField txtLName;
    private javax.swing.JTextField txtMId;
    private javax.swing.JTextField txtMIdLab;
    private javax.swing.JTextField txtMName;
    private javax.swing.JTextField txtMQuantity;
    private javax.swing.JTextArea txtPurpose;
    private javax.swing.JTextField txtUId;
    private javax.swing.JTextField txtUMail;
    private javax.swing.JTextField txtUName;
    private javax.swing.JPasswordField txtUPassword;
    // End of variables declaration//GEN-END:variables
}
