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
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;


public class DashboardAdmin extends javax.swing.JFrame {

    String nombre_usuario;
    
    public static final String URL = "jdbc:mysql://localhost:3306/labtimemanager?useTimeZone=true&serverTimezone=UTC&autoReconnect=true&useSSL=false";
    public static final String usuario = "root";
    public static final String contrasena = "password";
    PreparedStatement ps;
    ResultSet rs;
    
    public DashboardAdmin(String nombre_usuario) {
        this.nombre_usuario = nombre_usuario;
        initComponents();
        txtBievenida.setText("!Bienvenido " + nombre_usuario + "!");
        mostrarEstatus();
        rellenarComboBoxMateriales();
        agregarListenerTablaU();  // Listener para la tabla de usuarios
        agregarListenerTablaM();  // Listener para la tabla de materiales
        agregarListenerTablaL(); // Añadir el listener a la tabla de laboratorios
        agregarHorasCombo();
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
        txtIdReservacion = new javax.swing.JTextField();
        btnBuscarReservacion = new javax.swing.JButton();
        jLabel33 = new javax.swing.JLabel();
        btnActualizarReservacion = new javax.swing.JButton();
        jLabel34 = new javax.swing.JLabel();
        btnBorrarReservacion = new javax.swing.JButton();
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
        Statitics = new javax.swing.JDialog();
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
        opcionModificarHorarios = new javax.swing.JMenuItem();
        opcionEstadisticas = new javax.swing.JMenuItem();
        menuAyuda = new javax.swing.JMenu();
        opcionAyuda = new javax.swing.JMenuItem();

        Reservation.setTitle("Modificar Reservaciones");
        Reservation.setResizable(false);

        btnAgregarMaterial.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/aceptar.png"))); // NOI18N
        btnAgregarMaterial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarMaterialActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel19.setText("Tipo:");

        cboHorasF.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00" }));
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

        btnBuscarReservacion.setText("Buscar");
        btnBuscarReservacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarReservacionActionPerformed(evt);
            }
        });

        jLabel33.setText("Actualizar");

        btnActualizarReservacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/actualizar.png"))); // NOI18N
        btnActualizarReservacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarReservacionActionPerformed(evt);
            }
        });

        jLabel34.setText("Borrar");

        btnBorrarReservacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/papelera.png"))); // NOI18N
        btnBorrarReservacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBorrarReservacionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelReservationLayout = new javax.swing.GroupLayout(panelReservation);
        panelReservation.setLayout(panelReservationLayout);
        panelReservationLayout.setHorizontalGroup(
            panelReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelReservationLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(panelReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelReservationLayout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtIdReservacion, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnBuscarReservacion))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelReservationLayout.createSequentialGroup()
                        .addComponent(lblSelectLaboratorio)
                        .addGap(18, 18, 18)
                        .addComponent(cboLaboratorios, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelReservationLayout.createSequentialGroup()
                        .addGroup(panelReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelReservationLayout.createSequentialGroup()
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
                                    .addComponent(lblSelectHora))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 162, Short.MAX_VALUE))
                            .addGroup(panelReservationLayout.createSequentialGroup()
                                .addGroup(panelReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(panelReservationLayout.createSequentialGroup()
                                        .addComponent(jLabel33)
                                        .addGap(24, 24, 24)
                                        .addComponent(btnActualizarReservacion, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(panelReservationLayout.createSequentialGroup()
                                        .addComponent(jLabel12)
                                        .addGap(34, 34, 34)
                                        .addComponent(btnHacerReservacion, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel34)
                                .addGap(18, 18, 18)
                                .addComponent(btnBorrarReservacion, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(37, 37, 37)
                                .addGroup(panelReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel14)
                                    .addComponent(jLabel9))
                                .addGap(27, 27, 27)
                                .addGroup(panelReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btnExportarPDF, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(39, 39, 39)))
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
                .addGap(27, 27, 27))
        );
        panelReservationLayout.setVerticalGroup(
            panelReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelReservationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelReservationLayout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addGap(29, 29, 29))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelReservationLayout.createSequentialGroup()
                        .addGroup(panelReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtIdReservacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnBuscarReservacion))
                        .addGap(18, 18, 18)))
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                        .addGroup(panelReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnHacerReservacion)
                            .addComponent(jLabel12))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel33)
                            .addComponent(btnActualizarReservacion))
                        .addGap(12, 12, 12))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelReservationLayout.createSequentialGroup()
                        .addGroup(panelReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelReservationLayout.createSequentialGroup()
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
                                .addComponent(jScrollPane3))
                            .addGroup(panelReservationLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(panelReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnExportarPDF)
                                    .addComponent(jLabel14)
                                    .addComponent(btnBorrarReservacion)
                                    .addComponent(jLabel34))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelReservationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnCancelar)
                                    .addComponent(jLabel9))))
                        .addGap(14, 14, 14))))
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        ModifyUsers.setTitle("Modificar Usuarios");

        jLabel5.setText("Nombre:");

        jLabel15.setText("Contraseña");

        cboUDepartment.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "seleccionar", "Sistemas", "Industrial", "Química", "Electrica", "Electronica", "Gestion", "Ciencias" }));

        btnUInsertar.setText("Insertar");
        btnUInsertar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUInsertarActionPerformed(evt);
            }
        });

        jLabel16.setText("Correo eléctronico:");

        btnUModificar.setText("Modificar");
        btnUModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUModificarActionPerformed(evt);
            }
        });

        jLabel17.setText("Role:");

        btnUEliminar.setText("Eliminar");
        btnUEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUEliminarActionPerformed(evt);
            }
        });

        jLabel18.setText("Departamento:");

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

        btnUBuscar.setText("Buscar");
        btnUBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUBuscarActionPerformed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel20.setText("Datos");

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

        javax.swing.GroupLayout panelModifyUsersLayout = new javax.swing.GroupLayout(panelModifyUsers);
        panelModifyUsers.setLayout(panelModifyUsersLayout);
        panelModifyUsersLayout.setHorizontalGroup(
            panelModifyUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelModifyUsersLayout.createSequentialGroup()
                .addGroup(panelModifyUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelModifyUsersLayout.createSequentialGroup()
                        .addGap(187, 187, 187)
                        .addComponent(jLabel20))
                    .addGroup(panelModifyUsersLayout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addComponent(jLabel21))
                    .addGroup(panelModifyUsersLayout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(panelModifyUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(panelModifyUsersLayout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtUName, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                                    .addComponent(cboURole, javax.swing.GroupLayout.Alignment.TRAILING, 0, 230, Short.MAX_VALUE)
                                    .addComponent(cboUDepartment, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnUModificar))))
                        .addGap(18, 18, 18)
                        .addGroup(panelModifyUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelModifyUsersLayout.createSequentialGroup()
                                .addComponent(btnUEliminar)
                                .addGap(30, 30, 30)
                                .addComponent(btnULimpiar))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelModifyUsersLayout.createSequentialGroup()
                                .addComponent(txtUId, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnUBuscar))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 435, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        panelModifyUsersLayout.setVerticalGroup(
            panelModifyUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelModifyUsersLayout.createSequentialGroup()
                .addGroup(panelModifyUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelModifyUsersLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(panelModifyUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtUId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnUBuscar)))
                    .addGroup(panelModifyUsersLayout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(jLabel21)))
                .addGap(45, 45, 45)
                .addGroup(panelModifyUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelModifyUsersLayout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelModifyUsersLayout.createSequentialGroup()
                        .addGroup(panelModifyUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panelModifyUsersLayout.createSequentialGroup()
                                .addComponent(jLabel20)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel5))
                            .addComponent(txtUName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(panelModifyUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(txtUPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(panelModifyUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtUMail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16))
                        .addGap(18, 18, 18)
                        .addGroup(panelModifyUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelModifyUsersLayout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addGap(55, 55, 55)
                                .addGroup(panelModifyUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel18)
                                    .addComponent(cboUDepartment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(cboURole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                        .addGroup(panelModifyUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnUInsertar)
                            .addComponent(btnUModificar)
                            .addComponent(btnUEliminar)
                            .addComponent(btnULimpiar))
                        .addGap(55, 55, 55))))
        );

        javax.swing.GroupLayout ModifyUsersLayout = new javax.swing.GroupLayout(ModifyUsers.getContentPane());
        ModifyUsers.getContentPane().setLayout(ModifyUsersLayout);
        ModifyUsersLayout.setHorizontalGroup(
            ModifyUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelModifyUsers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        ModifyUsersLayout.setVerticalGroup(
            ModifyUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelModifyUsers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        ModifyLabs.setTitle("Modificar Laboratorios");

        jLabel22.setText("Nombre:");

        jLabel23.setText("Ubcación:");

        btnLInsertar.setText("Insertar");
        btnLInsertar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLInsertarActionPerformed(evt);
            }
        });

        jLabel24.setText("Capacidad:");

        btnLModificar.setText("Modificar");
        btnLModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLModificarActionPerformed(evt);
            }
        });

        jLabel25.setText("Tipo:");

        btnLEliminar.setText("Eliminar");
        btnLEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLEliminarActionPerformed(evt);
            }
        });

        btnLLimpiar.setText("Limpiar");
        btnLLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLLimpiarActionPerformed(evt);
            }
        });

        btnLBuscar.setText("Buscar");
        btnLBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLBuscarActionPerformed(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel26.setText("Datos");

        cboLType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Fisicoquímica", "Ingeniería Civil", "Ingeniería Eléctrica", "Ingeniería Industrial", "Simulación" }));
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

        javax.swing.GroupLayout panelModifyLabsLayout = new javax.swing.GroupLayout(panelModifyLabs);
        panelModifyLabs.setLayout(panelModifyLabsLayout);
        panelModifyLabsLayout.setHorizontalGroup(
            panelModifyLabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelModifyLabsLayout.createSequentialGroup()
                .addGroup(panelModifyLabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelModifyLabsLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnLEliminar)
                        .addGap(18, 18, 18)
                        .addComponent(btnLLimpiar))
                    .addGroup(panelModifyLabsLayout.createSequentialGroup()
                        .addGroup(panelModifyLabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelModifyLabsLayout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addGroup(panelModifyLabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panelModifyLabsLayout.createSequentialGroup()
                                        .addGroup(panelModifyLabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel24)
                                            .addComponent(jLabel25))
                                        .addGap(72, 72, 72)
                                        .addGroup(panelModifyLabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(txtLCapacity, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cboLType, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                                        .addGap(18, 18, 18)
                                        .addComponent(btnLModificar))))
                            .addGroup(panelModifyLabsLayout.createSequentialGroup()
                                .addGap(187, 187, 187)
                                .addComponent(jLabel26)))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelModifyLabsLayout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtLId, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnLBuscar)))
                .addGap(19, 19, 19))
        );
        panelModifyLabsLayout.setVerticalGroup(
            panelModifyLabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelModifyLabsLayout.createSequentialGroup()
                .addGroup(panelModifyLabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelModifyLabsLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(panelModifyLabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnLBuscar)
                            .addComponent(txtLId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelModifyLabsLayout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(jLabel27)))
                .addGap(46, 46, 46)
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
                .addContainerGap(32, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout ModifyLabsLayout = new javax.swing.GroupLayout(ModifyLabs.getContentPane());
        ModifyLabs.getContentPane().setLayout(ModifyLabsLayout);
        ModifyLabsLayout.setHorizontalGroup(
            ModifyLabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ModifyLabsLayout.createSequentialGroup()
                .addComponent(panelModifyLabs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        ModifyLabsLayout.setVerticalGroup(
            ModifyLabsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelModifyLabs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        ModifyMaterials.setTitle("Modificar Materiales");

        panelModifyMaterials.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        panelModifyMaterials.add(txtMIdLab, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 194, 230, -1));

        jLabel28.setText("Nombre:");
        panelModifyMaterials.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 110, -1, -1));

        jLabel29.setText("Cantidad");
        panelModifyMaterials.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 150, -1, -1));

        btnMInsertar.setText("Insertar");
        btnMInsertar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMInsertarActionPerformed(evt);
            }
        });
        panelModifyMaterials.add(btnMInsertar, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 336, -1, -1));

        jLabel30.setText("id Lab");
        panelModifyMaterials.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 190, -1, -1));

        btnMModificar.setText("Modificar");
        btnMModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMModificarActionPerformed(evt);
            }
        });
        panelModifyMaterials.add(btnMModificar, new org.netbeans.lib.awtextra.AbsoluteConstraints(119, 336, -1, -1));
        panelModifyMaterials.add(txtMName, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 110, 290, -1));

        jLabel31.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel31.setText("Datos");
        panelModifyMaterials.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(187, 86, -1, -1));
        panelModifyMaterials.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(391, 37, -1, -1));
        panelModifyMaterials.add(txtMId, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 20, 72, -1));

        btnMBuscar.setText("Buscar");
        btnMBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMBuscarActionPerformed(evt);
            }
        });
        panelModifyMaterials.add(btnMBuscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 20, -1, -1));

        btnMEliminar.setText("Eliminar");
        btnMEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMEliminarActionPerformed(evt);
            }
        });
        panelModifyMaterials.add(btnMEliminar, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 330, -1, -1));

        btnMLimpiar.setText("Limpiar");
        btnMLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMLimpiarActionPerformed(evt);
            }
        });
        panelModifyMaterials.add(btnMLimpiar, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 330, -1, -1));

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

        panelModifyMaterials.add(jScrollPane6, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 100, 328, 210));
        panelModifyMaterials.add(txtMQuantity, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 150, 290, -1));

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

        opcionClaro.setSelected(true);
        opcionClaro.setText("Claro");
        subMenuApariencia.add(opcionClaro);

        opcionOscuro.setSelected(true);
        opcionOscuro.setText("Oscuro");
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

        opcionModificarHorarios.setText("Modificar Horarios");
        menuOpciones.add(opcionModificarHorarios);

        opcionEstadisticas.setText("Ver estádisticas");
        menuOpciones.add(opcionEstadisticas);

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
                String material = materialModel.getValueAt(i, 0).toString();
                int idMaterial = obtenerIdMaterial(material);

                // Verificar si el material ya está asociado a la reservación para evitar duplicados
                if (!existeReservaMaterial(idReservation, idMaterial)) {
                    insertarReservationMaterial(idReservation, idMaterial, 1); // Puedes ajustar la cantidad según sea necesario
                }
            }

            JOptionPane.showMessageDialog(this, "Reservación guardada exitosamente");
            Reservation.setVisible(false);
            //mostrarReservaciones(); // Actualizar la tabla de reservaciones
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
    }//GEN-LAST:event_btnUInsertarActionPerformed

    private void btnUModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUModificarActionPerformed
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
    }//GEN-LAST:event_btnUModificarActionPerformed

    private void btnULimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnULimpiarActionPerformed
        limpiarCamposUsuario();
    }//GEN-LAST:event_btnULimpiarActionPerformed

    private void btnUBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUBuscarActionPerformed
        String idOrUsername = txtUId.getText().trim();
        boolean isNumeric = idOrUsername.matches("\\d+"); // Verifica si la entrada es un número (ID)

        String query;
        if (isNumeric) {
            query = "SELECT id_user, username, password, email, role, department FROM USER WHERE id_user = ?";
        } else {
            query = "SELECT id_user, username, password, email, role, department FROM USER WHERE username = ?";
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, usuario, contrasena);
            ps = conn.prepareStatement(query);
            if (isNumeric) {
                ps.setInt(1, Integer.parseInt(idOrUsername));
            } else {
                ps.setString(1, idOrUsername);
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
        String idOrName = txtLId.getText().trim();
        boolean isNumeric = idOrName.matches("\\d+"); // Verifica si la entrada es un número (ID)

        String query;
        if (isNumeric) {
            query = "SELECT id_lab, name, location, capacity, type FROM LABORATORY WHERE id_lab = ?";
        } else {
            query = "SELECT id_lab, name, location, capacity, type FROM LABORATORY WHERE name = ?";
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, usuario, contrasena);
            ps = conn.prepareStatement(query);
            if (isNumeric) {
                ps.setInt(1, Integer.parseInt(idOrName));
            } else {
                ps.setString(1, idOrName);
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
        String idOrName = txtMId.getText().trim();
        boolean isNumeric = idOrName.matches("\\d+"); // Verifica si la entrada es un número (ID)

        String query;
        if (isNumeric) {
            query = "SELECT id_material, name, quantity, id_lab FROM MATERIAL WHERE id_material = ?";
        } else {
            query = "SELECT id_material, name, quantity, id_lab FROM MATERIAL WHERE name = ?";
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, usuario, contrasena);
            ps = conn.prepareStatement(query);
            if (isNumeric) {
                ps.setInt(1, Integer.parseInt(idOrName));
            } else {
                ps.setString(1, idOrName);
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
        cargarUsuariosEnTabla();
        ModifyUsers.setSize(890, 450);
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
        cargarMaterialesEnTabla();
        ModifyMaterials.setSize(780, 450);
        ModifyMaterials.setLocationRelativeTo(null);
        ModifyMaterials.setVisible(true);
    }//GEN-LAST:event_opcionModificarMaterialActionPerformed

    private void opcionModificarLaboratoriosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcionModificarLaboratoriosActionPerformed
        cargarLaboratoriosEnTabla();
        ModifyLabs.setSize(880, 450);
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
        String queryMaterials = "SELECT M.name FROM MATERIAL M JOIN RESERVATION_MATERIAL RM ON M.id_material = RM.id_material WHERE RM.id_reservation = ?";

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
                    materialModel.addRow(new Object[]{materialName});
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
                insertarReservationMaterial(idReservacion, idMaterial, 1); // Puedes ajustar la cantidad según sea necesario
            }

            JOptionPane.showMessageDialog(this, "Reservación actualizada exitosamente");
            Reservation.setVisible(false);
            //mostrarReservaciones(); // Actualizar la tabla de reservaciones
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
            java.util.logging.Logger.getLogger(DashboardAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DashboardAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DashboardAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DashboardAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
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
                new DashboardAdmin(null).setVisible(true);
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
    private javax.swing.JComboBox<String> cboType;
    private javax.swing.JComboBox<String> cboUDepartment;
    private javax.swing.JComboBox<String> cboURole;
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
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
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
    private javax.swing.JMenuItem opcionModificarHorarios;
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
