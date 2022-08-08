package it.gov.pagopa.afm.calculator.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "TRANSFERCATEGORY", schema = "AFM_CALCULATOR")
public class TransferCategory {

    @Id
    private Long id;

    private String name;

    public TransferCategory(String name) {
        super();
        this.id = System.currentTimeMillis();
        this.name = name;
    }

}
