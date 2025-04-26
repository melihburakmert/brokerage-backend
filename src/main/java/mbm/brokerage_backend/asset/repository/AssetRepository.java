package mbm.brokerage_backend.asset.repository;

import mbm.brokerage_backend.asset.repository.entity.AssetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<AssetEntity, String> {
    List<AssetEntity> findByCustomerId(String customerId);
}