package utils;

import lombok.extern.slf4j.Slf4j;
import model.ServiceNotifier;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/3/24
 */
@Slf4j
public class DbUtils {

    private Connection connection;
    private Statement statement;
    private static DbUtils dbUtils = new DbUtils();

    private DbUtils() {
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

    public static List<ServiceNotifier> queryServiceNotifiers() {
        List<ServiceNotifier> serviceNotifiers = new ArrayList<>();
        try {
            ResultSet rs = dbUtils.statement.executeQuery("select * from service");
            while (rs.next()) {
                serviceNotifiers.add(new ServiceNotifier(rs.getString("service_id"), rs.getString("url")));
            }
            rs.close();
        } catch (Exception e) {
            log.error("sql connection error:", e);
        }
        return serviceNotifiers;
    }

    public static int queryErrorCount(String serviceId) {
        int errorCount = 0;
        try {
            ResultSet rs = dbUtils.statement.executeQuery(String.format("select * from check_log where service_id = '%s' order by create_time desc limit 0,1", serviceId));
            if (rs.next()) {
                errorCount =  rs.getInt("error_count");
            }
            rs.close();
        } catch (Exception e) {
            log.error("sql connection error:", e);
        }
        return errorCount;
    }

    public static Timestamp queryErrorStartTime(String serviceId) {
        Timestamp startTime = null;
        try {
            ResultSet rs = dbUtils.statement.executeQuery(String.format("select * from check_log where service_id = '%s' and error_count = 1 order by create_time desc limit 0,1", serviceId));
            if (rs.next()) {
                startTime = rs.getTimestamp("create_time");
            }
            rs.close();
        } catch (Exception e) {
            log.error("sql connection error:", e);
        }
        return startTime;
    }

    public static void updateCheckLog(List<ServiceNotifier> serviceNotifiers) {
        for (ServiceNotifier serviceNotifier : serviceNotifiers) {
            if (!serviceNotifier.isNeedSave()) {
                log.info("{} needn't save into database", serviceNotifier.getServiceId());
                continue;
            }
            try {
                dbUtils.statement.executeUpdate(String.format("insert into check_log (service_id,success,result,error_count) values('%s',%s,'%s',%d)",
                                                              serviceNotifier.getServiceId(),
                                                              serviceNotifier.getConnectFlag(),
                                                              serviceNotifier.getConnectResult(),
                                                              serviceNotifier.getErrorCount()));
            } catch (Exception e) {
                log.error("sql connection error:", e);
            }
        }

    }

    public static int queryYesterdayErrorCount() {
        int errorCount = 0;
        try {
            ResultSet rs = dbUtils.statement.executeQuery(String.format("SELECT count(*) FROM check_log where success = 0 and create_time between '%s' and '%s';",
                                                                        TimeUtils.yesterday(),
                                                                        TimeUtils.now()));
            if (rs.next()) {
                errorCount =  rs.getInt(1);
            }
            rs.close();
        } catch (Exception e) {
            log.error("sql connection error:", e);
        }
        return errorCount;
    }

    @Override
    protected void finalize() throws SQLException {
        statement.close();
        connection.close();
    }
}
