package uhk.fim;

import uhk.fim.database.DBInitializer;
import uhk.fim.gui.MainFrame;
import javax.swing.*;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame mainFrame = new MainFrame(800, 600);
                mainFrame.setVisible(true);
            }
        });
/*
        DBInitializer dbInitializer = new DBInitializer("org.apache.derby.jdbc.EmbeddedDriver", "jdbc:derby:SampleDB;create=true");
        try {
            dbInitializer.init();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
 */
    }
}
