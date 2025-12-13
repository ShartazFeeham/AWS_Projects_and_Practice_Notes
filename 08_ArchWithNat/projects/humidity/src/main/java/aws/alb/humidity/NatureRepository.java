package aws.alb.humidity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NatureRepository extends JpaRepository<NatureDataEntity, Long> {
}
