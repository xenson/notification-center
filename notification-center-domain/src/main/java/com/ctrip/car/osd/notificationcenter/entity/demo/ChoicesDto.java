package com.ctrip.car.osd.notificationcenter.entity.demo;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by xiayx on 2023/5/26.
 */
@Getter
@Setter
public class ChoicesDto {
    Integer index;
    String finish_reason;
    MessageDto message;
}
