import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class neil extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtName, txtPrice, txtCategory;
    private JTable table;
    private DefaultTableModel model;

    private static final String URL = "jdbc:mysql://localhost:3306/neil_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private Connection conn;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                neil frame = new neil();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public neil() {
        setTitle("Restaurant Menu System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 700, 400);
        contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);

        JPanel panelTop = new JPanel(new GridLayout(2, 4, 10, 10));
        txtName = new JTextField();
        txtPrice = new JTextField();
        txtCategory = new JTextField();
        panelTop.add(new JLabel("Name:"));
        panelTop.add(txtName);
        panelTop.add(new JLabel("Price:"));
        panelTop.add(txtPrice);
        panelTop.add(new JLabel("Category:"));
        panelTop.add(txtCategory);

        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        panelTop.add(btnAdd);
        panelTop.add(btnUpdate);
        contentPane.add(panelTop, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "Name", "Price", "Category"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        contentPane.add(scrollPane, BorderLayout.CENTER);
        contentPane.add(btnDelete, BorderLayout.SOUTH);

        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            loadMenuItems();
        } catch (SQLException e) {
            showMessage("Database connection failed: " + e.getMessage());
        }

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int i = table.getSelectedRow();
                if (i >= 0) {
                    txtName.setText(model.getValueAt(i, 1).toString());
                    txtPrice.setText(model.getValueAt(i, 2).toString());
                    txtCategory.setText(model.getValueAt(i, 3).toString());
                }
            }
        });git

        btnAdd.addActionListener(e -> addMenuItem());
        btnUpdate.addActionListener(e -> updateMenuItem());
        btnDelete.addActionListener(e -> deleteMenuItem());
    }

    void loadMenuItems() {
        try {
            model.setRowCount(0);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM MenuItems");
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("MenuItemID"),
                        rs.getString("Name"),
                        rs.getDouble("Price"),
                        rs.getString("Category")
                });
            }
        } catch (SQLException e) {
            showMessage("Load failed: " + e.getMessage());
        }
    }

    void addMenuItem() {
        String name = txtName.getText();
        String priceText = txtPrice.getText();
        String category = txtCategory.getText();

        if (name.isEmpty() || priceText.isEmpty()) {
            showMessage("Name and Price are required.");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            PreparedStatement pst = conn.prepareStatement("INSERT INTO MenuItems (Name, Price, Category) VALUES (?, ?, ?)");
            pst.setString(1, name);
            pst.setDouble(2, price);
            pst.setString(3, category);
            pst.executeUpdate();
            loadMenuItems();
            clearFields();
        } catch (Exception e) {
            showMessage("Insert failed: " + e.getMessage());
        }
    }

    void updateMenuItem() {
        int row = table.getSelectedRow();
        if (row == -1) {
            showMessage("Select an item to update.");
            return;
        }

        try {
            int id = (int) model.getValueAt(row, 0);
            String name = txtName.getText();
            double price = Double.parseDouble(txtPrice.getText());
            String category = txtCategory.getText();

            PreparedStatement pst = conn.prepareStatement("UPDATE MenuItems SET Name=?, Price=?, Category=? WHERE MenuItemID=?");
            pst.setString(1, name);
            pst.setDouble(2, price);
            pst.setString(3, category);
            pst.setInt(4, id);
            pst.executeUpdate();
            loadMenuItems();
            clearFields();
        } catch (Exception e) {
            showMessage("Update failed: " + e.getMessage());
        }
    }

    void deleteMenuItem() {
        int row = table.getSelectedRow();
        if (row == -1) {
            showMessage("Select an item to delete.");
            return;
        }

        try {
            int id = (int) model.getValueAt(row, 0);
            PreparedStatement pst = conn.prepareStatement("DELETE FROM MenuItems WHERE MenuItemID=?");
            pst.setInt(1, id);
            pst.executeUpdate();
            loadMenuItems();
            clearFields();
        } catch (Exception e) {
            showMessage("Delete failed: " + e.getMessage());
        }
    }

    void clearFields() {
        txtName.setText("");
        txtPrice.setText("");
        txtCategory.setText("");
    }

    void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }
}