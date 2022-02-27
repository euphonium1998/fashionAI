package ices.fashion.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RenderGANCriteria {

    private String sketchFileName;
    private String colorFileName;

    private Integer id;
    private String sketch;
    private String color;

    public void init() {
        this.id = 1;
        this.sketchFileName = null;
        this.colorFileName = null;
    }
}