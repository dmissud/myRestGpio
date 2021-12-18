package org.dbs.pi4j.myrestgpio.myrest.dto;

import lombok.Data;

@Data
public class PinDTO {
    private String globalName;
    private int address;
    private String type;
    private String name;
    private String pinName;
    private String mode;
    private Number state;
}
