package aws.alb.temperature;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/api")
public class TemperatureController {

    private final Random random = new Random();

    @GetMapping("/current-temperature")
    public String getHumidity() {
        int randomIntIn100 = random.nextInt(50);
        double randomDoubleIn100 = random.nextDouble();

        double humidity = randomIntIn100 + randomDoubleIn100;
        return String.format("%.2f", humidity) + "°C";
    }
}
