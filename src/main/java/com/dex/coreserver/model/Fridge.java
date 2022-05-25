package com.dex.coreserver.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Fridge extends AbstractDataModel {
    private String name;
    private boolean milk;
    private boolean eggs;
    private boolean meat;
}
