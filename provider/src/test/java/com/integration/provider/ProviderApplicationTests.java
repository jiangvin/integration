package com.integration.provider;

import com.integration.provider.manager.MapManager;
import com.integration.provider.manager.PackageScannerManager;
import com.integration.provider.manager.TalkManager;
import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProviderApplicationTests {

    @Test
    public void Map() {
        MapManager mapManager = new MapManager();
        Random r = new Random(20181220);
        for (int i = 0; i < 100000; ++i) {
            long x = r.nextInt(10000) - 5000;
            long y = r.nextInt(10000) - 5000;
            mapManager.AddPoint(x, y);
        }
        Assert.assertEquals(mapManager.getPointCount(), 99953);
        MapManager.Point p = mapManager.findNearest(0, 0);
        Assert.assertEquals(p.getX(), 3);
        Assert.assertEquals(p.getY(), -10);
    }

    @Test
    public void talk() {
        TalkManager talkManager = new TalkManager();
        talkManager.init("src/main/resources/talk.txt");
        talkManager.run();
    }

    @Test
    public void doScan() {
        PackageScannerManager packageScannerManager = new PackageScannerManager("com.integration.provider.manager");
        List<String> results = packageScannerManager.getFullyQualifiedClassNameList();
        Assert.assertTrue(results.size() > 1);
        Assert.assertEquals(results.get(0), "com.integration.provider.manager.AspectManager");
    }
}
