package aws.arch.vpc.nature;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class NatureController {

    private final NatureRepository natureRepository;

    public NatureController(NatureRepository natureRepository) {
        this.natureRepository = natureRepository;
    }

    @GetMapping("/nature")
    public List<NatureDataEntity> getData(){
        return natureRepository.findAll();
    }

}
