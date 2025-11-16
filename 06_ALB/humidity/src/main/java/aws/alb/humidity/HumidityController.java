package aws.alb.humidity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/api")
public class HumidityController {

    private final Random random = new Random();

    @GetMapping("/current-humidity")
    public String getHumidity() {
        int randomIntIn100 = random.nextInt(99);
        double randomDoubleIn100 = random.nextDouble();

        double humidity = randomIntIn100 + randomDoubleIn100;
        return String.format("%.2f", humidity) + "%";
    }

}
