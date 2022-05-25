package com.dex.coreserver.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table
public class FilterModel extends AbstractDataModel {

    private String name;
    private String mail;
    private Double doubleVal;
    private Integer intVal;
    @OneToOne(fetch = FetchType.EAGER)
    private User user;

}
