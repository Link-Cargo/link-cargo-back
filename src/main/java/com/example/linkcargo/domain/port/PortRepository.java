package com.example.linkcargo.domain.port;

import com.example.linkcargo.domain.schedule.PortType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortRepository extends JpaRepository <Port, Long> {
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long portId);

    List<Port> findAllByType(PortType type);
}
