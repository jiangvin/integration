package dao;

import lombok.extern.slf4j.Slf4j;
import model.ServiceNotifier;
import utils.PropertyUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/3/24
 */
@Slf4j
public class BaseDao {

    private Connection connection;
    private Statement statement;

    public BaseDao() {
        try {
            connection = DriverManager.getConnection(
                             PropertyUtils.getDbUrl(),
                             PropertyUtils.getDbUsername(),
                             PropertyUtils.getDbPassword());

            //创建Statement，执行sql
            statement = connection.createStatement();
        } catch (Exception e) {
            log.error("sql connection error:", e);
        }
    }

    public List<ServiceNotifier> queryServiceNotifiers() {
        List<ServiceNotifier> serviceNotifiers = new ArrayList<>();
        try {
            ResultSet rs = statement.executeQuery("select * from service");
            while (rs.next()) {
                serviceNotifiers.add(new ServiceNotifier(rs.getString("service_id"), rs.getString("url")));
            }
            rs.close();
        } catch (Exception e) {
            log.error("sql connection error:", e);
        }
        return serviceNotifiers;
    }

    public int queryErrorCount(String serviceId) {
        int errorCount = 0;
        try {
            ResultSet rs = statement.executeQuery(String.format("select * from check_log where service_id = '%s' order by create_time desc limit 0,1", serviceId));
            if (rs.next()) {
                errorCount =  rs.getInt("error_count");
            }
            rs.close();
        } catch (Exception e) {
            log.error("sql connection error:", e);
        }
        return errorCount;
    }

    public void updateCheckLog(List<ServiceNotifier> serviceNotifiers) {
        for (ServiceNotifier serviceNotifier : serviceNotifiers) {
            if (!serviceNotifier.isNeedSave()) {
                log.info("{} needn't save into database", serviceNotifier.getServiceId());
                continue;
            }
            try {
                statement.executeUpdate(String.format("insert into check_log (service_id,success,result,error_count) values('%s',%s,'%s',%d)",
                                                      serviceNotifier.getServiceId(),
                                                      serviceNotifier.getConnectFlag(),
                                                      serviceNotifier.getConnectResult(),
                                                      serviceNotifier.getErrorCount()));
            } catch (Exception e) {
                log.error("sql connection error:", e);
            }
        }

    }

    @Override
    protected void finalize() throws SQLException {
        statement.close();
        connection.close();
    }
}
