package aws.arch.vpc.nature;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NatureRepository extends JpaRepository<NatureDataEntity, Long> {
}
