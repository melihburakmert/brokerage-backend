package mbm.brokerage_backend.asset.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;

@Entity
@Table(name = "asset")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "id", nullable = false, updatable = false)
    private String id;
    
    @Column(name = "customer_id", nullable = false)
    private String customerId;
    
    @Column(name = "asset_name", nullable = false, length = 5)
    private String assetName;
    
    @Column(name = "size", nullable = false, precision = 19, scale = 8)
    private BigDecimal size;
    
    @Column(name = "usable_size", nullable = false, precision = 19, scale = 8)
    private BigDecimal usableSize;
}