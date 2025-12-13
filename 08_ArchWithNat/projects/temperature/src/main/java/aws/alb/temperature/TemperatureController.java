package aws.alb.temperature;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/api")
public class TemperatureController {

    private final Random random = new Random();
    private final NatureRepository natureRepository;

    public TemperatureController(NatureRepository natureRepository) {
        this.natureRepository = natureRepository;
    }

    @GetMapping("/temperature")
    public NatureDataEntity getTemp() {
        double temperature = getTemperature();
        return saveAndGetAsObject(temperature);
    }

    private NatureDataEntity saveAndGetAsObject(double temperature) {
        return natureRepository.save(new NatureDataEntity("TEMPERATURE", temperature, "°C"));
    }

    private double getTemperature() {
        int randomIntIn100 = random.nextInt(70) - 20;
        double randomDoubleIn100 = random.nextDouble();
        return randomIntIn100 + randomDoubleIn100;
    }
}
