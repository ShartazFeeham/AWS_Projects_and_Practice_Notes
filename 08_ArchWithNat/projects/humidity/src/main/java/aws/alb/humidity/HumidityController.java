package aws.alb.humidity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/api")
public class HumidityController {

    private final NatureRepository natureRepository;

    private final Random random = new Random();

    public HumidityController(NatureRepository natureRepository) {
        this.natureRepository = natureRepository;
    }

    @GetMapping("/humidity")
    public NatureDataEntity getHumidity() {
        double humidity = getHumidityRandom();
        return saveAndGetAsObject(humidity);
    }

    private NatureDataEntity saveAndGetAsObject(double humidity) {
        NatureDataEntity data = new NatureDataEntity("HUMIDITY", humidity, "%");
        return natureRepository.save(data);
    }

    private double getHumidityRandom() {
        int randomIntIn100 = random.nextInt(99);
        double randomDoubleIn100 = random.nextDouble();

        return randomIntIn100 + randomDoubleIn100;
    }

}
