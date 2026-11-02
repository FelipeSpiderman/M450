package ch.tbz.m450.healthleveling.day;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/days")
public class DayController {

    private final DayService dayService;

    public DayController(DayService dayService) {
        this.dayService = dayService;
    }

    @GetMapping
    public List<DayDetail> getDays() {
        return dayService.getDays();
    }

    @GetMapping("/{date}")
    public DayDetail getDay(@PathVariable LocalDate date) {
        return dayService.getDay(date);
    }

    /** Manual input. Creates the day or updates the given values of an existing day. */
    @PostMapping
    public DayDetail save(@RequestBody DailyValues values) {
        dayService.saveAll(List.of(values));
        return dayService.getDay(values.date());
    }
}
