package com.wezik.app.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class PartLog {

    private final String id;
    private final String state;
    private final String type;
    private final String host;
    private final Long timestamp;

}
