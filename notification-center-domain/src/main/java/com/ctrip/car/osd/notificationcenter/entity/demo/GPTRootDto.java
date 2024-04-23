package com.ctrip.car.osd.notificationcenter.entity.demo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by xiayx on 2023/5/26.
 */
@Getter
@Setter
public class GPTRootDto {
    String id;
    String object;
    Long created;
    String model;
    List<ChoicesDto> choices;
}
