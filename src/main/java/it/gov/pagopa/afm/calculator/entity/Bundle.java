package it.gov.pagopa.afm.calculator.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.gov.pagopa.afm.calculator.model.BundleType;
import it.gov.pagopa.afm.calculator.model.PaymentMethod;
import it.gov.pagopa.afm.calculator.model.Touchpoint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "BUNDLE", schema = "AFM_CALCULATOR")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Bundle {

    @Id
    private String id;

    private String idPsp;

    private Long paymentAmount;
    private Long minPaymentAmount;
    private Long maxPaymentAmount;

    private PaymentMethod paymentMethod;

    private Touchpoint touchpoint;

    private BundleType type;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<TransferCategory> transferCategoryList;

}
