package org.openjfx;

import javax.swing.*;
import java.util.List;

public class BookSystemService {
    private DAO dao;
    private UI ui;

    public BookSystemService() {
        this.dao = new DAO();
        this.ui = new UI(this);
    }

    // 初始化
    public void init() {
        try {
            // 加载数据库驱动
            ui.showLoginUI();
        } catch (RuntimeException e) {
            ui.showError("数据库驱动加载失败，请检查驱动是否正确添加");
            e.printStackTrace();
        }
    }

    // 登录
    public void doLogin(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            ui.showWarning("请输入用户名和密码");
            return;
        }
        try {
            if (dao.login(username, password)) {
                ui.closeLoginFrame();
                ui.showMainUI();
            } else {
                ui.showError("用户名或密码错误");
            }
        } catch (RuntimeException e) {
            ui.showError("登录失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 注册
    public void doRegister(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            ui.showWarning("请输入用户名和密码");
            return;
        }
        try {
            if (dao.register(username, password)) {
                ui.showInfo("注册成功，请登录");
            } else {
                ui.showError("用户名已存在");
            }
        } catch (RuntimeException e) {
            ui.showError("注册失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 加载图书数据
    public void loadBookData() {
        try {
            List<Object[]> books = dao.load();
            ui.updateTableData(books.toArray(new Object[0][]));
        } catch (RuntimeException e) {
            ui.showError("加载数据失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 添加图书
    public void doAdd(String name, String author, String publisher, String countStr) {
        if (name.isEmpty() || author.isEmpty() || publisher.isEmpty() || countStr.isEmpty()) {
            ui.showWarning("请填写完整信息");
            return;
        }
        try {
            int count = Integer.parseInt(countStr);
            if (dao.add(name, author, publisher, count)) {
                ui.showInfo("添加成功");
                ui.resetForm();
                loadBookData();
            }
        } catch (NumberFormatException e) {
            ui.showError("库存必须是数字");
        } catch (RuntimeException e) {
            ui.showError("添加失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 更新图书
    public void doUpdate(String idStr, String name, String author, String publisher, String countStr) {
        if (idStr.isEmpty()) {
            ui.showWarning("请选择要更新的记录");
            return;
        }
        if (name.isEmpty() || author.isEmpty() || publisher.isEmpty() || countStr.isEmpty()) {
            ui.showWarning("请填写完整信息");
            return;
        }
        try {
            int id = Integer.parseInt(idStr);
            int count = Integer.parseInt(countStr);
            if (dao.update(id, name, author, publisher, count)) {
                ui.showInfo("更新成功");
                ui.resetForm();
                loadBookData();
            } else {
                ui.showError("更新失败，记录不存在");
            }
        } catch (NumberFormatException e) {
            ui.showError("ID或库存必须是数字");
        } catch (RuntimeException e) {
            ui.showError("更新失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 删除图书
    public void doDelete(String idStr) {
        if (idStr.isEmpty()) {
            ui.showWarning("请选择要删除的记录");
            return;
        }
        if (JOptionPane.showConfirmDialog(ui.getActiveFrame(), "确定要删除这条记录吗?", "确认", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            int id = Integer.parseInt(idStr);
            if (dao.delete(id)) {
                ui.showInfo("删除成功");
                ui.resetForm();
                loadBookData();
            } else {
                ui.showError("删除失败，记录不存在");
            }
        } catch (NumberFormatException e) {
            ui.showError("ID必须是数字");
        } catch (RuntimeException e) {
            ui.showError("删除失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 搜索图书
    public void doSearch(String keyword) {
        try {
            List<Object[]> books = dao.search(keyword);
            ui.updateTableData(books.toArray(new Object[0][]));
        } catch (RuntimeException e) {
            ui.showError("搜索失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // 程序入口
    public static void main(String[] args) {
        new BookSystemService().init();
    }
}

