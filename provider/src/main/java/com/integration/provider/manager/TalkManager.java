package com.integration.provider.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.integration.provider.domain.Talk;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.Cleanup;

/**
 * @author 蒋文龙(Vin)
 * @className TalkManager
 * @description
 * @date 2019/6/13
 */
public class TalkManager {
    private List<Talk> talkList = new ArrayList<>();

    public void init(String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            @Cleanup Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8);
            for (Object object : stream.toArray()) {
                String line = (String) object;
                Talk talk = objectMapper.readValue(line, Talk.class);
                talkList.add(talk);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        talkList.sort((o1, o2) -> o2.getValue() - o1.getValue());
    }

    public void run() {
        int totalValue = 0;
        for (Talk talk : talkList) {
            totalValue += talk.getValue();
        }

        //上午标准180，下午最低120，可以先用 180 + 180 当成1天的工作量预计算
        int totalDays = totalValue / 360;
        int residue = totalValue % 360;
        if (residue > 0) {
            totalDays += 1;
        }

        int valuesOfDay = totalValue / totalDays;
        int valuesOfMorning = 180;
        int valuesOfAfternoon = valuesOfDay - valuesOfMorning;

        //开始排期
        int dayNum = 1;
        int valueNum = valuesOfMorning;
        List<Talk> route = new ArrayList<>();
        while (findRoute(talkList, route, valueNum)) {
            if (valueNum == valuesOfMorning) {
                //上午
                System.out.println("Day" + dayNum);
                System.out.println("morning:" + valuesOfMorning);
                valueNum = valuesOfAfternoon;
            } else {
                //下午
                System.out.println("afternoon:" + valuesOfAfternoon);
                valueNum = valuesOfMorning;
                ++dayNum;
            }
            route.forEach(t-> {
                System.out.println(t.toString());
                talkList.remove(t);
            });
            route.clear();
        }

        totalValue = 0;
        for (Talk talk : talkList) {
            totalValue += talk.getValue();
        }
        System.out.println("last afternoon:" + totalValue);
        talkList.forEach(t->System.out.println(t.toString()));
    }

    private boolean findRoute(List<Talk> talks, List<Talk> route, int value) {
        for (int i = 0; i < talks.size(); ++i) {
            Talk talk = talks.get(i);
            if (talk.getValue() > value) {
                continue;
            }

            route.add(talk);
            int newValue = value - talk.getValue();
            if (newValue == 0) {
                return true;
            }
            List<Talk> newTalks = talks.subList(i + 1, talks.size());
            if (findRoute(newTalks, route, newValue)) {
                return true;
            }
            route.remove(talk);
        }
        return false;
    }
}
