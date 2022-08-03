package it.gov.pagopa.afm.calculator.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CIBUNDLE", schema = "AFM_CALCULATOR")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CiBundle {

    @Id
    private String id;

    private String ciFiscalCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="bundleId", referencedColumnName = "id")
    private Bundle bundle;

    @OneToMany (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CiBundleAttribute> attributes;

}
