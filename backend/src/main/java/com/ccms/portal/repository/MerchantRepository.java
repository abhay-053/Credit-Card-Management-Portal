package com.ccms.portal.repository;

import com.ccms.portal.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    
    /**
     * Find all merchants ordered by name
     */
    List<Merchant> findAllByOrderByNameAsc();
    
    /**
     * Find merchants by category
     */
    List<Merchant> findByCategoryOrderByNameAsc(String category);
    
    /**
     * Find merchants by name containing (case insensitive)
     */
    List<Merchant> findByNameContainingIgnoreCaseOrderByNameAsc(String name);
}
