package ro.mpp2024;

import java.sql.SQLException;

public interface IObserver {
    public void update() throws MyAppException, SQLException;
}