package feusalamander.cs_mo.Managers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static feusalamander.cs_mo.CS_MO.main;

public class MySQL {
    private Connection connection;
    public MySQL() {
        this.connect();
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

    private Connection getConnection() throws SQLException {
        if(this.connection != null){
            if(!this.connection.isClosed()){
                return connection;
            }
        }
        connect();
        return this.connection;
    }
}
