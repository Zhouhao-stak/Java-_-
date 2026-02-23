package org.openjfx;

import javax.swing.*;
import javax.swing.border.EmptyBorder;       // 空边框
import javax.swing.table.DefaultTableModel;    //  调用表格模型工具的包
import java.awt.*;

public class UI {
    private JFrame loginFrame, mainFrame;
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, authorField, publisherField, countField, idField, searchField, usernameField;
    private JPasswordField passwordField;
    private BookSystemService service;

    public UI(BookSystemService service) {
        this.service = service;    // 传递成员变量
    }

    // 登录
    public void showLoginUI() {
        loginFrame = new JFrame("图书管理系统 - 登录");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(500, 400); // 调整窗口大小
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));  //BorderLayout界面布局方法
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(Color.WHITE);

        // 标题
        JLabel titleLabel = new JLabel("图书管理系统");   // 创建标题
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));   // 字体   BOLD加粗
        titleLabel.setForeground(new Color(0, 68, 204)); // 使用蓝色作为主题色
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);  //setHorizontalAlignment()水平对齐方式
        mainPanel.add(titleLabel, BorderLayout.NORTH);  //

        // 表单
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10); // 增加间距
        gbc.anchor = GridBagConstraints.WEST;

        // 用户名
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("用户名:");
        usernameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        formPanel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        usernameField.setPreferredSize(new Dimension(200, 35)); // 设置输入框大小
        formPanel.add(usernameField, gbc);

        // 密码
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel passwordLabel = new JLabel("密码:");
        passwordLabel.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;   // 水平方向填补
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        passwordField.setPreferredSize(new Dimension(200, 35)); // 设置输入框大小
        formPanel.add(passwordField, gbc);

        // 添加一个空标签用于对齐
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel(), gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // 按钮区域
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0)); // 增加按钮间距
        btnPanel.setBackground(Color.WHITE);
        JButton loginBtn = createPrimaryButton("登录");
        JButton registerBtn = createNormalButton("注册");

        loginBtn.addActionListener(e -> {
            String username = getUsername();
            String password = getPassword();
            if (username.isEmpty() || password.isEmpty()) {
                showWarning("请输入用户名和密码！");
                return;
            }
            service.doLogin(username, password);
        });

        registerBtn.addActionListener(e -> {
            String username = getUsername();
            String password = getPassword();
            if (username.isEmpty() || password.isEmpty()) {
                showWarning("请输入用户名和密码！");
                return;
            }
            service.doRegister(username, password);
        });

        btnPanel.add(loginBtn);
        btnPanel.add(registerBtn);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        // 添加背景装饰
        JPanel Wrapper = new JPanel(new BorderLayout());
        Wrapper.setBackground(new Color(240, 248, 255));
        Wrapper.setBorder(new EmptyBorder(20, 20, 20, 20));
        Wrapper.add(mainPanel, BorderLayout.CENTER);

        loginFrame.setContentPane(Wrapper);
        loginFrame.setVisible(true);
    }

    // 主界面
    public void showMainUI() {
        mainFrame = new JFrame("图书管理系统");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1200, 900);
        mainFrame.setLocationRelativeTo(null);

        // 顶部标题栏
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(0, 102, 204)); // 使用蓝色作为主题色
        headerPanel.setBorder(new EmptyBorder(10, 15, 10, 10));
        JLabel headerLabel = new JLabel("图书信息管理");
        headerLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        mainFrame.add(headerPanel, BorderLayout.NORTH);

        // 表格区域
        String[] columns = {"ID", "书名", "作者", "出版社", "库存"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookTable = new JTable(tableModel);
        initTableStyle();
        mainFrame.add(new JScrollPane(bookTable), BorderLayout.CENTER);

        // 底部操作区
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 表单区域
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.anchor = GridBagConstraints.WEST;

        // 隐藏ID字段
        idField = new JTextField(8);
        idField.setVisible(false);

        // 书名
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("书名:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        nameField = new JTextField(12);
        nameField.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        formPanel.add(nameField, gbc);

        // 作者
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("作者:"), gbc);
        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        authorField = new JTextField(12);
        authorField.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        formPanel.add(authorField, gbc);

        // 出版社
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("出版社:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        publisherField = new JTextField(12);
        publisherField.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        formPanel.add(publisherField, gbc);

        // 库存
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("库存:"), gbc);
        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        countField = new JTextField(12);
        countField.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        formPanel.add(countField, gbc);

        bottomPanel.add(formPanel, BorderLayout.NORTH);

        // 按钮和搜索区
        JPanel controlPanel = new JPanel(new BorderLayout());

        // 搜索区
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        searchPanel.add(new JLabel("搜索:"));
        searchField = new JTextField(20);
        searchField.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        JButton searchBtn = createNormalButton("查询");
        searchBtn.addActionListener(e -> service.doSearch(getSearchKeyword()));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        controlPanel.add(searchPanel, BorderLayout.WEST);

        // 操作按钮
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton addBtn = createPrimaryButton("添加");
        JButton updateBtn = createNormalButton("更新");
        JButton deleteBtn = createNormalButton("删除");
        JButton resetBtn = createNormalButton("重置");

        addBtn.addActionListener(e -> service.doAdd(
                getName(), getAuthor(), getPublisher(), getCountStr()
        ));
        updateBtn.addActionListener(e -> service.doUpdate(
                getId(), getName(), getAuthor(), getPublisher(), getCountStr()
        ));
        deleteBtn.addActionListener(e -> service.doDelete(getId()));
        resetBtn.addActionListener(e -> resetForm());

        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(resetBtn);
        controlPanel.add(btnPanel, BorderLayout.EAST);

        bottomPanel.add(controlPanel, BorderLayout.SOUTH);
        mainFrame.add(bottomPanel, BorderLayout.SOUTH);

        // 表格选择事件
        bookTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && bookTable.getSelectedRow() != -1) {
                loadRowData(bookTable.getSelectedRow());
            }
        });

        // 加载数据
        service.loadBookData();
        mainFrame.setVisible(true);
    }

    // 创建主要按钮（登录/添加）
    public JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("微软雅黑", Font.BOLD, 20));
        btn.setPreferredSize(new Dimension(120, 40)); // 调整按钮大小
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(0, 102, 204)); // 蓝色主题
        btn.setBackground(new Color(0, 86, 174));
        return btn;
    }

    // 创建普通按钮
    public JButton createNormalButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("微软雅黑", Font.PLAIN, 20));
        btn.setPreferredSize(new Dimension(120, 40)); // 调整按钮大小
        btn.setForeground(new Color(0, 102, 204)); // 蓝色文字
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204))); // 蓝色边框
        return btn;
    }

    // 初始化表格样式
    private void initTableStyle() {
        bookTable.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        bookTable.setRowHeight(30);
        bookTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 18));
        bookTable.getTableHeader().setBackground(new Color(240, 240, 240));
        bookTable.setShowGrid(true);
        bookTable.setGridColor(new Color(220, 220, 220));
    }

    // 加载选中行数据到表单
    private void loadRowData(int row) {
        idField.setText(tableModel.getValueAt(row, 0).toString());
        nameField.setText(tableModel.getValueAt(row, 1).toString());
        authorField.setText(tableModel.getValueAt(row, 2).toString());
        publisherField.setText(tableModel.getValueAt(row, 3).toString());
        countField.setText(tableModel.getValueAt(row, 4).toString());
    }

    // 重置表单
    public void resetForm() {
        idField.setText("");
        nameField.setText("");
        authorField.setText("");
        publisherField.setText("");
        countField.setText("");
        if (bookTable != null) {
            bookTable.clearSelection();
        }
    }

    // 获取当前活动窗口
    public JFrame getActiveFrame() {
        return (loginFrame != null && loginFrame.isVisible()) ? loginFrame : mainFrame;
    }

    // 表单数据获取方法
    public String getUsername() {
        return usernameField != null ? usernameField.getText().trim() : "";
    }

    public String getPassword() {
        return passwordField != null ? new String(passwordField.getPassword()).trim() : "";
    }

    public String getName() {
        return nameField != null ? nameField.getText().trim() : "";
    }

    public String getAuthor() {
        return authorField != null ? authorField.getText().trim() : "";
    }

    public String getPublisher() {
        return publisherField != null ? publisherField.getText().trim() : "";
    }

    public String getCountStr() {
        return countField != null ? countField.getText().trim() : "";
    }

    public String getId() {
        return idField != null ? idField.getText().trim() : "";
    }

    public String getSearchKeyword() {
        return searchField != null ? searchField.getText().trim() : "";
    }

    // 关闭登录窗口
    public void closeLoginFrame() {
        if (loginFrame != null) {
            loginFrame.dispose();
        }
    }

    // 更新表格数据
    public void updateTableData(Object[][] data) {
        tableModel.setRowCount(0);
        for (Object[] row : data) {
            tableModel.addRow(row);
        }
    }

    // 消息提示封装
    public void showInfo(String msg) {
        JOptionPane.showMessageDialog(getActiveFrame(), msg, "提示", JOptionPane.INFORMATION_MESSAGE);  //INFORMATION_MESSAGE红色的叉
    }

    public void showWarning(String msg) {
        JOptionPane.showMessageDialog(getActiveFrame(), msg, "警告", JOptionPane.WARNING_MESSAGE);
    }

    public void showError(String msg) {
        JOptionPane.showMessageDialog(getActiveFrame(), msg, "错误", JOptionPane.ERROR_MESSAGE);
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }
}



