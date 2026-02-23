package org.openjfx;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DAO {
    static {
        try {
            Class.forName(JDBCTools.DB_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("数据库驱动加载失败", e);
        }
    }

    // 获取数据库连接
    private Connection getConnection() throws SQLException {   // 抛出sql的异常
        return DriverManager.getConnection(
                JDBCTools.DB_URL,       // 加载URL
                JDBCTools.DB_USER,      // 加载数据库账号
                JDBCTools.DB_PWD        // 加载密码
        );
    }

    // 登录验证
    public boolean login(String username, String password) {
        //从阿里云数据库中验证账户密码
        String sql = "SELECT id FROM librarian WHERE username=? AND password=?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("登录验证失败", e);
        }
    }

    // 注册新用户
    public boolean register(String username, String password) {
        // 检查用户名是否存在
        String checkSql = "SELECT id FROM librarian WHERE username=?";
        try (Connection conn = getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                return false; // 用户名已存在
            }
            // 从阿里云数据库中插入新用户与密码
            String insertSql = "INSERT INTO librarian (username, password) VALUES (?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, password);
                insertStmt.executeUpdate();
                return true;
            }

        } catch (SQLException e) {
            throw new RuntimeException("注册用户失败", e);
        }
    }

    // 加载所有图书数据
    public List<Object[]> load() {

        List<Object[]> books = new ArrayList<>();// Object可以兼容所有数据格式
        //查询阿里云库的数据
        String sql = "SELECT id, name, author, publisher, count FROM books";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                books.add(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("author"),
                        rs.getString("publisher"),
                        rs.getInt("count")
                });
            }

        } catch (SQLException e) {
            throw new RuntimeException("加载图书数据失败", e);
        }
        return books;
    }

    // 搜索图书
    public List<Object[]> search(String keyword) {
        List<Object[]> books = new ArrayList<>();
        String sql = keyword.isEmpty() ?
                "SELECT id, name, author, publisher, count FROM books" :
                "SELECT id, name, author, publisher, count FROM books WHERE name LIKE ? OR author LIKE ?";  //模糊匹配LIKE

        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
                if (!keyword.isEmpty()) {
                    pst.setString(1, "%" + keyword + "%");   // %模糊查询的通配符，表示无线长度的字符
                    pst.setString(2, "%" + keyword + "%");
                }
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                books.add(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("author"),
                        rs.getString("publisher"),
                        rs.getInt("count")
                });
            }
        } catch (SQLException e) {
            throw new RuntimeException("搜索图书失败", e);
        }
        return books;
    }

    // 添加图书
    public boolean add(String name, String author, String publisher, int count) {
        String sql = "INSERT INTO books (name, author, publisher, count) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, name);
                pstmt.setString(2, author);
                pstmt.setString(3, publisher);
                pstmt.setInt(4, count);
                return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("添加图书失败", e);
        }
    }

    // 更新图书
    public boolean update(int id, String name, String author, String publisher, int count) {
        String sql = "UPDATE books SET name=?, author=?, publisher=?, count=? WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setString(2, author);
                pstmt.setString(3, publisher);
                pstmt.setInt(4, count);
                pstmt.setInt(5, id);
                return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("更新图书失败", e);
        }
    }

    // 删除图书
    public boolean delete(int id) {
        String sql = "DELETE FROM books WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("删除图书失败", e);
        }
    }
}
