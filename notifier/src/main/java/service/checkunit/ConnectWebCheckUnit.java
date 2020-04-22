package service.checkunit;

import lombok.extern.slf4j.Slf4j;
import model.Service;
import model.ServiceType;
import util.HttpUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 蒋文龙(Vin)
 * @description
 * @date 2020/4/22
 */

@Slf4j
public class ConnectWebCheckUnit extends BaseCheckUnit {

    private static final Pattern JS_PATH = Pattern.compile("/js/app.[\\w]+.js");

    private static final Pattern VERSION_TAG = Pattern.compile("\\{version:\"[0-9.]+\"\\}");


    @Override
    boolean isCheck(Service service) {
        return service.getServiceType() == ServiceType.WEB;
    }

    @Override
    void startCheck(List<Service> services) {
        for (Service service : services) {
            try {
                String str = HttpUtils.getRequest(service.getUrl(), String.class);
                service.setConnectResult(str, true);
                if (!findVersionInfo(service)) {
                    service.setConnectResult("抓取版本信息失败", false);
                }
            } catch (Exception e) {
                service.setConnectResult(e.getMessage(), false);
            }
        }
    }

    private boolean findVersionInfo(Service service) {
        String[] urlInfos = service.getUrl().split("/");
        if (urlInfos.length < 3) {
            log.error("can not parse host in {}", service.getUrl());
            return false;
        }

        Matcher m = JS_PATH.matcher(service.getConnectResult());
        if (!m.find()) {
            log.error("can not find js path in :\n{}", service.getConnectResult());
            return false;
        }
        String jsPath = m.group();

        String jsContent = HttpUtils.getRequest(String.format("%s//%s%s",
                                                              urlInfos[0], urlInfos[2], jsPath),
                                                String.class);
        m = VERSION_TAG.matcher(jsContent);
        if (!m.find()) {
            log.error("can not find version in :\n{}", jsContent);
            return false;
        }
        String versionInfo = m.group();
        String version = versionInfo.split(":")[1]
                         .replace("\"", "")
                         .replace("}", "");
        service.setVersion(version);
        return true;
    }
}
