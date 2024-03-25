package com.stepanew.goodmarksman.server.response;

import com.stepanew.goodmarksman.models.PlayerInfo;
import com.stepanew.goodmarksman.models.Point;
import lombok.AccessLevel;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class ServerResponse {

    List<PlayerInfo> playerInfoList;
    List<Point> circleList;
    List<Point> lineList;
    String winner;

}
