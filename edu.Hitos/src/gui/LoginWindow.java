package gui;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JTextPane;
import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;
import conexion.Conexion;

public class LoginWindow extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtHito;
    private JPasswordField passwordField;
    private JTextField textField;
    private int intentos = 0;
    private static final int MAX_INTENTOS = 3;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    LoginWindow frame = new LoginWindow();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private int validarCredenciales(String username, String passwd) {
        Conexion conexion = new Conexion();
        Connection con = conexion.getConnection();
        int userId = -1;
        try {
            String query = "SELECT id FROM userData WHERE username = ? AND passwd = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, passwd);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                userId = rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println("Error al validar credenciales: " + e.getMessage());
        } finally {
            conexion.close();
        }
        return userId;
    }

    private void registrarLogin(int userId) {
        Conexion conexion = new Conexion();
        Connection con = conexion.getConnection();
        try {
            String query = "INSERT INTO loginLog (user_id, fecha_hora) VALUES (?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, userId);
            ps.setObject(2, LocalDateTime.now());
            ps.executeUpdate();
            System.out.println("Login registrado correctamente");
        } catch (SQLException e) {
            System.out.println("Error al registrar login: " + e.getMessage());
        } finally {
            conexion.close();
        }
    }

    public LoginWindow() {
        setBackground(new Color(0, 0, 64));
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 457, 297);
        contentPane = new JPanel();
        contentPane.setBackground(new Color(255, 255, 255));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        txtHito = new JTextField();
        txtHito.setEditable(false);
        txtHito.setFont(new Font("Arial Black", Font.PLAIN, 13));
        txtHito.setText("Hito yo que sé que es esto");
        txtHito.setBounds(23, 10, 238, 18);
        contentPane.add(txtHito);
        txtHito.setColumns(10);

        JTextPane passwd = new JTextPane();
        passwd.setText("Contraseña:");
        passwd.setFont(new Font("Arial", Font.PLAIN, 16));
        passwd.setEditable(false);
        passwd.setBounds(23, 173, 107, 25);
        contentPane.add(passwd);

        passwordField = new JPasswordField();
        passwordField.setBounds(159, 173, 139, 18);
        contentPane.add(passwordField);

        JTextPane user = new JTextPane();
        user.setText("Usuario:");
        user.setFont(new Font("Arial", Font.PLAIN, 16));
        user.setEditable(false);
        user.setBounds(23, 95, 107, 25);
        contentPane.add(user);

        textField = new JTextField();
        textField.setBounds(159, 102, 139, 18);
        contentPane.add(textField);
        textField.setColumns(10);

        JButton btnNewButton = new JButton("Iniciar");
        btnNewButton.setBounds(298, 207, 84, 20);
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (intentos >= MAX_INTENTOS) {
                    JOptionPane.showMessageDialog(null, "Acceso bloqueado. Reinicia la aplicación.", "Acceso bloqueado", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String username = textField.getText();
                String passwd = new String(passwordField.getPassword());
                int userId = validarCredenciales(username, passwd);

                if (userId != -1) {
                    registrarLogin(userId);
                    String hora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                    new VentanaPrincipal(username, hora).setVisible(true);
                    dispose();
                } else {
                    intentos++;
                    JOptionPane.showMessageDialog(null, "Credenciales incorrectas. Intentos restantes: " + (MAX_INTENTOS - intentos), "Error de login", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        contentPane.add(btnNewButton);
    }
}