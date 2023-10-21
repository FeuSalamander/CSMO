package feusalamander.cs_mo.Managers;

import it.unimi.dsi.fastutil.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static feusalamander.cs_mo.CS_MO.main;

public class MySQL {
    private Connection connection;
    public MySQL() {
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            this.connect();
            this.createTable();
        });
    }
    private void connect(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection(main.getConf().toURL(),
                    main.getConf().getUser(),
                    main.getConf().getPass());
            main.getLogger().info("Successfully connected to MySQL");
        } catch (SQLException | ClassNotFoundException e) {
            main.getLogger().info(e.toString());
        }
    }
    private void createTable() {
        try {
            String query = "SELECT 1 FROM player_stats LIMIT 1";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.execute();
            } catch (SQLException e) {
                final PreparedStatement preparedStatement = connection.prepareStatement(
                        "CREATE TABLE `player_stats` (" +
                                "  `uuid` varchar(36) NOT NULL," +
                                "  `name` varchar(16) NOT NULL," +
                                "  `elo` int(11) NOT NULL," +
                                "  `kills` int(11) NOT NULL," +
                                "  `deaths` int(11) NOT NULL," +
                                "  `wins` int(11) NOT NULL," +
                                "  `looses` int(11) NOT NULL, " +
                                "PRIMARY KEY(uuid)" +
                                ") ENGINE=InnoDB DEFAULT CHARSET=latin1;");
                preparedStatement.executeUpdate();
                preparedStatement.close();
            }
        } catch (SQLException e) {
            main.getLogger().info(e.toString());
        }
    }
    public void close(){
        try{
            if(this.connection != null){
                if(!this.connection.isClosed()){
                    this.connection.close();
                }
            }
        } catch (SQLException e) {
            main.getLogger().info(e.toString());
        }

    }
    public Connection getConnection(){
        if(this.connection != null){
            try{
                if(!this.connection.isClosed())return connection;
            } catch (SQLException e) {
                main.getLogger().info(e.toString());
            }
        }
        connect();
        return this.connection;
    }
    public void createUuid(Player p){
        try{
            final Connection connection = this.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT uuid, name FROM player_stats WHERE uuid = ?");
            preparedStatement.setString(1, p.getUniqueId().toString());
            final ResultSet resultSet = preparedStatement.executeQuery();
            if(!resultSet.next())createUserStats(connection, p);
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            main.getLogger().info(e.toString());
        }
    }
    private void createUserStats(Connection connection, Player p) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO player_stats (uuid, name, elo, kills, deaths, wins, looses) VALUES (?, ?, ?, ?, ?, ?, ?)");
        preparedStatement.setString(1, p.getUniqueId().toString());
        preparedStatement.setString(2, p.getName());
        preparedStatement.setInt(3, 0);
        preparedStatement.setInt(4, 0);
        preparedStatement.setInt(5, 0);
        preparedStatement.setInt(6, 0);
        preparedStatement.setInt(7, 0);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }
    public Pair<ResultSet, PreparedStatement> getResult(UUID uuid, String request){
        try {
            final Connection connection = this.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement(request);
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return Pair.of(resultSet, preparedStatement);
        } catch (SQLException e) {
            main.getLogger().info(e.toString());
        }
        return null;
    }
    public void setValue(UUID uuid, String request, int i){
        try {
            final PreparedStatement preparedStatement = connection.prepareStatement(request);
            preparedStatement.setInt(1, i);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            main.getLogger().info(e.toString());
        }
    }
    public Pair<ResultSet, PreparedStatement> export(){
        try {
            final Connection connection = this.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM player_stats");
            ResultSet resultSet = preparedStatement.executeQuery();
            return Pair.of(resultSet, preparedStatement);
        } catch (SQLException e) {
            main.getLogger().info(e.toString());
        }
        return null;
    }
    public void importC(List<HashMap<String, Object>> list){
        try {
            final Connection connection = this.getConnection();
            final PreparedStatement preparedStatement1 = connection.prepareStatement("DELETE FROM player_stats;");
            preparedStatement1.executeUpdate();
            preparedStatement1.close();

            for(HashMap<String, Object> map : list){
                final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO player_stats (uuid, name, elo, kills, deaths, wins, looses) VALUES (?, ?, ?, ?, ?, ?, ?)");
                preparedStatement.setString(1, map.get("uuid").toString());
                preparedStatement.setString(2, map.get("name").toString());
                preparedStatement.setInt(3, Integer.parseInt(map.get("elo").toString()));
                preparedStatement.setInt(4, Integer.parseInt(map.get("kills").toString()));
                preparedStatement.setInt(5, Integer.parseInt(map.get("deaths").toString()));
                preparedStatement.setInt(6, Integer.parseInt(map.get("wins").toString()));
                preparedStatement.setInt(7, Integer.parseInt(map.get("looses").toString()));
                preparedStatement.executeUpdate();
                preparedStatement.close();
            }
        } catch (SQLException e) {
            main.getLogger().info(e.toString());
        }
    }
}
