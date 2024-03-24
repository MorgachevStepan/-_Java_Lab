package com.stepanew.goodmarksman.server.response;

import com.stepanew.goodmarksman.models.PlayerInfo;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
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
    List<Circle> circleList;
    List<Line> lineList;
    String winner;

}
