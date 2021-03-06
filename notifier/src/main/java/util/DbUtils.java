package util;

import lombok.extern.slf4j.Slf4j;
import model.Service;
import model.ServiceType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
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

    public static boolean checkInstance() {
        if (PropertyUtils.isDebug()) {
            return true;
        }

        try {
            //时间上乘110，多算一些，排除几秒内的时间差
            int update = dbUtils.statement.executeUpdate(format("INSERT INTO check_instance (instance_id, priority, description) " +
                                                                "select '{0}', {1}, 'running' from dual where not exists (select instance_id from check_instance " +
                                                                "where update_time >= '{2}' and (priority < {1} or description = 'running') and instance_id != '{0}') " +
                                                                "ON DUPLICATE KEY UPDATE priority = {1}, description = 'running', update_time = current_timestamp();",
                                                                PropertyUtils.getInstanceId(),
                                                                PropertyUtils.getPriority(),
                                                                format(TimeUtils.getTimeWithOffset((long)(PropertyUtils.getInterval() * 60000 * 1.1 * -1)))));
            if (update != 0) {
                return true;
            }

            //被其他服务占用了，只做记录
            dbUtils.statement.executeUpdate(format("INSERT INTO check_instance " +
                                                   "(instance_id, priority, description) VALUES " +
                                                   "('{0}', {1}, 'waiting') " +
                                                   "ON DUPLICATE KEY UPDATE " +
                                                   "priority = {1}, description = 'waiting', update_time = current_timestamp();",
                                                   PropertyUtils.getInstanceId(), PropertyUtils.getPriority()));
            return false;
        } catch (Exception e) {
            log.error("sql connection error:", e);
            return false;
        }
    }

    public static List<Service> queryServices() {
        List<Service> services = new ArrayList<>();
        try {
            ResultSet rs = dbUtils.statement.executeQuery("select * from service");
            while (rs.next()) {
                Service service = new Service(rs.getString("service_id"), rs.getString("url"));
                service.setCoPhone(rs.getString("component_owner_phone"));
                service.setServiceType(ServiceType.getServiceType(rs.getString("service_type")));
                if (service.getServiceType() == ServiceType.UNKNOWN) {
                    log.warn("{}:unknown type, will be ignored!", service.getServiceId());
                } else {
                    services.add(service);
                }
            }
            rs.close();
        } catch (Exception e) {
            log.error("sql connection error:", e);
        }
        return services;
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

    public static void updateCheckLog(List<Service> services) {
        if (PropertyUtils.isDebug()) {
            log.info("debug mode no need change check_log");
            return;
        }

        for (Service service : services) {
            if (!service.isNeedSave()) {
                log.info("{} needn't save into check_log", service.getServiceId());
                continue;
            }
            try {
                dbUtils.statement.executeUpdate(String.format("insert into check_log (service_id,success,result,error_count) values('%s',%s,'%s',%d)",
                                                              service.getServiceId(),
                                                              service.getConnectFlag(),
                                                              service.getConnectResult(),
                                                              service.getErrorCount()));
            } catch (Exception e) {
                log.error("sql connection error:", e);
            }
        }

    }

    public static void updateMemoryLog(List<Service> services) {
        if (PropertyUtils.isDebug()) {
            log.info("debug mode no need change memory_check");
            return;
        }

        for (Service service : services) {
            if (service.getOldGenMax() == null
                    || service.getOldGenUsed() == null
                    || service.getStartTime() == null) {
                continue;
            }

            int rate = service.getOldGenUsed() * 100 / service.getOldGenMax();
            int rateTag = rate / 10;
            Integer lastRate = queryLastMemoryRate(service);
            int lastRateTag = 0;
            if (lastRate != null) {
                lastRateTag = lastRate / 10;
            }

            //很正常，不需要追踪
            int tag = PropertyUtils.getMemoryTag(service.getServiceId());
            if (lastRateTag < tag && rateTag < tag) {
                continue;
            }

            //状态有变化，需要追踪
            if (lastRateTag != rateTag) {
                updateMemoryLog(service);
            }
        }
    }

    private static Integer queryLastMemoryRate(Service service) {
        Integer rate = null;
        try {
            ResultSet rs = dbUtils.statement.executeQuery(String.format("SELECT memory_rate FROM memory_check where service_id = '%s' and service_start_time = '%s' order by create_time desc limit 0,1;",
                                                                        service.getServiceId(),
                                                                        service.getStartTime()));
            if (rs.next()) {
                rate =  rs.getInt("memory_rate");
            }
            rs.close();
        } catch (Exception e) {
            log.error("sql connection error:", e);
        }
        return rate;
    }

    private static void updateMemoryLog(Service service) {
        try {
            dbUtils.statement.executeUpdate(String.format("insert into memory_check (service_id,service_start_time,memory_rate,memory_max) values('%s','%s',%d,%d)",
                                                          service.getServiceId(),
                                                          service.getStartTime(),
                                                          service.getOldGenUsed() * 100 / service.getOldGenMax(),
                                                          service.getOldGenMax()));
        } catch (Exception e) {
            log.error("sql connection error:", e);
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

    private static String format(String key, Object...args) {
        return MessageFormat.format(key.replace("'", "''"), args);
    }

    private static String format(Timestamp timestamp) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(timestamp);
    }
}
