package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;
import conexion.Conexion;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class VentanaPrincipal extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTabbedPane tabbedPane;
    private String username;

    // =========================================================
    // ATRIBUTOS PESTAÑA 1 - EMPLEADOS
    // =========================================================
    private JTextArea textAreaEmpleados;
    private JTextField txtNombreEmpleado;
    private JComboBox<String> comboDepartamento;

    // =========================================================
    // ATRIBUTOS PESTAÑA 2 - PRODUCTOS
    // =========================================================
    private JTextArea textAreaProductos;
    private JTextField txtNombreProducto;
    private JTextField txtPrecio;
    private JTextField txtStock;
    private JComboBox<String> comboAlmacen;
	    
	 // =========================================================
	 // ATRIBUTOS PESTAÑA 3 - STOCK
	 // =========================================================
	 private JTextArea textAreaStock;
	 private JLabel lblAlmacenMax;
	 private JTextArea textAreaSinStock;
	 private JComboBox<String> comboPais;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    VentanaPrincipal frame = new VentanaPrincipal("test", "00:00:00");
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // =========================================================
    // MÉTODOS PESTAÑA 1 - EMPLEADOS
    // =========================================================

    private void cargarEmpleados() {
        Conexion conexion = new Conexion();
        Connection con = conexion.getConnection();
        textAreaEmpleados.setText("");
        try {
            String query = "SELECT e.nombre, d.nombre AS departamento " +
                           "FROM empleados e " +
                           "JOIN departamentos d ON e.id_departamento = d.id " +
                           "ORDER BY e.nombre ASC LIMIT 10";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                textAreaEmpleados.append(rs.getString("nombre") + " - " + rs.getString("departamento") + "\n");
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar empleados: " + e.getMessage());
        } finally {
            conexion.close();
        }
    }

    private void cargarDepartamentos() {
        Conexion conexion = new Conexion();
        Connection con = conexion.getConnection();
        try {
            String query = "SELECT nombre FROM departamentos ORDER BY nombre ASC";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            comboDepartamento.removeAllItems();
            while (rs.next()) {
                comboDepartamento.addItem(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar departamentos: " + e.getMessage());
        } finally {
            conexion.close();
        }
    }

    private void añadirEmpleado() {
        String nombre = txtNombreEmpleado.getText().trim();
        String departamento = (String) comboDepartamento.getSelectedItem();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(null, "El nombre no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Conexion conexion = new Conexion();
        Connection con = conexion.getConnection();
        try {
            String queryDept = "SELECT id FROM departamentos WHERE nombre = ?";
            PreparedStatement psDept = con.prepareStatement(queryDept);
            psDept.setString(1, departamento);
            ResultSet rs = psDept.executeQuery();
            if (rs.next()) {
                int idDept = rs.getInt("id");
                String query = "INSERT INTO empleados (nombre, id_departamento) VALUES (?, ?)";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, nombre);
                ps.setInt(2, idDept);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(null, "Empleado añadido correctamente.");
                txtNombreEmpleado.setText("");
                cargarEmpleados();
            }
        } catch (SQLException e) {
            System.out.println("Error al añadir empleado: " + e.getMessage());
        } finally {
            conexion.close();
        }
    }

    private void eliminarEmpleado() {
        String nombre = txtNombreEmpleado.getText().trim();
        String departamento = (String) comboDepartamento.getSelectedItem(); //nuevo añadido
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Escribe el nombre del empleado a eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Conexion conexion = new Conexion();
        Connection con = conexion.getConnection();
        try {
            String query = "DELETE FROM empleados WHERE nombre = ? " +
                           "AND id_departamento = (SELECT id FROM departamentos WHERE nombre = ?)"; //nuevo añadido. Comparador nomombre con subquery
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, nombre);
            ps.setString(2, departamento); //nuevo añadido
            int filas = ps.executeUpdate();
            if (filas > 0) {
                JOptionPane.showMessageDialog(null, "Empleado eliminado correctamente.");
                txtNombreEmpleado.setText("");
                cargarEmpleados();
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró ningún empleado con ese nombre en ese departamento.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException e) {
            System.out.println("Error al eliminar empleado: " + e.getMessage());
        } finally {
            conexion.close();
        }
    }

    // =========================================================
    // MÉTODOS PESTAÑA 2 - PRODUCTOS
    // =========================================================

    private void cargarProductos() {
        Conexion conexion = new Conexion();
        Connection con = conexion.getConnection();
        textAreaProductos.setText("");
        try {
            String query = "SELECT p.nombre, p.precio, a.nombre AS almacen, sa.stock " +
                           "FROM productos p " +
                           "JOIN stock_almacen sa ON (p.id = sa.id_producto) " +
                           "JOIN almacenes a ON (sa.id_almacen = a.id) " +
                           "ORDER BY p.nombre ASC";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                textAreaProductos.append(rs.getString("nombre") + " - " +
                        rs.getDouble("precio") + "€ - " +
                        rs.getString("almacen") + " - Stock: " +
                        rs.getInt("stock") + "\n");
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar productos: " + e.getMessage());
        } finally {
            conexion.close();
        }
    }

    private void cargarAlmacenes() {
        Conexion conexion = new Conexion();
        Connection con = conexion.getConnection();
        try {
            String query = "SELECT nombre FROM almacenes ORDER BY nombre ASC";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            comboAlmacen.removeAllItems();
            while (rs.next()) {
                comboAlmacen.addItem(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar almacenes: " + e.getMessage());
        } finally {
            conexion.close();
        }
    }

    private void añadirProducto() {
        String nombre = txtNombreProducto.getText().trim();
        String precioStr = txtPrecio.getText().trim();
        String stockStr = txtStock.getText().trim();
        String almacen = (String) comboAlmacen.getSelectedItem();

        if (nombre.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double precio;
        int stock;
        try {
            precio = Double.parseDouble(precioStr);
            stock = Integer.parseInt(stockStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El precio y el stock deben ser números.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (precio < 0) {
            JOptionPane.showMessageDialog(null, "El precio no puede ser negativo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (stock < 0) {
            JOptionPane.showMessageDialog(null, "El stock no puede ser menor a cero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Conexion conexion = new Conexion();
        Connection con = conexion.getConnection();
        try {
            // Comprueba si el producto ya existe
            String queryCheck = "SELECT id FROM productos WHERE nombre = ?";
            PreparedStatement psCheck = con.prepareStatement(queryCheck);
            psCheck.setString(1, nombre);
            ResultSet rsCheck = psCheck.executeQuery();
            if (rsCheck.next()) {
                JOptionPane.showMessageDialog(null, "El producto ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String queryProducto = "INSERT INTO productos (nombre, precio) VALUES (?, ?)";
            PreparedStatement ps = con.prepareStatement(queryProducto, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, nombre);
            ps.setDouble(2, precio);
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int idProducto = keys.getInt(1);
                String queryAlmacen = "SELECT id FROM almacenes WHERE nombre = ?";
                PreparedStatement psAlmacen = con.prepareStatement(queryAlmacen);
                psAlmacen.setString(1, almacen);
                ResultSet rs = psAlmacen.executeQuery();
                if (rs.next()) {
                    int idAlmacen = rs.getInt("id");
                    String queryStock = "INSERT INTO stock_almacen (id_producto, id_almacen, stock) VALUES (?, ?, ?)";
                    PreparedStatement psStock = con.prepareStatement(queryStock);
                    psStock.setInt(1, idProducto);
                    psStock.setInt(2, idAlmacen);
                    psStock.setInt(3, stock);
                    psStock.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Producto añadido correctamente.");
                    txtNombreProducto.setText("");
                    txtPrecio.setText("");
                    txtStock.setText("");
                    cargarProductos();
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al añadir producto: " + e.getMessage());
        } finally {
            conexion.close();
        }
    }

    private void aplicarDescuento() {
        String descuentoStr = JOptionPane.showInputDialog(null, "Introduce el porcentaje de descuento:", "Aplicar descuento", JOptionPane.QUESTION_MESSAGE);
        if (descuentoStr == null || descuentoStr.trim().isEmpty()) return;

        double descuento;
        try {
            descuento = Double.parseDouble(descuentoStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El descuento debe ser un número.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (descuento < 0 || descuento > 100) {
            JOptionPane.showMessageDialog(null, "El descuento debe estar entre 0 y 100.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Conexion conexion = new Conexion();
        Connection con = conexion.getConnection();
        try {
            String query = "UPDATE productos SET precio = precio * (1 - ? / 100)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setDouble(1, descuento);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Descuento del " + descuento + "% aplicado correctamente.");
            cargarProductos();
        } catch (SQLException e) {
            System.out.println("Error al aplicar descuento: " + e.getMessage());
        } finally {
            conexion.close();
        }
    }
    
    
	 // =========================================================
	 // MÉTODOS PESTAÑA 3 - STOCK
	 // =========================================================
	
	 private void cargarPaises() {
	     Conexion conexion = new Conexion();
	     Connection con = conexion.getConnection();
	     try {
	         String query = "SELECT DISTINCT pais FROM almacenes ORDER BY pais ASC";
	         PreparedStatement ps = con.prepareStatement(query);
	         ResultSet rs = ps.executeQuery();
	         comboPais.removeAllItems();
	         while (rs.next()) {
	             comboPais.addItem(rs.getString("pais"));
	         }
	     } catch (SQLException e) {
	         System.out.println("Error al cargar paises: " + e.getMessage());
	     } finally {
	         conexion.close();
	     }
	 }
	
	 private void cargarStockPorPais() {
	     String pais = (String) comboPais.getSelectedItem();
	     if (pais == null) return;
	
	     Conexion conexion = new Conexion();
	     Connection con = conexion.getConnection();
	     textAreaStock.setText("");
	
	     try {
	         // Stock total por producto filtrado por país
	         String queryStock = "SELECT p.nombre, SUM(sa.stock) AS stock_total " +
	                             "FROM productos p " +
	                             "JOIN stock_almacen sa ON (p.id = sa.id_producto) " +
	                             "JOIN almacenes a ON (sa.id_almacen = a.id) " +
	                             "WHERE a.pais = ? " +
	                             "GROUP BY p.nombre " +
	                             "ORDER BY p.nombre ASC";
	         PreparedStatement ps = con.prepareStatement(queryStock);
	         ps.setString(1, pais);
	         ResultSet rs = ps.executeQuery();
	         while (rs.next()) {
	             textAreaStock.append(rs.getString("nombre") + " - Stock total: " + rs.getInt("stock_total") + "\n");
	         }
	
	         // Almacén con más unidades en el país seleccionado
	         String queryMax = "SELECT a.nombre, SUM(sa.stock) AS total " +
	                           "FROM almacenes a " +
	                           "JOIN stock_almacen sa ON (a.id = sa.id_almacen) " +
	                           "WHERE a.pais = ? " +
	                           "GROUP BY a.nombre " +
	                           "ORDER BY total DESC LIMIT 1";
	         PreparedStatement psMax = con.prepareStatement(queryMax);
	         psMax.setString(1, pais);
	         ResultSet rsMax = psMax.executeQuery();
	         if (rsMax.next()) {
	             lblAlmacenMax.setText("Almacén con más stock: " + rsMax.getString("nombre") + " (" + rsMax.getInt("total") + " unidades)");
	         } else {
	             lblAlmacenMax.setText("Almacén con más stock: ninguno");
	         }
	
	     } catch (SQLException e) {
	         System.out.println("Error al cargar stock por pais: " + e.getMessage());
	     } finally {
	         conexion.close();
	     }
	 }
	
	 private void cargarAlmacenesSinStock() {
	     Conexion conexion = new Conexion();
	     Connection con = conexion.getConnection();
	     textAreaSinStock.setText("");
	     try {
	         String query = "SELECT a.nombre FROM almacenes a " +
	                        "LEFT JOIN stock_almacen sa ON (a.id = sa.id_almacen) " +
	                        "WHERE sa.id_almacen IS NULL";
	         PreparedStatement ps = con.prepareStatement(query);
	         ResultSet rs = ps.executeQuery();
	         while (rs.next()) {
	             textAreaSinStock.append(rs.getString("nombre") + "\n");
	         }
	     } catch (SQLException e) {
	         System.out.println("Error al cargar almacenes sin stock: " + e.getMessage());
	     } finally {
	         conexion.close();
	     }
	 }

    // =========================================================
    // CONSTRUCTOR - DISEÑO DE LA VENTANA
    // =========================================================

    public VentanaPrincipal(String username, String hora) {
        this.username = username;
        setTitle("Ventana Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 700, 500);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        contentPane.add(tabbedPane, BorderLayout.CENTER);

        boolean esAdmin = username.equals("admin");

        // ---- PESTAÑA 1 - EMPLEADOS ----
        JPanel panel1 = new JPanel();
        panel1.setLayout(null);
        tabbedPane.addTab("Uno", null, panel1, null);

        JLabel lblUsuario = new JLabel("Usuario: " + username);
        lblUsuario.setForeground(new Color(255, 0, 0));
        lblUsuario.setHorizontalAlignment(SwingConstants.RIGHT);
        lblUsuario.setBounds(390, 10, 250, 20);
        panel1.add(lblUsuario);

        JLabel lblHora = new JLabel("Hora de acceso: " + hora);
        lblHora.setForeground(new Color(255, 0, 0));
        lblHora.setBounds(10, 326, 250, 20);
        panel1.add(lblHora);

        JLabel lblLista = new JLabel("Lista de empleados:");
        lblLista.setBounds(10, 10, 150, 20);
        panel1.add(lblLista);

        textAreaEmpleados = new JTextArea();
        textAreaEmpleados.setEditable(false);
        JScrollPane scrollPane1 = new JScrollPane(textAreaEmpleados);
        scrollPane1.setBounds(10, 35, 250, 270);
        panel1.add(scrollPane1);

        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(290, 35, 80, 20);
        panel1.add(lblNombre);

        txtNombreEmpleado = new JTextField();
        txtNombreEmpleado.setBounds(370, 35, 150, 20);
        panel1.add(txtNombreEmpleado);

        JLabel lblDept = new JLabel("Departamento:");
        lblDept.setBounds(290, 70, 100, 20);
        panel1.add(lblDept);

        comboDepartamento = new JComboBox<>();
        comboDepartamento.setBounds(390, 70, 130, 20);
        panel1.add(comboDepartamento);

        JButton btnAnadir = new JButton("Añadir");
        btnAnadir.setBounds(290, 110, 100, 25);
        panel1.add(btnAnadir);

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(400, 110, 100, 25);
        panel1.add(btnEliminar);

        lblNombre.setVisible(esAdmin);
        txtNombreEmpleado.setVisible(esAdmin);
        lblDept.setVisible(esAdmin);
        comboDepartamento.setVisible(esAdmin);
        btnAnadir.setVisible(esAdmin);
        btnEliminar.setVisible(esAdmin);

        btnAnadir.addActionListener(e -> añadirEmpleado());
        btnEliminar.addActionListener(e -> eliminarEmpleado());

        // ---- PESTAÑA 2 - PRODUCTOS ----
        JPanel panel2 = new JPanel();
        panel2.setLayout(null);
        tabbedPane.addTab("Dos", null, panel2, null);

        JLabel lblListaProductos = new JLabel("Lista de productos:");
        lblListaProductos.setBounds(10, 10, 150, 20);
        panel2.add(lblListaProductos);

        textAreaProductos = new JTextArea();
        textAreaProductos.setEditable(false);
        JScrollPane scrollPane2 = new JScrollPane(textAreaProductos);
        scrollPane2.setBounds(10, 35, 350, 370);
        panel2.add(scrollPane2);

        JLabel lblNombreProducto = new JLabel("Nombre:");
        lblNombreProducto.setBounds(390, 35, 80, 20);
        panel2.add(lblNombreProducto);

        txtNombreProducto = new JTextField();
        txtNombreProducto.setBounds(470, 35, 150, 20);
        panel2.add(txtNombreProducto);

        JLabel lblPrecio = new JLabel("Precio (€):");
        lblPrecio.setBounds(390, 70, 80, 20);
        panel2.add(lblPrecio);

        txtPrecio = new JTextField();
        txtPrecio.setBounds(470, 70, 150, 20);
        panel2.add(txtPrecio);

        JLabel lblAlmacen = new JLabel("Almacen:");
        lblAlmacen.setBounds(390, 105, 80, 20);
        panel2.add(lblAlmacen);

        comboAlmacen = new JComboBox<>();
        comboAlmacen.setBounds(470, 105, 150, 20);
        panel2.add(comboAlmacen);

        JLabel lblStock = new JLabel("Stock:");
        lblStock.setBounds(390, 140, 80, 20);
        panel2.add(lblStock);

        txtStock = new JTextField();
        txtStock.setBounds(470, 140, 150, 20);
        panel2.add(txtStock);

        JButton btnAnadirProducto = new JButton("Añadir producto");
        btnAnadirProducto.setBounds(390, 180, 150, 25);
        panel2.add(btnAnadirProducto);

        JButton btnDescuento = new JButton("Aplicar descuento");
        btnDescuento.setBounds(390, 220, 150, 25);
        panel2.add(btnDescuento);

        lblNombreProducto.setVisible(esAdmin);
        txtNombreProducto.setVisible(esAdmin);
        lblPrecio.setVisible(esAdmin);
        txtPrecio.setVisible(esAdmin);
        lblAlmacen.setVisible(esAdmin);
        comboAlmacen.setVisible(esAdmin);
        lblStock.setVisible(esAdmin);
        txtStock.setVisible(esAdmin);
        btnAnadirProducto.setVisible(esAdmin);
        btnDescuento.setVisible(esAdmin);

        btnAnadirProducto.addActionListener(e -> añadirProducto());
        btnDescuento.addActionListener(e -> aplicarDescuento());

     // ---- PESTAÑA 3 - STOCK ----
        JPanel panel3 = new JPanel();
        panel3.setLayout(null);
        tabbedPane.addTab("Tres", null, panel3, null);

        JLabel lblPais = new JLabel("País:");
        lblPais.setBounds(10, 10, 40, 20);
        panel3.add(lblPais);

        comboPais = new JComboBox<>();
        comboPais.setBounds(55, 10, 150, 20);
        panel3.add(comboPais);

        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.setBounds(215, 10, 80, 20);
        panel3.add(btnFiltrar);

        JLabel lblStockTotal = new JLabel("Stock por producto:");
        lblStockTotal.setBounds(10, 45, 150, 20);
        panel3.add(lblStockTotal);

        textAreaStock = new JTextArea();
        textAreaStock.setEditable(false);
        JScrollPane scrollStock = new JScrollPane(textAreaStock);
        scrollStock.setBounds(10, 70, 280, 270);
        panel3.add(scrollStock);

        lblAlmacenMax = new JLabel("Almacén con más stock: -");
        lblAlmacenMax.setBounds(310, 70, 350, 20);
        panel3.add(lblAlmacenMax);

        JLabel lblSinStock = new JLabel("Almacenes sin productos:");
        lblSinStock.setBounds(310, 110, 200, 20);
        panel3.add(lblSinStock);

        textAreaSinStock = new JTextArea();
        textAreaSinStock.setEditable(false);
        JScrollPane scrollSinStock = new JScrollPane(textAreaSinStock);
        scrollSinStock.setBounds(310, 135, 280, 200);
        panel3.add(scrollSinStock);

        btnFiltrar.addActionListener(e -> cargarStockPorPais());

        cargarPaises();
        cargarAlmacenesSinStock();

        // Carga inicial de datos
        cargarEmpleados();
        cargarProductos();
        if (esAdmin) {
            cargarDepartamentos();
            cargarAlmacenes();
        }
    }
}