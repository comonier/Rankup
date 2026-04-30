package com.comonier;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;

public class Database {

    private final Rankup plugin;
    private Connection connection;

    public Database(Rankup plugin) {
        this.plugin = plugin;
        init();
    }

    private void init() {
        try {
            if (plugin.getConfig().getBoolean("database.mysql.enabled")) {
                String host = plugin.getConfig().getString("database.mysql.host");
                String port = plugin.getConfig().getString("database.mysql.port");
                String db = plugin.getConfig().getString("database.mysql.database");
                String user = plugin.getConfig().getString("database.mysql.username");
                String pass = plugin.getConfig().getString("database.mysql.password");
                
                connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + db + "?useSSL=true&verifyServerCertificate=false", user, pass);
            } else {
                File dbFile = new File(plugin.getDataFolder(), "database.db");
                if (dbFile.exists()) {
                    backupSQLite(dbFile);
                }
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getPath());
            }
            createTable();
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Erro critico no banco de dados!", e);
        }
    }

    private void backupSQLite(File dbFile) {
        File backupDir = new File(plugin.getDataFolder(), "backups");
        if (!backupDir.exists()) backupDir.mkdirs();
        
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
        File backupFile = new File(backupDir, "backup_" + timeStamp + ".db");
        
        try {
            Files.copy(dbFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            plugin.getLogger().warning("Falha ao gerar backup: " + e.getMessage());
        }
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS rankup_players (" +
                     "uuid VARCHAR(36) PRIMARY KEY, " +
                     "rank_id INT DEFAULT 0);";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized int getPlayerRank(UUID uuid) {
        String sql = "SELECT rank_id FROM rankup_players WHERE uuid = ?;";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, uuid.toString());
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("rank_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public synchronized void setPlayerRank(UUID uuid, int rankId) {
        String sql = "REPLACE INTO rankup_players (uuid, rank_id) VALUES (?, ?);";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, uuid.toString());
            st.setInt(2, rankId);
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
